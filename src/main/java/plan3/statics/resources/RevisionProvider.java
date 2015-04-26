package plan3.statics.resources;

import static javax.ws.rs.core.HttpHeaders.IF_MATCH;

import plan3.pure.jersey.exceptions.BadRequestException;
import plan3.pure.jersey.providers.AbstractInjectableProvider;
import plan3.statics.model.Revision;

import com.google.common.hash.HashCode;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

public class RevisionProvider extends AbstractInjectableProvider<Revision> {
    public RevisionProvider() {
        super(Revision.class);
    }

    @Override
    public Revision getValue(final HttpContext c) {
        return revision(IF_MATCH, c.getRequest());
    }

    public static Revision revision(final String header, final HttpRequestContext request) {
        final String etag = request.getHeaderValue(header);
        if(etag == null) {
            throw new BadRequestException("Must specify " + header);
        }
        return new Revision(HashCode.fromString(etag));
    }
}