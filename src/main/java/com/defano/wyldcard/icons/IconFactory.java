package com.defano.wyldcard.icons;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A factory for creating {@link ButtonIcon} objects by name or id.
 */
public class IconFactory {

    @SuppressWarnings("unused")
    private List<HyperCardIcon> icons = new ArrayList<>();
    private final static IconFactory instance = new IconFactory();

    public static IconFactory getInstance() {
        return instance;
    }

    private IconFactory() {
        XStream xStream = new XStream();
        xStream.allowTypes(new Class[] {IconFactory.class, HyperCardIcon.class});
        xStream.alias("ResourcesArray", IconFactory.class);
        xStream.addImplicitCollection(IconFactory.class, "icons");
        xStream.alias("Resource", HyperCardIcon.class);
        xStream.aliasField("ResourceID", HyperCardIcon.class, "resourceId");
        xStream.aliasField("ResourceName", HyperCardIcon.class, "resourceName");
        xStream.aliasField("ResourceFlags", HyperCardIcon.class, "resourceFlags");
        xStream.aliasField("ResourceData", HyperCardIcon.class, "resourceData");

        xStream.fromXML(IconFactory.class.getResourceAsStream("/button-icons/button-icons.xml"), this);
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
        Optional<ButtonIcon> icon = icons.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();

        return icon.orElse(null);
    }

    private ButtonIcon findIconById(int id, List<ButtonIcon> icons) {
        if (id < 1) return null;

        Optional<ButtonIcon> icon = icons.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        return icon.orElse(null);
    }

}
