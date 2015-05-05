package plan3.statics.model;

public interface Persistence {

    void put(final Content content);

    public boolean exists(Located item);

    void remove(Located item);
}
