package plan3.statics.mocks;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Revision;
import plan3.statics.model.Location;

import java.util.HashMap;
import java.util.Map;

public class MockCache implements Cache {
    private final Map<String, Revision> cache = new HashMap<>();

    @Override
    public Location get(final Location path) {
        return path.withRevision(this.cache.get(key(path)));
    }

    @Override
    public boolean hasId(final Location path) {
        return this.cache.containsKey(key(path));
    }

    @Override
    public boolean exists(final Location path) {
        return path.revision().equals(this.cache.get(key(path)));
    }

    @Override
    public void put(final Content content) {
        final Location path = content.path();
        this.cache.put(key(path), path.revision());
    }

    @Override
    public void remove(final Location path) {
        this.cache.remove(key(path));
    }
}
