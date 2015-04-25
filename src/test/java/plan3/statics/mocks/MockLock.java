package plan3.statics.mocks;

import plan3.statics.model.Lock;

import plan3.pure.util.Timeout;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MockLock extends Lock {
    final Set<String> locks = new HashSet<>();

    public MockLock() {
        super(new Timeout(200, TimeUnit.MILLISECONDS));
    }

    @Override
    protected void unlock(final String key) {
        this.locks.remove(key);
    }

    @Override
    protected boolean lock(final String key) {
        return this.locks.add(key);
    }
}