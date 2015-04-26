package plan3.statics.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import org.junit.Test;

public class StaticTest {

    @Test
    public void current() {
        final Static path = new Static("domain", "type", "id", new Revision("revision"));
        assertEquals("domain/type/id/current", path.current());
    }

    @Test
    public void withAndWithoutRevision() {
        final Static path = new Static("domain", "type", "id", new Revision("revision"));
        assertEquals("domain/type/id/b5f927bae9a11c2967a368e3e9bd9e75", path.toString('/'));
        assertEquals("domain/type/id", path.toStringWithoutRevision('/'));
    }

    @Test
    public void invalidArguments() {
        shouldThrow(NullPointerException.class, () -> new Static(null, "type", "id", new Revision("revision")));
        shouldThrow(NullPointerException.class, () -> new Static("domain", null, "id", new Revision("revision")));
        shouldThrow(NullPointerException.class, () -> new Static("domain", "type", null, new Revision("revision")));
        shouldThrow(NullPointerException.class, () -> new Static("domain", "type", "id", null));
        shouldThrow(IllegalArgumentException.class, () -> new Static("", "type", "id", new Revision("revision")));
        shouldThrow(IllegalArgumentException.class, () -> new Static("domain", "", "id", new Revision("revision")));
        shouldThrow(IllegalArgumentException.class, () -> new Static("domain", "type", "", new Revision("revision")));
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
