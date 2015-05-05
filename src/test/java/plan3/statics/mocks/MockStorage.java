package plan3.statics.mocks;

import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Storage;

import java.util.HashMap;
import java.util.Map;

public class MockStorage implements Storage {
    private final Map<String, Content> storage = new HashMap<>();

    @Override
    public void put(final Content content) {
        this.storage.put(key(content), content);
    }

    @Override
    public boolean exists(final Location location) {
        return this.storage.containsKey(key(location));
    }

    @Override
    public Content get(final Location location) {
        return this.storage.get(key(location));
    }

    @Override
    public void remove(final Location location) {
        this.storage.remove(key(location));
    }
}
