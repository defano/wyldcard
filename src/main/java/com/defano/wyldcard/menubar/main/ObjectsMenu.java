package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.runtime.ExecutionContext;

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
        WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolMode -> ObjectsMenu.this.setVisible(ToolMode.PAINT != toolMode));

        MenuItemBuilder.ofDefaultType()
                .named("Button Info...")
                .withEnabledProvider(WyldCard.getInstance().getPartToolManager().getSelectedPartProvider().map(toolEditablePart -> toolEditablePart.isPresent() && toolEditablePart.get() instanceof ButtonPart))
                .withDoMenuAction(a -> WyldCard.getInstance().getPartToolManager().getSelectedPart().getPartModel().editProperties(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .withEnabledProvider(WyldCard.getInstance().getPartToolManager().getSelectedPartProvider().map(toolEditablePart -> toolEditablePart.isPresent() && toolEditablePart.get() instanceof FieldPart))
                .withDoMenuAction(a -> WyldCard.getInstance().getPartToolManager().getSelectedPart().getPartModel().editProperties(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Card Info...")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedCard().getPartModel().editProperties(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedCard().getPartModel().getBackgroundModel().editProperties(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel().editProperties(new ExecutionContext()))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Bring Closer")
                .withEnabledProvider(WyldCard.getInstance().getPartToolManager().getSelectedPartProvider().map(Optional::isPresent))
                .withDoMenuAction(a -> WyldCard.getInstance().getPartToolManager().bringSelectedPartCloser())
                .withShortcut('=')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .withEnabledProvider(WyldCard.getInstance().getPartToolManager().getSelectedPartProvider().map(Optional::isPresent))
                .withDoMenuAction(a -> WyldCard.getInstance().getPartToolManager().sendSelectedPartFurther())
                .withShortcut('-')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedCard().newButton(new ExecutionContext(), null))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedCard().newField(new ExecutionContext(), null))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().newBackground(new ExecutionContext()))
                .build(this);
    }

    public void reset() {
        instance = new ObjectsMenu();
    }
}
