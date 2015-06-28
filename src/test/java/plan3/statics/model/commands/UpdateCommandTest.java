package plan3.statics.model.commands;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import plan3.statics.exceptions.NotModifiedException;
import plan3.statics.exceptions.RevisionMismatchException;

import plan3.pure.jersey.exceptions.PreconditionFailedException;
import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Revision;
import plan3.statics.model.Storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCommandTest {

    @Mock
    private Cache cache;
    @Mock
    private Storage storage;

    @Test
    public void success() throws Exception {
        final Location previous = new Location("domain", "type", "id", new Revision("foo"));
        final Content candidate = new Content(previous.withRevision(new Revision("bar")), "bar");
        when(this.cache.hasId(previous)).thenReturn(true);
        when(this.cache.exists(previous)).thenReturn(true);
        when(this.cache.exists(candidate.where())).thenReturn(false);
        new UpdateCommand(this.cache, this.storage, previous, candidate).call();
        verify(this.cache).put(candidate);
        verify(this.storage).put(candidate);
    }

    @Test
    public void alreadyInCache() throws Exception {
        final Location previous = new Location("domain", "type", "id", new Revision("bar"));
        Content candidate = new Content(previous.withRevision(new Revision("bar")), "bar");
        when(this.cache.hasId(previous)).thenReturn(true);
        when(this.cache.exists(candidate)).thenReturn(true);
        try {
            new UpdateCommand(this.cache, this.storage, previous, candidate).call();
            fail("Exception expected");
        }
        catch(final NotModifiedException expected) {}
        verify(this.cache, never()).remove(previous);
        verify(this.storage, never()).remove(previous);
    }

    @Test
    public void wrongRevisionInCache() throws Exception {
        final Location previous = new Location("domain", "type", "id", new Revision("bar"));
        when(this.cache.hasId(previous)).thenReturn(true);
        try {
            new UpdateCommand(this.cache, this.storage, previous, new Content(previous, "bar")).call();
            fail("Exception expected");
        }
        catch(final PreconditionFailedException expected) {}
        verify(this.cache, never()).remove(previous);
        verify(this.storage, never()).remove(previous);
    }

    @Test
    public void notInCache() throws Exception {
        final Location previous = new Location("domain", "type", "id", new Revision("bar"));
        when(this.cache.hasId(previous)).thenReturn(false);
        try {
            new UpdateCommand(this.cache, this.storage, previous, new Content(previous, "bar")).call();
            fail("Exception expected");
        }
        catch(final RevisionMismatchException expected) {}
        verify(this.cache, never()).remove(previous);
        verify(this.storage, never()).remove(previous);
    }
}
