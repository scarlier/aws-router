package co.carlier.software.router;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Map;

/**
 * extending APIGatewayProxyRequestEvent to add authorizer variable
 */
public class APIGatewayProxyRequestEventCustom extends APIGatewayProxyRequestEvent {

    private ProxyRequestContextCustom requestContext;

    public ProxyRequestContextCustom getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(ProxyRequestContextCustom requestContext) {
        this.requestContext = requestContext;
    }

    public static class ProxyRequestContextCustom extends ProxyRequestContext {

        private Map<String, String> authorizer;

        public Map<String, String> getAuthorizer() {
            return authorizer;
        }

        public void setAuthorizer(Map<String, String> authorizer) {
            this.authorizer = authorizer;
        }

        public ProxyRequestContext withAuthorizer(Map<String, String> authorizer) {
            this.authorizer = authorizer;
            return this;
        }
    }
}