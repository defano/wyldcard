package com.defano.wyldcard.menubar.script;

import com.defano.wyldcard.window.layouts.ScriptEditor;

import javax.swing.*;

public class ScriptEditorMenuBar extends JMenuBar {

    public ScriptEditorMenuBar(ScriptEditor editor) {
        removeAll();

        add(new FileMenu(editor));
        add(new EditMenu(editor));
        add(new ScriptMenu(editor));
        add(new DebugMenu(editor));
    }

}
