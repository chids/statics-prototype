package plan3.statics.model.commands;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import plan3.statics.model.Cache;

import plan3.statics.exceptions.DoesntExistException;
import plan3.statics.exceptions.NotModifiedException;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Revision;
import plan3.statics.model.Storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddCommandTest {

    @Mock
    private Cache cache;
    @Mock
    private Storage storage;

    @Test
    public void success() throws Exception {
        final Location path = new Location("domain", "type", "id", new Revision("foo"));
        final Content candidate = new Content(path, "foo");
        when(this.cache.hasId(path)).thenReturn(false);
        when(this.cache.exists(path)).thenReturn(false);
        when(this.cache.exists(candidate.where())).thenReturn(false);
        new AddCommand(this.cache, this.storage, candidate).call();
        verify(this.cache).put(candidate);
        verify(this.storage).put(candidate);
    }

    @Test
    public void existsInStorageAndCache() throws Exception {
        final Content candidate = new Content(new Location("domain", "type", "id", new Revision("foo")), "foo");
        when(this.cache.hasId(candidate)).thenReturn(true);
        when(this.cache.exists(candidate)).thenReturn(true);
        when(this.storage.exists(candidate)).thenReturn(true);
        try {
            new AddCommand(this.cache, this.storage, candidate).call();
            fail("Exception expected");
        }
        catch(final NotModifiedException expected) {}
    }

    @Test
    public void existsInCacheNotInStorage() throws Exception {
        final Content candidate = new Content(new Location("domain", "type", "id", new Revision("foo")), "foo");
        when(this.cache.hasId(candidate)).thenReturn(true);
        when(this.cache.exists(candidate)).thenReturn(true);
        when(this.storage.exists(candidate)).thenReturn(false);
        try {
            new AddCommand(this.cache, this.storage, candidate).call();
            fail("Exception expected");
        }
        catch(final DoesntExistException expected) {}
        verify(this.cache).remove(candidate);
        verify(this.storage, never()).put(candidate);
    }

    @Test
    public void existsInStorageNotInCache() throws Exception {
        final Content candidate = new Content(new Location("domain", "type", "id", new Revision("foo")), "foo");
        when(this.cache.hasId(candidate)).thenReturn(true);
        when(this.cache.exists(candidate)).thenReturn(false);
        when(this.storage.exists(candidate)).thenReturn(true);
        try {
            new AddCommand(this.cache, this.storage, candidate).call();
            fail("Exception expected");
        }
        catch(final NotModifiedException expected) {}
        verify(this.cache).put(candidate);
        verify(this.storage, never()).put(candidate);
    }
}
