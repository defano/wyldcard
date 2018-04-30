package com.defano.wyldcard.menubar;

import com.defano.wyldcard.util.ThreadUtils;
import io.reactivex.Observable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A builder of HyperCard menu items.
 */
public class MenuItemBuilder {

    private final JMenuItem item;
    private Observable<Boolean> checkmarkProvider;
    private Observable<Boolean> disabledProvider;
    private Observable<Boolean> enabledProvider;
    private List<ActionListener> actionListeners = new ArrayList<>();
    private Integer atIndex;

    private MenuItemBuilder(JMenuItem item) {
        this.item = item;
    }

    public static MenuItemBuilder ofCheckType () {
        return new MenuItemBuilder(new JCheckBoxMenuItem());
    }

    public static MenuItemBuilder ofDefaultType () {
        return new MenuItemBuilder(new JMenuItem());
    }

    public static MenuItemBuilder ofAction(Action action) {
        return new MenuItemBuilder(new JMenuItem(action));
    }

    public static MenuItemBuilder ofHierarchicalType() {
        return new MenuItemBuilder(new JMenu());
    }

    public MenuItemBuilder withAction (ActionListener action) {
        this.actionListeners.add(action);
        return this;
    }

    public MenuItemBuilder fontStyle (int fontStyle) {
        Font defaultFont = this.item.getFont();
        Font customFont = new Font(defaultFont.getFamily(), fontStyle, defaultFont.getSize());
        this.item.setFont(customFont);
        return this;

    }

    public MenuItemBuilder fontFamily (String fontFamily) {
        Font defaultFont = this.item.getFont();
        Font customFont = new Font(fontFamily, defaultFont.getStyle(), defaultFont.getSize());
        this.item.setFont(customFont);
        return this;
    }

    public MenuItemBuilder withCheckmarkProvider(Observable<Boolean> checkmarkProvider) {
        this.checkmarkProvider = checkmarkProvider;
        return this;
    }

    public MenuItemBuilder disabled () {
        this.item.setEnabled(false);
        return this;
    }

    public MenuItemBuilder withDisabledProvider(Observable<Boolean> disabledProvider) {
        this.disabledProvider = disabledProvider;
        return this;
    }

    public MenuItemBuilder withEnabledProvider(Observable<Boolean> enabledProvider) {
        this.enabledProvider = enabledProvider;
        return this;
    }

    public MenuItemBuilder atIndex(int index) {
        this.atIndex = index;
        return this;
    }

    public MenuItemBuilder named (String name) {
        this.item.setName(name);
        this.item.setText(name);
        return this;
    }

    public MenuItemBuilder withShiftShortcut (char shortcut) {
        this.item.setMnemonic(shortcut);
        this.item.setAccelerator(KeyStroke.getKeyStroke(shortcut, KeyEvent.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        return this;
    }

    public MenuItemBuilder withShortcut (char shortcut) {
        this.item.setAccelerator(KeyStroke.getKeyStroke(shortcut, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.item.setMnemonic(shortcut);
        return this;
    }

    public MenuItemBuilder withIcon(Icon icon) {
        this.item.setIcon(icon);
        return this;
    }

    public MenuItemBuilder withActionCommand(String actionCommand) {
        this.item.setActionCommand(actionCommand);
        return this;
    }

    public JMenuItem build (JMenuItem intoMenu) {

        if (actionListeners.size() > 0) {
            this.item.addActionListener(new DeferredMenuAction(intoMenu.getText(), this.item.getText(), actionListeners));
        }

        if (checkmarkProvider != null) {
            checkmarkProvider.subscribe(checked -> ThreadUtils.invokeAndWaitAsNeeded(() -> item.setSelected(checked)));
            item.setSelected(checkmarkProvider.blockingFirst());
        }

        if (disabledProvider != null) {
            disabledProvider.subscribe(disabled -> ThreadUtils.invokeAndWaitAsNeeded(() -> item.setEnabled(!disabled)));
            item.setEnabled(!disabledProvider.blockingFirst());
        }

        if (enabledProvider != null) {
            enabledProvider.subscribe(enabled -> ThreadUtils.invokeAndWaitAsNeeded(() -> item.setEnabled(enabled)));
            item.setEnabled(enabledProvider.blockingFirst());
        }

        if (atIndex == null) {
            intoMenu.add(item);
        } else {
            intoMenu.add(item, atIndex.intValue());
        }

        return item;
    }
}
