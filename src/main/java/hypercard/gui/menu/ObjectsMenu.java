package hypercard.gui.menu;

import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.ProviderTransform;
import hypercard.context.PartToolContext;
import hypercard.context.ToolsContext;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.base.PaintTool;
import hypercard.parts.*;
import hypercard.HyperCard;

import javax.swing.*;

public class ObjectsMenu extends JMenu {

    public ObjectsMenu() {
        super("Objects");

        // Show this menu only when an object tool is active
        ToolsContext.getInstance().getPaintToolProvider().addObserver((oldValue, newValue) -> {
            PaintTool selectedTool = (PaintTool) newValue;
            ObjectsMenu.this.setVisible(selectedTool.getToolType() == PaintToolType.ARROW);
        });

        MenuItemBuilder.ofDefaultType()
                .named("Button Info...")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), value -> !(value instanceof ButtonPart)))
                .withAction(a -> PartToolContext.getInstance().getSelectedPartProvider().get().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), value -> !(value instanceof FieldPart)))
                .withAction(a -> PartToolContext.getInstance().getSelectedPartProvider().get().editProperties())
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
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), value -> value == null))
                .withAction(a -> PartToolContext.getInstance().bringCloser())
                .withShortcut('+')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), value -> value == null))
                .withAction(a -> PartToolContext.getInstance().sendFurther())
                .withShortcut('-')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .withAction(e -> {
                    try {
                        CardPart currentCard = HyperCard.getInstance().getStack().getCurrentCard();
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
                        CardPart currentCard = HyperCard.getInstance().getStack().getCurrentCard();
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
