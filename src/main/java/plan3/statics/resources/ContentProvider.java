package plan3.statics.resources;

import plan3.pure.jersey.providers.AbstractInjectableProvider;
import plan3.statics.model.Content;
import plan3.statics.model.Revision;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

public class ContentProvider extends AbstractInjectableProvider<Content> {
    public ContentProvider() {
        super(Content.class);
    }

    @Override
    public Content getValue(final HttpContext c) {
        final HttpRequestContext request = c.getRequest();
        try(InputStream payload = request.getEntity(InputStream.class)) {
            final byte[] content = ByteStreams.toByteArray(payload);
            return new Content(StaticProvider.from(request, new Revision(content)), request.getMediaType(), content);
        }
        catch(final IOException e) {
            throw new WebApplicationException(e);
        }
    }
}
