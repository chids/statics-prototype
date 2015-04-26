package plan3.statics.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Charsets;

public class RevisionTest {

    @Test
    public void equals() {
        assertEquals(new Revision("content"), new Revision("content".getBytes(Charsets.UTF_8)));
    }

    @Test
    public void hashcode() {
        assertEquals(
                new Revision("content").hashCode(),
                new Revision("content".getBytes(Charsets.UTF_8)).hashCode());
    }
}
