package plan3.statics.exceptions;

import static javax.ws.rs.core.HttpHeaders.ETAG;

import plan3.statics.model.Located;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

abstract class AbstractHttpException extends WebApplicationException {

    private static final long serialVersionUID = 3814795729581246178L;

    protected AbstractHttpException(final Status status, final Located entity) {
        super(Response
                .status(status)
                .location(entity.where().toURI())
                .header(ETAG, entity.where().revision())
                .entity(entity)
                .build());
    }
}
