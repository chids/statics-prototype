package plan3.statics.model;

public interface Persistence {

    void put(final Content content);

    public boolean exists(final Path path);

    public void remove(final Path path);
}
