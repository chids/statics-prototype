package plan3.statics.model;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import plan3.pure.jersey.exceptions.PreconditionFailedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import com.google.common.base.Charsets;

public class Content {
    private final Location path;
    private final MediaType mime;
    private final byte[] content;

    public Content(final String domain, final String type, final String id, final String content) {
        this(new Location(domain, type, id, new Revision(content)), content);
    }

    public Content(final Location path, final String content) {
        this(path, TEXT_PLAIN_TYPE, content.getBytes(Charsets.UTF_8));
    }

    public Content(final Location path, final MediaType mime, final byte[] content) {
        final Revision revision = new Revision(content);
        if(!path.revision().equals(revision)) {
            throw new PreconditionFailedException("Revision mismatch: " + path.revision() + " != " + revision);
        }
        this.path = path.withRevision(revision);
        this.mime = mime;
        this.content = content;
    }

    public Content update(final String content) {
        return new Content(this.path.withRevision(new Revision(content)), content);
    }

    @Override
    public String toString() {
        return this.content.length + " bytes of " + this.mime.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Content) {
            final Content that = (Content)o;
            return Objects.equals(this.mime, that.mime) && Arrays.equals(this.content, that.content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mime, Arrays.hashCode(this.content));
    }

    public InputStream content() {
        return new ByteArrayInputStream(this.content);
    }

    public Location path() {
        return this.path;
    }

    public MediaType mime() {
        return this.mime;
    }
}
