package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.DefaultWyldCardProperties;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ResetPaintCmd extends Command {

    public ResetPaintCmd(ParserRuleContext context) {
        super(context, "reset");
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        WyldCard.getInstance().getToolsManager().setSelectedBrush(PaintBrush.ROUND_12X12);
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_CENTERED, new Value(false));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_FILLED, new Value(false));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_GRID, new Value(false));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_LINESIZE, new Value(1));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_MULTIPLE, new Value(false));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_PATTERN, new Value(12));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_POLYSIDES, new Value(4));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_TEXTFONT, new Value("Geneva"));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_TEXTSIZE, new Value(12));
        WyldCard.getInstance().getWyldCardProperties().setKnownProperty(context, DefaultWyldCardProperties.PROP_TEXTSTYLE, new Value("plain"));
    }
}
