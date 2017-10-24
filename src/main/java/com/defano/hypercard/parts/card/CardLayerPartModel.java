package com.defano.hypercard.parts.card;

import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;

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

    public CardLayerPartModel(PartType type, Owner owner) {
        super(type, owner);

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

    public TextStyleSpecifier getFont() {
        return TextStyleSpecifier.fromNameStyleSize(getKnownProperty(PROP_TEXTFONT), getKnownProperty(PROP_TEXTSTYLE), getKnownProperty(PROP_TEXTSIZE));
    }

    public void setFont(TextStyleSpecifier style) {
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
}
