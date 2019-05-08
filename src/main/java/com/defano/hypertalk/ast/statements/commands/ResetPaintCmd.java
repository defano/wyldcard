package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.parts.wyldcard.WyldCardProperties;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ResetPaintCmd extends Command {

    public ResetPaintCmd(ParserRuleContext context) {
        super(context, "reset");
    }

    @Override
    protected void onExecute(ExecutionContext context) {
        WyldCard.getInstance().getPaintManager().setSelectedBrush(PaintBrush.ROUND_12X12);
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_CENTERED, new Value(false));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_FILLED, new Value(false));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_GRID, new Value(false));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_LINESIZE, new Value(1));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_MULTIPLE, new Value(false));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_PATTERN, new Value(12));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_POLYSIDES, new Value(4));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_TEXTFONT, new Value("Geneva"));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_TEXTSIZE, new Value(12));
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardProperties.PROP_TEXTSTYLE, new Value("plain"));
    }
}
