package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;

import plan3.statics.exceptions.DoesntExistException;
import plan3.statics.exceptions.InternalConflictException;
import plan3.statics.exceptions.NotModifiedException;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Storage;

public class UpdateCommand extends Command {

    private final Location previous;
    private final Content candidate;

    public UpdateCommand(final Cache cache, final Storage storage, final Location previous, final Content candidate) {
        super(cache, storage);
        this.previous = requireNonNull(previous, "Previous");
        this.candidate = requireNonNull(candidate, "Candidate");
    }

    @Override
    public Location call() throws Exception {
        if(this.cache.hasId(this.previous)) {
            if(this.cache.exists(this.candidate)) {
                this.LOG.warn("{} NOT written, content already in cache", this.candidate.where());
                throw new NotModifiedException(this.candidate.where());
            }
            final boolean inStorage = this.storage.exists(this.previous);
            final boolean inCache = this.cache.exists(this.previous);
            if(inCache) {
                if(inStorage) {
                    this.LOG.warn("{} WRITTEN, previous version matched", this.previous);
                    return write(this.candidate);
                }
                // In cache but not in storage
                // Case: removed directly from storage
                this.cache.remove(this.previous);
                throw new DoesntExistException(this.previous);
            }
            // We know about another revision
            throw new InternalConflictException(this.cache.get(this.previous));
        }
        // The cache knows nothing about the previous version
        // => Reject until cache has refreshed
        throw new InternalConflictException(this.previous);
    }
}
