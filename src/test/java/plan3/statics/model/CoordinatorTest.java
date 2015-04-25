package plan3.statics.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import plan3.pure.jersey.exceptions.PreconditionFailedException;
import plan3.statics.mocks.MockCache;
import plan3.statics.mocks.MockLock;
import plan3.statics.mocks.MockStorage;

import java.util.Observer;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.sun.jersey.api.ConflictException;

public class CoordinatorTest {

    @Test
    @SuppressWarnings({ "unchecked" })
    public void locking() throws Exception {
        final Lock lock = mock(Lock.class);
        final Coordinator coordinator = new Coordinator(new MockCache(), new MockStorage(), lock);
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Path revision1 = coordinator.add(version1);
        verify(lock).execute(eq(version1), any(Callable.class));
        final Content version2 = version1.update("mooo");
        coordinator.update(revision1, version2);
        verify(lock).execute(eq(version2), any(Callable.class));
    }

    @Test
    public void cacheAndStorageInconsistecny() throws Exception {
        final Coordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Content version2 = version1.update("bleh");
        coordinator.add(version1);
        version2.writeTo(coordinator.cache); // Write version 2 only to cache
        try {
            coordinator.add(version2.update("bloh")); // Attempt to write version 3
            fail("Expected conflict");
        }
        catch(final ConflictException expeted) {}
        try {
            coordinator.add(version2.update("bloh")); // Attempt to write version 3, again
            fail("Expected conflict");
        }
        catch(final ConflictException expected) {}
    }

    @Test
    public void add() throws Exception {
        final Observer observer = mock(Observer.class);
        final Coordinator coordinator = coordinator(observer);
        final Content version1 = new Content("domain", "type", "id", "blah");
        coordinator.add(version1);
        assertEquals(version1, version1.readFrom(coordinator.storage));
        verify(observer).update(coordinator, version1);
    }

    @Test
    public void update() throws Exception {
        final Observer observer = mock(Observer.class);
        final Coordinator coordinator = coordinator(observer);
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Path revision1 = coordinator.add(version1);
        final Content version2 = version1.update("foo");
        coordinator.update(revision1, version2);
        assertEquals(version2.readFrom(coordinator.storage), version2);
        verify(observer, times(1)).update(coordinator, version1);
        verify(observer, times(1)).update(coordinator, version2);
    }

    @Test(expected = ConflictException.class)
    public void overwriteWithAdd() throws Exception {
        final Coordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        coordinator.add(version1);
        coordinator.add(version1.update("bleh"));
    }

    @Test(expected = PreconditionFailedException.class)
    public void cacheDoesntMatchStorageConflict() throws Exception {
        final Coordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Path revision1 = coordinator.add(version1);
        final Content version2 = version1.update("foo");
        version2.writeTo(coordinator.cache); // Write version 2 to cache only
        coordinator.update(revision1, version1.update("version 3"));
    }

    @Test
    public void cacheNewerThanStorageOverwrites() throws Exception {
        final Coordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Path revision1 = coordinator.add(version1);
        final Content version2 = version1.update("foo");
        version2.writeTo(coordinator.storage);
        coordinator.update(revision1, version1.update("version 3"));
    }

    @Test(expected = ConflictException.class)
    public void inStorageButEvictedFromCache() throws Exception {
        final Coordinator coordinator = coordinator();
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Path revision1 = coordinator.add(version1);
        version1.removeFrom(coordinator.cache);
        coordinator.update(revision1, version1.update("version 2"));
    }

    @Test
    public void inCacheButRemovedFromStorageOverwrites() throws Exception {
        final Observer observer = mock(Observer.class);
        final Coordinator coordinator = coordinator(observer);
        final Content version1 = new Content("domain", "type", "id", "blah");
        final Path revision1 = coordinator.add(version1);
        version1.removeFrom(coordinator.storage);
        final Content version2 = version1.update("version 2");
        coordinator.update(revision1, version2);
        assertEquals(version2.readFrom(coordinator.storage), version2);
        verify(observer, times(1)).update(coordinator, version1);
        verify(observer, times(1)).update(coordinator, version2);
    }

    private static Coordinator coordinator(final Observer observer) {
        return new Coordinator(new MockCache(), new MockStorage(), new MockLock(), observer);
    }

    private static Coordinator coordinator() {
        return new Coordinator(new MockCache(), new MockStorage(), new MockLock());
    }
}
