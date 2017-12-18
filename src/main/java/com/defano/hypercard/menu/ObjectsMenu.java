package com.defano.hypercard.menu;

import com.defano.hypercard.window.WindowBuilder;
import com.defano.hypercard.window.forms.BackgroundPropertyEditor;
import com.defano.hypercard.window.forms.CardPropertyEditor;
import com.defano.hypercard.window.forms.StackPropertyEditor;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.button.ButtonPart;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.window.WindowManager;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.HyperCard;

import java.util.Objects;

/**
 * The HyperCard Objects menu.
 */
public class ObjectsMenu extends HyperCardMenu {

    public static ObjectsMenu instance = new ObjectsMenu();

    private ObjectsMenu() {
        super("Objects");

        // Show this menu only when an object tool is active
        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> ObjectsMenu.this.setVisible(ToolMode.PAINT != arg));

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
                .withAction(e -> WindowBuilder.make(new CardPropertyEditor())
                        .asModal()
                        .withTitle("Card Properties")
                        .withModel(HyperCard.getInstance().getDisplayedCard())
                        .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .withAction(e -> WindowBuilder.make(new BackgroundPropertyEditor())
                        .withTitle("Background Properties")
                        .asModal()
                        .withModel(HyperCard.getInstance().getDisplayedCard())
                        .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .withAction(e -> WindowBuilder.make(new StackPropertyEditor())
                        .withTitle("Stack Properties")
                        .asModal()
                        .withModel(HyperCard.getInstance().getStack().getStackModel())
                        .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Bring Closer")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), Objects::isNull))
                .withAction(a -> PartToolContext.getInstance().bringSelectedPartCloser())
                .withShortcut('+')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), Objects::isNull))
                .withAction(a -> PartToolContext.getInstance().sendSelectedPartFurther())
                .withShortcut('-')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .withAction(e -> HyperCard.getInstance().getDisplayedCard().newButton())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .withAction(e -> HyperCard.getInstance().getDisplayedCard().newField())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .withAction(e -> HyperCard.getInstance().getStack().newBackground())
                .build(this);
    }

    public void reset() {
        instance = new ObjectsMenu();
    }
}
