package hypertalk;

import hypertalk.ast.common.*;
import hypertalk.ast.constructs.*;
import hypertalk.ast.containers.*;
import hypertalk.ast.expressions.*;
import hypertalk.ast.functions.*;
import hypertalk.ast.statements.*;
import hypertalk.exception.HtParseError;
import hypertalk.parser.HyperTalkBaseVisitor;
import hypertalk.parser.HyperTalkParser;
import hypertalk.parser.HyperTalkVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

public class HyperTalkTreeVisitor extends HyperTalkBaseVisitor<Object> {
//public class HyperTalkTreeVisitor extends HyperTalkVisitor<Object> {

    @Override
    public Object visitHandlerScript(HyperTalkParser.HandlerScriptContext ctx) {
        Script script = new Script();
        script.defineHandler((NamedBlock) visit(ctx.handler()));
        return script;
    }

    @Override
    public Object visitWaitCmdStmnt(HyperTalkParser.WaitCmdStmntContext ctx) {
        return visit(ctx.waitCmd());
    }

    @Override
    public Object visitWaitCountCmd(HyperTalkParser.WaitCountCmdContext ctx) {
        return new StatWaitCmd((Expression) visit(ctx.factor()), (TimeUnit) visit(ctx.timeUnit()));
    }

    @Override
    public Object visitWaitUntilCmd(HyperTalkParser.WaitUntilCmdContext ctx) {
        return new StatWaitCmd((Expression) visit(ctx.expression()), true);
    }

    @Override
    public Object visitWaitWhileCmd(HyperTalkParser.WaitWhileCmdContext ctx) {
        return new StatWaitCmd((Expression) visit(ctx.expression()), false);
    }

    @Override
    public Object visitTicksTimeUnit(HyperTalkParser.TicksTimeUnitContext ctx) {
        return TimeUnit.TICKS;
    }

    @Override
    public Object visitSecondsTimeUnit(HyperTalkParser.SecondsTimeUnitContext ctx) {
        return TimeUnit.SECONDS;
    }

    @Override
    public Object visitGoCmdStmnt(HyperTalkParser.GoCmdStmntContext ctx) {
        return new StatGoCmd((Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitDestinationType(HyperTalkParser.DestinationTypeContext ctx) {
        return DestinationType.CARD;
    }

    @Override
    public Object visitNextPosition(HyperTalkParser.NextPositionContext ctx) {
        return Position.NEXT;
    }

    @Override
    public Object visitPrevPosition(HyperTalkParser.PrevPositionContext ctx) {
        return Position.PREV;
    }

    @Override
    public Object visitCardNumber(HyperTalkParser.CardNumberContext ctx) {
        return new Destination((Expression) visit(ctx.expression()), (DestinationType) visit(ctx.destinationType()));
    }

    @Override
    public Object visitCardOrdinal(HyperTalkParser.CardOrdinalContext ctx) {
        return new Destination((Ordinal) visit(ctx.ordinal()), (DestinationType) visit(ctx.destinationType()));
    }

    @Override
    public Object visitCardPosition(HyperTalkParser.CardPositionContext ctx) {
        return new Destination((Position) visit(ctx.position()), (DestinationType) visit(ctx.destinationType()));
    }

    @Override
    public Object visitPropertyDest(HyperTalkParser.PropertyDestContext ctx) {
        return new ContainerProperty((PropertySpecifier) visit(ctx.propertySpec()));
    }

    @Override
    public Object visitStatementScript(HyperTalkParser.StatementScriptContext ctx) {
        Script script = new Script();
        Statement statement = (Statement) visit(ctx.nonEmptyStmnt());
        script.defineStatementList(new StatementList(statement));
        return script;
    }

    @Override
    public Object visitScriptFunctionScript(HyperTalkParser.ScriptFunctionScriptContext ctx) {
        Script script = (Script) visit(ctx.script());
        script.defineUserFunction((UserFunction) visit(ctx.function()));
        return script;
    }

    @Override
    public Object visitFunctionScript(HyperTalkParser.FunctionScriptContext ctx) {
        Script script = new Script();
        script.defineUserFunction((UserFunction) visit(ctx.function()));
        return script;
    }

    @Override
    public Object visitScriptHandlerScript(HyperTalkParser.ScriptHandlerScriptContext ctx) {
        Script script = (Script) visit(ctx.script());
        script.defineHandler((NamedBlock) visit(ctx.handler()));
        return script;
    }

    @Override
    public Object visitCommentScript(HyperTalkParser.CommentScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitWhitespaceScript(HyperTalkParser.WhitespaceScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitEofScript(HyperTalkParser.EofScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitScriptNewlineScript(HyperTalkParser.ScriptNewlineScriptContext ctx) {
        return visit(ctx.script());
    }

    @Override
    public Object visitPopulatedHandler(HyperTalkParser.PopulatedHandlerContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on " + open + "' does not match 'end " + close + "'");
        }

        return new NamedBlock(open, (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitEmptyHandler(HyperTalkParser.EmptyHandlerContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on " + open + "' does not match 'end " + close + "'");
        }

        return new NamedBlock(open, new StatementList());
    }

    @Override
    public Object visitPopulatedFunction(HyperTalkParser.PopulatedFunctionContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on function " + open + "' does not match 'end " + close + "'");
        }

        return new UserFunction(open, (ParameterList) visit(ctx.parameterList()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitEmptyFunction(HyperTalkParser.EmptyFunctionContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on function " + open + "' does not match 'end " + close + "'");
        }

        return new UserFunction(open, (ParameterList) visit(ctx.parameterList()), new StatementList());
    }

    @Override
    public Object visitEmptyArgList(HyperTalkParser.EmptyArgListContext ctx) {
        return new ExpressionList();
    }

    @Override
    public Object visitSingleExpArgList(HyperTalkParser.SingleExpArgListContext ctx) {
        return new ExpressionList((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitMultiExpArgList(HyperTalkParser.MultiExpArgListContext ctx) {
        ExpressionList argumentList = (ExpressionList) visit(ctx.expressionList());
        argumentList.addArgument((Expression) visit(ctx.expression()));
        return argumentList;
    }

    @Override
    public Object visitEmptyParamList(HyperTalkParser.EmptyParamListContext ctx) {
        return new ParameterList();
    }

    @Override
    public Object visitSingleParamList(HyperTalkParser.SingleParamListContext ctx) {
        return new ParameterList((String) visit(ctx.ID()));
    }

    @Override
    public Object visitMultiParamList(HyperTalkParser.MultiParamListContext ctx) {
        ParameterList parameterList = (ParameterList) visit(ctx.parameterList());
        parameterList.addParameter((String) visit(ctx.ID()));
        return parameterList;
    }

    @Override
    public Object visitSingleStmntList(HyperTalkParser.SingleStmntListContext ctx) {
        return new StatementList((Statement) visit(ctx.nonEmptyStmnt()));
    }

    @Override
    public Object visitNewlineStmntList(HyperTalkParser.NewlineStmntListContext ctx) {
        return new StatementList();
    }

    @Override
    public Object visitMultiStmntList(HyperTalkParser.MultiStmntListContext ctx) {
        StatementList statementList = (StatementList) visit(ctx.statementList());
        statementList.append((Statement) visit(ctx.nonEmptyStmnt()));
        return statementList;
    }

    @Override
    public Object visitStmntListNewlineStmntList(HyperTalkParser.StmntListNewlineStmntListContext ctx) {
        return visit(ctx.statementList());
    }

    @Override
    public Object visitNonEmptyCommandStmnt(HyperTalkParser.NonEmptyCommandStmntContext ctx) {
        return visit(ctx.commandStmnt());
    }

    @Override
    public Object visitNonEmptyGlobalStmnt(HyperTalkParser.NonEmptyGlobalStmntContext ctx) {
        return visit(ctx.globalStmnt());
    }

    @Override
    public Object visitNonEmptyIfStmnt(HyperTalkParser.NonEmptyIfStmntContext ctx) {
        return visit(ctx.ifStatement());
    }

    @Override
    public Object visitNonEmptyRepeatStmnt(HyperTalkParser.NonEmptyRepeatStmntContext ctx) {
        return visit(ctx.repeatStatement());
    }

    @Override
    public Object visitNonEmptyDoStmnt(HyperTalkParser.NonEmptyDoStmntContext ctx) {
        return visit(ctx.doStmnt());
    }

    @Override
    public Object visitNonEmptyReturnStmnt(HyperTalkParser.NonEmptyReturnStmntContext ctx) {
        return visit(ctx.returnStmnt());
    }

    @Override
    public Object visitNonEmptyExpStmnt(HyperTalkParser.NonEmptyExpStmntContext ctx) {
        return new StatExp((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitVoidReturnStmnt(HyperTalkParser.VoidReturnStmntContext ctx) {
        return new StatReturn();
    }

    @Override
    public Object visitEprReturnStmnt(HyperTalkParser.EprReturnStmntContext ctx) {
        return new StatReturn((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitDoStmnt(HyperTalkParser.DoStmntContext ctx) {
        return new StatDo((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitAnswerCmdStmnt(HyperTalkParser.AnswerCmdStmntContext ctx) {
        return visit(ctx.answerCmd());
    }

    @Override
    public Object visitAskCmdStmnt(HyperTalkParser.AskCmdStmntContext ctx) {
        return visit(ctx.askCmd());
    }

    @Override
    public Object visitPutCmdStmnt(HyperTalkParser.PutCmdStmntContext ctx) {
        return visit(ctx.putCmd());
    }

    @Override
    public Object visitGetCmdStmnt(HyperTalkParser.GetCmdStmntContext ctx) {
        return visit(ctx.getCmd());
    }

    @Override
    public Object visitSetCmdStmnt(HyperTalkParser.SetCmdStmntContext ctx) {
        return visit(ctx.setCmd());
    }

    @Override
    public Object visitSendCmdStmnt(HyperTalkParser.SendCmdStmntContext ctx) {
        return visit(ctx.sendCmd());
    }

    @Override
    public Object visitAddCmdStmnt(HyperTalkParser.AddCmdStmntContext ctx) {
        return new StatAddCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitSubtractCmdStmnt(HyperTalkParser.SubtractCmdStmntContext ctx) {
        return new StatSubtractCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitMultiplyCmdStmnt(HyperTalkParser.MultiplyCmdStmntContext ctx) {
        return new StatMultiplyCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitDivideCmdStmnt(HyperTalkParser.DivideCmdStmntContext ctx) {
        return new StatDivideCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitChooseToolCmdStmt(HyperTalkParser.ChooseToolCmdStmtContext ctx) {
        return new StatChooseCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitChooseToolNumberCmdStmt(HyperTalkParser.ChooseToolNumberCmdStmtContext ctx) {
        return new StatChooseCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitClickCmdStmt(HyperTalkParser.ClickCmdStmtContext ctx) {
        return new StatClickCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitClickWithKeyCmdStmt(HyperTalkParser.ClickWithKeyCmdStmtContext ctx) {
        return new StatClickCmd((Expression) visit(ctx.expression()), (ExpressionList) visit(ctx.expressionList()));
    }

    @Override
    public Object visitDragCmdStmt(HyperTalkParser.DragCmdStmtContext ctx) {
        return new StatDragCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitDragWithKeyCmdStmt(HyperTalkParser.DragWithKeyCmdStmtContext ctx) {
        return new StatDragCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (ExpressionList) visit(ctx.expressionList()));
    }

    @Override
    public Object visitIfThenSingleLine(HyperTalkParser.IfThenSingleLineContext ctx) {
        return new StatIf((Expression) visit(ctx.expression()), (ThenElseBlock) visit(ctx.singleThen()));
    }

    @Override
    public Object visitIfThenMultiline(HyperTalkParser.IfThenMultilineContext ctx) {
        return new StatIf((Expression) visit(ctx.expression()), (ThenElseBlock) visit(ctx.multiThen()));
    }

    @Override
    public Object visitSingleThenNewlineElse(HyperTalkParser.SingleThenNewlineElseContext ctx) {
        return new ThenElseBlock(new StatementList((Statement) visit(ctx.nonEmptyStmnt())), (StatementList) visit(ctx.elseBlock()));
    }

    @Override
    public Object visitSingleThenElse(HyperTalkParser.SingleThenElseContext ctx) {
        return new ThenElseBlock(new StatementList((Statement) visit(ctx.nonEmptyStmnt())), (StatementList) visit(ctx.elseBlock()));
    }

    @Override
    public Object visitSingleThenNoElse(HyperTalkParser.SingleThenNoElseContext ctx) {
        return new ThenElseBlock(new StatementList((Statement) visit(ctx.nonEmptyStmnt())), new StatementList());
    }

    @Override
    public Object visitEmptyElse(HyperTalkParser.EmptyElseContext ctx) {
        return new ThenElseBlock((StatementList) visit(ctx.statementList()), new StatementList());
    }

    @Override
    public Object visitEmptyThenEmptyElse(HyperTalkParser.EmptyThenEmptyElseContext ctx) {
        return new ThenElseBlock(new StatementList(), new StatementList());
    }

    @Override
    public Object visitThenElse(HyperTalkParser.ThenElseContext ctx) {
        return new ThenElseBlock((StatementList) visit(ctx.statementList()), (StatementList) visit(ctx.elseBlock()));
    }

    @Override
    public Object visitElseStmntBlock(HyperTalkParser.ElseStmntBlockContext ctx) {
        return new StatementList((Statement) visit(ctx.nonEmptyStmnt()));
    }

    @Override
    public Object visitElseStmntListBlock(HyperTalkParser.ElseStmntListBlockContext ctx) {
        return visit(ctx.statementList());
    }

    @Override
    public Object visitElseEmptyBlock(HyperTalkParser.ElseEmptyBlockContext ctx) {
        return new StatementList();
    }

    @Override
    public Object visitRepeatStmntList(HyperTalkParser.RepeatStmntListContext ctx) {
        return new StatRepeat((RepeatSpecifier) visit(ctx.repeatRange()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitRepeatEmpty(HyperTalkParser.RepeatEmptyContext ctx) {
        return new StatRepeat((RepeatSpecifier) visit(ctx.repeatRange()), new StatementList());
    }

    @Override
    public Object visitInfiniteLoop(HyperTalkParser.InfiniteLoopContext ctx) {
        return new RepeatForever();
    }

    @Override
    public Object visitDurationLoop(HyperTalkParser.DurationLoopContext ctx) {
        return visit(ctx.duration());
    }

    @Override
    public Object visitCountLoop(HyperTalkParser.CountLoopContext ctx) {
        return visit(ctx.count());
    }

    @Override
    public Object visitWithLoop(HyperTalkParser.WithLoopContext ctx) {
        return new RepeatWith((String) visit(ctx.ID()), (RepeatRange) visit(ctx.range()));
    }

    @Override
    public Object visitUntilDuration(HyperTalkParser.UntilDurationContext ctx) {
        return new RepeatDuration(RepeatDuration.POLARITY_UNTIL, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitWhileDuration(HyperTalkParser.WhileDurationContext ctx) {
        return new RepeatDuration(RepeatDuration.POLARITY_WHILE, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitCount(HyperTalkParser.CountContext ctx) {
        return new RepeatCount((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitRangeDownTo(HyperTalkParser.RangeDownToContext ctx) {
        return new RepeatRange(RepeatRange.POLARITY_DOWNTO, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitRangeUpTo(HyperTalkParser.RangeUpToContext ctx) {
        return new RepeatRange(RepeatRange.POLARITY_UPTO, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitGlobalStmnt(HyperTalkParser.GlobalStmntContext ctx) {
        return new StatGlobal((String) visit(ctx.ID()));
    }

    @Override
    public Object visitAnswerThreeButtonCmd(HyperTalkParser.AnswerThreeButtonCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (Expression) visit(ctx.expression(2)), (Expression) visit(ctx.expression(3)));
    }

    @Override
    public Object visitAnswerTwoButtonCmd(HyperTalkParser.AnswerTwoButtonCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (Expression) visit(ctx.expression(2)));
    }

    @Override
    public Object visitAnswerOneButtonCmd(HyperTalkParser.AnswerOneButtonCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAnswerDefaultCmd(HyperTalkParser.AnswerDefaultCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitAskExpWithCmd(HyperTalkParser.AskExpWithCmdContext ctx) {
        return new StatAskCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAskExpCmd(HyperTalkParser.AskExpCmdContext ctx) {
        return new StatAskCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitPutIntoCmd(HyperTalkParser.PutIntoCmdContext ctx) {
        return new StatPutCmd((Expression) visit(ctx.expression()), Preposition.INTO, (Container) visit(ctx.container()));
    }

    @Override
    public Object visitPutPrepositionCmd(HyperTalkParser.PutPrepositionCmdContext ctx) {
        return new StatPutCmd((Expression) visit(ctx.expression()), (Preposition) visit(ctx.preposition()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitGetCmd(HyperTalkParser.GetCmdContext ctx) {
        return new StatGetCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSetCmd(HyperTalkParser.SetCmdContext ctx) {

        return new StatSetCmd((PropertySpecifier) visit(ctx.propertySpec()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSendCmd(HyperTalkParser.SendCmdContext ctx) {
        return new StatSendCmd((ExpPart) visit(ctx.part()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitBeforePreposition(HyperTalkParser.BeforePrepositionContext ctx) {
        return Preposition.BEFORE;
    }

    @Override
    public Object visitAfterPreposition(HyperTalkParser.AfterPrepositionContext ctx) {
        return Preposition.AFTER;
    }

    @Override
    public Object visitIntoPreposition(HyperTalkParser.IntoPrepositionContext ctx) {
        return Preposition.INTO;
    }

    @Override
    public Object visitVariableDest(HyperTalkParser.VariableDestContext ctx) {
        return new ContainerVariable((String) visit(ctx.ID()));
    }

    @Override
    public Object visitCompositeChunk(HyperTalkParser.CompositeChunkContext ctx) {
        Chunk lChunk = (Chunk) visit(ctx.chunk(0));
        Chunk rChunk = (Chunk) visit(ctx.chunk(1));
        return new CompositeChunk(rChunk.type, rChunk.start, rChunk.end, lChunk);
    }

    @Override
    public Object visitChunkVariableDest(HyperTalkParser.ChunkVariableDestContext ctx) {
        return new ContainerVariable((String) visit(ctx.ID()), (Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitPartDest(HyperTalkParser.PartDestContext ctx) {
        return new ContainerPart((ExpPart) visit(ctx.part()));
    }

    @Override
    public Object visitChunkPartDest(HyperTalkParser.ChunkPartDestContext ctx) {
        return new ContainerPart((ExpPart) visit(ctx.part()), (Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitMessageDest(HyperTalkParser.MessageDestContext ctx) {
        return new ContainerMsgBox();
    }

    @Override
    public Object visitChunkMessageDest(HyperTalkParser.ChunkMessageDestContext ctx) {
        return new ContainerMsgBox((Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitChunkDest(HyperTalkParser.ChunkDestContext ctx) {
        return new ContainerMsgBox((Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitDefaultDest(HyperTalkParser.DefaultDestContext ctx) {
        return new ContainerMsgBox();
    }

    @Override
    public Object visitPropertySpecGlobal(HyperTalkParser.PropertySpecGlobalContext ctx) {
        return new PropertySpecifier((String) visit(ctx.ID()));
    }

    @Override
    public Object visitPropertySpecPart(HyperTalkParser.PropertySpecPartContext ctx) {
        return new PropertySpecifier((String) visit(ctx.ID()), (ExpPart) visit(ctx.part()));
    }

    @Override
    public Object visitFieldPart(HyperTalkParser.FieldPartContext ctx) {
        return new ExpPartName(PartType.FIELD, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitFieldIdPart(HyperTalkParser.FieldIdPartContext ctx) {
        return new ExpPartId(PartType.FIELD, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitButtonPart(HyperTalkParser.ButtonPartContext ctx) {
        return new ExpPartName(PartType.BUTTON, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitButtonIdPart(HyperTalkParser.ButtonIdPartContext ctx) {
        return new ExpPartId(PartType.BUTTON, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitMePart(HyperTalkParser.MePartContext ctx) {
        return new ExpPartMe();
    }

    @Override
    public Object visitOrdinalCharChunk(HyperTalkParser.OrdinalCharChunkContext ctx) {
        return new Chunk(ChunkType.CHAR, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeCharChunk(HyperTalkParser.RangeCharChunkContext ctx) {
        return new Chunk(ChunkType.CHARRANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitCharCharChunk(HyperTalkParser.CharCharChunkContext ctx) {
        return new Chunk(ChunkType.CHAR, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrdinalWordChunk(HyperTalkParser.OrdinalWordChunkContext ctx) {
        return new Chunk(ChunkType.WORD, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeWordChunk(HyperTalkParser.RangeWordChunkContext ctx) {
        return new Chunk(ChunkType.WORDRANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitWordWordChunk(HyperTalkParser.WordWordChunkContext ctx) {
        return new Chunk(ChunkType.WORD, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrdinalItemChunk(HyperTalkParser.OrdinalItemChunkContext ctx) {
        return new Chunk(ChunkType.ITEM, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeItemChunk(HyperTalkParser.RangeItemChunkContext ctx) {
        return new Chunk(ChunkType.ITEMRANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitItemItemChunk(HyperTalkParser.ItemItemChunkContext ctx) {
        return new Chunk(ChunkType.ITEM, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrdinalLineChunk(HyperTalkParser.OrdinalLineChunkContext ctx) {
        return new Chunk(ChunkType.LINE, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeLineChunk(HyperTalkParser.RangeLineChunkContext ctx) {
        return new Chunk(ChunkType.LINERANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitLineLineChunk(HyperTalkParser.LineLineChunkContext ctx) {
        return new Chunk(ChunkType.LINE, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitFirstOrd(HyperTalkParser.FirstOrdContext ctx) {
        return Ordinal.FIRST;
    }

    @Override
    public Object visitSecondOrd(HyperTalkParser.SecondOrdContext ctx) {
        return Ordinal.SECOND;
    }

    @Override
    public Object visitThirdOrd(HyperTalkParser.ThirdOrdContext ctx) {
        return Ordinal.THIRD;
    }

    @Override
    public Object visitFourthOrd(HyperTalkParser.FourthOrdContext ctx) {
        return Ordinal.FOURTH;
    }

    @Override
    public Object visitFifthOrd(HyperTalkParser.FifthOrdContext ctx) {
        return Ordinal.FIFTH;
    }

    @Override
    public Object visitSixthOrd(HyperTalkParser.SixthOrdContext ctx) {
        return Ordinal.SIXTH;
    }

    @Override
    public Object visitSeventhOrd(HyperTalkParser.SeventhOrdContext ctx) {
        return Ordinal.SEVENTH;
    }

    @Override
    public Object visitEigthOrd(HyperTalkParser.EigthOrdContext ctx) {
        return Ordinal.EIGTH;
    }

    @Override
    public Object visitNinthOrd(HyperTalkParser.NinthOrdContext ctx) {
        return Ordinal.NINTH;
    }

    @Override
    public Object visitTenthOrd(HyperTalkParser.TenthOrdContext ctx) {
        return Ordinal.TENTH;
    }

    @Override
    public Object visitMidOrd(HyperTalkParser.MidOrdContext ctx) {
        return Ordinal.MIDDLE;
    }

    @Override
    public Object visitLastOrd(HyperTalkParser.LastOrdContext ctx) {
        return Ordinal.LAST;
    }

    @Override
    public Object visitMultiplicationExp(HyperTalkParser.MultiplicationExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitChunkExp(HyperTalkParser.ChunkExpContext ctx) {
        return new ExpChunk((Chunk) visit(ctx.chunk()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrExp(HyperTalkParser.OrExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.OR, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAndExp(HyperTalkParser.AndExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.AND, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitEqualityExp(HyperTalkParser.EqualityExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitCaratExp(HyperTalkParser.CaratExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.EXP, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitComparisonExp(HyperTalkParser.ComparisonExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAdditionExp(HyperTalkParser.AdditionExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitNegateExp(HyperTalkParser.NegateExpContext ctx) {
        return new ExpUnaryOperator(UnaryOperator.NEGATE, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitNotExp(HyperTalkParser.NotExpContext ctx) {
        return new ExpUnaryOperator(UnaryOperator.NOT, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitFactorExp(HyperTalkParser.FactorExpContext ctx) {
        return visit(ctx.factor());
    }

    @Override
    public Object visitFunctionExp(HyperTalkParser.FunctionExpContext ctx) {
        return new ExpUserFunction((String) visit(ctx.ID()), (ExpressionList) visit(ctx.expressionList()));
    }

    @Override
    public Object visitMouseFunc(HyperTalkParser.MouseFuncContext ctx) {
        return BuiltInFunction.MOUSE;
    }

    @Override
    public Object visitMouseLocFunc(HyperTalkParser.MouseLocFuncContext ctx) {
        return BuiltInFunction.MOUSELOC;
    }

    @Override
    public Object visitResultFunc(HyperTalkParser.ResultFuncContext ctx) {
        return BuiltInFunction.RESULT;
    }

    @Override
    public Object visitMessageFunc(HyperTalkParser.MessageFuncContext ctx) {
        return BuiltInFunction.MESSAGE;
    }

    @Override
    public Object visitTicksFunc(HyperTalkParser.TicksFuncContext ctx) {
        return BuiltInFunction.TICKS;
    }

    @Override
    public Object visitSecondsFunc(HyperTalkParser.SecondsFuncContext ctx) {
        return BuiltInFunction.SECONDS;
    }

    @Override
    public Object visitDateFormatFunc(HyperTalkParser.DateFormatFuncContext ctx) {
        switch ((DateFormat) visit(ctx.dateFormat())) {
            case LONG: return BuiltInFunction.LONG_DATE;
            case SHORT: return BuiltInFunction.SHORT_DATE;
            case ABBREVIATED: return BuiltInFunction.ABBREV_DATE;
            default: throw new RuntimeException("Bug! Unimplemented case.");
        }
    }

    @Override
    public Object visitTimeFormatFunc(HyperTalkParser.TimeFormatFuncContext ctx) {
        switch ((DateFormat) visit(ctx.dateFormat())){
            case LONG: return BuiltInFunction.LONG_TIME;
            case SHORT: return BuiltInFunction.SHORT_TIME;
            case ABBREVIATED: return BuiltInFunction.ABBREV_TIME;
            default: throw new RuntimeException("Bug! Unimplemented case.");
        }
    }

    @Override
    public Object visitLongDateFormat(HyperTalkParser.LongDateFormatContext ctx) {
        return DateFormat.LONG;
    }

    @Override
    public Object visitShortDateFormat(HyperTalkParser.ShortDateFormatContext ctx) {
        return DateFormat.SHORT;
    }

    @Override
    public Object visitAbbrevDateFormat(HyperTalkParser.AbbrevDateFormatContext ctx) {
        return DateFormat.ABBREVIATED;
    }

    @Override
    public Object visitAbbreviatedDateFormat(HyperTalkParser.AbbreviatedDateFormatContext ctx) {
        return DateFormat.ABBREVIATED;
    }

    @Override
    public Object visitDefaultDateFormat(HyperTalkParser.DefaultDateFormatContext ctx) {
        return DateFormat.SHORT;
    }

    @Override
    public Object visitLiteralFactor(HyperTalkParser.LiteralFactorContext ctx) {
        return new ExpLiteral(visit(ctx.literal()));
    }

    @Override
    public Object visitIdFactor(HyperTalkParser.IdFactorContext ctx) {
        return new ExpVariable((String) visit(ctx.ID()));
    }

    @Override
    public Object visitPartFactor(HyperTalkParser.PartFactorContext ctx) {
        return visit(ctx.part());
    }

    @Override
    public Object visitExpressionFactor(HyperTalkParser.ExpressionFactorContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitIdOfPartFactor(HyperTalkParser.IdOfPartFactorContext ctx) {
        return new ExpProperty((PropertySpecifier) visit(ctx.propertySpec()));
    }

    @Override
    public Object visitEmptyExp(HyperTalkParser.EmptyExpContext ctx) {
        return new ExpLiteral("");
    }

    @Override
    public Object visitConcatExp(HyperTalkParser.ConcatExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.expression(0)), BinaryOperator.CONCAT, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitStmntTerminator(HyperTalkParser.StmntTerminatorContext ctx) {
        return visit(ctx.NEWLINE());
    }

    @Override
    public Object visitWaitForCountCmd(HyperTalkParser.WaitForCountCmdContext ctx) {
        return new StatWaitCmd((Expression) visit(ctx.factor()), true);
    }

    @Override
    public Object visitTickTimeUnit(HyperTalkParser.TickTimeUnitContext ctx) {
        return TimeUnit.TICKS;
    }

    @Override
    public Object visitSecTimeUnit(HyperTalkParser.SecTimeUnitContext ctx) {
        return TimeUnit.SECONDS;
    }

    @Override
    public Object visitSecondTimeUnit(HyperTalkParser.SecondTimeUnitContext ctx) {
        return TimeUnit.SECONDS;
    }

    @Override
    public Object visitTheOrdinalVal(HyperTalkParser.TheOrdinalValContext ctx) {
        return visit(ctx.ordinalValue());
    }

    @Override
    public Object visitBuiltinFuncExp(HyperTalkParser.BuiltinFuncExpContext ctx) {
        return visit(ctx.builtinFunc());
    }

    @Override
    public Object visitBuiltinFuncOneArgs(HyperTalkParser.BuiltinFuncOneArgsContext ctx) {
        switch ((BuiltInFunction) visit(ctx.oneArgFunc())) {
            case MIN: return new ExpMinFun((Expression) visit(ctx.factor()));
            case MAX: return new ExpMaxFun((Expression) visit(ctx.factor()));
            case AVERAGE: return new ExpAverageFun((Expression) visit(ctx.factor()));
            case NUMBER_CHARS: return new ExpNumberOfFun(ChunkType.CHAR, (Expression) visit(ctx.factor()));
            case NUMBER_ITEMS: return new ExpNumberOfFun(ChunkType.ITEM, (Expression) visit(ctx.factor()));
            case NUMBER_LINES: return new ExpNumberOfFun(ChunkType.LINE, (Expression) visit(ctx.factor()));
            case NUMBER_WORDS: return new ExpNumberOfFun(ChunkType.WORD, (Expression) visit(ctx.factor()));
            case RANDOM: return new ExpRandomFun((Expression) visit(ctx.factor()));
            default: throw new RuntimeException("Bug! Unimplemented case.");
        }
    }

    @Override
    public Object visitBuiltinFuncNoArg(HyperTalkParser.BuiltinFuncNoArgContext ctx) {
        switch ((BuiltInFunction) visit(ctx.noArgFunc())) {
            case MOUSE: return new ExpMouseFun();
            case MOUSELOC: return new ExpMouseLocFun();
            case RESULT: return new ExpResultFun();
            case MESSAGE: return new ExpMessageBoxFun();
            case TICKS: return new ExpTicksFun();
            case SECONDS: return new ExpSecondsFun();
            case ABBREV_DATE: return new ExpDateFun(DateFormat.ABBREVIATED);
            case SHORT_DATE: return new ExpDateFun(DateFormat.SHORT);
            case LONG_DATE: return new ExpDateFun(DateFormat.LONG);
            case ABBREV_TIME: return new ExpTimeFun(DateFormat.ABBREVIATED);
            case LONG_TIME: return new ExpTimeFun(DateFormat.LONG);
            case SHORT_TIME: return new ExpTimeFun(DateFormat.SHORT);
            default: throw new RuntimeException("Bug! Unimplemented case.");
        }
    }

    @Override
    public Object visitBuiltinFuncArgList(HyperTalkParser.BuiltinFuncArgListContext ctx) {
        switch ((BuiltInFunction) visit(ctx.oneArgFunc())) {
            case MIN: return new ExpMinFun((ExpressionList) visit(ctx.expressionList()));
            case MAX: return new ExpMaxFun((ExpressionList) visit(ctx.expressionList()));
            case AVERAGE: return new ExpAverageFun((ExpressionList) visit(ctx.expressionList()));
            case RANDOM: return new ExpRandomFun((ExpressionList) visit(ctx.expressionList()));
            default: throw new RuntimeException("Bug! Unimplemented case.");
        }
    }

    @Override
    public Object visitAverageFunc(HyperTalkParser.AverageFuncContext ctx) {
        return BuiltInFunction.AVERAGE;
    }

    @Override
    public Object visitMinFunc(HyperTalkParser.MinFuncContext ctx) {
        return BuiltInFunction.MIN;
    }

    @Override
    public Object visitMaxFunc(HyperTalkParser.MaxFuncContext ctx) {
        return BuiltInFunction.MAX;
    }

    @Override
    public Object visitNumberOfCharsFunc(HyperTalkParser.NumberOfCharsFuncContext ctx) {
        return BuiltInFunction.NUMBER_CHARS;
    }

    @Override
    public Object visitNumberOfWordsFunc(HyperTalkParser.NumberOfWordsFuncContext ctx) {
        return BuiltInFunction.NUMBER_WORDS;
    }

    @Override
    public Object visitNumberOfItemsFunc(HyperTalkParser.NumberOfItemsFuncContext ctx) {
        return BuiltInFunction.NUMBER_ITEMS;
    }

    @Override
    public Object visitNumberOfLinesFunc(HyperTalkParser.NumberOfLinesFuncContext ctx) {
        return BuiltInFunction.NUMBER_LINES;
    }

    @Override
    public Object visitRandomFunc(HyperTalkParser.RandomFuncContext ctx) {
        return BuiltInFunction.RANDOM;
    }

    @Override
    public Object visitStringLiteral(HyperTalkParser.StringLiteralContext ctx) {
        String quotedLiteral = ctx.getText();

        if (!quotedLiteral.startsWith("\"") || !quotedLiteral.endsWith("\"")) {
            throw new IllegalStateException("Bug! No quotes around quoted literal.");
        }

        return new Value(String.valueOf(quotedLiteral.substring(1, quotedLiteral.length() - 1)));
    }

    @Override
    public Object visitDotNumberLiteral(HyperTalkParser.DotNumberLiteralContext ctx) {
        Object fractional = ctx.INTEGER_LITERAL().getText();
        return new Value("0." + String.valueOf(fractional));
    }

    @Override
    public Object visitNumberDotNumberLiteral(HyperTalkParser.NumberDotNumberLiteralContext ctx) {
        Object whole = ctx.INTEGER_LITERAL(0).getText();
        Object fractional = ctx.INTEGER_LITERAL(1).getText();
        return new Value(String.valueOf(whole) + "." + String.valueOf(fractional));
    }

    @Override
    public Object visitNumberDotLiteral(HyperTalkParser.NumberDotLiteralContext ctx) {
        return new Value(ctx.getText());
    }

    @Override
    public Object visitNumberLiteral(HyperTalkParser.NumberLiteralContext ctx) {
        return new Value(ctx.getText());
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        return node.getText();
    }
}
