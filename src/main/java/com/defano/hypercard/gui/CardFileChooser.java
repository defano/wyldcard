/*
 * CardFileChooser
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * CardFileChooser.java
 * @author matt.defano@gmail.com
 * 
 * Trivial class for filtering acceptable choices when opening a .card doc 
 */

package com.defano.hypercard.gui;

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
