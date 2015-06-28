package plan3.statics.model;

import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import plan3.statics.exceptions.DoesntExistException;
import plan3.statics.exceptions.InternalConflictException;
import plan3.statics.mocks.MockCache;
import plan3.statics.mocks.MockLock;
import plan3.statics.mocks.MockStorage;
import plan3.statics.model.commands.AddCommand;

import java.util.Observer;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

public class CoordinatorTest {

    @Test
    @SuppressWarnings({ "unchecked" })
    public void locking() throws Exception {
        final Lock lock = mock(Lock.class);
        final Content version1 = new Content("domain", "type", "id", "blah");
        when(lock.execute(eq(version1.where()), isA(AddCommand.class))).thenReturn(version1.where());
        final Coordinator coordinator = new ObservableCoordinator(new MockCache(), new MockStorage(), lock);
        final Location revision1 = coordinator.add(version1);
        verify(lock).execute(eq(version1.where()), any(Callable.class));
        final Content version2 = version1.update("mooo");
        coordinator.update(revision1, version2);
        verify(lock).execute(eq(version2.where()), any(Callable.class));
    }

    @Test
    public void cacheAndStorageInconsistecny() throws Exception {
        final ObservableCoordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Content version2 = version1.update("bleh");
        coordinator.add(version1);
        coordinator.cache.put(version2); // Write version 2 only to cache
        try {
            coordinator.add(version2.update("bloh")); // Attempt to write version 3
            fail("Exception expected");
        }
        catch(final InternalConflictException expected) {
            final MultivaluedMap<String, Object> headers = expected.getResponse().getMetadata();
            assertEquals(version2.where().revision(), headers.getFirst(ETAG));
            assertEquals(version2.where().toString('/'), headers.getFirst(LOCATION).toString());
        }
        try {
            coordinator.add(version2.update("bloh")); // Attempt to write version 3, again
            fail("Exception expected");
        }
        catch(final InternalConflictException expected) {
            final MultivaluedMap<String, Object> headers = expected.getResponse().getMetadata();
            assertEquals(version2.where().revision(), headers.getFirst(ETAG));
            assertEquals(version2.where().toString('/'), headers.getFirst(LOCATION).toString());
        }
    }

    @Test
    public void add() throws Exception {
        final Observer observer = mock(Observer.class);
        final ObservableCoordinator coordinator = coordinator(observer);
        final Content version1 = new Content("domain", "type", "id", "blah");
        coordinator.add(version1);
        assertEquals(version1, coordinator.storage.get(version1.where()));
        verify(observer).update(coordinator, version1.where());
    }

    @Test
    public void update() throws Exception {
        final Observer observer = mock(Observer.class);
        final ObservableCoordinator coordinator = coordinator(observer);
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Location revision1 = coordinator.add(version1);
        final Content version2 = version1.update("foo");
        coordinator.update(revision1, version2);
        assertEquals(coordinator.storage.get(version2.where()), version2);
        verify(observer, times(1)).update(coordinator, version1.where());
        verify(observer, times(1)).update(coordinator, version2.where());
    }

    @Test
    public void overwriteWithAdd() throws Exception {
        final Coordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        coordinator.add(version1);
        try {
            coordinator.add(version1.update("bleh"));
        }
        catch(final InternalConflictException expected) {
            final MultivaluedMap<String, Object> headers = expected.getResponse().getMetadata();
            assertEquals(version1.where().revision(), headers.getFirst(ETAG));
            assertEquals(version1.where().toString('/'), headers.getFirst(LOCATION).toString());
        }
    }

    @Test
    public void cacheDoesntMatchStorageConflict() throws Exception {
        final ObservableCoordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Location revision1 = coordinator.add(version1);
        final Content version2 = version1.update("foo");
        coordinator.cache.put(version2); // Write version 2 to cache only
        try {
            coordinator.update(revision1, version1.update("version 3"));
        }
        catch(final InternalConflictException expected) {
            final MultivaluedMap<String, Object> headers = expected.getResponse().getMetadata();
            assertEquals(version2.where().revision(), headers.getFirst(ETAG));
            assertEquals(version2.where().toString('/'), headers.getFirst(LOCATION).toString());
        }
    }

    @Test
    public void cacheNewerThanStorageOverwrites() throws Exception {
        final ObservableCoordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Location revision1 = coordinator.add(version1);
        final Content version2 = version1.update("foo");
        coordinator.storage.put(version2); // Write version 2 to storage only
        coordinator.update(revision1, version1.update("version 3"));
    }

    @Test
    public void inStorageButEvictedFromCache() throws Exception {
        final ObservableCoordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Location revision1 = coordinator.add(version1);
        coordinator.cache.remove(version1.where()); // Remove from cache only
        try {
            coordinator.update(revision1, version1.update("version 2"));
            fail("Exception expected");
        }
        catch(final InternalConflictException expected) {
            final MultivaluedMap<String, Object> headers = expected.getResponse().getMetadata();
            assertEquals(version1.where().revision(), headers.getFirst(ETAG));
            assertEquals(version1.where().toString('/'), headers.getFirst(LOCATION).toString());
        }
    }

    @Test
    public void inCacheButRemovedFromStorageRejects() throws Exception {
        final Observer observer = mock(Observer.class);
        final ObservableCoordinator coordinator = coordinator(observer);
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Location revision1 = coordinator.add(version1);
        coordinator.storage.remove(version1.where()); // Remove from storage only
        final Content version2 = version1.update("version 2");
        try {
            coordinator.update(revision1, version2);
        }
        catch(DoesntExistException expected) {
            final MultivaluedMap<String, Object> headers = expected.getResponse().getMetadata();
            assertEquals(version1.where().revision(), headers.getFirst(ETAG));
            assertEquals(version1.where().toString('/'), headers.getFirst(LOCATION).toString());
        }
        assertNull(coordinator.storage.get(version2.where()));
        verify(observer, times(1)).update(coordinator, version1.where());
    }

    private static ObservableCoordinator coordinator(final Observer observer) {
        return new ObservableCoordinator(new MockCache(), new MockStorage(), new MockLock(), observer);
    }

    private static ObservableCoordinator coordinator() {
        return new ObservableCoordinator(new MockCache(), new MockStorage(), new MockLock());
    }
}
