package plan3.statics.model;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NotModifiedException extends WebApplicationException {

    private static final long serialVersionUID = 4596663617056526582L;

    public NotModifiedException(final Located entity) {
        super(Response
                .notModified(entity.where().revision().toString())
                .location(URI.create(entity.where().toString('/')))
                .entity(entity.where().toString()).build());
    }
}
