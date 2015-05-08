/**
 * FieldContextMenu.java
 * @author matt.defano@motorola.com
 * 
 * Implements the context-sensitive menu that appears when the right mouse 
 * button is pressed over a field part.   
 */

package hypercard.gui;

import hypercard.context.GlobalContext;
import hypercard.parts.FieldPart;

import java.io.Serializable;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class FieldContextMenu extends JPopupMenu implements Serializable {
private static final long serialVersionUID = -8909810305050480968L;

	FieldPart field;

    JMenuItem jMenuItemEdit = new JMenuItem("Edit Field Properties...");
    JMenuItem jMenuItemEditScript = new JMenuItem("Edit Script...");
    JSeparator jSeparator1 = new JSeparator();
    JMenuItem jMenuItemMove = new JMenuItem("Move");
    JMenuItem jMenuItemResize = new JMenuItem("Resize");
    JMenuItem jMenuItemDelete = new JMenuItem("Delete");
    
    public FieldContextMenu (FieldPart field) {
        super();
        
        this.field = field;
        
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
        field.editProperties();
    }

    public void jMenuItemEditScriptActionPerformed(java.awt.event.ActionEvent evt) {
        field.editScript();
    }

    public void jMenuItemMoveActionPerformed(java.awt.event.ActionEvent evt) {
        field.move();
    }

    public void jMenuItemResizeActionPerformed(java.awt.event.ActionEvent evt) {
        field.resize();
    }

    public void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        GlobalContext.getContext().getCard().removeField(field);
    }
    
}
