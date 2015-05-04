package plan3.statics.model.commands;

import static java.util.Objects.requireNonNull;

import plan3.pure.jersey.exceptions.PreconditionFailedException;
import plan3.statics.model.Cache;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

public class DeleteCommand extends Command {

    private final Static target;

    public DeleteCommand(final Cache cache, final Storage storage, final Static target) {
        super(cache, storage);
        this.target = requireNonNull(target, "Target");
    }

    @Override
    public Static call() throws Exception {
        if(this.cache.hasId(this.target)) {
            if(this.cache.exists(this.target)) {
                this.cache.remove(this.target);
                if(this.storage.exists(this.target)) {
                    this.storage.remove(this.target);
                }
            }
            else {
                throw new PreconditionFailedException("Not current revision: " + this.target);
            }
        }
        return this.target;
    }
}
