/*
 * HyperCardMenuBar
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import javax.swing.*;

public class HyperCardMenuBar extends JMenuBar {

    public final static HyperCardMenuBar instance = new HyperCardMenuBar();

    private HyperCardMenuBar() {
        add(new FileMenu());
        add(new EditMenu());
        add(new GoMenu());
        add(new ToolsMenu());
        add(new PaintMenu());
        add(new OptionsMenu());
        add(new ObjectsMenu());
        add(new FontMenu());
        add(new StyleMenu());
    }
}
