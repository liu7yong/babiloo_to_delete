package ja.lingo.engine.dictionaryindex.reader;

import ja.lingo.engine.dictionaryindex.Token;

public interface ITokenReader {
    Token getToken( int index );
    int getTokenStart( int index );
    // TODO optimize? merge(881) - english3 VS getTokenStart(...) - with (20) why???
    int getTokenLength( int index );
    int size();
}
