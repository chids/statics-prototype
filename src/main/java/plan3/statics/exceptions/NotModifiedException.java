package plan3.statics.exceptions;

import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;

import plan3.statics.model.Located;

public class NotModifiedException extends AbstractHttpException {

    private static final long serialVersionUID = 4596663617056526582L;

    public NotModifiedException(final Located entity) {
        super(NOT_MODIFIED, entity);
    }
}
