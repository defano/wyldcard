package com.defano.wyldcard.icons;

import com.thoughtworks.xstream.XStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IconFactory {

    public static List<HyperCardIcon> buildAllIcons() {
        ArrayList<HyperCardIcon> icons = new ArrayList<>();

        icons.addAll(buildIcons(IconDatabase.class.getResourceAsStream("/button-icons/home-icons.xml")));
        icons.addAll(buildIcons(IconDatabase.class.getResourceAsStream("/button-icons/button-icons.xml")));
        icons.addAll(buildIcons(IconDatabase.class.getResourceAsStream("/button-icons/system-icons.xml")));

        return icons;
    }

    public static List<HyperCardIcon> buildIcons(InputStream fromStream) {
        XStream xStream = new XStream();
        xStream.allowTypes(new Class[] {CollectionHolder.class, HyperCardIcon.class});
        xStream.alias("ResourcesArray", CollectionHolder.class);
        xStream.addImplicitCollection(CollectionHolder.class, "icons");
        xStream.alias("Resource", HyperCardIcon.class);
        xStream.aliasField("ResourceID", HyperCardIcon.class, "resourceId");
        xStream.aliasField("ResourceName", HyperCardIcon.class, "resourceName");
        xStream.aliasField("ResourceFlags", HyperCardIcon.class, "resourceFlags");
        xStream.aliasField("ResourceData", HyperCardIcon.class, "resourceData");

        CollectionHolder ch = new CollectionHolder();
        xStream.fromXML(fromStream, ch);
        return ch.icons;
    }

    private static class CollectionHolder {
        ArrayList<HyperCardIcon> icons;
    }

}
