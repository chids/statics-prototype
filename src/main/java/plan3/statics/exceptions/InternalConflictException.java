package plan3.statics.exceptions;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import plan3.statics.model.Located;

public class InternalConflictException extends AbstractHttpException {

    private static final long serialVersionUID = 3982012876991408827L;

    public InternalConflictException(final Located entity) {
        super(CONFLICT, entity);
    }
}
