package com.defano.hypertalk.ast.model.enums;

import com.defano.hypertalk.ast.expression.CountableExp;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.function.*;
import org.antlr.v4.runtime.ParserRuleContext;

public enum BuiltInFunction {
    ABBREV_DATE, ABBREV_TIME, ABS, ANNUITY, ATAN, AVERAGE, CHAR_TO_NUM, CLICKCHUNK, CLICKH, CLICKLINE, CLICKLOC,
    CLICKTEXT, CLICKV, COMMAND_KEY, COMPOUND, COS, DESTINATION, DISK_SPACE, EXP, EXP1, EXP2, FOUNDFIELD, FOUNDCHUNK,
    FOUNDLINE, FOUNDTEXT, LENGTH, LN, LN1, LOG2, LONG_DATE, LONG_TIME, MAX, MENUS, MIN, MOUSE, MOUSEH, MOUSECLICK,
    MOUSELOC, MOUSEV, NUM_TO_CHAR, NUMBER, SOUND, SYSTEMVERSION, OFFSET, OPTION_KEY, PARAM, PARAMS, PARAM_COUNT, RANDOM,
    RESULT, SCREENRECT, SECONDS, SELECTEDBUTTON, SELECTEDCHUNK, SELECTEDFIELD, SELECTEDLINE, SELECTEDLOC, SELECTEDTEXT,
    SHIFT_KEY, SHORT_DATE, SHORT_TIME, SIN, SPEECH, SQRT, STACKS, STACKSPACE, SUM, TAN, TARGET, TICKS, TOOL, TRUNC,
    VALUE, VOICES, WINDOWS, ROUND;

    public Object asListFunction(ParserRuleContext ctx, Expression listArg) {
        switch (this) {
            case MIN:
                return new MinFunc(ctx, listArg);
            case MAX:
                return new MaxFunc(ctx, listArg);
            case SUM:
                return new SumFunc(ctx, listArg);
            case AVERAGE:
                return new AverageFunc(ctx, listArg);
            case RANDOM:
                return new RandomFunc(ctx, listArg);
            case ANNUITY:
                return new AnnuityFunc(ctx, listArg);
            case COMPOUND:
                return new CompoundFunc(ctx, listArg);
            case OFFSET:
                return new OffsetFunc(ctx, listArg);
        }

        throw new IllegalStateException("Bug! Not a known list-argument function: " + this);
    }

    public Object asNoArgumentFunction(ParserRuleContext ctx) {
        switch (this) {
            case MOUSE:
                return new MouseFunc(ctx);
            case MOUSELOC:
                return new MouseLocFunc(ctx);
            case RESULT:
                return new ResultFunc(ctx);
            case TICKS:
                return new TicksFunc(ctx);
            case SECONDS:
                return new SecondsFunc(ctx);
            case ABBREV_DATE:
                return new DateFunc(ctx, LengthAdjective.ABBREVIATED);
            case SHORT_DATE:
                return new DateFunc(ctx, LengthAdjective.SHORT);
            case LONG_DATE:
                return new DateFunc(ctx, LengthAdjective.LONG);
            case ABBREV_TIME:
                return new TimeFunc(ctx, LengthAdjective.ABBREVIATED);
            case LONG_TIME:
                return new TimeFunc(ctx, LengthAdjective.LONG);
            case SHORT_TIME:
                return new TimeFunc(ctx, LengthAdjective.SHORT);
            case OPTION_KEY:
                return new ModifierKeyFunc(ctx, ModifierKey.OPTION);
            case COMMAND_KEY:
                return new ModifierKeyFunc(ctx, ModifierKey.COMMAND);
            case SHIFT_KEY:
                return new ModifierKeyFunc(ctx, ModifierKey.SHIFT);
            case TOOL:
                return new ToolFunc(ctx);
            case MENUS:
                return new MenusFunc(ctx);
            case DISK_SPACE:
                return new DiskSpaceFunc(ctx);
            case PARAM_COUNT:
                return new ParamCountFunc(ctx);
            case PARAMS:
                return new ParamsFunc(ctx);
            case TARGET:
                return new TargetFunc(ctx);
            case SPEECH:
                return new SpeechFunc(ctx);
            case VOICES:
                return new VoicesFunc(ctx);
            case MOUSECLICK:
                return new MouseClickFunc(ctx);
            case WINDOWS:
                return new WindowsFunc(ctx);
            case STACKS:
                return new StacksFunc(ctx);
            case CLICKH:
                return new ClickHFunc(ctx);
            case CLICKV:
                return new ClickVFunc(ctx);
            case CLICKCHUNK:
                return new ClickChunkFunc(ctx);
            case CLICKLOC:
                return new ClickLocFunc(ctx);
            case CLICKLINE:
                return new ClickLineFunc(ctx);
            case CLICKTEXT:
                return new ClickTextFunc(ctx);
            case FOUNDCHUNK:
                return new FoundChunkFunc(ctx);
            case FOUNDFIELD:
                return new FoundFieldFunc(ctx);
            case FOUNDLINE:
                return new FoundLineFunc(ctx);
            case FOUNDTEXT:
                return new FoundTextFunc(ctx);
            case MOUSEH:
                return new MouseHFunc(ctx);
            case MOUSEV:
                return new MouseVFunc(ctx);
            case SCREENRECT:
                return new ScreenRectFunc(ctx);
            case SELECTEDCHUNK:
                return new SelectedChunkFunc(ctx);
            case SELECTEDLINE:
                return new SelectedLineFunc(ctx);
            case SELECTEDFIELD:
                return new SelectedFieldFunc(ctx);
            case SELECTEDTEXT:
                return new SelectedTextFunc(ctx);
            case SELECTEDLOC:
                return new SelectedLocFunc(ctx);
            case SOUND:
                return new SoundFunc(ctx);
            case SYSTEMVERSION:
                return new SystemVersionFunc(ctx);

            case DESTINATION:
            case SELECTEDBUTTON:
            case STACKSPACE:
                throw new IllegalStateException("Bug! Not implemented.");
        }

        throw new IllegalStateException("Bug! Not a known no-argument function: " + this);
    }

    public Object asSingleArgumentFunction(ParserRuleContext ctx, Expression arg) {
        switch (this) {
            case MIN:
                return new MinFunc(ctx, arg);
            case MAX:
                return new MaxFunc(ctx, arg);
            case SUM:
                return new SumFunc(ctx, arg);
            case AVERAGE:
                return new AverageFunc(ctx, arg);
            case RANDOM:
                return new RandomFunc(ctx, arg);
            case CHAR_TO_NUM:
                return new CharToNumFunc(ctx, arg);
            case VALUE:
                return new ValueFunc(ctx, arg);
            case LENGTH:
                return new NumberFunc(ctx, new CountableExp(ctx, Countable.CHARS_OF, arg));
            case DISK_SPACE:
                return new DiskSpaceFunc(ctx, arg);
            case PARAM:
                return new ParamFunc(ctx, arg);
            case NUMBER:
                return new NumberFunc(ctx, arg);
            case ROUND:
                return new RoundFunc(ctx, arg);
            case SQRT:
            case TRUNC:
            case SIN:
            case COS:
            case TAN:
            case ATAN:
            case EXP:
            case EXP1:
            case EXP2:
            case LN:
            case LN1:
            case LOG2:
            case ABS:
            case NUM_TO_CHAR:
                return new MathFunc(ctx, this, arg);
        }

        throw new IllegalStateException("Bug! Not a known single-argument function: " + this);
    }
}