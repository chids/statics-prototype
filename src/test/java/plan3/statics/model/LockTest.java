package plan3.statics.model;

import static org.fest.assertions.api.Assertions.fail;

import plan3.statics.mocks.MockLock;

import plan3.statics.model.Content;
import plan3.statics.model.Lock;
import plan3.statics.model.Lock.Token;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class LockTest {

    @Test(expected = TimeoutException.class)
    public void timeout() throws Exception {
        final MockLock lock = new MockLock();
        final Content version1 = new Content("domain", "type", "id", "version 1");
        lock.execute(version1, () -> {
            sleep(lock.timeout.to(TimeUnit.MILLISECONDS) * 2);
            return null;
        });
    }

    private static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch(final InterruptedException e) {}
    }

    @Test
    public void sequential() {
        final Lock lock = new MockLock();
        final Content version1 = new Content("domain", "type", "id", "version 1");
        try(Token v1 = lock.acquire(version1)) {
            try {
                lock.acquire(version1.update("version 2")).close();
                fail("Lock already acquired");
            }
            catch(final ConcurrentModificationException e) {}
        }
        lock.acquire(version1).close();
    }
}
