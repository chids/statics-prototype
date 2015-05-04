package plan3.statics.model;

import plan3.statics.model.commands.AddCommand;
import plan3.statics.model.commands.Command;
import plan3.statics.model.commands.DeleteCommand;
import plan3.statics.model.commands.UpdateCommand;

import java.util.Observable;
import java.util.Observer;
import java.util.stream.Stream;

public class ObservableCoordinator extends Observable implements Coordinator {

    final Cache cache;
    final Storage storage;
    private final Lock lock;

    public ObservableCoordinator(final Cache cache, final Storage storage, final Lock lock, final Observer... observers) {
        this.cache = cache;
        this.storage = storage;
        this.lock = lock;
        Stream.of(observers).forEach(this::addObserver);
    }

    @Override
    public Static update(final Static previous, final Content candidate) throws Exception {
        return execute(candidate.path(), new UpdateCommand(this.cache, this.storage, previous, candidate));
    }

    @Override
    public Static add(final Content candidate) throws Exception {
        return execute(candidate.path(), new AddCommand(this.cache, this.storage, candidate));
    }

    @Override
    public Static delete(final Static target) throws Exception {
        return execute(target, new DeleteCommand(this.cache, this.storage, target));
    }

    private Static execute(final Static path, final Command action) throws Exception {
        final Static result = this.lock.execute(path, action);
        super.setChanged();
        super.notifyObservers(result);
        return result;
    }
}