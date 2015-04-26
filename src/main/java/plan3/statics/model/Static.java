package plan3.statics.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.base.Joiner;

public class Static {
    private static final String CURRENT = "/current";
    private final String domain;
    private final String type;
    private final String id;
    private final Revision revision;

    public Static(final String domain, final String type, final String id, final Revision revision) {
        this.domain = notEmpty(domain, "Domain");
        this.type = notEmpty(type, "Type");
        this.id = notEmpty(id, "Id");
        this.revision = requireNonNull(revision, "Revision");
    }

    public Static withRevision(final Revision revision) {
        return new Static(this.domain, this.type, this.id, revision);
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
        if(o instanceof Static) {
            final Static that = (Static)o;
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
}