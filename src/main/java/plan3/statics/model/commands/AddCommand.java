package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;

import plan3.statics.exceptions.DoesntExistException;
import plan3.statics.exceptions.NotModifiedException;
import plan3.statics.exceptions.InternalConflictException;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
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
            if(inCache) {
                if(inStorage) {
                    this.LOG.warn("{} NOT written, content exists in storage and cache", this.candidate);
                    throw new NotModifiedException(this.candidate);
                }
                // Case: Removed directly from S3
                this.LOG.warn("{} ACCEPTING, content exists in cache but NOT in storage", this.candidate);
                this.cache.remove(this.candidate);
                throw new DoesntExistException(this.candidate);
            }
            else if(inStorage) {
                // Update cache from storage and reject
                this.LOG.warn("{} REFRESHED, exists in storage but cache has OTHER revision", this.candidate);
                this.cache.put(this.candidate);
                throw new NotModifiedException(this.candidate);
            }
            else {
                this.LOG.warn("{} REJECTED, known to cache but with OTHER revision", this.candidate);
                throw new InternalConflictException(this.cache.get(this.candidate));
            }
        }
        if(this.storage.exists(this.candidate)) {
            // Already exists in storage but not in cache
            // Case: Evicted from cache
            this.LOG.warn("{} ACCEPTING, content exists in storage but NOT in cache", this.candidate);
        }
        // NEW WRITE
        this.LOG.warn("{} WRITTEN", this.candidate);
        return write(this.candidate);
    }
}
