package plan3.statics.model;

public interface Persistence {

    void put(final Content content);

    public boolean exists(final Location path);

    void remove(final Location path);
}
