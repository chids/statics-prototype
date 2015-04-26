package plan3.statics.resources;

import static java.util.stream.Collectors.toList;

import plan3.pure.jersey.exceptions.BadRequestException;
import plan3.pure.jersey.providers.AbstractInjectableProvider;
import plan3.statics.model.Content;
import plan3.statics.model.Revision;
import plan3.statics.model.Static;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.PathSegment;

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
        final List<String> segments = segments(request);
        try(InputStream payload = request.getEntity(InputStream.class)) {
            final byte[] content = ByteStreams.toByteArray(payload);
            final Static path = new Static(segments.get(0), segments.get(1), segments.get(2), new Revision(content));
            return new Content(path, request.getMediaType(), content);
        }
        catch(final IOException e) {
            throw new WebApplicationException(e);
        }
    }

    private static List<String> segments(final HttpRequestContext request) {
        final List<PathSegment> segments = request.getPathSegments();
        if(segments.size() == 3) {
            return segments.stream().map(PathSegment::getPath).collect(toList());
        }
        throw new BadRequestException("Path must contain [domain]/[type]/[id], was: " + request.getPath());
    }
}
