package plan3.statics.model;

public interface Cache extends Persistence {

    default Location get(final Located item) {
        return get(item.where());
    }

    Location get(Location location);

    default boolean hasId(final Located item) {
        return hasId(item.where());
    }

    boolean hasId(Location location);

    // Caches must use the path without the revision as the cache key
    default String key(final Located item) {
        return item.where().toStringWithoutRevision(':');
    }
}