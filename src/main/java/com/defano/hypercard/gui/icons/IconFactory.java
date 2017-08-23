package com.defano.hypercard.gui.icons;

import javax.swing.*;
import java.util.ArrayList;
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
        List<ButtonIcon> icons = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            icons.add(new ButtonIcon(new ImageIcon(IconFactory.class.getResource("/button-icons/" + ((i % 7) + 1) + ".png")), i));
        }

        return icons;
    }

    public static List<ButtonIcon> getStackIcons() {
        return new ArrayList<>();
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
