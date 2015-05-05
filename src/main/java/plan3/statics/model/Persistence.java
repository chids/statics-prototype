package plan3.statics.model;

public interface Persistence {

    void put(final Content content);

    default boolean exists(final Located item) {
        return exists(item.where());
    }

    boolean exists(Location location);

    default void remove(final Located item) {
        remove(item.where());
    }

    void remove(Location location);
}
