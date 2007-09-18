package ja.lingo.readers.babiloo;

import java.io.*;
//import com.sun.java.util.collections.*;
//import java.util.*;
public abstract class KeyList implements IList {
	private int[] fIndexes;
	private String fEncoding = "utf-8";
/**
 * KeyList constructor comment.
 */
protected KeyList() {
	super();
}

public Key getOffetsKey(byte[] b, int offset, int length){
    byte[] offset_bytes = new byte[4];
    byte[] length_bytes = new byte[4];
    System.arraycopy(b, offset + length- Key.COMPLEMENTS_SIZE + 1, offset_bytes, 0, 4);
    System.arraycopy(b, offset + length- Key.COMPLEMENTS_SIZE + 6, length_bytes, 0, 4);
    long off = BASE64Converter.bytesToInt(offset_bytes);
    long len = BASE64Converter.bytesToInt(length_bytes);
    int wordlength = length- Key.COMPLEMENTS_SIZE;

    String s;
    try {
            s = new String(b, offset, length- Key.COMPLEMENTS_SIZE, getEncoding()).trim();
    } catch (Exception e) {
            s = new String(b, offset, length- Key.COMPLEMENTS_SIZE).trim();
    }
    return new Key(s,offset,wordlength, off, len);
}
public Key createKey(byte[] b, int offset, int length) {
	
	byte[] offset_bytes = new byte[4];
	byte[] length_bytes = new byte[4];
	System.arraycopy(b, offset + length- Key.COMPLEMENTS_SIZE + 1, offset_bytes, 0, 4);
	System.arraycopy(b, offset + length- Key.COMPLEMENTS_SIZE + 6, length_bytes, 0, 4);
	long off = BASE64Converter.bytesToInt(offset_bytes);
	long len = BASE64Converter.bytesToInt(length_bytes);
	
	String s;
	try {
		s = new String(b, offset, length- Key.COMPLEMENTS_SIZE, getEncoding()).trim();
	} catch (Exception e) {
		s = new String(b, offset, length- Key.COMPLEMENTS_SIZE).trim();
	}
	/*int first = s.indexOf(IKey.TAB);
	int last = s.indexOf(IKey.TAB, first+1);
	if (first < 0 || last < 0) {
		//System.out.println(s);
		return new Key(s, "A", "A");
	}
	String k = s.substring(0, first);
	String off = s.substring(first+1, last);
	String len = s.substring(last+1);*/
	return new Key(s, off, len);
}
public abstract Object get(int index);
public abstract Object getOffsets(int index);
//public abstract Object getTokens(int index);
public static byte[] getData(String fileName) throws IOException {
	BufferedInputStream fis = null;
	try {
	fis = new BufferedInputStream(new FileInputStream(fileName));
	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	byte[] b = new byte[1024];
	int len;
	while ((len = fis.read(b)) > 0) {
		bout.write(b, 0, len);
	}
	return bout.toByteArray();
	} finally {
		try {
			fis.close();
			fis = null;
		} catch (Throwable t) {}
	}
        
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:33:10)
 * @return java.lang.String
 */
public java.lang.String getEncoding() {
	return fEncoding;
}
/**
 * Insert the method's description here.
 * Creation date: (22.06.2001 10:34:16)
 * @return int[]
 */
public int[] getIndexes() {
	return fIndexes;
}
public static int[] getLineMarkers(byte[] b) {
	java.util.Vector ls = new java.util.Vector(50000);
	ls.addElement(new Integer(0));
	for (int i = 0; i < b.length-1; i++){
		if (b[i] == '\t') {
			if(i+1+4+1+4+2 < b.length){
				ls.addElement(new Integer(i+1+4+1+4+2));
				i = i+1+4+1+4+1; // le quitamos una unidad porque se la anyadirá el bucle.
			}
			
		}
	}
	
	//copiamos el vector ls al vector arr
	int[] arr = new int[ls.size()];
	for (int i = 0; i < arr.length; i++){
		arr[i] = ((Integer)ls.elementAt(i)).intValue();
	}
	ls.removeAllElements();
	ls = null;
	return arr;
}
/**
 * Insert the method's description here.
 * Creation date: (03.09.01 22:33:10)
 * @param newEncoding java.lang.String
 */
public void setEncoding(java.lang.String newEncoding) {
	fEncoding = newEncoding;
}
/**
 * Insert the method's description here.
 * Creation date: (22.06.2001 10:34:16)
 * @param newIndexes int[]
 */
public void setIndexes(int[] newIndexes) {
	fIndexes = newIndexes;
}
public void shutDown() {
}
	/**
	 * Returns the number of elements in this collection.  If this collection
	 * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return the number of elements in this collection
	 */
public int size() {
	return getIndexes().length;
}
public void startUp() {
}
}
