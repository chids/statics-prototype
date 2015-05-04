package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.NotModifiedException;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

import com.sun.jersey.api.ConflictException;

public class AddCommand extends Command {

    private final Content candidate;

    public AddCommand(final Cache cache, final Storage storage, final Content candidate) {
        super(cache, storage);
        this.candidate = requireNonNull(candidate, "Candidate");
    }

    @Override
    public Static call() throws Exception {
        if(this.candidate.isKnown(this.cache)) {
            final boolean persisted = this.candidate.exists(this.storage);
            final boolean cached = this.candidate.exists(this.cache);
            if(persisted && cached) {
                this.LOG.warn("{} NOT written, content exists in storage and cache", this.candidate.path());
                throw new NotModifiedException(this.candidate.path());
            }
            else
                if(cached) {
                    // Case: Removed directly from S3
                    this.candidate.removeFrom(this.cache);
                    this.LOG.warn("{} EVICTED, content exists in cache but NOT in storage", this.candidate.path());
                }
                else
                    if(persisted) {
                        // => Update cache from storage and reject
                        this.candidate.writeTo(this.cache);
                        this.LOG.warn("{} REFRESHED, exists in storage but cache has OTHER revision",
                                this.candidate.path());
                    }
                    else {
                        this.LOG.warn("{} REJECTED, exists in cache but with OTHER revision", this.candidate.path());
                    }
            throw new ConflictException("Cache out of sync: " + this.candidate);
        }
        else
            if(this.candidate.exists(this.storage)) {
                // Already exists in storage but not in cache
                // => Update cache from storage and reject
                // (case: Evicted from cache)
                this.candidate.writeTo(this.cache);
                this.LOG.warn("{} REFRESHED, exists in storage, NOT in cache", this.candidate.path());
                throw new ConflictException("Cache out of sync: " + this.candidate);
            }
        // Not in cache, not in storage: NEW WRITE
        this.LOG.warn("{} WRITTEN, not in cache not storage", this.candidate.path());
        return write(this.candidate);
    }
}
