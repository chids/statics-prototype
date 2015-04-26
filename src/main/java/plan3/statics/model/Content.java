package plan3.statics.model;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import plan3.pure.jersey.exceptions.PreconditionFailedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

public class Content {
    private final Static path;
    final MediaType mime;
    private final byte[] content;

    public Content(final String domain, final String type, final String id, final String content) {
        this(new Static(domain, type, id, hash(content)), content);
    }

    public Content(final Static path, final String content) {
        this(path, TEXT_PLAIN_TYPE, content.getBytes(Charsets.UTF_8));
    }

    public Content(final Static path, final MediaType mime, final byte[] content) {
        final HashCode revision = hash(content);
        if(!path.revision().equals(revision)) {
            throw new PreconditionFailedException("Revision mismatch: " + path.revision() + " != " + revision);
        }
        this.path = path.withRevision(revision);
        this.mime = mime;
        this.content = content;
    }

    public Content update(final String content) {
        return new Content(this.path.withRevision(Content.hash(content)), content);
    }

    @Override
    public String toString() {
        return this.content.length + " bytes of " + this.mime.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Content) {
            final Content that = (Content)o;
            return Objects.equals(this.mime, that.mime) && Objects.equals(this.content, that.content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mime, this.content);
    }

    public boolean isKnown(final Cache cache) {
        return cache.hasId(this.path);
    }

    public boolean exists(final Persistence persistence) {
        return persistence.exists(this.path);
    }

    public void writeTo(final Persistence persistence) {
        persistence.put(this);
    }

    public void removeFrom(final Persistence persistence) {
        persistence.remove(this.path);
    }

    public Content readFrom(final Storage storage) {
        return storage.get(this.path);
    }

    static HashCode hash(final String content) {
        return hash(content.getBytes(Charsets.UTF_8));
    }

    private static HashCode hash(final byte[] content) {
        return Hashing.md5().hashBytes(content);
    }

    public InputStream content() {
        return new ByteArrayInputStream(this.content);
    }

    public Static path() {
        return this.path;
    }
}
