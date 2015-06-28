package plan3.statics.model;

import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class RevisionMismatchException extends WebApplicationException {

    private static final long serialVersionUID = 3982012876991408827L;

    public RevisionMismatchException(final Located entity) {
        super(Response.status(CONFLICT)
                .location(URI.create(entity.where().toString('/')))
                .header(ETAG, entity.where().revision())
                .entity(entity.toString()).build());
    }
}
