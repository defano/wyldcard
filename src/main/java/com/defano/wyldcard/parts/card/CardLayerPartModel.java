package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.enums.LengthAdjective;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.fonts.TextStyleSpecifier;
import com.defano.wyldcard.parts.NamedPart;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * A model of properties common to parts that live on a layer of the card (i.e., buttons and fields).
 */
public abstract class CardLayerPartModel extends PartModel implements NamedPart {

    public static final String PROP_ZORDER = "zorder";
    public static final String PROP_STYLE = "style";
    public static final String PROP_SELECTEDTEXT = "selectedtext";
    public static final String PROP_SELECTEDLINE = "selectedline";
    public static final String PROP_SELECTEDCHUNK = "selectedchunk";
    public static final String PROP_TEXTSIZE = "textsize";
    public static final String PROP_TEXTFONT = "textfont";
    public static final String PROP_TEXTSTYLE = "textstyle";
    public static final String PROP_TEXTALIGN = "textalign";
    public static final String PROP_ENABLED = "enabled";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";

    // The id of the card to which this part is currently "bound"; used when referring to a part on a card other than
    // the current card.
    private transient ThreadLocal<Integer> currentCardId = new ThreadLocal<>();

    public CardLayerPartModel(PartType type, Owner owner, PartModel parentPartModel) {
        super(type, owner, parentPartModel);

        define(PROP_ZORDER).asValue(0);
        define(PROP_SELECTEDTEXT).asValue();
        define(PROP_SELECTEDLINE).asValue();
        define(PROP_SELECTEDCHUNK).asValue();
        define(PROP_TEXTSIZE).asValue(((Font) UIManager.get("Button.font")).getSize());
        define(PROP_TEXTFONT).asValue(((Font) UIManager.get("Button.font")).getFamily());
        define(PROP_TEXTSTYLE).asValue("plain");
        define(PROP_TEXTALIGN).asValue("center");
        define(PROP_ENABLED).asValue(true);

        postConstructCardLayerPartModel();
    }

    @PostConstruct
    public void postConstructCardLayerPartModel() {
        super.postConstructPartModel();

        this.currentCardId = new ThreadLocal<>();
        this.currentCardId.set(new ExecutionContext().getCurrentCard().getId(new ExecutionContext()));

        define(PROP_LONGNAME).asComputedReadOnlyValue((context, model) -> new Value(getLongName(context)));
        define(PROP_ABBREVNAME).asComputedReadOnlyValue((context, model) -> new Value(getAbbreviatedName(context)));
        define(PROP_SHORTNAME).asComputedReadOnlyValue((context, model) -> new Value(getShortName(context)));
    }

    /** {@inheritDoc} */
    @Override
    public LengthAdjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return LengthAdjective.ABBREVIATED;
        } else {
            return LengthAdjective.DEFAULT;
        }
    }

    /** {@inheritDoc} */
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME);
    }

    public TextStyleSpecifier getTextStyle(ExecutionContext context) {
        return TextStyleSpecifier.fromAlignNameStyleSize(
                get(context, PROP_TEXTALIGN),
                get(context, PROP_TEXTFONT),
                get(context, PROP_TEXTSTYLE),
                get(context, PROP_TEXTSIZE));
    }

    public void setTextStyle(ExecutionContext context, TextStyleSpecifier style) {
        if (style != null) {
            if (style.getFontSize() > 0) {
                set(context, PROP_TEXTSIZE, new Value(style.getFontSize()));
            }

            if (style.getFontFamily() != null) {
                set(context, PROP_TEXTFONT, new Value(style.getFontFamily()));
            }

            set(context, PROP_TEXTSTYLE, style.getHyperTalkStyle());
        }
    }

    /**
     * Gets the ID of the card to which this part is bound in the current HyperTalk execution context. See
     * {@link #setCurrentCardId(int)} for details. Returns the current card of the execution context when not explicitly
     * set.
     *
     * @return The ID of the card to which this part is currently bound.
     * @param context The execution context.
     */
    public int getCurrentCardId(ExecutionContext context) {
        if (this.currentCardId.get() == null) {
            return context.getCurrentCard().getId(context);
        }

        return this.currentCardId.get();
    }

    /**
     * Sets the ID of the card to which this part is bound in the current HyperTalk execution context.
     * <p>
     * This property has two primary uses:
     * <p>
     * 1. For the purposes of background fields that do not have the sharedText property set, this value determines
     * which card's text is actively displayed in the field. For example, 'the first word of fld 1 of card id 3' does
     * not necessarily refer to the same string as 'the first word of fld 1 of this card'. In the first case, this
     * method should be invoked with '3' to assure that subsequent queries deal with the text bound to card id 3.
     * <p>
     * 2. When referring to 'the long name' of cards and buttons, the id of the referenced card is returned which is
     * not necessarily the current card. For example, when evaluating 'the long name of button 3 of card 12', we need
     * to know the ID of card 12.
     * <p>
     * @param cardId The ID of the card to which this part is currently bound.
     */
    public void setCurrentCardId(int cardId) {
        this.currentCardId.set(cardId);
    }

    /**
     * Gets the ID of the card to which this part is currently bound, or null, if the part is not currently bound to a
     * card. See {@link #getCurrentCardId(ExecutionContext)}.
     *
     * @return The bound card id.
     */
    protected Integer getCurrentCardIdOrNull() {
        return this.currentCardId.get();
    }

    /**
     * Gets the "number" of this part (equivalent to its z-order within its layer), relative to all parts on the same
     * layer.
     *
     * @return The part number.
     * @param context The execution context.
     */
    public long getPartNumber(ExecutionContext context) {
        return ((LayeredPartFinder) getParentPartModel()).getPartNumber(context, this);
    }

    /**
     * Gets the number of this part (equivalent to its z-order within its layer) relative only to other parts of the
     * same kind (button or field) on this layer.
     *
     * @param context The exeuction context
     * @return The number of this part relative to other parts of the same kind
     */
    public long getButtonOrFieldNumber(ExecutionContext context) {
        return ((LayeredPartFinder) getParentPartModel()).getPartNumber(context, this, getType());
    }

    /**
     * Gets the number of parts existing on the same layer (cd or bkgnd) as this part.
     *
     * @return The number of parts.
     * @param context The execution context.
     */
    public long getPartCount(ExecutionContext context) {
        return ((LayeredPartFinder) getParentPartModel()).getPartCount(context, null, getOwner());
    }

    /**
     * Gets the number of parts of this type (i.e., number of buttons or number of fields) on the same layer (card or
     * background) as this part.
     *
     * @param context The execution context
     * @return The number of the same kind of part as this on the same layer
     */
    public long getButtonOrFieldCount(ExecutionContext context) {
        return ((LayeredPartFinder) getParentPartModel()).getPartCount(context, getType(), getOwner());
    }

    public String getLayerNumberHypertalkIdentifier(ExecutionContext context) {
        return getOwner().hyperTalkName.toLowerCase() + " button " + getPartNumber(context);
    }

    @Override
    public String getShortName(ExecutionContext context) {
        return get(context, PROP_NAME).toString();
    }

    @Override
    public String getAbbreviatedName(ExecutionContext context) {
        return getOwner().hyperTalkName.toLowerCase() + " " + getType().hypertalkName + " \"" + getShortName(context) + "\"";
    }

    @Override
    public String getLongName(ExecutionContext context) {
        StackModel parentStack = getParentStackModel();

        if (parentStack != null) {
            return getAbbreviatedName(context)
                    + " of card id "
                    + getCurrentCardId(context)
                    + " of "
                    + parentStack.getLongName(context);
        } else {
            return getAbbreviatedName(context);
        }
    }

    /**
     * Gets a HyperTalk expression referring to this part by its number and layer, for example, 'card button 2'.
     *
     * @return A HyperTalk expression referring to this part by its number and layer.
     * @throws IllegalStateException Thrown if this is a layered part (like a card, window or stack)
     */
    public String getHyperTalkAddress(ExecutionContext context) {
        if (!getType().isLayeredPart()) {
            throw new IllegalStateException("Bug! Not a layered part.");
        }

        return getDisplayLayer().getHyperTalkName() + " " +
                getType().hypertalkName + " " +
                get(context, PROP_NUMBER);
    }
}
