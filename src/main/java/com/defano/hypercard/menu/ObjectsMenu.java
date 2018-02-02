package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.button.ButtonPart;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowBuilder;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypercard.window.forms.BackgroundPropertyEditor;
import com.defano.hypercard.window.forms.CardPropertyEditor;
import com.defano.hypercard.window.forms.StackPropertyEditor;

import java.util.Optional;

/**
 * The HyperCard Objects menu.
 */
public class ObjectsMenu extends HyperCardMenu {

    public static ObjectsMenu instance = new ObjectsMenu();

    private ObjectsMenu() {
        super("Objects");

        // Show this menu only when an object tool is active
        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolMode -> ObjectsMenu.this.setVisible(ToolMode.PAINT != toolMode));

        MenuItemBuilder.ofDefaultType()
                .named("Button Info...")
                .withEnabledProvider(PartToolContext.getInstance().getSelectedPartProvider().map(toolEditablePart -> toolEditablePart.isPresent() && toolEditablePart.get() instanceof ButtonPart))
                .withAction(a -> PartToolContext.getInstance().getSelectedPart().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .withEnabledProvider(PartToolContext.getInstance().getSelectedPartProvider().map(toolEditablePart -> toolEditablePart.isPresent() && toolEditablePart.get() instanceof FieldPart))
                .withAction(a -> PartToolContext.getInstance().getSelectedPart().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Card Info...")
                .withAction(e -> WindowBuilder.make(new CardPropertyEditor())
                        .asModal()
                        .withTitle("Card Properties")
                        .withModel(HyperCard.getInstance().getActiveStackDisplayedCard())
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .withAction(e -> WindowBuilder.make(new BackgroundPropertyEditor())
                        .withTitle("Background Properties")
                        .asModal()
                        .withModel(HyperCard.getInstance().getActiveStackDisplayedCard())
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .withAction(e -> WindowBuilder.make(new StackPropertyEditor())
                        .withTitle("Stack Properties")
                        .asModal()
                        .withModel(HyperCard.getInstance().getActiveStack().getStackModel())
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Bring Closer")
                .withEnabledProvider(PartToolContext.getInstance().getSelectedPartProvider().map(Optional::isPresent))
                .withAction(a -> PartToolContext.getInstance().bringSelectedPartCloser())
                .withShortcut('=')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .withEnabledProvider(PartToolContext.getInstance().getSelectedPartProvider().map(Optional::isPresent))
                .withAction(a -> PartToolContext.getInstance().sendSelectedPartFurther())
                .withShortcut('-')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .withAction(e -> HyperCard.getInstance().getActiveStackDisplayedCard().newButton())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .withAction(e -> HyperCard.getInstance().getActiveStackDisplayedCard().newField())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .withAction(e -> HyperCard.getInstance().getActiveStack().newBackground())
                .build(this);
    }

    public void reset() {
        instance = new ObjectsMenu();
    }
}
