package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.NotModifiedException;
import plan3.statics.model.RevisionMismatchException;
import plan3.statics.model.ServiceUnavailableException;
import plan3.statics.model.Storage;

public class AddCommand extends Command {

    private final Content candidate;

    public AddCommand(final Cache cache, final Storage storage, final Content candidate) {
        super(cache, storage);
        this.candidate = requireNonNull(candidate, "Candidate");
    }

    @Override
    public Location call() throws Exception {
        if(this.cache.hasId(this.candidate)) {
            final boolean inCache = this.cache.exists(this.candidate);
            final boolean inStorage = this.storage.exists(this.candidate);
            if(inStorage && inCache) {
                this.LOG.warn("{} NOT written, content exists in storage and cache", this.candidate);
                throw new NotModifiedException(this.candidate.where());
            }
            if(inCache && !inStorage) {
                // Case: Removed directly from S3
                this.cache.remove(this.candidate);
                this.LOG.warn("{} EVICTED, content exists in cache but NOT in storage", this.candidate);
            }
            if(inStorage && !inCache) {
                // => Update cache from storage and reject
                this.cache.put(this.candidate);
                this.LOG.warn("{} REFRESHED, exists in storage but cache has OTHER revision", this.candidate);
            }
            else {
                this.LOG.warn("{} REJECTED, exists in cache but with OTHER revision", this.candidate);
                throw new RevisionMismatchException(this.candidate);
            }
            throw new ServiceUnavailableException(this.candidate);
        }
        if(this.storage.exists(this.candidate)) {
            // Already exists in storage but not in cache
            // => Update cache from storage and reject
            // (case: Evicted from cache)
            this.cache.put(this.candidate);
            this.LOG.warn("{} REFRESHED, exists in storage, NOT in cache", this.candidate);
            throw new ServiceUnavailableException(this.candidate);
        }
        // Not in cache, not in storage: NEW WRITE
        this.LOG.warn("{} WRITTEN, not in cache not storage", this.candidate);
        return write(this.candidate);
    }
}
