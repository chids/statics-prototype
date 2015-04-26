package plan3.statics.mocks;

import plan3.statics.model.Content;
import plan3.statics.model.Static;
import plan3.statics.model.Storage;

import java.util.HashMap;
import java.util.Map;

public class MockStorage implements Storage {
    private final Map<String, Content> storage = new HashMap<>();

    @Override
    public void put(final Content content) {
        this.storage.put(key(content.path()), content);
    }

    @Override
    public boolean exists(final Static path) {
        return this.storage.containsKey(key(path));
    }

    @Override
    public Content get(final Static path) {
        return this.storage.get(key(path));
    }

    @Override
    public void remove(final Static path) {
        this.storage.remove(key(path));
    }
}
