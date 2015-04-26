package plan3.statics.model;

public interface Persistence {

    void put(final Content content);

    public boolean exists(final Static path);

    public void remove(final Static path);
}
