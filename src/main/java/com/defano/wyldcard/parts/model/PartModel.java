package com.defano.wyldcard.parts.model;

import com.defano.wyldcard.parts.Messagable;
import com.defano.wyldcard.parts.card.CardLayer;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.interpreter.CompilationUnit;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.ButtonPropertyEditor;
import com.defano.wyldcard.window.forms.FieldPropertyEditor;
import com.defano.wyldcard.window.forms.ScriptEditor;
import com.defano.hypertalk.ast.expressions.LiteralPartExp;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A base model object for all HyperCard "parts" that Defines properties common to all part objects.
 */
public abstract class PartModel extends PropertiesModel implements Messagable {

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_RIGHT = "right";
    public static final String PROP_BOTTOM = "bottom";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_RECT = "rect";
    public static final String PROP_RECTANGLE = "rectangle";
    public static final String PROP_TOPLEFT = "topleft";
    public static final String PROP_BOTTOMRIGHT = "bottomright";
    public static final String PROP_BOTRIGHT = "botright";
    public static final String PROP_VISIBLE = "visible";
    public static final String PROP_LOC = "loc";
    public static final String PROP_LOCATION = "location";
    public static final String PROP_CONTENTS = "contents";
    public static final String PROP_SCRIPTTEXT = "scripttext";
    public static final String PROP_BREAKPOINTS = "breakpoints";
    public static final String PROP_CHECKPOINTS = "checkpoints";

    private final PartType type;
    private Owner owner;
    private int scriptEditorCaretPosition;

    private transient PartModel parentPartModel;
    private transient Script script;

    public PartModel(PartType type, Owner owner, PartModel parentPartModel) {
        super();

        this.type = type;
        this.owner = owner;
        this.parentPartModel = parentPartModel;

        defineProperty(PROP_VISIBLE, new Value(true), false);
        defineProperty(PROP_SCRIPTTEXT, new Value(), false);
        defineProperty(PROP_BREAKPOINTS, new Value(), false);

        initialize();
    }

    /**
     * Recursively re-establish the parent-child part model relationship. Sets the value returned by
     * {@link #getParentPartModel()} to the given part model and causes this model to invoke this method on all its
     * children.
     * <p>
     * The relationship between a parent and it's child parts are persistent when serialized, but the reverse
     * relationship (between child and parent) is transient. This is a side effect of the serialization engine being
     * unable to deal with cycles in the model object graph (a child cannot depend on a parent that also depends on
     * it.). Thus, as a workaround, we programmatically re-establish the child-to-parent relationship after the stack
     * has completed deserializing from JSON.
     *
     * @param parentPartModel The {@link PartModel} of the parent of this part. Null for models that do not have a
     *                        parent part (i.e., stacks and the message box).
     */
    public abstract void relinkParentPartModel(PartModel parentPartModel);

    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        // Convert rectangle (consisting of top left and bottom right coordinates) into top, left, height and width
        defineComputedSetterProperty(PROP_RECT, (context, model, propertyName, value) -> {
            if (value.isRectE(context)) {
                model.setKnownProperty(context, PROP_LEFT, value.getItemAt(context, 0));
                model.setKnownProperty(context, PROP_TOP, value.getItemAt(context, 1));
                model.setKnownProperty(context, PROP_HEIGHT, new Value(value.getItemAt(context, 3).longValue() - value.getItemAt(context, 1).longValue()));
                model.setKnownProperty(context, PROP_WIDTH, new Value(value.getItemAt(context, 2).longValue() - value.getItemAt(context, 0).longValue()));
            } else {
                throw new HtSemanticException("Expected a rectangle, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_RECT, (context, model, propertyName) -> {
            Value left = model.getKnownProperty(context, PROP_LEFT);
            Value top = model.getKnownProperty(context, PROP_TOP);
            Value height = model.getKnownProperty(context, PROP_HEIGHT);
            Value width = model.getKnownProperty(context, PROP_WIDTH);

            return new Value(left.integerValue(), top.integerValue(), left.integerValue() + width.integerValue(), top.integerValue() + height.integerValue());
        });

        defineComputedGetterProperty(PROP_RIGHT, (context, model, propertyName) ->
                new Value(model.getKnownProperty(context, PROP_LEFT).integerValue() + model.getKnownProperty(context, PROP_WIDTH).integerValue())
        );

        defineComputedSetterProperty(PROP_RIGHT, (context, model, propertyName, value) ->
                model.setKnownProperty(context, PROP_LEFT, new Value(value.integerValue() - model.getKnownProperty(context, PROP_WIDTH).integerValue()))
        );

        defineComputedGetterProperty(PROP_BOTTOM, (context, model, propertyName) ->
                new Value(model.getKnownProperty(context, PROP_TOP).integerValue() + model.getKnownProperty(context, PROP_HEIGHT).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOM, (context, model, propertyName, value) ->
                model.setKnownProperty(context, PROP_TOP, new Value(value.integerValue() - model.getKnownProperty(context, PROP_HEIGHT).integerValue()))
        );

        defineComputedSetterProperty(PROP_TOPLEFT, (context, model, propertyName, value) -> {
            if (value.isPoint(context)) {
                model.setKnownProperty(context, PROP_LEFT, value.getItemAt(context, 0));
                model.setKnownProperty(context, PROP_TOP, value.getItemAt(context, 1));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_TOPLEFT, (context, model, propertyName) ->
                new Value(model.getKnownProperty(context, PROP_LEFT).integerValue(), model.getKnownProperty(context, PROP_TOP).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOMRIGHT, (context, model, propertyName, value) -> {
            if (value.isPoint(context)) {
                model.setKnownProperty(context, PROP_LEFT, new Value(value.getItemAt(context, 0).longValue() - model.getKnownProperty(context, PROP_WIDTH).longValue()));
                model.setKnownProperty(context, PROP_TOP, new Value(value.getItemAt(context, 1).longValue() - model.getKnownProperty(context, PROP_HEIGHT).longValue()));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });
        definePropertyAlias(PROP_BOTTOMRIGHT, PROP_BOTRIGHT);

        defineComputedGetterProperty(PROP_BOTTOMRIGHT, (context, model, propertyName) ->
                new Value(
                        model.getKnownProperty(context, PROP_LEFT).integerValue() + model.getKnownProperty(context, PROP_WIDTH).integerValue(),
                        model.getKnownProperty(context, PROP_TOP).integerValue() + model.getKnownProperty(context, PROP_HEIGHT).integerValue()
                )
        );

        definePropertyAlias(PROP_LOCATION, PROP_LOC);
        defineComputedGetterProperty(PROP_LOCATION, (context, model, propertyName) ->
                new Value(
                        model.getKnownProperty(context, PROP_LEFT).integerValue() + model.getKnownProperty(context, PROP_WIDTH).integerValue() / 2,
                        model.getKnownProperty(context, PROP_TOP).integerValue() + model.getKnownProperty(context, PROP_HEIGHT).integerValue() / 2
                )
        );
        defineComputedSetterProperty(PROP_LOCATION, (context, model, propertyName, value) -> {
            if (value.isPoint(context)) {
                model.setKnownProperty(context, PROP_LEFT, new Value(value.getItemAt(context, 0).longValue() - model.getKnownProperty(context, PROP_WIDTH).longValue() / 2));
                model.setKnownProperty(context, PROP_TOP, new Value(value.getItemAt(context, 1).longValue() - model.getKnownProperty(context, PROP_HEIGHT).longValue() / 2));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        definePropertyAlias(PROP_RECT, PROP_RECTANGLE);

        defineComputedGetterProperty(PROP_SCRIPT, (context, model, propertyName) -> model.getKnownProperty(context, PROP_SCRIPTTEXT));
        defineComputedSetterProperty(PROP_SCRIPT, (context, model, propertyName, value) -> {
            model.setKnownProperty(context, PROP_SCRIPTTEXT, value);
            precompile(context);
        });

        // When breakpoints change, automatically apply them to the script
        addPropertyChangedObserver((context, model, property, oldValue, newValue) -> {
            if (property.equalsIgnoreCase(PROP_BREAKPOINTS)) {
                getScript(context).applyBreakpoints(getBreakpoints());
            }
        });
        definePropertyAlias(PROP_BREAKPOINTS, PROP_CHECKPOINTS);

        precompile(new ExecutionContext());
    }

    /**
     * Gets the "default" adjective associated with a given property. That is, the length adjective that is
     * automatically applied when referring to a property without explicitly specifying an adjective.
     * <p>
     * For example, 'the name of btn 1' actually refers to 'the abbreviated name' property.
     *
     * @param propertyName The name of the property whose default adjective should be returned.
     * @return The default adjective.
     */
    public Adjective getDefaultAdjectiveForProperty(String propertyName) {
        return Adjective.DEFAULT;
    }

    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return false;
    }

    public Rectangle getRect(ExecutionContext context) {
        try {
            Rectangle rect = new Rectangle();
            rect.x = getProperty(context, PROP_LEFT).integerValue();
            rect.y = getProperty(context, PROP_TOP).integerValue();
            rect.height = getProperty(context, PROP_HEIGHT).integerValue();
            rect.width = getProperty(context, PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry for part model.", e);
        }
    }

    public PartType getType() {
        return type;
    }

    private void precompile(ExecutionContext context) {
        if (hasProperty(PROP_SCRIPTTEXT)) {
            Interpreter.asyncCompile(CompilationUnit.SCRIPT, getKnownProperty(context, PROP_SCRIPTTEXT).stringValue(), (scriptText, compiledScript, generatedError) -> {
                if (generatedError == null) {
                    script = (Script) compiledScript;
                    script.applyBreakpoints(getBreakpoints());
                }
            });
        }
    }

    public Script getScript(ExecutionContext context) {
        if (script == null) {
            try {
                script = Interpreter.blockingCompileScript(getKnownProperty(context, PROP_SCRIPTTEXT).stringValue());
                script.applyBreakpoints(getBreakpoints());
            } catch (HtException e) {
                e.printStackTrace();
            }
        }
        return script;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public CardLayer getLayer() {
        switch (owner) {
            case BACKGROUND:
                return CardLayer.BACKGROUND_PARTS;
            case CARD:
                return CardLayer.CARD_PARTS;
            default:
                throw new IllegalStateException("Bug! Not a card layered part: " + owner);
        }
    }

    public int getId(ExecutionContext context) {
        return getKnownProperty(context, PROP_ID).integerValue();
    }

    public String getName(ExecutionContext context) {
        return getKnownProperty(context, PROP_NAME).stringValue();
    }

    public PartSpecifier getPartSpecifier(ExecutionContext context) {
        return new PartIdSpecifier(getOwner(), getType(), getId(context));
    }

    /**
     * Gets the value of this part; thus, reads the value of the property returned by {@link #getValueProperty()}.
     *
     * @return The value of this property
     * @param context The execution context.
     */
    public Value getValue(ExecutionContext context) {
        return getKnownProperty(context, getValueProperty());
    }

    /**
     * Sets the value of this part; thus, sets the value of the property returned by {@link #getValueProperty()}.
     *
     * @param value The value of this part.
     * @param context The execution context.
     */
    public void setValue(Value value, ExecutionContext context) {
        try {
            setProperty(context, getValueProperty(), value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the name of the property that is read or written when a value is placed into the part i.e., ('put "Hello"
     * into card field 1'). Typically the 'contents' property, but other parts (like fields) may override to provide
     * a different property (e.g., fields use the 'text' property as their contents).
     *
     * @return The name of the part's value property
     */
    public String getValueProperty() {
        return PROP_CONTENTS;
    }

    /**
     * Gets a part specifier that refers to this part in the stack. If this part is a button or a field, the part
     * specifier is a {@link CompositePartSpecifier} referring to the button or field on a specific card or background.
     *
     * @return A part specifier referring to this part.
     * @param context The execution context.
     */
    public PartSpecifier getMe(ExecutionContext context) {
        PartModel parent = getParentPartModel();
        PartSpecifier localPart = new PartIdSpecifier(getOwner(), getType(), getId(context));

        if (getType() == PartType.BUTTON || getType() == PartType.FIELD) {
            return new CompositePartSpecifier(localPart, new LiteralPartExp(null, parent.getMe(context)));
        } else {
            return localPart;
        }
    }

    public PartModel getParentPartModel() {
        return parentPartModel;
    }

    public void setParentPartModel(PartModel parentPartModel) {
        this.parentPartModel = parentPartModel;
    }

    public int getScriptEditorCaretPosition() {
        return scriptEditorCaretPosition;
    }

    public void setScriptEditorCaretPosition(int scriptEditorCaretPosition) {
        this.scriptEditorCaretPosition = scriptEditorCaretPosition;
    }

    public List<Integer> getBreakpoints() {
        ExecutionContext context = new ExecutionContext();
        ArrayList<Integer> breakpoints = new ArrayList<>();
        List<Value> breakpointValues = getKnownProperty(context, PROP_BREAKPOINTS).getItems(context);
        for (Value thisBreakpoint : breakpointValues) {
            breakpoints.add(thisBreakpoint.integerValue());
        }
        return breakpoints;
    }

    /**
     * Show the script editor for this part.
     * <p>
     * Typically invoked when the user has selected and double-control-clicked the part, chosen the appropriate
     * command from the Objects menu, or invoked the 'edit script of' command.
     * @param context The execution context
     */
    public ScriptEditor editScript(ExecutionContext context) {
        return editScript(context, null);
    }

    /**
     * Show the script editor for this part, positioning the caret in the editor field accordingly.
     * <p>
     * Typically invoked when the user has selected and double-control-clicked the part, chosen the appropriate
     * command from the Objects menu, or invoked the 'edit script of' command.
     *
     * @param context The execution context
     * @param caretPosition The location where the caret should be positioned in the text or null to use the last saved
     */
    public ScriptEditor editScript(ExecutionContext context, Integer caretPosition) {
        ScriptEditor editor = WindowManager.getInstance().findScriptEditorForPart(this);

        // Existing script editor for this part; show it
        if (editor != null) {
            SwingUtilities.invokeLater(() -> {
                editor.setVisible(true);
                editor.requestFocus();
            });

            return editor;
        }

        // Create new editor
        else {
            AtomicReference<ScriptEditor> newEditor = new AtomicReference<>();
            ThreadUtils.invokeAndWaitAsNeeded(() -> {
                newEditor.set(new ScriptEditor());

                if (caretPosition != null) {
                    setScriptEditorCaretPosition(caretPosition);
                }

                WindowBuilder.make(newEditor.get())
                        .withTitle("Script of " + getName(context))
                        .ownsMenubar()
                        .withModel(this)
                        .resizeable(true)
                        .withLocationStaggeredOver(WindowManager.getInstance().getStackWindow(context).getWindowPanel())
                        .build();
            });

            SwingUtilities.invokeLater(() -> newEditor.get().requestFocus());
            return newEditor.get();
        }
    }

    /**
     * Show the property editor for this part.
     * <p>
     * Typically invoked when the user has selected and double-clicked the part, or chosen the appropriate command from
     * the Objects menu.
     * @param context The execution context.
     */
    public void editProperties(ExecutionContext context) {
        ThreadUtils.invokeAndWaitAsNeeded(() ->
                WindowBuilder.make(getType() == PartType.FIELD ? new FieldPropertyEditor() : new ButtonPropertyEditor())
                        .asModal()
                        .withTitle(getName(context))
                        .withModel(this)
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow(context).getWindowPanel())
                        .resizeable(false)
                        .build());
    }
}