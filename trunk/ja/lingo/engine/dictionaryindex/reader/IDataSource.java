package ja.lingo.engine.dictionaryindex.reader;

import ja.centre.util.io.ByteArray;

import java.io.Closeable;

public interface IDataSource extends Closeable {
    void getData( int start, int length, ByteArray byteArray );
}
