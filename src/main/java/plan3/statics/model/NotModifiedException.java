package plan3.statics.model;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NotModifiedException extends WebApplicationException {

    private static final long serialVersionUID = 4596663617056526582L;

    public NotModifiedException(final Static path) {
        // Set location to GET API endpoint?
        super(Response.notModified(path.revision().toString()).entity(path.toString()).build());
    }
}
