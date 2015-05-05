package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.NotModifiedException;
import plan3.statics.model.Location;
import plan3.statics.model.Storage;

import com.sun.jersey.api.ConflictException;

public class AddCommand extends Command {

    private final Content candidate;

    public AddCommand(final Cache cache, final Storage storage, final Content candidate) {
        super(cache, storage);
        this.candidate = requireNonNull(candidate, "Candidate");
    }

    @Override
    public Location call() throws Exception {
        if(this.cache.hasId(this.candidate)) {
            final boolean cached = this.cache.exists(this.candidate);
            final boolean persisted = this.storage.exists(this.candidate);
            if(persisted && cached) {
                this.LOG.warn("{} NOT written, content exists in storage and cache", this.candidate);
                throw new NotModifiedException(this.candidate.where());
            }
            if(cached) {
                // Case: Removed directly from S3
                this.cache.remove(this.candidate);
                this.LOG.warn("{} EVICTED, content exists in cache but NOT in storage", this.candidate);
            }
            if(persisted) {
                // => Update cache from storage and reject
                this.cache.put(this.candidate);
                this.LOG.warn("{} REFRESHED, exists in storage but cache has OTHER revision", this.candidate);
            }
            else {
                this.LOG.warn("{} REJECTED, exists in cache but with OTHER revision", this.candidate);
            }
            throw new ConflictException("Cache out of sync: " + this.candidate);
        }
        if(this.storage.exists(this.candidate)) {
            // Already exists in storage but not in cache
            // => Update cache from storage and reject
            // (case: Evicted from cache)
            this.cache.put(this.candidate);
            this.LOG.warn("{} REFRESHED, exists in storage, NOT in cache", this.candidate);
            throw new ConflictException("Cache out of sync: " + this.candidate);
        }
        // Not in cache, not in storage: NEW WRITE
        this.LOG.warn("{} WRITTEN, not in cache not storage", this.candidate);
        return write(this.candidate);
    }
}
