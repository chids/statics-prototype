package plan3.statics.model;

public interface Storage extends Persistence {

    Content get(Static path);

    // This assumes S3 or in memory HashMap
    default String key(final Static path) {
        return path.toString('/');
    }

    default String key(final Content content) {
        return key(content.path());
    }
}