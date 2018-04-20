package com.defano.hypertalk.ast.model.specifiers;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.window.HyperCardWindow;
import com.defano.wyldcard.window.WindowManager;

import java.util.List;

public class WindowNameSpecifier extends WindowSpecifier {

    private final String windowName;

    public WindowNameSpecifier(String windowName) {
        this.windowName = windowName;
    }

    @Override
    public Object getValue() {
        return windowName;
    }

    @Override
    public HyperCardWindow getSpecifiedWindow() throws PartException {
        List<HyperCardWindow> foundWindows = WindowManager.getInstance().getWindow(windowName);
        if (foundWindows.size() == 0) {
            throw new PartException("No such window.");
        } else {
            return foundWindows.get(0);
        }
    }
}
