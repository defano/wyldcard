/**
 * ButtonContextMenu.java
 * @author matt.defano@gmail.com
 *  
 * Implements the context-sensitive menu that appears when the right mouse 
 * button is pressed over a button part. 
 */

package hypercard.gui.menu.context;

import hypercard.context.GlobalContext;
import hypercard.parts.ButtonPart;

import javax.swing.*;

public class ButtonContextMenu extends JPopupMenu {

    private ButtonPart button;

    private final JMenuItem jMenuItemEdit = new JMenuItem("Edit Button Properties...");
    private final JMenuItem jMenuItemEditScript = new JMenuItem("Edit Script...");
    private final JSeparator jSeparator1 = new JSeparator();
    private final JMenuItem jMenuItemMove = new JMenuItem("Move");
    private final JMenuItem jMenuItemResize = new JMenuItem("Resize");
    private final JMenuItem jMenuItemDelete = new JMenuItem("Delete");
    
    public ButtonContextMenu (ButtonPart button) {
        super();
        
        this.button = button;

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
        button.editProperties();
    }

    private void jMenuItemEditScriptActionPerformed() {
        button.editScript();
    }

    private void jMenuItemMoveActionPerformed() {
        button.move();
    }

    private void jMenuItemResizeActionPerformed() {
        button.resize();
    }

    private void jMenuItemDeleteActionPerformed() {
        GlobalContext.getContext().getCard().removeButton(button);
    }
    
}
