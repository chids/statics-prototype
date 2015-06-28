package plan3.statics.exceptions;

import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

import plan3.pure.util.Timeout;
import plan3.statics.model.Located;

import java.util.concurrent.TimeUnit;

public class ServiceUnavailableException extends AbstractHttpException {

    private static final long serialVersionUID = -1642449627776024755L;

    public ServiceUnavailableException(final Located entity) {
        this(entity, new Timeout(2, TimeUnit.SECONDS));
    }

    public ServiceUnavailableException(final Located entity, final Timeout retry) {
        super(SERVICE_UNAVAILABLE, entity);
        super.getResponse().getMetadata().putSingle("Retry-After", (int)retry.to(TimeUnit.SECONDS));
    }
}
