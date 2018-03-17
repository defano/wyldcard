package com.defano.wyldcard.parts.model;

import com.defano.wyldcard.parts.Messagable;
import com.defano.wyldcard.parts.card.CardLayer;
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
import java.awt.*;

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

    private final PartType type;
    private Owner owner;
    private int scriptEditorCaretPosition;

    private transient PartModel parentPartModel;
    private transient Script script = new Script();

    public PartModel(PartType type, Owner owner, PartModel parentPartModel) {
        super();

        this.type = type;
        this.owner = owner;
        this.parentPartModel = parentPartModel;

        defineProperty(PROP_VISIBLE, new Value(true), false);
        defineProperty(PROP_SCRIPTTEXT, new Value(""), false);

        initialize();
    }

    /**
     * Recursively re-establish the parent-child part model relationship. Sets the value returned by
     * {@link #getParentPartModel()} to the given part model and causes this model to invoke this method on all its
     * children.
     * <p>
     * The relationship between a parent and it's child parts are persistent, but the reverse relationship (between
     * child and parent) is transient. This is a side effect of the serialization engine being unable to deal with
     * cycles in the model object graph (a child cannot depend on a parent that also depends on it.). Thus, as a
     * workaround, we programmatically re-establish the child-to-parent relationship after the stack has completed
     * deserializing from JSON.
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
        defineComputedSetterProperty(PROP_RECT, (model, propertyName, value) -> {
            if (value.isRect()) {
                model.setKnownProperty(PROP_LEFT, value.getItemAt(0));
                model.setKnownProperty(PROP_TOP, value.getItemAt(1));
                model.setKnownProperty(PROP_HEIGHT, new Value(value.getItemAt(3).longValue() - value.getItemAt(1).longValue()));
                model.setKnownProperty(PROP_WIDTH, new Value(value.getItemAt(2).longValue() - value.getItemAt(0).longValue()));
            } else {
                throw new HtSemanticException("Expected a rectangle, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_RECT, (model, propertyName) -> {
            Value left = model.getKnownProperty(PROP_LEFT);
            Value top = model.getKnownProperty(PROP_TOP);
            Value height = model.getKnownProperty(PROP_HEIGHT);
            Value width = model.getKnownProperty(PROP_WIDTH);

            return new Value(left.integerValue(), top.integerValue(), left.integerValue() + width.integerValue(), top.integerValue() + height.integerValue());
        });

        defineComputedGetterProperty(PROP_RIGHT, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue())
        );

        defineComputedSetterProperty(PROP_RIGHT, (model, propertyName, value) ->
                model.setKnownProperty(PROP_LEFT, new Value(value.integerValue() - model.getKnownProperty(PROP_WIDTH).integerValue()))
        );

        defineComputedGetterProperty(PROP_BOTTOM, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOM, (model, propertyName, value) ->
                model.setKnownProperty(PROP_TOP, new Value(value.integerValue() - model.getKnownProperty(PROP_HEIGHT).integerValue()))
        );

        defineComputedSetterProperty(PROP_TOPLEFT, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, value.getItemAt(0));
                model.setKnownProperty(PROP_TOP, value.getItemAt(1));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_TOPLEFT, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_LEFT).integerValue(), model.getKnownProperty(PROP_TOP).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOMRIGHT, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, new Value(value.getItemAt(0).longValue() - model.getKnownProperty(PROP_WIDTH).longValue()));
                model.setKnownProperty(PROP_TOP, new Value(value.getItemAt(1).longValue() - model.getKnownProperty(PROP_HEIGHT).longValue()));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });
        definePropertyAlias(PROP_BOTTOMRIGHT, PROP_BOTRIGHT);

        defineComputedGetterProperty(PROP_BOTTOMRIGHT, (model, propertyName) ->
                new Value(
                        model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue(),
                        model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue()
                )
        );

        definePropertyAlias(PROP_LOCATION, PROP_LOC);
        defineComputedGetterProperty(PROP_LOCATION, (model, propertyName) ->
                new Value(
                        model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue() / 2,
                        model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue() / 2
                )
        );
        defineComputedSetterProperty(PROP_LOCATION, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, new Value(value.getItemAt(0).longValue() - model.getKnownProperty(PROP_WIDTH).longValue() / 2));
                model.setKnownProperty(PROP_TOP, new Value(value.getItemAt(1).longValue() - model.getKnownProperty(PROP_HEIGHT).longValue() / 2));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        definePropertyAlias(PROP_RECT, PROP_RECTANGLE);

        defineComputedGetterProperty(PROP_SCRIPT, (model, propertyName) -> model.getKnownProperty(PROP_SCRIPTTEXT));
        defineComputedSetterProperty(PROP_SCRIPT, (model, propertyName, value) -> {
            model.setKnownProperty(PROP_SCRIPTTEXT, value);
            precompile();
        });

        precompile();
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

    public Rectangle getRect() {
        try {
            Rectangle rect = new Rectangle();
            rect.x = getProperty(PROP_LEFT).integerValue();
            rect.y = getProperty(PROP_TOP).integerValue();
            rect.height = getProperty(PROP_HEIGHT).integerValue();
            rect.width = getProperty(PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry for part model.", e);
        }
    }

    public PartType getType() {
        return type;
    }

    private void precompile() {
        if (hasProperty(PROP_SCRIPTTEXT)) {
            Interpreter.asyncCompile(CompilationUnit.SCRIPT, getKnownProperty(PROP_SCRIPTTEXT).stringValue(), (scriptText, compiledScript, generatedError) -> {
                if (generatedError == null) {
                    script = (Script) compiledScript;
                }
            });
        }
    }

    public Script getScript() {
        if (script == null) {
            try {
                script = Interpreter.blockingCompileScript(getKnownProperty(PROP_SCRIPTTEXT).stringValue());
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

    public int getId() {
        return getKnownProperty(PROP_ID).integerValue();
    }

    public String getName() {
        return getKnownProperty(PROP_NAME).stringValue();
    }

    public PartSpecifier getPartSpecifier() {
        return new PartIdSpecifier(getOwner(), getType(), getId());
    }

    /**
     * Gets the value of this part; thus, reads the value of the property returned by {@link #getValueProperty()}.
     *
     * @return The value of this property
     */
    public Value getValue() {
        return getKnownProperty(getValueProperty());
    }

    /**
     * Sets the value of this part; thus, sets the value of the property returned by {@link #getValueProperty()}.
     *
     * @param value The value of this part.
     */
    public void setValue(Value value) {
        try {
            setProperty(getValueProperty(), value);
        } catch (Exception e) {
            throw new RuntimeException("Bug! Part's value cannot be set.");
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
     */
    public PartSpecifier getMe() {
        PartModel parent = getParentPartModel();
        PartSpecifier localPart = new PartIdSpecifier(getOwner(), getType(), getId());

        if (getType() == PartType.BUTTON || getType() == PartType.FIELD) {
            return new CompositePartSpecifier(localPart, new LiteralPartExp(null, parent.getMe()));
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

    /**
     * Show the script editor for this part.
     * <p>
     * Typically invoked when the user has selected and double-control-clicked the part, or chosen the appropriate
     * command from the Objects menu.
     */
    public void editScript() {
        editScript(0);
    }

    /**
     * Show the script editor for this part.
     * <p>
     * Typically invoked when the user has selected and double-control-clicked the part, or chosen the appropriate
     * command from the Objects menu.
     */
    public void editScript(int caretPosition) {
        ThreadUtils.invokeAndWaitAsNeeded(() ->
                ((ScriptEditor) WindowBuilder.make(new ScriptEditor())
                        .withTitle("Script of " + getName())
                        .withModel(this)
                        .resizeable(true)
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .build())
                        .moveCaretToPosition(caretPosition));
    }

    /**
     * Show the property editor for this part.
     * <p>
     * Typically invoked when the user has selected and double-clicked the part, or chosen the appropriate command from
     * the Objects menu.
     */
    public void editProperties() {
        ThreadUtils.invokeAndWaitAsNeeded(() ->
                WindowBuilder.make(getType() == PartType.FIELD ? new FieldPropertyEditor() : new ButtonPropertyEditor())
                        .asModal()
                        .withTitle(getName())
                        .withModel(this)
                        .withLocationCenteredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .resizeable(false)
                        .build());
    }

    @Override
    public String toString() {
        return "PartModel{" +
                owner + " " + type +
                (hasProperty(PROP_ID) ? " id=" + getKnownProperty(PROP_ID) : "") +
                ", parent=" + String.valueOf(getParentPartModel()) +
                '}';
    }
}
