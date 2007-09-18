package ja.lingo.readers.babiloo;

import java.util.Vector;
import java.io.*;


/**
 * Insert the type's description here.
 * Creation date: (28.07.01 21:54:33)
 * @author: Administrator
 */
public class Database  {
	public static int MAX_MATCHES = Integer.getInteger("matches", 20).intValue();
        public static final int STRATEGY_NONE = -1; // Dont look for matches
	public static final int STRATEGY_EXACT = 0;
	public static final int STRATEGY_PREFIX = 1;
	public static final int STRATEGY_SUBSTRING = 2;
	public static final int STRATEGY_SUFFIX = 3;


	private IDataAccessor fDataAccessor;
	private String fEncoding = "utf-8";

	private String fID;
	private KeyList fIndex;

	private String fName;

	private File indexFile, dataFile;
	/**
	 * Database constructor comment.
	 */
	public Database() {
		super();
	}


	public boolean equals(Object o) {
		if (o == null || !(o instanceof Database)) {
			return false;
		}
		Database db = (Database) o;
		return dataFile.equals(db.dataFile) && indexFile.equals(db.indexFile); 	}


	public IDataAccessor getDataAccessor() {
		return fDataAccessor;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28.07.01 22:01:41)
	 * @return java.lang.String
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (03.09.01 21:55:54)
	 * @return java.lang.String
	 */
	public java.lang.String getEncoding() {
		return fEncoding;
	}

	public java.lang.String getID() {
		return fID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28.07.01 22:01:41)
	 * @return org.dict.IKeyList
	 */
	public KeyList getIndex() {
		return fIndex;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02.09.01 12:46:21)
	 * @return org.dict.kernel.IMorphAnalyzer
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (29.07.01 22:00:15)
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		return fName;
	}

	public int getSize() {
		return getIndex().size();
	}
	
	public int hashCode() {
		return dataFile.hashCode();
	}

	public String initialize(DatabaseConfiguration dc) throws Exception {
		StringBuffer sb = new StringBuffer();
		String id = dc.getId();
		setID(id);
		dataFile = dc.getData();
		indexFile = dc.getIndex();
		if (!dataFile.exists()) {
			throw new Exception("File does not exist: "+dataFile);
		}
		if (!indexFile.exists()) {
			throw new Exception("File does not exist: "+indexFile);
		}
		sb.append("\nCreating database with data file " + dataFile);
		IDataAccessor acc = null;
		if (dataFile.getName().toLowerCase().endsWith(".dz")) {
			acc = new DictZipDataAccessor(dataFile.getAbsolutePath());
		} else {
			acc = new FlatDataAccessor(dataFile.getAbsolutePath());
		}
		setDataAccessor(acc);
		String enc = dc.getEncoding();
		setEncoding(enc);
		// Init key list
		KeyList kl = null;
		boolean inMemory = dc.isMemoryIndex();
		if (inMemory) {
			kl = new MemoryKeyList(indexFile.getAbsolutePath());
		} else {
			kl = new FileKeyList(indexFile.getAbsolutePath());
		}
		kl.setEncoding(enc);
		setIndex(kl);
                
                Key k;
                int start = 0,end=30000;
		for (int i = start; i <= end; i++) {
			k = (Key) getIndex().get(i);
                        long off = k.getOffset();
			long len = k.getLength();
		
		
                }
                return sb.toString();
	}
	/**
	 * match method comment.
	 */

	byte[] readAll(int start, int end) {
		Key k = (Key) getIndex().get(start);
		byte[] ret = new byte[0];
                String HTMLSeparator = "<BR><BR><HR><BR>";
                byte[] ByteSeparator = HTMLSeparator.getBytes();
		for (int i = start; i <= end; i++) {
			k = (Key) getIndex().get(i);
			/*long off = BASE64Converter.parse(k.getOffset());
			long len = BASE64Converter.parse(k.getLength());*/
			long off = k.getOffset();
			long len = k.getLength();
			//System.out.println("Read "+len+" from "+off);
			byte[] b;
			try {
				b = getDataAccessor().readData(off, len);
				//System.out.println("Length: "+new String(b, "UTF-8").length());
			} catch (IOException e) {
				System.out.println(e);
				ByteArrayOutputStream w = new ByteArrayOutputStream();
				e.printStackTrace(new PrintWriter(w));
				b = w.toByteArray();
			}
                        byte[] tmp;
                        if (i != end){
                           tmp = new byte[ret.length + b.length + ByteSeparator.length];
                           System.arraycopy(ret, 0, tmp, 0, ret.length);
                           System.arraycopy(b, 0, tmp, ret.length, b.length);
                           System.arraycopy(ByteSeparator, 0, tmp, ret.length + b.length, ByteSeparator.length);
                        }else{
                            tmp = new byte[ret.length + b.length];
                            System.arraycopy(ret, 0, tmp, 0, ret.length);
                            System.arraycopy(b, 0, tmp, ret.length, b.length);
                        }
			ret = tmp;
		}
		return ret;
	}

	

	public void setDataAccessor(IDataAccessor acc) {
		fDataAccessor = acc;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (03.09.01 21:55:54)
	 * @param newEncoding java.lang.String
	 */
	public void setEncoding(java.lang.String newEncoding) {
		fEncoding = newEncoding;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (7/30/01 6:39:21 PM)
	 * @param newID java.lang.String
	 */
	public void setID(java.lang.String newID) {
		fID = newID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28.07.01 22:01:41)
	 * @param newIndex org.dict.KeyList
	 */
	public void setIndex(KeyList newIndex) {
		fIndex = newIndex;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02.09.01 12:46:21)
	 * @param newMorphAnalyzer org.dict.kernel.IMorphAnalyzer
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (29.07.01 22:00:15)
	 * @param newName java.lang.String
	 */
	public void setName(java.lang.String newName) {
		fName = newName;
	}



	public static Database createDatabase(DatabaseConfiguration dc) throws Exception {
		Database ret = new Database();
		String dbClass = dc.getDbClass(); //aqui podriamos definir nuestro nuevo tipo de BD, el TIE o el otro
		if (dbClass != null) {
			try {
				ret = (Database) Class.forName(dbClass).newInstance();
			} catch (Throwable t) {
				//DatabaseFactory.log("Instantiation error: " + t);
			}
		}
		try {
			String msg = ret.initialize(dc);
			//DatabaseFactory.log(msg);
		} catch (Throwable t) {
			//t.printStackTrace();
			//DatabaseFactory.log(t.toString());
			throw new Exception(t);
		}
	    return ret;
	}


}
