package plan3.statics.exceptions;

import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.junit.Assert.assertEquals;

import plan3.statics.model.Located;
import plan3.statics.model.Location;
import plan3.statics.model.Revision;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class ExceptionsTest {

    private static final Revision revision = new Revision("content");
    private static final Located entity = new Location("domain", "type", "id", revision);

    @Test
    public void notModifiedException() {
        assertResponse(NOT_MODIFIED, new NotModifiedException(entity).getResponse());
    }

    @Test
    public void internalConflictException() {
        assertResponse(CONFLICT, new InternalConflictException(entity).getResponse());
    }

    @Test
    public void doesntExistsException() {
        assertResponse(NOT_FOUND, new DoesntExistException(entity).getResponse());
    }

    @Test
    public void serviceUnavailableException() {
        final ServiceUnavailableException exception = new ServiceUnavailableException(entity);
        assertResponse(SERVICE_UNAVAILABLE, exception.getResponse());
        assertEquals(2, exception.getResponse().getMetadata().getFirst("Retry-After"));
    }

    private static void assertResponse(final Status status, final Response response) {
        assertEquals(status.getStatusCode(), response.getStatus());
        assertEquals(entity, response.getEntity());
        final MultivaluedMap<String, Object> headers = response.getMetadata();
        assertEquals(revision.toString(), headers.getFirst(ETAG).toString());
        assertEquals(entity.where().toURI(), headers.getFirst(LOCATION));
    }
}
