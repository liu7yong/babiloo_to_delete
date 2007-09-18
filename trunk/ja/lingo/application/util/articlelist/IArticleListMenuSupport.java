package ja.lingo.application.util.articlelist;

import ja.lingo.engine.IArticleList;
import ja.lingo.engine.beans.IArticle;

public interface IArticleListMenuSupport {
    public IArticle getSelectedArticle();

    public IArticleList getArticleListForExport();
}
