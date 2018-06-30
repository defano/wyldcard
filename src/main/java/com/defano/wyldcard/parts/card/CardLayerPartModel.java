package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.LengthAdjective;
import com.defano.wyldcard.fonts.TextStyleSpecifier;
import com.defano.wyldcard.parts.NamedPart;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * A model of properties common to parts that live on a layer of the card (i.e., buttons and fields).
 */
public abstract class CardLayerPartModel extends PartModel implements NamedPart {

    public static final String PROP_ZORDER = "zorder";
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

        defineProperty(PROP_ZORDER, new Value(0), false);
        defineProperty(PROP_SELECTEDTEXT, new Value(""), true);
        defineProperty(PROP_SELECTEDLINE, new Value(""), true);
        defineProperty(PROP_SELECTEDCHUNK, new Value(""), true);
        defineProperty(PROP_TEXTSIZE, new Value(((Font) UIManager.get("Button.font")).getSize()), false);
        defineProperty(PROP_TEXTFONT, new Value(((Font) UIManager.get("Button.font")).getFamily()), false);
        defineProperty(PROP_TEXTSTYLE, new Value("plain"), false);
        defineProperty(PROP_TEXTALIGN, new Value("center"), false);
        defineProperty(PROP_ENABLED, new Value(true), false);
    }

    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        this.currentCardId = new ThreadLocal<>();
        this.currentCardId.set(new ExecutionContext().getCurrentCard().getId(new ExecutionContext()));

        defineComputedReadOnlyProperty(PROP_LONGNAME, (context, model, propertyName) -> new Value(getLongName(context)));
        defineComputedReadOnlyProperty(PROP_ABBREVNAME, (context, model, propertyName) -> new Value(getAbbreviatedName(context)));
        defineComputedReadOnlyProperty(PROP_SHORTNAME, (context, model, propertyName) -> new Value(getShortName(context)));
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
                getKnownProperty(context, PROP_TEXTALIGN),
                getKnownProperty(context, PROP_TEXTFONT),
                getKnownProperty(context, PROP_TEXTSTYLE),
                getKnownProperty(context, PROP_TEXTSIZE));
    }

    public void setTextStyle(ExecutionContext context, TextStyleSpecifier style) {
        if (style != null) {
            if (style.getFontSize() > 0) {
                setKnownProperty(context, PROP_TEXTSIZE, new Value(style.getFontSize()));
            }

            if (style.getFontFamily() != null) {
                setKnownProperty(context, PROP_TEXTFONT, new Value(style.getFontFamily()));
            }

            setKnownProperty(context, PROP_TEXTSTYLE, style.getHyperTalkStyle());
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
     * Gets the "number" of this part (equivalent to its z-order within its layer).
     *
     * @return The part number.
     * @param context The execution context.
     */
    public long getPartNumber(ExecutionContext context) {
        return ((LayeredPartFinder) getParentPartModel()).getPartNumber(context, this);
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

    @Override
    public String getShortName(ExecutionContext context) {
        return getKnownProperty(context, PROP_NAME).stringValue();
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

}
