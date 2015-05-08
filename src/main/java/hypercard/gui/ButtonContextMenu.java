/**
 * ButtonContextMenu.java
 * @author matt.defano@gmail.com
 *  
 * Implements the context-sensitive menu that appears when the right mouse 
 * button is pressed over a button part. 
 */

package hypercard.gui;

import hypercard.context.GlobalContext;
import hypercard.parts.ButtonPart;

import java.io.Serializable;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author MGIA4532
 */
public class ButtonContextMenu extends JPopupMenu implements Serializable {
private static final long serialVersionUID = 3341214641780506203L;

	ButtonPart button;

    JMenuItem jMenuItemEdit = new JMenuItem("Edit Button Properties...");
    JMenuItem jMenuItemEditScript = new JMenuItem("Edit Script...");
    JSeparator jSeparator1 = new JSeparator();
    JMenuItem jMenuItemMove = new JMenuItem("Move");
    JMenuItem jMenuItemResize = new JMenuItem("Resize");
    JMenuItem jMenuItemDelete = new JMenuItem("Delete");
    
    public ButtonContextMenu (ButtonPart button) {
        super();
        
        this.button = button;
        
        this.add(jMenuItemEdit);
        this.add(jMenuItemEditScript);
        this.add(jSeparator1);
        this.add(jMenuItemMove);
        this.add(jMenuItemResize);
        this.add(jMenuItemDelete);

        jMenuItemEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditActionPerformed(evt);
            }
        });
                
        jMenuItemEditScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditScriptActionPerformed(evt);
            }
        });

        jMenuItemMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMoveActionPerformed(evt);
            }
        });
        
        jMenuItemResize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemResizeActionPerformed(evt);
            }
        });

        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        
    }    
        
    public void jMenuItemEditActionPerformed(java.awt.event.ActionEvent evt) {
        button.editProperties();
    }

    public void jMenuItemEditScriptActionPerformed(java.awt.event.ActionEvent evt) {
        button.editScript();
    }

    public void jMenuItemMoveActionPerformed(java.awt.event.ActionEvent evt) {
        button.move();
    }

    public void jMenuItemResizeActionPerformed(java.awt.event.ActionEvent evt) {
        button.resize();
    }

    public void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        GlobalContext.getContext().getCard().removeButton(button);
    }
    
}
