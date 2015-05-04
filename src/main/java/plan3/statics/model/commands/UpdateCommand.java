package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.pure.jersey.exceptions.PreconditionFailedException;
import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.NotModifiedException;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

import com.sun.jersey.api.ConflictException;

public class UpdateCommand extends Command {

    private final Static previous;
    private final Content candidate;

    public UpdateCommand(final Cache cache, final Storage storage, final Static previous, final Content candidate) {
        super(cache, storage);
        this.previous = requireNonNull(previous, "Previous");
        this.candidate = requireNonNull(candidate, "Candidate");
    }

    @Override
    public Static call() throws Exception {
        if(this.cache.hasId(this.previous)) {
            if(this.cache.exists(this.candidate.path())) {
                this.LOG.warn("{} NOT written, content already in cache", this.candidate.path());
                throw new NotModifiedException(this.candidate.path());
            }
            if(this.cache.exists(this.previous)) {
                this.LOG.warn("{} WRITTEN, previous version matched", this.previous);
                return write(this.candidate);
            }
            // We know about another revision
            final Static actual = this.cache.get(this.previous);
            throw new PreconditionFailedException("Current version is " + actual + ", not " + this.previous);
        }
        // The cache knows nothing about the previous version
        // => Reject until cache has refreshed
        throw new ConflictException("Cache out of sync: " + this.candidate);
    }
}
