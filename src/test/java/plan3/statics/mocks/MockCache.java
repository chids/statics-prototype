package plan3.statics.mocks;

import plan3.statics.model.Located;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Revision;
import plan3.statics.model.Location;

import java.util.HashMap;
import java.util.Map;

public class MockCache implements Cache {
    private final Map<String, Revision> cache = new HashMap<>();

    @Override
    public Location get(final Located item) {
        return item.where().withRevision(this.cache.get(key(item)));
    }

    @Override
    public boolean hasId(final Located item) {
        return this.cache.containsKey(key(item));
    }

    @Override
    public boolean exists(final Located item) {
        return item.where().revision().equals(this.cache.get(key(item)));
    }

    @Override
    public void put(final Content content) {
        final Location path = content.where();
        this.cache.put(key(path), path.revision());
    }

    @Override
    public void remove(final Located item) {
        this.cache.remove(key(item));
    }
}
