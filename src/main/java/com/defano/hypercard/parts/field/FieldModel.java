/*
 * FieldModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.field;

import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A data model representing a field part on a card. See {@link FieldPart} for the associated
 * view object.
 */
public class FieldModel extends CardLayerPartModel {

    public static final String PROP_TEXT = "text";
    public static final String PROP_DONTWRAP = "dontwrap";
    public static final String PROP_LOCKTEXT = "locktext";
    public static final String PROP_SHOWLINES = "showlines";
    public static final String PROP_STYLE = "style";
    public static final String PROP_SHAREDTEXT = "sharedtext";

    private byte[] sharedRtf;
    private Map<Integer, byte[]> unsharedRtf = new HashMap<>();

    private transient int currentCardId = 0;

    public FieldModel (Owner owner) {
        super(PartType.FIELD, owner);
    }

    public static FieldModel newFieldModel(int id, Rectangle geometry, Owner owner, int parentCardId) {
        FieldModel partModel = new FieldModel(owner);

        partModel.currentCardId = parentCardId;

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Text Field " + id), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_DONTWRAP, new Value(false), false);
        partModel.defineProperty(PROP_VISIBLE, new Value(true), false);
        partModel.defineProperty(PROP_LOCKTEXT, new Value(false), false);
        partModel.defineProperty(PROP_SHOWLINES, new Value(true), false);
        partModel.defineProperty(PROP_STYLE, new Value(FieldStyle.TRANSPARENT.getName()), false);
        partModel.defineProperty(PROP_TEXTALIGN, new Value("left"), false);
        partModel.defineProperty(PROP_CONTENTS, new Value(""), false);
        partModel.defineProperty(PROP_SHAREDTEXT, new Value(false), false);

        partModel.defineComputedGetterProperty(PROP_TEXT, (model, propertyName) -> new Value(partModel.getPlainText()));

        return partModel;
    }

    public byte[] getRtf() {
        if (useSharedText()) {
            return sharedRtf;
        } else {
            return unsharedRtf.getOrDefault(getCurrentCardId(), new byte[0]);
        }
    }

    public void setRtf(byte[] rtf) {
        if (useSharedText()) {
            this.sharedRtf = rtf;
        } else {
            unsharedRtf.put(getCurrentCardId(), rtf);
        }
    }

    private boolean useSharedText() {
        return getOwner() == Owner.CARD || getKnownProperty(PROP_SHAREDTEXT).booleanValue();
    }

    private String getPlainText() {

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(getRtf());
            StyledDocument doc = new DefaultStyledDocument();

            new RTFEditorKit().read(bais, doc, 0);
            bais.close();

            // RTFEditorKit appears to (erroneously) append a newline when we deserialize; get rid of that.
            return doc.getText(0, doc.getLength() - 1);

        } catch (Exception e) {
            return "";
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getValueProperty() {
        return PROP_TEXT;
    }

    /**
     * For the purposes of background fields that do not have the sharedText property set, this value determines which
     * card's text is actively displayed in the field.
     * @param cardId
     */
    public void setCurrentCardId(int cardId) {
        this.currentCardId = cardId;
    }

    private int getCurrentCardId() {
        return currentCardId;
    }
}

