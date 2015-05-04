package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.NotModifiedException;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

import plan3.pure.jersey.exceptions.PreconditionFailedException;

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
        if(this.candidate.isKnown(this.cache)) {
            final Static cached = this.cache.get(this.candidate);
            if(this.candidate.exists(this.cache)) {
                this.LOG.warn("{} NOT written, content already in cache", this.candidate.path());
                throw new NotModifiedException(this.candidate.path());
            }
            if(cached.equals(this.previous)) {
                this.LOG.warn("{} WRITTEN, previous version matched", this.previous);
                return write(this.candidate);
            }
            // We know about another revision
            throw new PreconditionFailedException("Current version is " + cached + ", not " + this.previous);
        }
        // The cache knows nothing about the previous version
        // => Reject until cache has refreshed
        throw new ConflictException("Cache out of sync: " + this.candidate);
    }
}
