package plan3.statics.model;

public interface Storage extends Persistence {

    Content get(Location path);

    // This assumes S3 or in memory HashMap
    default String key(final Located item) {
        return item.where().toString('/');
    }
}