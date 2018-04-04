package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ResetPaintCmd extends Command {

    public ResetPaintCmd(ParserRuleContext context) {
        super(context, "reset");
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Breakpoint {
        ToolsContext.getInstance().setSelectedBrush(PaintBrush.ROUND_12X12);
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_CENTERED, new Value(false));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_FILLED, new Value(false));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_GRID, new Value(false));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_LINESIZE, new Value(1));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_MULTIPLE, new Value(false));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_PATTERN, new Value(12));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_POLYSIDES, new Value(4));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_TEXTFONT, new Value("Geneva"));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_TEXTSIZE, new Value(12));
        HyperCardProperties.getInstance().setKnownProperty(context, HyperCardProperties.PROP_TEXTSTYLE, new Value("plain"));
    }
}
