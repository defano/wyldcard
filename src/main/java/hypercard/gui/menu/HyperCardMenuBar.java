package hypercard.gui.menu;

import javax.swing.*;

public class HyperCardMenuBar extends JMenuBar {

    public final static HyperCardMenuBar instance = new HyperCardMenuBar();

    private HyperCardMenuBar() {
        add(new FileMenu());
        add(new EditMenu());
        add(new GoMenu());
        add(new ToolsMenu());
        add(new ObjectsMenu());
        add(new FontMenu());
        add(new StyleMenu());
    }
}
