package hypercard.paint.patterns;

import java.awt.*;

public enum BasicBrush {

    SQUARE_16X16(16, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER),
    SQUARE_12X12(12, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER),
    SQUARE_8X8(8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER),
    SQUARE_4X4(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER),
    ROUND_16X16(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
    ROUND_12X12(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
    ROUND_8X8(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
    ROUND_4X4(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
    LINE_16(16, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL),
    LINE_12(12, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL),
    LINE_8(8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL),
    LINE_4(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

    public final Stroke stroke;

    BasicBrush(int size, int cap, int join) {
        this.stroke = new BasicStroke(size, cap, join);
    }
}
