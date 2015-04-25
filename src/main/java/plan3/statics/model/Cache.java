package plan3.statics.model;

public interface Cache extends Persistence {

    Path get(Content content);

    boolean hasId(Path path);

    // Caches must use the path without the revision as the cache key
    default String key(final Path path) {
        return path.toStringWithoutRevision(':');
    }
}