package hypercard.parts.fields;

import javax.swing.text.AttributeSet;

public class FieldStyleSpan {
    public final int start;
    public final int length;
    public AttributeSet style;

    public FieldStyleSpan(int start, int length, AttributeSet style) {
        this.start = start;
        this.length = length;
        this.style = style;
    }
}
