package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Storage;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command implements Callable<Location> {
    protected final Storage storage;
    protected final Cache cache;
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected Command(final Cache cache, final Storage storage) {
        this.cache = requireNonNull(cache, "Cache");
        this.storage = requireNonNull(storage, "Storage");
    }

    protected Location write(final Content content) {
        this.storage.put(content);
        this.cache.put(content);
        return content.path();
    }
}
