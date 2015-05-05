package plan3.statics.mocks;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Revision;

import java.util.HashMap;
import java.util.Map;

public class MockCache implements Cache {
    private final Map<String, Revision> cache = new HashMap<>();

    @Override
    public Location get(final Location location) {
        return location.withRevision(this.cache.get(key(location)));
    }

    @Override
    public boolean hasId(final Location location) {
        return this.cache.containsKey(key(location));
    }

    @Override
    public boolean exists(final Location location) {
        return location.revision().equals(this.cache.get(key(location)));
    }

    @Override
    public void put(final Content content) {
        final Location path = content.where();
        this.cache.put(key(path), path.revision());
    }

    @Override
    public void remove(final Location location) {
        this.cache.remove(key(location));
    }
}
