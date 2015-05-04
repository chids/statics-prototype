package plan3.statics.model;

public interface Persistence {

    void put(final Content content);

    public boolean exists(final Static path);

    void remove(final Static path);
}
