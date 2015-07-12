package plan3.statics.model.impl;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Coordinator;
import plan3.statics.model.Located;
import plan3.statics.model.Location;
import plan3.statics.model.Lock;
import plan3.statics.model.Storage;
import plan3.statics.model.commands.AddCommand;
import plan3.statics.model.commands.Command;
import plan3.statics.model.commands.DeleteCommand;
import plan3.statics.model.commands.UpdateCommand;
import plan3.statics.model.impl.Event.Added;
import plan3.statics.model.impl.Event.Deleted;
import plan3.statics.model.impl.Event.Updated;

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
        return execute(candidate,
                (Updated)() -> candidate.where(),
                new UpdateCommand(this.cache, this.storage, previous, candidate));
    }

    @Override
    public Location add(final Content candidate) throws Exception {
        return execute(candidate,
                (Added)() -> candidate.where(),
                new AddCommand(this.cache, this.storage, candidate));
    }

    @Override
    public Location delete(final Location target) throws Exception {
        return execute(target,
                (Deleted)() -> target.where(),
                new DeleteCommand(this.cache, this.storage, target));
    }

    private Location execute(final Located item, final Event event, final Command action) throws Exception {
        final Location result = this.lock.execute(item.where(), action);
        super.setChanged();
        super.notifyObservers(event);
        return result;
    }
}