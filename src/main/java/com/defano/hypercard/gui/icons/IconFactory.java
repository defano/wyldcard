package com.defano.hypercard.gui.icons;

import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class IconFactory {

    public static List<ButtonIcon> getAllIcons() {
        ArrayList<ButtonIcon> icons = new ArrayList<>();
        icons.addAll(getHyperCardIcons());
        icons.addAll(getStackIcons());

        return icons;
    }

    public static List<ButtonIcon> getHyperCardIcons() {
        return Arrays.asList(HyperCardIcon.values());
    }

    public static List<ButtonIcon> getStackIcons() {
        return new ArrayList<>();
    }

    public static ImageIcon iconForValue(Value value) {
        if (value.isInteger()) {
            return iconForId(value.integerValue());
        } else {
            return iconForName(value.stringValue());
        }
    }

    public static ImageIcon iconForName(String name) {
        Optional<ButtonIcon> icon = getAllIcons().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();

        if (icon.isPresent()) {
            return icon.get().getIcon();
        } else {
            return null;
        }
    }

    public static ImageIcon iconForId(int id) {
        if (id < 1) return null;

        Optional<ButtonIcon> icon = getAllIcons().stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (icon.isPresent()) {
            return icon.get().getIcon();
        } else {
            return null;
        }
    }

}
