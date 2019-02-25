package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.functions.*;
import org.antlr.v4.runtime.ParserRuleContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public enum BuiltInFunction {
    ABBREV_DATE, ABBREV_TIME, ABS, ANNUITY, ATAN, AVERAGE, CHAR_TO_NUM, CLICKCHUNK, CLICKH, CLICKLINE, CLICKLOC, CLICKTEXT, CLICKV, COMMAND_KEY, COMPOUND, COS, DESTINATION, DISK_SPACE, EXP,
    EXP1, EXP2, FOUNDFIELD, FOUNDCHUNK, FOUNDLINE, FOUNDTEXT, LENGTH, LN, LN1, LOG2, LONG_DATE, LONG_TIME, MAX, MENUS, MIN, MOUSE, MOUSEH, MOUSECLICK, MOUSELOC, MOUSEV,
    NUMBER_BKGNDS, NUMBER_BKGND_BUTTONS, NUMBER_BKGND_CARDS, NUMBER_BKGND_FIELDS, NUMBER_BKGND_PARTS, NUMBER_CARDS,
    NUMBER_CARD_BUTTONS, NUMBER_CARD_FIELDS, NUMBER_CARD_PARTS, NUMBER_CHARS, NUMBER_ITEMS, NUMBER_LINES,
    NUMBER_MARKED_CARDS, NUMBER_MENUITEMS, NUMBER_MENUS, NUMBER_OF_PART, NUMBER_WINDOWS, NUMBER_WORDS, NUM_TO_CHAR,
    SOUND, SYSTEMVERSION, OFFSET, OPTION_KEY, PARAM, PARAMS, PARAM_COUNT, RANDOM, RESULT, SCREENRECT, SECONDS, SELECTEDBUTTON, SELECTEDCHUNK, SELECTEDFIELD, SELECTEDLINE, SELECTEDLOC, SELECTEDTEXT, SHIFT_KEY, SHORT_DATE, SHORT_TIME, SIN,
    SPEECH, SQRT, STACKS, STACKSPACE, SUM, TAN, TARGET, TICKS, TOOL, TRUNC, VALUE, VOICES, WINDOWS;

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
            case NUMBER_CARD_PARTS:
                return new NumberOfFunc(ctx, Countable.CARD_PARTS);
            case NUMBER_BKGND_PARTS:
                return new NumberOfFunc(ctx, Countable.BKGND_PARTS);
            case NUMBER_CARD_BUTTONS:
                return new NumberOfFunc(ctx, Countable.CARD_BUTTONS);
            case NUMBER_BKGND_BUTTONS:
                return new NumberOfFunc(ctx, Countable.BKGND_BUTTONS);
            case NUMBER_CARD_FIELDS:
                return new NumberOfFunc(ctx, Countable.CARD_FIELDS);
            case NUMBER_BKGND_FIELDS:
                return new NumberOfFunc(ctx, Countable.BKGND_FIELDS);
            case NUMBER_MENUS:
                return new NumberOfFunc(ctx, Countable.MENUS);
            case NUMBER_CARDS:
                return new NumberOfFunc(ctx, Countable.CARDS);
            case NUMBER_MARKED_CARDS:
                return new NumberOfFunc(ctx, Countable.MARKED_CARDS);
            case NUMBER_BKGNDS:
                return new NumberOfFunc(ctx, Countable.BKGNDS);
            case NUMBER_WINDOWS:
                return new NumberOfFunc(ctx, Countable.WINDOWS);
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
            case CLICKLOC:
                return new ClickLocFunc(ctx);
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
            case SOUND:
                return new SoundFunc(ctx);
            case SYSTEMVERSION:
                return new SystemVersionFunc(ctx);

            case CLICKLINE:
            case CLICKCHUNK:
            case DESTINATION:
            case SELECTEDBUTTON:
            case SELECTEDLOC:
            case STACKSPACE:
                throw new NotImplementedException();
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
            case NUMBER_CHARS:
                return new NumberOfFunc(ctx, Countable.CHAR, arg);
            case NUMBER_ITEMS:
                return new NumberOfFunc(ctx, Countable.ITEM, arg);
            case NUMBER_LINES:
                return new NumberOfFunc(ctx, Countable.LINE, arg);
            case NUMBER_WORDS:
                return new NumberOfFunc(ctx, Countable.WORD, arg);
            case NUMBER_MENUITEMS:
                return new NumberOfFunc(ctx, Countable.MENU_ITEMS, arg);
            case NUMBER_OF_PART:
                return new NumberOfPartFunc(ctx, arg);
            case NUMBER_CARDS:
                return new NumberOfFunc(ctx, Countable.CARDS, arg);
            case NUMBER_CARD_PARTS:
                return new NumberOfFunc(ctx, Countable.CARD_PARTS, arg);
            case NUMBER_BKGND_PARTS:
                return new NumberOfFunc(ctx, Countable.BKGND_PARTS, arg);
            case NUMBER_CARD_BUTTONS:
                return new NumberOfFunc(ctx, Countable.CARD_BUTTONS, arg);
            case NUMBER_BKGND_BUTTONS:
                return new NumberOfFunc(ctx, Countable.BKGND_BUTTONS, arg);
            case NUMBER_CARD_FIELDS:
                return new NumberOfFunc(ctx, Countable.CARD_FIELDS, arg);
            case NUMBER_BKGND_FIELDS:
                return new NumberOfFunc(ctx, Countable.BKGND_FIELDS, arg);
            case NUMBER_MARKED_CARDS:
                return new NumberOfFunc(ctx, Countable.MARKED_CARDS, arg);
            case NUMBER_BKGNDS:
                return new NumberOfFunc(ctx, Countable.BKGNDS, arg);
            case RANDOM:
                return new RandomFunc(ctx, arg);
            case CHAR_TO_NUM:
                return new CharToNumFunc(ctx, arg);
            case VALUE:
                return new ValueFunc(ctx, arg);
            case LENGTH:
                return new NumberOfFunc(ctx, Countable.CHAR, arg);
            case DISK_SPACE:
                return new DiskSpaceFunc(ctx, arg);
            case PARAM:
                return new ParamFunc(ctx, arg);
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