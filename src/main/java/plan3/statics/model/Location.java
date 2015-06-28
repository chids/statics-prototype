package plan3.statics.model;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Objects;

import com.google.common.base.Joiner;

public class Location implements Located {
    private static final String CURRENT = "/current";
    private final String domain;
    private final String type;
    private final String id;
    private final Revision revision;

    public Location(final String domain, final String type, final String id, final Revision revision) {
        this.domain = notEmpty(domain, "Domain");
        this.type = notEmpty(type, "Type");
        this.id = notEmpty(id, "Id");
        this.revision = requireNonNull(revision, "Revision");
    }

    public Location withRevision(final Revision revision) {
        return new Location(this.domain, this.type, this.id, revision);
    }

    @Override
    public Location where() {
        return this;
    }

    public String current() {
        return toStringWithoutRevision('/').concat(CURRENT);
    }

    @Override
    public String toString() {
        return "[".concat(toString("][")).concat("]");
    }

    public String toString(final char separator) {
        return toString(Character.toString(separator));
    }

    public String toString(final String separator) {
        return Joiner.on(separator).skipNulls().join(this.domain, this.type, this.id, this.revision.toString());
    }

    String toStringWithoutRevision(final char separator) {
        return Joiner.on(Character.toString(separator)).skipNulls().join(this.domain, this.type, this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Location) {
            final Location that = (Location)o;
            return Objects.equals(this.domain, that.domain)
                    && Objects.equals(this.type, that.type)
                    && Objects.equals(this.id, that.id)
                    && Objects.equals(this.revision, that.revision);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.domain, this.type, this.id, this.revision);
    }

    public Revision revision() {
        return this.revision;
    }

    private static String notEmpty(final String value, final String name) {
        if(value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return value.trim();
    }

    public URI toURI() {
        return URI.create(toString('/'));
    }
}
