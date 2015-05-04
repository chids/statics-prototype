package plan3.statics.model.commands;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Revision;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.jersey.api.ConflictException;

@RunWith(MockitoJUnitRunner.class)
public class AddCommandTest {

    @Mock
    private Cache cache;
    @Mock
    private Storage storage;

    @Test
    public void success() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        final Content candidate = new Content(path, "foo");
        when(this.cache.hasId(path)).thenReturn(false);
        when(this.cache.exists(path)).thenReturn(false);
        when(this.cache.exists(candidate.path())).thenReturn(false);
        new AddCommand(this.cache, this.storage, candidate).call();
        verify(this.cache).put(candidate);
        verify(this.storage).put(candidate);
    }

    @Test
    public void existsInStorageAndCache() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        final Content candidate = new Content(path, "foo");
        when(this.cache.hasId(path)).thenReturn(true);
        when(this.cache.exists(path)).thenReturn(true);
        try {
            new AddCommand(this.cache, this.storage, candidate).call();
            fail("Exception expected");
        }
        catch(final ConflictException expected) {}
    }

    @Test
    public void existsInCacheNotInStorage() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        final Content candidate = new Content(path, "foo");
        when(this.cache.hasId(path)).thenReturn(true);
        when(this.cache.exists(path)).thenReturn(true);
        when(this.storage.exists(path)).thenReturn(false);
        try {
            new AddCommand(this.cache, this.storage, candidate).call();
            fail("Exception expected");
        }
        catch(final ConflictException expected) {}
        verify(this.cache).remove(path);
        verify(this.storage, never()).put(candidate);
    }

    @Test
    public void existsInStorageNotInCache() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        final Content candidate = new Content(path, "foo");
        when(this.cache.hasId(path)).thenReturn(true);
        when(this.cache.exists(path)).thenReturn(false);
        when(this.storage.exists(path)).thenReturn(true);
        try {
            new AddCommand(this.cache, this.storage, candidate).call();
            fail("Exception expected");
        }
        catch(final ConflictException expected) {}
        verify(this.cache).put(candidate);
        verify(this.storage, never()).put(candidate);
    }
}
