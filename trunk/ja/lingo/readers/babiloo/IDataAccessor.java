/*
 * Created on 03.09.2003
 *
 */
package ja.lingo.readers.babiloo;

/**
 * @author duc
 *
 */
public interface IDataAccessor {
	public byte[] readData(long offset, long len) throws java.io.IOException;
}
