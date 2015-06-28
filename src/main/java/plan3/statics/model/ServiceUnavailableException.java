package plan3.statics.model;

import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

import plan3.pure.util.Timeout;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ServiceUnavailableException extends WebApplicationException {

    private static final long serialVersionUID = -1642449627776024755L;

    public ServiceUnavailableException(final Located entity) {
        this(entity, new Timeout(2, TimeUnit.SECONDS));
    }

    public ServiceUnavailableException(final Located entity, final Timeout retry) {
        super(Response.status(SERVICE_UNAVAILABLE)
                .location(URI.create(entity.where().toString('/')))
                .header("Retry-After", retry.to(TimeUnit.SECONDS))
                .entity(entity.toString()).build());
    }
}
