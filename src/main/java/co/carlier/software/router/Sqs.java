package co.carlier.software.router;

import co.carlier.software.router.exception.RouterException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.UUID;

public abstract class Sqs implements RequestHandler<SQSEvent, Void> {

    private static final Logger LOG = LoggerFactory.getLogger(Sqs.class);

    private final Router<Void> router = new Router<>();
    private boolean initialized = false;

    public Sqs() {
    }

    /**
     * Only POST method is supported!!
     */
    protected abstract void routes(Router<Void> router);

    protected abstract void handleException(Exception e);

    private void initialize() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initializing routes");
        }

        routes(router);

        //check if added routes only contain the post method
        router.routes().forEach(r -> {
            if (!r.method().toLowerCase(Locale.ENGLISH).equals("post")) {
                throw new RouterException("sqs router only supports POST methods");
            }
        });
    }

    @Override
    public final Void handleRequest(final SQSEvent event, Context context) {
        try {
            if (!initialized) {
                initialize();
                initialized = true;
            }

            if (event.getRecords().isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("empty receive");
                }

                return null;
            }

            event.getRecords().forEach(o -> {
                LOG.info("start processing message " + o.getMessageId());

                if (!o.getMessageAttributes().containsKey("path")) {
                    LOG.error("sqs message '" + o.getMessageId() + "' does not contain a path attribute");
                    return;
                }

                LOG.info("request with path '" + o.getMessageAttributes().get("path").getStringValue() + "' found");

                Request request = new Request();
                request.setBody(o.getBody());

                if (o.getMessageAttributes().containsKey("ip")) {
                    request.setSourceIp(o.getMessageAttributes().get("ip").getStringValue());
                }

                if (o.getMessageAttributes().containsKey("tenantId")) {
                    LOG.debug("authorizer parameters found");

                    request.setAuth(new Request.Auth(
                            UUID.fromString(o.getMessageAttributes().get("tenantId").getStringValue())
                    ));
                }

                router.execute("post", o.getMessageAttributes().get("path").getStringValue(), request);
                LOG.info("successfully processed message " + o.getMessageId());
            });

            return null;

        } catch (RuntimeException e) {
            LOG.info("failed processing message");

            /*@todo we need a function to remove successful message in the batch from queue.
             *   they will requeue if even 1 message is faulty because of the low usage we set batch on 1 to prevent this
             */

            handleException(e);
            return null;
        }
    }
}