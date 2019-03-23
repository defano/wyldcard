package com.defano.wyldcard.icons;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory for creating {@link ButtonIcon} objects by name or id.
 */
public class IconDatabase {

    @SuppressWarnings("unused")
    private List<HyperCardIcon> icons = new ArrayList<>();
    private final static IconDatabase instance = new IconDatabase();

    public static IconDatabase getInstance() {
        return instance;
    }

    public IconDatabase() {
        icons.addAll(IconFactory.buildAllIcons());
    }

    void addIcons(List<HyperCardIcon> icons) {
        this.icons.addAll(icons);
    }

    public List<ButtonIcon> getAllIcons() {
        ArrayList<ButtonIcon> icons = new ArrayList<>();
        icons.addAll(getStackIcons());
        icons.addAll(getHyperCardIcons());
        return icons;
    }

    public ButtonIcon findIconForValue(Value value) {
        return findIconForValue(value, getAllIcons());
    }

    public ButtonIcon findIconForValue(Value value, List<ButtonIcon> icons) {
        if (value.isInteger()) {
            return findIconById(value.integerValue(), icons);
        }

        return findIconByName(value.toString(), icons);
    }

    private List<ButtonIcon> getHyperCardIcons() {
        return new ArrayList<>(icons);
    }

    private List<ButtonIcon> getStackIcons() {
        return WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel().getUserIcons();
    }

    private ButtonIcon findIconByName(String name, List<ButtonIcon> icons) {
        return icons.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private ButtonIcon findIconById(int id, List<ButtonIcon> icons) {
        return icons.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(findIconByName(String.valueOf(id), icons));
    }

}
