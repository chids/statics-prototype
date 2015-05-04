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
        if(this.cache.hasId(this.candidate.path())) {
            final boolean persisted = this.storage.exists(this.candidate.path());
            final boolean cached = this.cache.exists(this.candidate.path());
            if(persisted && cached) {
                this.LOG.warn("{} NOT written, content exists in storage and cache", this.candidate.path());
                throw new NotModifiedException(this.candidate.path());
            }
            if(cached) {
                // Case: Removed directly from S3
                this.cache.remove(this.candidate.path());
                this.LOG.warn("{} EVICTED, content exists in cache but NOT in storage", this.candidate.path());
            }
            if(persisted) {
                // => Update cache from storage and reject
                this.cache.put(this.candidate);
                this.LOG.warn("{} REFRESHED, exists in storage but cache has OTHER revision",
                        this.candidate.path());
            }
            else {
                this.LOG.warn("{} REJECTED, exists in cache but with OTHER revision", this.candidate.path());
            }
            throw new ConflictException("Cache out of sync: " + this.candidate);
        }
        if(this.storage.exists(this.candidate.path())) {
            // Already exists in storage but not in cache
            // => Update cache from storage and reject
            // (case: Evicted from cache)
            this.cache.put(this.candidate);
            this.LOG.warn("{} REFRESHED, exists in storage, NOT in cache", this.candidate.path());
            throw new ConflictException("Cache out of sync: " + this.candidate);
        }
        // Not in cache, not in storage: NEW WRITE
        this.LOG.warn("{} WRITTEN, not in cache not storage", this.candidate.path());
        return write(this.candidate);
    }
}
