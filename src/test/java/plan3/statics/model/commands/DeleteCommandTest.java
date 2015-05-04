package plan3.statics.model.commands;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import plan3.statics.model.Cache;
import plan3.statics.model.Revision;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCommandTest {

    @Mock
    private Cache cache;
    @Mock
    private Storage storage;

    @Test
    public void exceptionInStorage() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        when(this.cache.exists(path)).thenReturn(true);
        when(this.storage.exists(path)).thenReturn(true);
        doThrow(new IllegalStateException()).when(this.storage).remove(path);
        try {
            new DeleteCommand(this.cache, this.storage, path).call();
            fail("Exception expected");
        }
        catch(final IllegalStateException expected) {}
        verify(this.cache).exists(path);
        verify(this.storage).exists(path);
        verify(this.storage).remove(path);
        verifyNoMoreInteractions(this.storage, this.cache);
    }

    @Test
    public void exceptionInCache() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        when(this.cache.exists(path)).thenReturn(true);
        when(this.storage.exists(path)).thenReturn(true);
        doThrow(new IllegalStateException()).when(this.cache).remove(path);
        try {
            new DeleteCommand(this.cache, this.storage, path).call();
            fail("Exception expected");
        }
        catch(final IllegalStateException expected) {}
        verify(this.cache).exists(path);
        verify(this.cache).remove(path);
        verify(this.storage).exists(path);
        verify(this.storage).remove(path);
        verifyNoMoreInteractions(this.storage, this.cache);
    }

    @Test
    public void success() throws Exception {
        final Cache cache = mock(Cache.class);
        final Storage storage = mock(Storage.class);
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        when(cache.exists(path)).thenReturn(true);
        when(storage.exists(path)).thenReturn(true);
        new DeleteCommand(cache, storage, path).call();
        verify(cache).exists(path);
        verify(cache).remove(path);
        verify(storage).exists(path);
        verify(storage).remove(path);
        verifyNoMoreInteractions(storage, cache);
    }

    @Test
    public void notInCache() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        when(this.cache.exists(path)).thenReturn(false);
        new DeleteCommand(this.cache, this.storage, path).call();
        verify(this.cache).exists(path);
        verifyNoMoreInteractions(this.storage, this.cache);
    }

    @Test
    public void notInStorage() throws Exception {
        final Static path = new Static("domain", "type", "id", new Revision("foo"));
        when(this.cache.exists(path)).thenReturn(true);
        when(this.storage.exists(path)).thenReturn(false);
        new DeleteCommand(this.cache, this.storage, path).call();
        verify(this.cache).exists(path);
        verify(this.cache).remove(path);
        verify(this.storage).exists(path);
        verifyNoMoreInteractions(this.storage, this.cache);
    }
}
