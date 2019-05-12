package com.defano.wyldcard.awt.keyboard;

import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

public enum ModifierKey {
    SHIFT("shiftkey"),
    CONTROL("controlkey", "ctrlkey"),
    COMMAND("commandkey", "cmdkey"),
    OPTION("optionkey");

    private final List<String> hypertalkIdentifiers;

    ModifierKey(String... hypertalkIdentifiers) {
        this.hypertalkIdentifiers = Arrays.asList(hypertalkIdentifiers);
    }

    public static ModifierKey fromHypertalkIdentifier(String identifier) throws HtException {
        return Arrays.stream(values())
                .filter(k -> k.hypertalkIdentifiers.contains(identifier.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new HtSemanticException("Expected a modifier key, but got " + identifier));
    }

    public int getKeyCode() {
        switch (this) {
            case SHIFT:
                return KeyEvent.VK_SHIFT;
            case CONTROL:
                return KeyEvent.VK_CONTROL;
            case COMMAND:
                return WyldCard.getInstance().getWindowManager().isMacOsTheme() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL;
            case OPTION:
                return WyldCard.getInstance().getWindowManager().isMacOsTheme() ? 58 : KeyEvent.VK_ALT;
        }

        throw new IllegalStateException("Bug! Unimplemented modifier key: " + this);
    }
}
