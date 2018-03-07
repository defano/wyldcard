package com.defano.hypercard.parts.card;

import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.parts.finder.LayeredPartFinder;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * A model of properties common to parts that live on a layer of the card (i.e., buttons and fields).
 */
public abstract class CardLayerPartModel extends PartModel {

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

    public CardLayerPartModel(PartType type, Owner owner, PartModel parentPartModel) {
        super(type, owner, parentPartModel);

        defineProperty(PROP_ZORDER, new Value(0), false);
        defineProperty(PROP_SELECTEDTEXT, new Value(""), true);
        defineProperty(PROP_SELECTEDLINE, new Value(""), true);
        defineProperty(PROP_SELECTEDCHUNK, new Value(""), true);
        defineProperty(PROP_TEXTSIZE, new Value(((Font) UIManager.get("Button.font")).getSize()), false);
        defineProperty(PROP_TEXTFONT, new Value(((Font)UIManager.get("Button.font")).getFamily()), false);
        defineProperty(PROP_TEXTSTYLE, new Value("plain"), false);
        defineProperty(PROP_TEXTALIGN, new Value("center"), false);
        defineProperty(PROP_ENABLED, new Value(true), false);
    }

    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        defineComputedReadOnlyProperty(PROP_LONGNAME, (model, propertyName) -> new Value(getLongName()));
        defineComputedReadOnlyProperty(PROP_ABBREVNAME, (model, propertyName) -> new Value(getAbbrevName()));
        defineComputedReadOnlyProperty(PROP_SHORTNAME, (model, propertyName) -> new Value(getShortName()));
    }

    @Override
    public Adjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return Adjective.ABBREVIATED;
        } else {
            return Adjective.DEFAULT;
        }
    }

    public TextStyleSpecifier getTextStyle() {
        return TextStyleSpecifier.fromNameStyleSize(getKnownProperty(PROP_TEXTFONT), getKnownProperty(PROP_TEXTSTYLE), getKnownProperty(PROP_TEXTSIZE));
    }

    public void setTextStyle(TextStyleSpecifier style) {
        if (style != null) {
            if (style.getFontSize() > 0) {
                setKnownProperty(PROP_TEXTSIZE, new Value(style.getFontSize()));
            }

            if (style.getFontFamily() != null) {
                setKnownProperty(PROP_TEXTFONT, new Value(style.getFontFamily()));
            }

            setKnownProperty(PROP_TEXTSTYLE, style.getHyperTalkStyle());
        }
    }

    public long getPartNumber() {
        return ((LayeredPartFinder) getParentPartModel()).getPartNumber(this);
    }

    public long getPartCount() {
        return ((LayeredPartFinder) getParentPartModel()).getPartCount(null, getOwner());
    }

    public String getShortName() {
        return getKnownProperty(PROP_NAME).stringValue();
    }

    public String getAbbrevName() {
        return getOwner().hyperTalkName.toLowerCase() + " " + getType().hypertalkName + " \"" + getShortName() + "\"";
    }

    public String getLongName() {
        // TODO: Add 'of stack ...' portion (after supporting that HyperTalk syntax)
        return getAbbrevName() + " of card id " + ExecutionContext.getContext().getCurrentCard().getId();
    }

}
