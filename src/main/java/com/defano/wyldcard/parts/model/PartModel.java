package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.expressions.parts.LiteralPartExp;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.message.Messagable;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardLayer;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.properties.SimplePropertiesModel;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.Compiler;
import com.defano.wyldcard.runtime.compiler.TwoPhaseParser;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.layouts.ButtonPropertyEditor;
import com.defano.wyldcard.window.layouts.FieldPropertyEditor;
import com.defano.wyldcard.window.layouts.ScriptEditor;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A base model object for all HyperCard "parts" that Defines properties common to all part objects.
 */
public abstract class PartModel extends SimplePropertiesModel implements Messagable {

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_ID = "id";
    public static final String PROP_NUMBER = "number";
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
    private transient long deferCompilation = 0;
    private transient long scriptHash;

    public PartModel(PartType type, Owner owner, PartModel parentPartModel) {
        super();

        this.type = type;
        this.owner = owner;
        this.parentPartModel = parentPartModel;

        define(PROP_VISIBLE).asValue(true);
        define(PROP_SCRIPTTEXT).asValue();
        define(PROP_BREAKPOINTS, PROP_CHECKPOINTS).asValue();

        postConstructPartModel();
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
    protected void postConstructPartModel() {
        super.postConstructAdvancedPropertiesModel();

        define(PROP_RECT, PROP_RECTANGLE).asComputedValue()
                .withSetter((context, model, value) -> {
                    if (value.isRect()) {
                        model.set(context, PROP_LEFT, value.getItemAt(context, 0));
                        model.set(context, PROP_TOP, value.getItemAt(context, 1));
                        model.set(context, PROP_HEIGHT, new Value(value.getItemAt(context, 3).longValue() - value.getItemAt(context, 1).longValue()));
                        model.set(context, PROP_WIDTH, new Value(value.getItemAt(context, 2).longValue() - value.getItemAt(context, 0).longValue()));
                    } else {
                        throw new HtSemanticException("Expected a rectangle, but got " + value.toString());
                    }
                })
                .withGetter((context, model) -> {
                    Value left = model.get(context, PROP_LEFT);
                    Value top = model.get(context, PROP_TOP);
                    Value height = model.get(context, PROP_HEIGHT);
                    Value width = model.get(context, PROP_WIDTH);

                    return new Value(left.integerValue(), top.integerValue(), left.integerValue() + width.integerValue(), top.integerValue() + height.integerValue());
                });

        define(PROP_RIGHT).asComputedValue()
                .withGetter((context, model) -> new Value(model.get(context, PROP_LEFT).integerValue() + model.get(context, PROP_WIDTH).integerValue()))
                .withSetter((context, model, value) -> model.set(context, PROP_LEFT, new Value(value.integerValue() - model.get(context, PROP_WIDTH).integerValue())));

        define(PROP_BOTTOM).asComputedValue()
                .withGetter((context, model) -> new Value(model.get(context, PROP_TOP).integerValue() + model.get(context, PROP_HEIGHT).integerValue()))
                .withSetter((context, model, value) -> model.set(context, PROP_TOP, new Value(value.integerValue() - model.get(context, PROP_HEIGHT).integerValue())));

        define(PROP_TOPLEFT).asComputedValue()
                .withSetter((context, model, value) -> {
                    if (value.isPoint()) {
                        model.set(context, PROP_LEFT, value.getItemAt(context, 0));
                        model.set(context, PROP_TOP, value.getItemAt(context, 1));
                    } else {
                        throw new HtSemanticException("Expected a point, but got " + value.toString());
                    }
                })
                .withGetter((context, model) -> new Value(model.get(context, PROP_LEFT).integerValue(), model.get(context, PROP_TOP).integerValue()));

        define(PROP_BOTTOMRIGHT, PROP_BOTRIGHT).asComputedValue()
                .withSetter((context, model, value) -> {
                    if (value.isPoint()) {
                        model.set(context, PROP_LEFT, new Value(value.getItemAt(context, 0).longValue() - model.get(context, PROP_WIDTH).longValue()));
                        model.set(context, PROP_TOP, new Value(value.getItemAt(context, 1).longValue() - model.get(context, PROP_HEIGHT).longValue()));
                    } else {
                        throw new HtSemanticException("Expected a point, but got " + value.toString());
                    }
                })
                .withGetter((context, model) -> new Value(
                        model.get(context, PROP_LEFT).integerValue() + model.get(context, PROP_WIDTH).integerValue(),
                        model.get(context, PROP_TOP).integerValue() + model.get(context, PROP_HEIGHT).integerValue()
                ));

        define(PROP_LOCATION, PROP_LOC).asComputedValue()
                .withGetter((context, model) -> new Value(
                        model.get(context, PROP_LEFT).integerValue() + model.get(context, PROP_WIDTH).integerValue() / 2,
                        model.get(context, PROP_TOP).integerValue() + model.get(context, PROP_HEIGHT).integerValue() / 2
                )).withSetter((context, model, value) -> {
            if (value.isPoint()) {
                model.set(context, PROP_LEFT, new Value(value.getItemAt(context, 0).longValue() - model.get(context, PROP_WIDTH).longValue() / 2));
                model.set(context, PROP_TOP, new Value(value.getItemAt(context, 1).longValue() - model.get(context, PROP_HEIGHT).longValue() / 2));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.toString());
            }
        });

        define(PROP_SCRIPT).asComputedValue()
                .withSetter((context, model, value) -> setScriptText(context, value.toString()))
                .withGetter((context, model) -> model.get(context, PROP_SCRIPTTEXT));

        // When breakpoints change, automatically apply them to the script
        addPropertyChangedObserver((context, model, property, oldValue, newValue) -> {
            if (property.equalsIgnoreCase(PROP_BREAKPOINTS)) {
                Script script = getScript(context);
                if (script != null) {
                    script.applyBreakpoints(getBreakpoints());
                }
            }
        });
    }

    /**
     * Gets the "default" adjective associated with the given property. That is, the length adjective that is
     * automatically applied when referring to a property without explicitly specifying an adjective.
     * <p>
     * For example, 'the name of btn 1' actually refers to 'the abbreviated name' property.
     *
     * @param propertyName The name of the property whose default adjective should be returned.
     * @return The default adjective.
     */
    public LengthAdjective getDefaultAdjectiveForProperty(String propertyName) {
        return LengthAdjective.DEFAULT;
    }

    /**
     * Determines if a length adjective may be applied to the given property of this part (i.e., 'the long name').
     *
     * @param propertyName The name of the property
     * @return True if the property supports length adjectives for this part, false otherwise.
     */
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return false;
    }

    public Rectangle getRect(ExecutionContext context) {
        try {
            Rectangle rect = new Rectangle();
            rect.x = get(context, PROP_LEFT).integerValue();
            rect.y = get(context, PROP_TOP).integerValue();
            rect.height = get(context, PROP_HEIGHT).integerValue();
            rect.width = get(context, PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry for part model.", e);
        }
    }

    public PartType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Script getScript(ExecutionContext context) {
        if (isScriptDirty(context) && System.currentTimeMillis() > deferCompilation) {
            try {
                String scriptText = getScriptText(context);
                Script script = (Script) Compiler.blockingCompile(CompilationUnit.SCRIPT, scriptText);

                if (script != null) {
                    return setScript(script, scriptText.hashCode());
                }
            } catch (HtException e) {
                deferCompilation = System.currentTimeMillis() + 5000;
                e.getBreadcrumb().setContext(context);
                e.getBreadcrumb().setPart(getPartSpecifier(context));
                WyldCard.getInstance().showErrorDialogAndAbort(e);
            }
        }

        return this.script == null ? new Script() : this.script;
    }

    private synchronized Script setScript(Script script, long scriptHash) {
        this.script = script;
        this.scriptHash = scriptHash;
        this.script.applyBreakpoints(getBreakpoints());

        return script;
    }

    public String getScriptText(ExecutionContext context) {
        return get(context, PROP_SCRIPT).toString();
    }

    private void setScriptText(ExecutionContext context, String scriptText) {
        set(context, PROP_SCRIPTTEXT, new Value(TwoPhaseParser.commentNonHandlerLines(scriptText)));
    }

    private boolean isScriptDirty(ExecutionContext context) {
        return hasProperty(PROP_SCRIPT) && getScriptText(context).hashCode() != scriptHash;
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
        return get(context, PROP_ID).integerValue();
    }

    public String getLongName(ExecutionContext context) {
        return getName(context);
    }

    public String getShortName(ExecutionContext context) {
        return getName(context);
    }

    public String getName(ExecutionContext context) {
        return get(context, PROP_NAME).toString();
    }

    public PartSpecifier getPartSpecifier(ExecutionContext context) {
        return new PartIdSpecifier(getOwner(), getType(), getId(context));
    }

    /**
     * Gets the value of this part; thus, reads the value of the property returned by {@link #getValueProperty()}.
     *
     * @param context The execution context.
     * @return The value of this property
     */
    public Value getValue(ExecutionContext context) {
        return get(context, getValueProperty());
    }

    /**
     * Sets the value of this part; thus, sets the value of the property returned by {@link #getValueProperty()}.
     *
     * @param value   The value of this part.
     * @param context The execution context.
     */
    public void setValue(Value value, ExecutionContext context) {
        try {
            set(context, getValueProperty(), value);
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
     * @param context The execution context.
     * @return A part specifier referring to this part.
     */
    public PartSpecifier getMe(ExecutionContext context) {
        PartModel parent = getParentPartModel();
        PartSpecifier localPart = new PartIdSpecifier(getOwner(), getType(), getId(context));

        if (getType() == PartType.BUTTON || getType() == PartType.FIELD) {
            return new CompositePartSpecifier(context, localPart, new LiteralPartExp(null, parent.getMe(context)));
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

    public StackModel getParentStackModel() {
        if (this instanceof StackModel) {
            return (StackModel) this;
        } else if (getParentPartModel() != null) {
            return getParentPartModel().getParentStackModel();
        } else {
            return null;
        }
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
        List<Value> breakpointValues = get(context, PROP_BREAKPOINTS).getItems(context);
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
     *
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
     * @param context       The execution context
     * @param caretPosition The location where the caret should be positioned in the text or null to use the last saved
     */
    public ScriptEditor editScript(ExecutionContext context, Integer caretPosition) {
        return Invoke.onDispatch(() -> {
            ScriptEditor editor = WyldCard.getInstance().getWindowManager().findScriptEditorForPart(PartModel.this);

            if (caretPosition != null) {
                setScriptEditorCaretPosition(caretPosition);
            }

            // Existing script editor for this part; show it
            if (editor != null) {
                editor.setVisible(true);
                editor.requestFocus();
            }

            // Create new editor
            else {
                editor = new ScriptEditor();

                new WindowBuilder<>(editor)
                        .withModel(PartModel.this)
                        .withTitle("Script of " + getLongName(context))
                        .ownsMenubar()
                        .resizeable(true)
                        .withLocationStaggeredOver(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindowPanel())
                        .build();

                editor.requestFocus();
            }

            return editor;
        });
    }

    /**
     * Show the property editor for this part.
     * <p>
     * Typically invoked when the user has selected and double-clicked the part, or chosen the appropriate command from
     * the Objects menu.
     *
     * @param context The execution context.
     */
    public void editProperties(ExecutionContext context) {
        Invoke.onDispatch(() -> {
            if (getType() == PartType.FIELD) {
                new WindowBuilder<>(new FieldPropertyEditor())
                        .withModel((FieldModel) this)
                        .asModal()
                        .withTitle(getName(context))
                        .withLocationCenteredOver(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindowPanel())
                        .resizeable(false)
                        .build();
            } else {
                new WindowBuilder<>(new ButtonPropertyEditor())
                        .withModel((ButtonModel) this)
                        .asModal()
                        .withTitle(getName(context))
                        .withLocationCenteredOver(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindowPanel())
                        .resizeable(false)
                        .build();
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartModel that = (PartModel) o;
        return this.getType() == that.getType() &&
                this.owner == that.getOwner() &&
                this.getId(null) == that.getId(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, getId(null), getOwner());
    }
}