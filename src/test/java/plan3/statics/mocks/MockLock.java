package plan3.statics.mocks;

import plan3.statics.model.Revision;
import plan3.pure.util.Timeout;
import plan3.statics.model.Location;
import plan3.statics.model.Lock;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MockLock extends Lock {
    private static final Revision REVISION = new Revision("");
    final Set<Location> locks = new HashSet<>();

    public MockLock() {
        super(new Timeout(200, TimeUnit.MILLISECONDS));
    }

    @Override
    public Token acquire(final Location location) {
        final Location key = location.withRevision(REVISION);
        if(this.locks.add(key)) {
            return () -> this.locks.remove(key);
        }
        throw new ConcurrentModificationException("Lock already acquired");
    }
}