package plan3.statics.model;

public interface Storage extends Persistence {

    Content get(Location path);

    default Content get(final Located item) {
        return get(item.where());
    }

    // This assumes S3 or in memory HashMap
    default String key(final Located item) {
        return item.where().toString('/');
    }
}