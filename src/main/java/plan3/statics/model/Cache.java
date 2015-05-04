package plan3.statics.model;

public interface Cache extends Persistence {

    Location get(Location path);

    boolean hasId(Location path);

    // Caches must use the path without the revision as the cache key
    default String key(final Location path) {
        return path.toStringWithoutRevision(':');
    }
}