package plan3.statics.model;

import static java.util.Objects.requireNonNull;

import plan3.pure.util.Timeout;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Lock {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor((r) -> new Thread(r, "lock"));
    protected final Timeout timeout;

    protected Lock(final Timeout timeout) {
        this.timeout = requireNonNull(timeout, "Timeout");
    }

    public Location execute(final Location path, final Callable<Location> task) throws Exception {
        try(Token token = acquire(path)) {
            final Future<Location> future = executor.submit(task);
            try {
                return future.get(this.timeout.to(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
            }
            catch(InterruptedException | TimeoutException e) {
                future.cancel(true);
                throw new TimeoutException("Lock expired");
            }
            catch(final ExecutionException e) {
                future.cancel(true);
                throw (Exception)e.getCause();
            }
        }
    }

    public abstract Token acquire(Location key);

    public interface Token extends AutoCloseable {
        @Override
        public void close();
    }
}
