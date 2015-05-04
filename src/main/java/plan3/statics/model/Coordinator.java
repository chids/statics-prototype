package plan3.statics.model;

public interface Coordinator {

    Static update(Static previous, Content candidate) throws Exception;

    Static add(Content candidate) throws Exception;

    Static delete(Static target) throws Exception;

}