package plan3.statics.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import plan3.statics.model.Content;
import plan3.statics.model.Path;

import java.util.concurrent.Callable;

import org.junit.Test;

public class PathTest {

    @Test
    public void current() {
        final Path path = new Path("domain", "type", "id", Content.hash("revision"));
        assertEquals("domain/type/id/current", path.current());
    }

    @Test
    public void withAndWithoutRevision() {
        final Path path = new Path("domain", "type", "id", Content.hash("revision"));
        assertEquals("domain/type/id/b5f927bae9a11c2967a368e3e9bd9e75", path.toString('/'));
        assertEquals("domain/type/id", path.toStringWithoutRevision('/'));
    }

    @Test
    public void invalidArguments() {
        shouldThrow(NullPointerException.class, () -> new Path(null, "type", "id", Content.hash("revision")));
        shouldThrow(NullPointerException.class, () -> new Path("domain", null, "id", Content.hash("revision")));
        shouldThrow(NullPointerException.class, () -> new Path("domain", "type", null, Content.hash("revision")));
        shouldThrow(NullPointerException.class, () -> new Path("domain", "type", "id", null));
        shouldThrow(IllegalArgumentException.class, () -> new Path("", "type", "id", Content.hash("revision")));
        shouldThrow(IllegalArgumentException.class, () -> new Path("domain", "", "id", Content.hash("revision")));
        shouldThrow(IllegalArgumentException.class, () -> new Path("domain", "type", "", Content.hash("revision")));
    }

    void shouldThrow(final Class<? extends Exception> expected, final Callable<?> expression) {
        try {
            expression.call();
        }
        catch(final Exception e) {
            if(expected.isInstance(e)) {
                return;
            }
        }
        fail("Expected " + expected);
    }
}
