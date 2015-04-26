package plan3.statics.model;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

public class Revision {
    private final HashCode value;

    public Revision(final String content) {
        this(content.getBytes(Charsets.UTF_8));
    }

    public Revision(final byte[] content) {
        this(Hashing.md5().hashBytes(content));
    }

    public Revision(final HashCode value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Revision) {
            return this.value.equals(((Revision)o).value);
        }
        return false;
    }
}
