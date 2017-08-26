package com.defano.hypercard.gui.icons;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class IconFactory {

    public static List<ButtonIcon> getAllIcons() {
        ArrayList<ButtonIcon> icons = new ArrayList<>();
        icons.addAll(getStackIcons());
        icons.addAll(getHyperCardIcons());
        return icons;
    }

    public static List<ButtonIcon> getHyperCardIcons() {
        return Arrays.asList(HyperCardIcon.values());
    }

    public static List<ButtonIcon> getStackIcons() {
        return HyperCard.getInstance().getStack().getStackModel().getUserIcons();
    }

    public static ButtonIcon findIconForValue(Value value) {
        return findIconForValue(value, getAllIcons());
    }

    public static ButtonIcon findIconForValue(Value value, List<ButtonIcon> icons) {
        if (value.isInteger()) {
            return findIconById(value.integerValue(), icons);
        }

        return findIconByName(value.stringValue(), icons);
    }

    public static ButtonIcon findIconByName(String name, List<ButtonIcon> icons) {
        Optional<ButtonIcon> icon = icons.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();

        return icon.orElse(null);
    }

    public static ButtonIcon findIconById(int id, List<ButtonIcon> icons) {
        if (id < 1) return null;

        Optional<ButtonIcon> icon = icons.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        return icon.orElse(null);
    }

}
