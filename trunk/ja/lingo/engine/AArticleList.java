package ja.lingo.engine;

public abstract class AArticleList implements IArticleList {
    public final boolean isEmpty() {
        return size() == 0;
    }
}
