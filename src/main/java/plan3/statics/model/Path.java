package plan3.statics.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;

public class Path {
    private static final String CURRENT = "/current";
    private final String domain;
    private final String type;
    private final String id;
    private final HashCode qualifier;

    public Path(final String domain, final String type, final String id, final HashCode revision) {
        this.domain = notEmpty(domain, "Domain");
        this.type = notEmpty(type, "Type");
        this.id = notEmpty(id, "Id");
        this.qualifier = requireNonNull(revision, "Revision");
    }

    public Path withRevision(final HashCode revision) {
        return new Path(this.domain, this.type, this.id, revision);
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
        return Joiner.on(separator).skipNulls().join(this.domain, this.type, this.id, this.qualifier.toString());
    }

    String toStringWithoutRevision(final char separator) {
        return Joiner.on(Character.toString(separator)).skipNulls().join(this.domain, this.type, this.id);
    }

    public Path withRevision() {
        revision();
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Path) {
            final Path that = (Path)o;
            return Objects.equals(this.domain, that.domain)
                    && Objects.equals(this.type, that.type)
                    && Objects.equals(this.id, that.id)
                    && Objects.equals(this.qualifier, that.qualifier);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.domain, this.type, this.id, this.qualifier);
    }

    public HashCode revision() {
        return this.qualifier;
    }

    private static String notEmpty(final String value, final String name) {
        if(value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return value.trim();
    }
}
