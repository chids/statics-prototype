package plan3.statics.exceptions;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import plan3.statics.model.Located;

public class DoesntExistException extends AbstractHttpException {

    private static final long serialVersionUID = 4596663617056526582L;

    public DoesntExistException(final Located entity) {
        super(NOT_FOUND, entity);
    }
}
