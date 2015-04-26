package plan3.statics.mocks;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Revision;
import plan3.statics.model.Static;

import java.util.HashMap;
import java.util.Map;

public class MockCache implements Cache {
    private final Map<String, Revision> cache = new HashMap<>();

    @Override
    public Static get(final Content content) {
        final Static path = content.path();
        return path.withRevision(this.cache.get(key(path)));
    }

    @Override
    public boolean hasId(final Static path) {
        return this.cache.containsKey(key(path));
    }

    @Override
    public boolean exists(final Static path) {
        return path.revision().equals(this.cache.get(key(path)));
    }

    @Override
    public void put(final Content content) {
        final Static path = content.path();
        this.cache.put(key(path), path.revision());
    }

    @Override
    public void remove(final Static path) {
        this.cache.remove(key(path));
    }
}
