package com.defano.wyldcard.menubar.main;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.runtime.ExecutionContext;

import javax.swing.*;
import java.util.List;

public interface WyldCardMenuBar {
    @RunOnDispatch
    void reset();

    void doMenu(ExecutionContext context, String theMenuItem) throws HtSemanticException;

    void createMenu(String name) throws HtSemanticException;

    void deleteMenu(JMenu menu);

    JMenu findMenuByNumber(int index);

    JMenu findMenuByName(String name);

    List<JMenu> getVisibleMenus();

    void setVisible(boolean visible);
}