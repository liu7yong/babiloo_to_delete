package ja.lingo.readers.babiloo;

public class Key{
	private String fKey;
        long fWordOffset, fWordLength;
	long fOffset, fLength;
	private String fComparableKey;
        static char TAB = '\t';
	static int COMPLEMENTS_SIZE = 12;
/**
 * Contruct a dummy key, used for comparisons only
 */
public Key(String k, long off, long len) {
    
	fKey = k;
	fOffset = off;
	fLength = len;
}

public Key(String k, long _fWordOffset, long _fWordLength,long off, long len) {
    	
	fWordOffset = _fWordOffset;
        fWordLength = _fWordLength;
	fOffset = off;
	fLength = len;
        fKey = k;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @return java.lang.String
 */
public java.lang.String getKey() {
	return fKey;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @return java.lang.String
 */
public long getLength() {
	return fLength;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @return java.lang.String
 */
public long getOffset() {
	return fOffset;
}

public long getWordLength() {
	return fWordLength;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @return java.lang.String
 */
public long getWordOffset() {
	return fWordOffset;
}

/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @param newKey java.lang.String
 */
public void setKey(java.lang.String newKey) {
	fKey = newKey;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @param newLength java.lang.String
 */
public void setLength(long newLength) {
	fLength = newLength;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:18:17)
 * @param newOffset java.lang.String
 */
public void setOffset(long newOffset) {
	fOffset = newOffset;
}
public String toString() {
	return fKey;
}
}
