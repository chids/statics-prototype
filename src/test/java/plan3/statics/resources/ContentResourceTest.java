package plan3.statics.resources;

import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.HttpHeaders.IF_MATCH;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.junit.ResourceTestRule;
import plan3.statics.model.Content;
import plan3.statics.model.Coordinator;
import plan3.statics.model.Revision;
import plan3.statics.model.Static;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

public class ContentResourceTest {

    private static final Coordinator coordinator = mock(Coordinator.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ContentResource(coordinator))
            .addProvider(ContentProvider.class)
            .addProvider(RevisionProvider.class)
            .addProvider(StaticProvider.class)
            .build();

    @Before
    public void reset() {
        Mockito.reset(coordinator);
    }

    @Test
    public void delete() throws Exception {
        final Revision revision = new Revision("blah");
        final Static version1 = new Static("domain", "type", "id", revision);
        final ClientResponse response = resources.client()
                .resource("/domain/type/id")
                .type(TEXT_PLAIN)
                .header(IF_MATCH, revision.toString())
                .delete(ClientResponse.class);
        verify(coordinator).delete(version1);
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void post() throws Exception {
        final Content version1 = new Content("domain", "type", "id", "content");
        when(coordinator.add(version1)).thenReturn(version1.path());
        final ClientResponse response = resources.client()
                .resource("/domain/type/id")
                .type(TEXT_PLAIN)
                .post(ClientResponse.class, version1.content());
        verify(coordinator).add(version1);
        assertEquals(CREATED.getStatusCode(), response.getStatus());
        assertThat(response.getLocation().toString()).endsWith(version1.path().toString('/'));
        assertEquals(version1.path().revision().toString(), response.getHeaders().getFirst(ETAG));
    }

    @Test
    public void put() throws Exception {
        post();
        final Content version1 = new Content("domain", "type", "id", "content");
        final Content version2 = version1.update("new content");
        when(coordinator.update(version1.path(), version2)).thenReturn(version2.path());
        final ClientResponse response = resources.client()
                .resource("/domain/type/id")
                .type(TEXT_PLAIN)
                .header(IF_MATCH, version1.path().revision())
                .put(ClientResponse.class, version2.content());
        verify(coordinator).add(version1);
        verify(coordinator).update(version1.path(), version2);
        assertEquals(ACCEPTED.getStatusCode(), response.getStatus());
        assertThat(response.getLocation().toString()).endsWith(version2.path().toString('/'));
        assertEquals(version2.path().revision().toString(), response.getHeaders().getFirst(ETAG));
    }
}
