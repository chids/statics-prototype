package plan3.statics.resources;

import static java.util.stream.Collectors.toList;

import plan3.pure.jersey.exceptions.BadRequestException;
import plan3.pure.jersey.providers.AbstractInjectableProvider;
import plan3.statics.model.Revision;
import plan3.statics.model.Static;

import java.util.List;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

public class StaticProvider extends AbstractInjectableProvider<Static> {
    public StaticProvider() {
        super(Static.class);
    }

    @Override
    public Static getValue(final HttpContext c) {
        final HttpRequestContext request = c.getRequest();
        return from(request, RevisionProvider.from(request));
    }

    public static Static from(final HttpRequestContext request, final Revision revision) {
        final List<String> segments = segments(request);
        return new Static(segments.get(0), segments.get(1), segments.get(2), revision);
    }

    private static List<String> segments(final HttpRequestContext request) {
        final List<PathSegment> segments = request.getPathSegments();
        if(segments.size() == 3) {
            return segments.stream().map(PathSegment::getPath).collect(toList());
        }
        throw new BadRequestException("Path must contain [domain]/[type]/[id], was: " + request.getPath());
    }
}
