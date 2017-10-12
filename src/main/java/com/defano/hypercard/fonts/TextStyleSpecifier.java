package com.defano.hypercard.fonts;

import com.defano.hypertalk.ast.common.Value;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class TextStyleSpecifier {

    private AttributeSet attributes;

    private String fontFamily;
    private int fontSize;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;
    private boolean isStrikeThrough;
    private boolean isSuperscript;
    private boolean isSubscript;

    private TextStyleSpecifier() {
    }

    public static TextStyleSpecifier fromHyperTalkFontStyle(Value fontStyle) {
        TextStyleSpecifier tss = new TextStyleSpecifier();
        tss.setFontStyle(fontStyle);
        return tss;
    }

    public static TextStyleSpecifier fromFontFamily(String fontFamily) {
        TextStyleSpecifier tss = new TextStyleSpecifier();
        tss.setFontFamily(fontFamily);
        return tss;
    }

    public static TextStyleSpecifier fromFontSize(int fontSize) {
        TextStyleSpecifier tss = new TextStyleSpecifier();
        tss.setFontSize(fontSize);
        return tss;
    }

    public static TextStyleSpecifier fromNameStyleSize(Value fontName, Value fontStyle, Value fontSize) {
        TextStyleSpecifier tss = new TextStyleSpecifier();

        tss.fontFamily = fontName.stringValue();
        tss.fontSize = fontSize.integerValue();
        tss.setFontStyle(fontStyle);

        return tss;
    }

    public static TextStyleSpecifier fromFont(Font font) {
        TextStyleSpecifier tss = new TextStyleSpecifier();

        tss.fontFamily = font.getFamily();
        tss.fontSize = font.getSize();
        tss.isBold = (font.getStyle() & Font.BOLD) != 0;
        tss.isItalic = (font.getStyle() & Font.ITALIC) != 0;

        return tss;
    }

    public static TextStyleSpecifier fromAttributeSet(AttributeSet as) {
        TextStyleSpecifier tss = new TextStyleSpecifier();

        tss.attributes = as;
        tss.fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
        tss.fontSize = Integer.valueOf(String.valueOf(as.getAttribute(StyleConstants.FontSize)));
        tss.isBold = Boolean.valueOf(String.valueOf(as.getAttribute(StyleConstants.Bold)));
        tss.isItalic = Boolean.valueOf(String.valueOf(as.getAttribute(StyleConstants.Italic)));
        tss.isUnderline = Boolean.valueOf(String.valueOf(as.getAttribute(StyleConstants.Underline)));
        tss.isStrikeThrough = Boolean.valueOf(String.valueOf(as.getAttribute(StyleConstants.StrikeThrough)));
        tss.isSubscript = Boolean.valueOf(String.valueOf(as.getAttribute(StyleConstants.Subscript)));
        tss.isSuperscript = Boolean.valueOf(String.valueOf(as.getAttribute(StyleConstants.Superscript)));

        return tss;
    }

    public Font toFont() {


        int fontStyle = Font.PLAIN;

        if (isBold) {
            fontStyle |= Font.BOLD;
        }

        if (isItalic) {
            fontStyle |= Font.ITALIC;
        }

        return FontFactory.byNameStyleSize(fontFamily, fontStyle, fontSize);
    }

    public Value getHyperTalkStyle() {
        StringBuilder id = new StringBuilder();
        if (isBold) {
            id.append(", bold");
        }
        if (isItalic) {
            id.append(", italic");
        }
        if (isUnderline) {
            id.append(", underline");
        }
        if (isSuperscript) {
            id.append(", superscript");
        }
        if (isSubscript) {
            id.append(", subscript");
        }
        if (isStrikeThrough) {
            id.append(", strikethrough");
        }

        if (id.length() == 0) {
            return new Value("plain");
        } else {
            id.delete(0, 1);
            return new Value(id.toString());
        }
    }

    public AttributeSet toAttributeSet() {
        SimpleAttributeSet sas = attributes == null ? new SimpleAttributeSet() : new SimpleAttributeSet(attributes);

        if (fontFamily != null) {
            sas.addAttribute(StyleConstants.FontFamily, fontFamily);
        }

        if (fontSize > 0) {
            sas.addAttribute(StyleConstants.FontSize, fontSize);
        }

        if (isBold) {
            sas.addAttribute(StyleConstants.Bold, true);
        } else {
            sas.removeAttribute(StyleConstants.Bold);
        }

        if (isItalic) {
            sas.addAttribute(StyleConstants.Italic, true);
        } else {
            sas.removeAttribute(StyleConstants.Italic);
        }

        if (isUnderline) {
            sas.addAttribute(StyleConstants.Underline, true);
        } else {
            sas.removeAttribute(StyleConstants.Underline);
        }

        if (isStrikeThrough) {
            sas.addAttribute(StyleConstants.StrikeThrough, true);
        } else {
            sas.removeAttribute(StyleConstants.StrikeThrough);
        }

        if (isSuperscript) {
            sas.addAttribute(StyleConstants.Superscript, true);
        } else {
            sas.removeAttribute(StyleConstants.Superscript);
        }

        if (isSubscript) {
            sas.addAttribute(StyleConstants.Subscript, true);
        } else {
            sas.removeAttribute(StyleConstants.Subscript);
        }

        return sas;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void toggleFontStyle(Value value) {
        String v = value.stringValue().toLowerCase();

        if (v.contains("bold")) {
            isBold = !isBold;
        }

        if (v.contains("italic")) {
            isItalic = !isItalic;
        }

        if (v.contains("underline")) {
            isUnderline = !isUnderline;
        }

        if (v.contains("strikethrough")) {
            isStrikeThrough = !isStrikeThrough;
        }

        if (v.contains("superscript")) {
            isSuperscript = !isSuperscript;
            isSubscript = false;
        }

        if (v.contains("subscript")) {
            isSubscript = !isSubscript;
            isSuperscript = false;
        }

        if (v.contains("plain")) {
            isBold = isItalic = isUnderline = isStrikeThrough = isSubscript = isSuperscript = false;
        }
    }

    public void setFontStyle(Value value) {
        String v = value.stringValue().toLowerCase();
        isBold = v.contains("bold");
        isItalic = v.contains("italic");
        isUnderline = v.contains("underline");
        isSubscript = v.contains("subscript");
        isSuperscript = v.contains("superscript");
        isStrikeThrough = v.contains("strikethrough");
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isPlain() {
        return !isBold() && !isItalic() && !isUnderline() && !isStrikeThrough() && !isSuperscript() && !isSubscript();
    }

    public boolean isBold() {
        return isBold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public boolean isStrikeThrough() {
        return isStrikeThrough;
    }

    public boolean isSuperscript() {
        return isSuperscript;
    }

    public boolean isSubscript() {
        return isSubscript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextStyleSpecifier that = (TextStyleSpecifier) o;

        if (fontSize != that.fontSize) return false;
        if (isBold != that.isBold) return false;
        if (isItalic != that.isItalic) return false;
        if (isUnderline != that.isUnderline) return false;
        if (isStrikeThrough != that.isStrikeThrough) return false;
        if (isSuperscript != that.isSuperscript) return false;
        if (isSubscript != that.isSubscript) return false;
        return fontFamily != null ? fontFamily.equals(that.fontFamily) : that.fontFamily == null;
    }
}
