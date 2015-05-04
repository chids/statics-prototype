package plan3.statics.model;

public interface Storage extends Persistence {

    Content get(Location path);

    // This assumes S3 or in memory HashMap
    default String key(final Location path) {
        return path.toString('/');
    }

    default String key(final Content content) {
        return key(content.path());
    }
}