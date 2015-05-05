package plan3.statics.model;

public interface Cache extends Persistence {

    Location get(Located item);

    boolean hasId(Located item);

    // Caches must use the path without the revision as the cache key
    default String key(final Located item) {
        return item.where().toStringWithoutRevision(':');
    }
}