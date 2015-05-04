package plan3.statics.model;

public interface Cache extends Persistence {

    Static get(Static path);

    boolean hasId(Static path);

    // Caches must use the path without the revision as the cache key
    default String key(final Static path) {
        return path.toStringWithoutRevision(':');
    }
}