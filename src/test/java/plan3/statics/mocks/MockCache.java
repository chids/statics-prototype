package plan3.statics.mocks;

import plan3.statics.model.Cache;
import plan3.statics.model.Content;
import plan3.statics.model.Path;

import java.util.HashMap;
import java.util.Map;

import com.google.common.hash.HashCode;

public class MockCache implements Cache {
    private final Map<String, HashCode> cache = new HashMap<>();

    @Override
    public Path get(final Content content) {
        final Path path = content.path();
        return path.withRevision(this.cache.get(key(path)));
    }

    @Override
    public boolean hasId(final Path path) {
        return this.cache.containsKey(key(path));
    }

    @Override
    public boolean exists(final Path path) {
        return path.revision().equals(this.cache.get(key(path)));
    }

    @Override
    public void put(final Content content) {
        final Path path = content.path();
        this.cache.put(key(path), path.revision());
    }

    @Override
    public void remove(final Path path) {
        this.cache.remove(key(path));
    }
}
