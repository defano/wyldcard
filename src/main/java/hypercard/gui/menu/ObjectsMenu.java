package hypercard.gui.menu;

import hypercard.parts.ButtonPart;
import hypercard.parts.CardPart;
import hypercard.parts.FieldPart;
import hypercard.parts.PartException;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;

public class ObjectsMenu extends JMenu {

    public ObjectsMenu() {
        super("Objects");

        MenuItemBuilder.ofDefaultType()
                .named("Button Info...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Card Info...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Bring Closer")
                .disabled()
                .withShortcut('+')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .disabled()
                .withShortcut('-')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .withAction(e -> {
                    try {
                        CardPart currentCard = RuntimeEnv.getRuntimeEnv().getStack().getCurrentCard();
                        currentCard.addButton(ButtonPart.newButton(currentCard));
                    } catch (PartException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .withAction(e -> {
                    try {
                        CardPart currentCard = RuntimeEnv.getRuntimeEnv().getStack().getCurrentCard();
                        currentCard.addField(FieldPart.newField(currentCard));
                    } catch (PartException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .disabled()
                .build(this);
    }
}
