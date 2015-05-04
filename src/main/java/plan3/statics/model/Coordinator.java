package plan3.statics.model;

public interface Coordinator {

    Location update(Location previous, Content candidate) throws Exception;

    Location add(Content candidate) throws Exception;

    Location delete(Location target) throws Exception;

}