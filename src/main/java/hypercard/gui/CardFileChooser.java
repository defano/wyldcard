/**
 * CardFileChooser.java
 * @author matt.defano@gmail.com
 * 
 * Trivial class for filtering acceptable choices when opening a .card doc 
 */

package hypercard.gui;

import java.io.File;
import java.io.Serializable;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class CardFileChooser extends JFileChooser implements Serializable {
private static final long serialVersionUID = 5843600369766743862L;

	private class CardFileFilter extends FileFilter {
        
        public boolean accept(File f) {
            return f.getName().endsWith(".card") || f.isDirectory();
        }
        
        public String getDescription() {
            return "Card";
        }
    }
    
    public CardFileChooser () {
        super();
        
        this.setFileFilter(new CardFileFilter());
        this.setAcceptAllFileFilterUsed(false);
    }
}
