/*
 * FileDescription.java
 *
 * Created on 14 de abril de 2006, 21:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ja.lingo.readers.babiloo;

/**
 *
 * @author ivan
 */
public class FileDescription {
    
    /** Creates a new instance of FileDescription */
    public FileDescription() {
    }
    
    private String title = "UNTITLED";
    private String author = "";

    public void setTitle(String _title){
        title = _title;
    }
    public String getTitle(){
        return title;
    }

    public void setAuthor(String _author){
        author = _author;
    }
    public String getAuthor(){
        return author;
    }

    public FileDescription(String _title,String _author){
        title = _title;
        author = _author;
    }
}
