package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.menu.main.WindowsMenu;
import com.defano.wyldcard.window.layout.ScriptEditor;

import javax.swing.*;

public class ScriptEditorMenuBar extends JMenuBar {

    public ScriptEditorMenuBar(ScriptEditor editor) {
        removeAll();

        add(new FileMenu(editor));
        add(new EditMenu(editor));
        add(new ScriptMenu(editor));
        add(new DebugMenu(editor));
        add(new WindowsMenu());
    }

}
