package com.defano.wyldcard.menu;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.BackgroundPropertyEditor;
import com.defano.wyldcard.window.forms.CardPropertyEditor;
import com.defano.wyldcard.window.forms.StackPropertyEditor;

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
                .withAction(a -> PartToolContext.getInstance().getSelectedPart().getPartModel().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .withEnabledProvider(PartToolContext.getInstance().getSelectedPartProvider().map(toolEditablePart -> toolEditablePart.isPresent() && toolEditablePart.get() instanceof FieldPart))
                .withAction(a -> PartToolContext.getInstance().getSelectedPart().getPartModel().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Card Info...")
                .withAction(e -> WindowBuilder.make(new CardPropertyEditor())
                        .asModal()
                        .withTitle("Card Properties")
                        .withModel(WyldCard.getInstance().getActiveStackDisplayedCard())
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .withAction(e -> WindowBuilder.make(new BackgroundPropertyEditor())
                        .withTitle("Background Properties")
                        .asModal()
                        .withModel(WyldCard.getInstance().getActiveStackDisplayedCard())
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .withAction(e -> WindowBuilder.make(new StackPropertyEditor())
                        .withTitle("Stack Properties")
                        .asModal()
                        .withModel(WyldCard.getInstance().getActiveStack().getStackModel())
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
                .withAction(e -> WyldCard.getInstance().getActiveStackDisplayedCard().newButton())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .withAction(e -> WyldCard.getInstance().getActiveStackDisplayedCard().newField())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .withAction(e -> WyldCard.getInstance().getActiveStack().newBackground())
                .build(this);
    }

    public void reset() {
        instance = new ObjectsMenu();
    }
}
