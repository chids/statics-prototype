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
    public Location update(final Location previous, final Content candidate) throws Exception {
        return execute(candidate, new UpdateCommand(this.cache, this.storage, previous, candidate));
    }

    @Override
    public Location add(final Content candidate) throws Exception {
        return execute(candidate, new AddCommand(this.cache, this.storage, candidate));
    }

    @Override
    public Location delete(final Location target) throws Exception {
        return execute(target, new DeleteCommand(this.cache, this.storage, target));
    }

    private Location execute(final Located item, final Command action) throws Exception {
        final Location result = this.lock.execute(item.where(), action);
        super.setChanged();
        super.notifyObservers(result);
        return result;
    }
}