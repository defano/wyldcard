package com.defano.wyldcard.menubar.main;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.menubar.DeferredMenuAction;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * An extension of {@link JMenuBar} representing the WyldCard menu bar.
 */
@Singleton
public class MainWyldCardMenuBar extends JMenuBar implements WyldCardMenuBar {

    @Override
    @RunOnDispatch
    public void reset() {
        // Reset menu items in each menu
        FileMenu.instance.reset();
        EditMenu.instance.reset();
        GoMenu.instance.reset();
        ToolsMenu.instance.reset();
        PaintMenu.instance.reset();
        OptionsMenu.instance.reset();
        ObjectsMenu.instance.reset();
        FontMenu.instance.reset();
        StyleMenu.instance.reset();
        WindowsMenu.instance.reset();

        // Reset menus in the menu bar
        removeAll();
        add(FileMenu.instance);
        add(EditMenu.instance);
        add(GoMenu.instance);
        add(ToolsMenu.instance);
        add(PaintMenu.instance);
        add(OptionsMenu.instance);
        add(ObjectsMenu.instance);
        add(FontMenu.instance);
        add(StyleMenu.instance);
        add(WindowsMenu.instance);
    }

    @Override
    public List<JMenu> getVisibleMenus() {
        List<JMenu> visibleMenus = new ArrayList<>();

        for (int idx = 0; idx < getMenuCount(); idx++) {
            JMenu thisMenu = getMenu(idx);
            if (thisMenu.isVisible()) {
                visibleMenus.add(thisMenu);
            }
        }

        return visibleMenus;
    }

    @Override
    public void doMenu(ExecutionContext context, String theMenuItem) throws HtSemanticException {
        ThreadUtils.assertWorkerThread();

        JMenuItem foundMenuItem = findMenuItemByName(theMenuItem);
        if (foundMenuItem != null) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "doMenu");
            for (ActionListener thisAction : foundMenuItem.getActionListeners()) {
                if (thisAction instanceof DeferredMenuAction) {
                    ((DeferredMenuAction) thisAction).blockingInvokeActionPerformed(context, event);
                } else {
                    thisAction.actionPerformed(event);
                }
            }
        } else {
            throw new HtSemanticException("Can't find menu item " + theMenuItem);
        }
    }

    @Override
    public void createMenu(String name) throws HtSemanticException {
        if (findMenuByName(name) != null) {
            throw new HtSemanticException("A menu named " + name + " already exists.");
        }

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            add(new HyperCardMenu(name));
            super.invalidate();
            super.repaint();

            // Required on non-macOS systems when menu is modified by message window
            WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindow().pack();
        });
    }

    @Override
    public void deleteMenu(JMenu menu) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            super.remove(menu);
            super.invalidate();
            super.repaint();

            // Required on non-macOS systems when menu is modified by message window
            WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindow().pack();
        });
    }

    @Override
    public JMenu findMenuByNumber(int index) {
        List<JMenu> visibleMenus = getVisibleMenus();

        return ThreadUtils.callAndWaitAsNeeded(() -> {
            if (index < 0 || index >= visibleMenus.size()) {
                return null;
            }

            return visibleMenus.get(index);
        });
    }

    @Override
    public JMenu findMenuByName(String name) {
        List<JMenu> visibleMenus = getVisibleMenus();

        return ThreadUtils.callAndWaitAsNeeded(() -> {
            for (JMenu thisMenu : visibleMenus) {
                if (thisMenu != null && name.equalsIgnoreCase(thisMenu.getText())) {
                    return thisMenu;
                }
            }

            return null;
        });
    }

    private JMenuItem findMenuItemByName(String name) {
        List<JMenu> visibleMenus = getVisibleMenus();

        return ThreadUtils.callAndWaitAsNeeded(() -> {
            for (JMenu thisMenu : visibleMenus) {
                for (int thisMenuItemIndex = 0; thisMenuItemIndex < thisMenu.getItemCount(); thisMenuItemIndex++) {
                    JMenuItem thisMenuItem = thisMenu.getItem(thisMenuItemIndex);

                    if (thisMenuItem != null && thisMenuItem.getText() != null && thisMenuItem.getText().equalsIgnoreCase(name)) {
                        return thisMenuItem;
                    }
                }
            }

            return null;
        });
    }

}