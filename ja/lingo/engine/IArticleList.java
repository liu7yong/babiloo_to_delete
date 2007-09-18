package ja.lingo.engine;

import ja.lingo.engine.beans.IArticle;

public interface IArticleList {
    /**
     * @return null if article is not available
     */
    IArticle get( int index );

    boolean isEmpty();
    int size();
}
