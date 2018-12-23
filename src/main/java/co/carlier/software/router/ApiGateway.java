package co.carlier.software.router;

import co.carlier.software.router.exception.ExceptionNotFound;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ApiGateway<R extends Response> implements RequestHandler<APIGatewayProxyRequestEventCustom, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGateway.class);

    private final ObjectMapper mapper;
    private final Router<R> router = new Router<>();
    private final Map<String, String> defaultHeaders = new HashMap<>();
    private boolean initialized = false;

    public ApiGateway(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    protected abstract void routes(Router<R> router);

    protected abstract void defaultHeaders(Map<String, String> headers);

    protected abstract ExceptionResponse handleException(Exception e);

    private void initialize() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initializing routes");
        }

        routes(router);

        if (LOG.isDebugEnabled()) {
            LOG.debug("loading default headers");
            LOG.debug(defaultHeaders.toString());
        }

        defaultHeaders(defaultHeaders);
    }

    @Override
    public final APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEventCustom event, final Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();

        try {
            if (!initialized) {
                initialize();
                initialized = true;
            }

            responseEvent.setHeaders(defaultHeaders);

            //request
            Request request = new Request();
            request.setSourceIp(event.getRequestContext().getIdentity().getSourceIp());
            request.setBody(event.getBody());

            // authorizer parameters
            Map<String, String> authContext = event.getRequestContext().getAuthorizer();
            if (authContext != null && authContext.containsKey("tenantId")) {
                LOG.debug("authorizer parameters found");

                request.setAuth(new Request.Auth(
                        UUID.fromString(authContext.get("tenantId")),
                        UUID.fromString(authContext.get("userId")),
                        authContext.get("role"),
                        authContext.get("email"),
                        authContext.get("accessToken")
                ));
            }

            //response
            R response = router.execute(event.getHttpMethod(), event.getPath(), request);

            if (!response.getHeaders().isEmpty()) {
                Map<String, String> currentHeaders = responseEvent.getHeaders();
                response.getHeaders().forEach(currentHeaders::put);
                responseEvent.setHeaders(currentHeaders);
            }

            responseEvent.setBody(mapper.writeValueAsString(response.getBody()));
            responseEvent.setStatusCode(response.getStatusCode());

            return responseEvent;

        } catch (Exception e) {
            ExceptionResponse response = handleException(e);
            if (response == null) {

                if (e instanceof JsonProcessingException) {
                    LOG.error(e.getMessage(), e);

                    responseEvent.setStatusCode(500);
                    responseEvent.setBody("{\"message\": \"json (de)serialization failed\"}");
                } else if (e instanceof ExceptionNotFound) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(e.getMessage(), e);
                    }

                    responseEvent.setStatusCode(404);
                    responseEvent.setBody("{\"message\": \"" + e.getMessage() + "\"}");

                } else {
                    LOG.error(e.getMessage(), e);
                    response = new ExceptionResponse("application error occurred");
                }
            }

            responseEvent.setStatusCode(response.getStatusCode());
            responseEvent.setBody(response.getBody());

            return responseEvent;
        }
    }
}