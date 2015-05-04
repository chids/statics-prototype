package plan3.statics.resources;

import static javax.ws.rs.core.HttpHeaders.ETAG;
import plan3.statics.model.Content;
import plan3.statics.model.Coordinator;
import plan3.statics.model.Revision;
import plan3.statics.model.Location;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("{domain}/{type}/{id}")
public class ContentResource {

    private final Coordinator coordinator;

    public ContentResource(final Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @POST
    public Response add(@Context final Content content) throws Exception {
        final Location added = this.coordinator.add(content);
        return Response.created(URI.create(added.toString('/')))
                .type(content.mime())
                .header(ETAG, content.path().revision())
                .entity(content.content()).build();
    }

    @PUT
    public Response update(@Context final Revision current, @Context final Content candidate) throws Exception {
        final Location updated = this.coordinator.update(candidate.path().withRevision(current), candidate);
        return Response.status(Status.ACCEPTED)
                .location(URI.create(updated.toString('/')))
                .type(candidate.mime())
                .header(ETAG, candidate.path().revision())
                .entity(candidate.content())
                .build();
    }

    @DELETE
    public void delete(@Context final Location target) throws Exception {
        this.coordinator.delete(target);
    }
}
