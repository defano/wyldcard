package com.defano.wyldcard.menubar.main;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;

public interface WyldCardMenuBar {
    @RunOnDispatch
    void reset();

    void doMenu(ExecutionContext context, String theMenuItem) throws HtSemanticException;

    void createMenu(String name) throws HtSemanticException;

    void deleteMenu(JMenu menu);

    JMenu findMenuByNumber(int index);

    JMenu findMenuByName(String name);

    /**
     * Returns the number of items in the menu bar.
     *
     * @return the number of items in the menu bar
     */
    int getMenuCount();

    /**
     * Returns the menu at the specified position in the menu bar.
     *
     * @param index an integer giving the position in the menu bar, where
     *              0 is the first position
     * @return the <code>JMenu</code> at that position, or <code>null</code> if
     * if there is no <code>JMenu</code> at that position (ie. if
     * it is a <code>JMenuItem</code>)
     */
    JMenu getMenu(int index);
}