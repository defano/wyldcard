/**
 * CardFileChooser.java
 * @author matt.defano@gmail.com
 * 
 * Trivial class for filtering acceptable choices when opening a .card doc 
 */

package hypercard.gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class CardFileChooser extends JFileChooser {

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
