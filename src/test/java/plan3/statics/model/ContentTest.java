package plan3.statics.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContentTest {

    @Test
    public void equals() {
        assertEquals(new Content("domain", "type", "id", "content"), new Content("domain", "type", "id", "content"));
    }

    @Test
    public void hashcode() {
        assertEquals(
                new Content("domain", "type", "id", "content").hashCode(),
                new Content("domain", "type", "id", "content").hashCode());
    }
}
