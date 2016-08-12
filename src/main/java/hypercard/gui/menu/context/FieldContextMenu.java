/**
 * FieldContextMenu.java
 * @author matt.defano@motorola.com
 * 
 * Implements the context-sensitive menu that appears when the right mouse 
 * button is pressed over a field part.   
 */

package hypercard.gui.menu.context;

import hypercard.context.GlobalContext;
import hypercard.parts.FieldPart;

import javax.swing.*;

public class FieldContextMenu extends JPopupMenu {

    private FieldPart field;

    private JMenuItem jMenuItemEdit = new JMenuItem("Edit Field Properties...");
    private JMenuItem jMenuItemEditScript = new JMenuItem("Edit Script...");
    private JSeparator jSeparator1 = new JSeparator();
    private JMenuItem jMenuItemMove = new JMenuItem("Move");
    private JMenuItem jMenuItemResize = new JMenuItem("Resize");
    private JMenuItem jMenuItemDelete = new JMenuItem("Delete");
    
    public FieldContextMenu (FieldPart field) {
        super();
        
        this.field = field;
        
        this.add(jMenuItemEdit);
        this.add(jMenuItemEditScript);
        this.add(jSeparator1);
        this.add(jMenuItemMove);
        this.add(jMenuItemResize);
        this.add(jMenuItemDelete);

        jMenuItemEdit.addActionListener(evt -> jMenuItemEditActionPerformed());
                
        jMenuItemEditScript.addActionListener(evt -> jMenuItemEditScriptActionPerformed());

        jMenuItemMove.addActionListener(evt -> jMenuItemMoveActionPerformed());
        
        jMenuItemResize.addActionListener(evt -> jMenuItemResizeActionPerformed());

        jMenuItemDelete.addActionListener(evt -> jMenuItemDeleteActionPerformed());
        
    }    
        
    private void jMenuItemEditActionPerformed() {
        field.editProperties();
    }

    private void jMenuItemEditScriptActionPerformed() {
        field.editScript();
    }

    private void jMenuItemMoveActionPerformed() {
        field.move();
    }

    private void jMenuItemResizeActionPerformed() {
        field.resize();
    }

    private void jMenuItemDeleteActionPerformed() {
        GlobalContext.getContext().getCard().removeField(field);
    }
    
}
