package hypertalk;

import hypertalk.ast.common.BinaryOperator;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.ChunkType;
import hypertalk.ast.common.DateFormat;
import hypertalk.ast.common.NamedBlock;
import hypertalk.ast.common.Ordinal;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.UnaryOperator;
import hypertalk.ast.common.Value;
import hypertalk.ast.constructs.RepeatCount;
import hypertalk.ast.constructs.RepeatDuration;
import hypertalk.ast.constructs.RepeatForever;
import hypertalk.ast.constructs.RepeatRange;
import hypertalk.ast.constructs.RepeatSpecifier;
import hypertalk.ast.constructs.RepeatWith;
import hypertalk.ast.constructs.ThenElseBlock;
import hypertalk.ast.containers.Destination;
import hypertalk.ast.containers.DestinationMsgBox;
import hypertalk.ast.containers.DestinationPart;
import hypertalk.ast.containers.DestinationVariable;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.ExpBinaryOperator;
import hypertalk.ast.expressions.ExpChunk;
import hypertalk.ast.expressions.ExpLiteral;
import hypertalk.ast.expressions.ExpPart;
import hypertalk.ast.expressions.ExpPartId;
import hypertalk.ast.expressions.ExpPartMe;
import hypertalk.ast.expressions.ExpPartName;
import hypertalk.ast.expressions.ExpProperty;
import hypertalk.ast.expressions.ExpUnaryOperator;
import hypertalk.ast.expressions.ExpUserFunction;
import hypertalk.ast.expressions.ExpVariable;
import hypertalk.ast.expressions.Expression;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.ExpAverageFun;
import hypertalk.ast.functions.ExpDateFun;
import hypertalk.ast.functions.ExpMaxFun;
import hypertalk.ast.functions.ExpMessageBoxFun;
import hypertalk.ast.functions.ExpMinFun;
import hypertalk.ast.functions.ExpMouseFun;
import hypertalk.ast.functions.ExpMouseLocFun;
import hypertalk.ast.functions.ExpNumberOfFun;
import hypertalk.ast.functions.ExpResultFun;
import hypertalk.ast.functions.ExpSecondsFun;
import hypertalk.ast.functions.ExpTicksFun;
import hypertalk.ast.functions.ExpTimeFun;
import hypertalk.ast.functions.ParameterList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.*;
import hypertalk.exception.HtParseError;
import hypertalk.parser.HypertalkBaseVisitor;
import hypertalk.parser.HypertalkParser;

import hypertalk.parser.HypertalkVisitor;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;

public class HypertalkTreeVisitor extends HypertalkBaseVisitor<Object> {
//public class HypertalkTreeVisitor implements HypertalkVisitor<Object> {
    @Override
    public Object visitHandlerScript(HypertalkParser.HandlerScriptContext ctx) {
        Script script = new Script();
        script.defineHandler((NamedBlock) visit(ctx.handler()));
        return script;
    }

    @Override
    public Object visitStatementScript(HypertalkParser.StatementScriptContext ctx) {
        Script script = new Script();
        Statement statement = (Statement) visit(ctx.nonEmptyStmnt());
        script.defineStatementList(new StatementList(statement));
        return script;
    }

    @Override
    public Object visitScriptFunctionScript(HypertalkParser.ScriptFunctionScriptContext ctx) {
        Script script = (Script) visit(ctx.script());
        script.defineUserFunction((UserFunction) visit(ctx.function()));
        return script;
    }

    @Override
    public Object visitFunctionScript(HypertalkParser.FunctionScriptContext ctx) {
        Script script = new Script();
        script.defineUserFunction((UserFunction) visit(ctx.function()));
        return script;
    }

    @Override
    public Object visitScriptHandlerScript(HypertalkParser.ScriptHandlerScriptContext ctx) {
        Script script = (Script) visit(ctx.script());
        script.defineHandler((NamedBlock) visit(ctx.handler()));
        return script;
    }

    @Override
    public Object visitCommentScript(HypertalkParser.CommentScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitWhitespaceScript(HypertalkParser.WhitespaceScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitEofScript(HypertalkParser.EofScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitScriptNewlineScript(HypertalkParser.ScriptNewlineScriptContext ctx) {
        return visit(ctx.script());
    }

    @Override
    public Object visitPopulatedHandler(HypertalkParser.PopulatedHandlerContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on " + open + "' does not match 'end " + close + "'");
        }

        return new NamedBlock(open, (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitEmptyHandler(HypertalkParser.EmptyHandlerContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on " + open + "' does not match 'end " + close + "'");
        }

        return new NamedBlock(open, new StatementList());
    }

    @Override
    public Object visitPopulatedFunction(HypertalkParser.PopulatedFunctionContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on function " + open + "' does not match 'end " + close + "'");
        }

        return new UserFunction(open, (ParameterList) visit(ctx.parameterList()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitEmptyFunction(HypertalkParser.EmptyFunctionContext ctx) {
        String open = (String) visit(ctx.ID(0));
        String close = (String) visit(ctx.ID(1));

        if (!open.equals(close)) {
            throw new HtParseError(ctx, "'on function " + open + "' does not match 'end " + close + "'");
        }

        return new UserFunction(open, (ParameterList) visit(ctx.parameterList()), new StatementList());
    }

    @Override
    public Object visitEmptyArgList(HypertalkParser.EmptyArgListContext ctx) {
        return new ArgumentList();
    }

    @Override
    public Object visitSingleExpArgList(HypertalkParser.SingleExpArgListContext ctx) {
        return new ArgumentList((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitMultiExpArgList(HypertalkParser.MultiExpArgListContext ctx) {
        ArgumentList argumentList = (ArgumentList) visit(ctx.argumentList());
        argumentList.addArgument((Expression) visit(ctx.expression()));
        return argumentList;
    }

    @Override
    public Object visitEmptyParamList(HypertalkParser.EmptyParamListContext ctx) {
        return new ParameterList();
    }

    @Override
    public Object visitSingleParamList(HypertalkParser.SingleParamListContext ctx) {
        return new ParameterList((String) visit(ctx.ID()));
    }

    @Override
    public Object visitMultiParamList(HypertalkParser.MultiParamListContext ctx) {
        ParameterList parameterList = (ParameterList) visit(ctx.parameterList());
        parameterList.addParameter((String) visit(ctx.ID()));
        return parameterList;
    }

    @Override
    public Object visitSingleStmntList(HypertalkParser.SingleStmntListContext ctx) {
        return new StatementList((Statement) visit(ctx.nonEmptyStmnt()));
    }

    @Override
    public Object visitNewlineStmntList(HypertalkParser.NewlineStmntListContext ctx) {
        return new StatementList();
    }

    @Override
    public Object visitMultiStmntList(HypertalkParser.MultiStmntListContext ctx) {
        StatementList statementList = (StatementList) visit(ctx.statementList());
        statementList.append((Statement) visit(ctx.nonEmptyStmnt()));
        return statementList;
    }

    @Override
    public Object visitStmntListNewlineStmntList(HypertalkParser.StmntListNewlineStmntListContext ctx) {
        return visit(ctx.statementList());
    }

    @Override
    public Object visitNonEmptyCommandStmnt(HypertalkParser.NonEmptyCommandStmntContext ctx) {
        return visit(ctx.commandStmnt());
    }

    @Override
    public Object visitNonEmptyGlobalStmnt(HypertalkParser.NonEmptyGlobalStmntContext ctx) {
        return visit(ctx.globalStmnt());
    }

    @Override
    public Object visitNonEmptyIfStmnt(HypertalkParser.NonEmptyIfStmntContext ctx) {
        return visit(ctx.ifStatement());
    }

    @Override
    public Object visitNonEmptyRepeatStmnt(HypertalkParser.NonEmptyRepeatStmntContext ctx) {
        return visit(ctx.repeatStatement());
    }

    @Override
    public Object visitNonEmptyDoStmnt(HypertalkParser.NonEmptyDoStmntContext ctx) {
        return visit(ctx.doStmnt());
    }

    @Override
    public Object visitNonEmptyReturnStmnt(HypertalkParser.NonEmptyReturnStmntContext ctx) {
        return visit(ctx.returnStmnt());
    }

    @Override
    public Object visitNonEmptyExpStmnt(HypertalkParser.NonEmptyExpStmntContext ctx) {
        return new StatExp((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitVoidReturnStmnt(HypertalkParser.VoidReturnStmntContext ctx) {
        return new StatReturn();
    }

    @Override
    public Object visitEprReturnStmnt(HypertalkParser.EprReturnStmntContext ctx) {
        return new StatReturn((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitDoStmnt(HypertalkParser.DoStmntContext ctx) {
        return new StatDo((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitAnswerCmdStmnt(HypertalkParser.AnswerCmdStmntContext ctx) {
        return visit(ctx.answerCmd());
    }

    @Override
    public Object visitAskCmdStmnt(HypertalkParser.AskCmdStmntContext ctx) {
        return visit(ctx.askCmd());
    }

    @Override
    public Object visitPutCmdStmnt(HypertalkParser.PutCmdStmntContext ctx) {
        return visit(ctx.putCmd());
    }

    @Override
    public Object visitGetCmdStmnt(HypertalkParser.GetCmdStmntContext ctx) {
        return visit(ctx.getCmd());
    }

    @Override
    public Object visitSetCmdStmnt(HypertalkParser.SetCmdStmntContext ctx) {
        return visit(ctx.setCmd());
    }

    @Override
    public Object visitSendCmdStmnt(HypertalkParser.SendCmdStmntContext ctx) {
        return visit(ctx.sendCmd());
    }

    @Override
    public Object visitAddCmdStmnt(HypertalkParser.AddCmdStmntContext ctx) {
        return new StatAddCmd((Expression) visit(ctx.expression()), (Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitSubtractCmdStmnt(HypertalkParser.SubtractCmdStmntContext ctx) {
        return new StatSubtractCmd((Expression) visit(ctx.expression()), (Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitMultiplyCmdStmnt(HypertalkParser.MultiplyCmdStmntContext ctx) {
        return new StatMultiplyCmd((Expression) visit(ctx.expression()), (Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitDivideCmdStmnt(HypertalkParser.DivideCmdStmntContext ctx) {
        return new StatDivideCmd((Expression) visit(ctx.expression()), (Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitIfThenSingleLine(HypertalkParser.IfThenSingleLineContext ctx) {
        return new StatIf((Expression) visit(ctx.expression()), (ThenElseBlock) visit(ctx.singleThen()));
    }

    @Override
    public Object visitIfThenMultiline(HypertalkParser.IfThenMultilineContext ctx) {
        return new StatIf((Expression) visit(ctx.expression()), (ThenElseBlock) visit(ctx.multiThen()));
    }

    @Override
    public Object visitSingleThen(HypertalkParser.SingleThenContext ctx) {
        return new ThenElseBlock(new StatementList((Statement) visit(ctx.nonEmptyStmnt())), (StatementList) visit(ctx.elseBlock()));
    }

    @Override
    public Object visitEmptyElse(HypertalkParser.EmptyElseContext ctx) {
        return new ThenElseBlock((StatementList) visit(ctx.statementList()), new StatementList());
    }

    @Override
    public Object visitEmptyThenEmptyElse(HypertalkParser.EmptyThenEmptyElseContext ctx) {
        return new ThenElseBlock(new StatementList(), new StatementList());
    }

    @Override
    public Object visitThenElse(HypertalkParser.ThenElseContext ctx) {
        return new ThenElseBlock((StatementList) visit(ctx.statementList()), (StatementList) visit(ctx.elseBlock()));
    }

    @Override
    public Object visitElseStmntBlock(HypertalkParser.ElseStmntBlockContext ctx) {
        return new StatementList((Statement) visit(ctx.nonEmptyStmnt()));
    }

    @Override
    public Object visitElseStmntListBlock(HypertalkParser.ElseStmntListBlockContext ctx) {
        return visit(ctx.statementList());
    }

    @Override
    public Object visitElseEmptyBlock(HypertalkParser.ElseEmptyBlockContext ctx) {
        return new StatementList();
    }

    @Override
    public Object visitRepeatStmntList(HypertalkParser.RepeatStmntListContext ctx) {
        return new StatRepeat((RepeatSpecifier) visit(ctx.repeatRange()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitRepeatEmpty(HypertalkParser.RepeatEmptyContext ctx) {
        return new StatRepeat((RepeatSpecifier) visit(ctx.repeatRange()), new StatementList());
    }

    @Override
    public Object visitInfiniteLoop(HypertalkParser.InfiniteLoopContext ctx) {
        return new RepeatForever();
    }

    @Override
    public Object visitDurationLoop(HypertalkParser.DurationLoopContext ctx) {
        return visit(ctx.duration());
    }

    @Override
    public Object visitCountLoop(HypertalkParser.CountLoopContext ctx) {
        return visit(ctx.count());
    }

    @Override
    public Object visitWithLoop(HypertalkParser.WithLoopContext ctx) {
        return new RepeatWith((String) visit(ctx.ID()), (RepeatRange) visit(ctx.range()));
    }

    @Override
    public Object visitUntilDuration(HypertalkParser.UntilDurationContext ctx) {
        return new RepeatDuration(RepeatDuration.POLARITY_UNTIL, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitWhileDuration(HypertalkParser.WhileDurationContext ctx) {
        return new RepeatDuration(RepeatDuration.POLARITY_WHILE, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitCount(HypertalkParser.CountContext ctx) {
        return new RepeatCount((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitRangeDownTo(HypertalkParser.RangeDownToContext ctx) {
        return new RepeatRange(RepeatRange.POLARITY_DOWNTO, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitRangeUpTo(HypertalkParser.RangeUpToContext ctx) {
        return new RepeatRange(RepeatRange.POLARITY_UPTO, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitGlobalStmnt(HypertalkParser.GlobalStmntContext ctx) {
        return new StatGlobal((String) visit(ctx.ID()));
    }

    @Override
    public Object visitAnswerThreeButtonCmd(HypertalkParser.AnswerThreeButtonCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (Expression) visit(ctx.expression(2)), (Expression) visit(ctx.expression(3)));
    }

    @Override
    public Object visitAnswerTwoButtonCmd(HypertalkParser.AnswerTwoButtonCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (Expression) visit(ctx.expression(2)));
    }

    @Override
    public Object visitAnswerOneButtonCmd(HypertalkParser.AnswerOneButtonCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAnswerDefaultCmd(HypertalkParser.AnswerDefaultCmdContext ctx) {
        return new StatAnswerCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitAskExpWithCmd(HypertalkParser.AskExpWithCmdContext ctx) {
        return new StatAskCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAskExpCmd(HypertalkParser.AskExpCmdContext ctx) {
        return new StatAskCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitPutIntoCmd(HypertalkParser.PutIntoCmdContext ctx) {
        return new StatPutCmd((Expression) visit(ctx.expression()), Preposition.INTO, (Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitPutPrepositionCmd(HypertalkParser.PutPrepositionCmdContext ctx) {
        return new StatPutCmd((Expression) visit(ctx.expression()), (Preposition) visit(ctx.preposition()), (Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitGetCmd(HypertalkParser.GetCmdContext ctx) {
        return new StatGetCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSetCmd(HypertalkParser.SetCmdContext ctx) {
        return new StatSetCmd((String) visit(ctx.ID()), (ExpPart) visit(ctx.part()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSendCmd(HypertalkParser.SendCmdContext ctx) {
        return new StatSendCmd((ExpPart) visit(ctx.part()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitBeforePreposition(HypertalkParser.BeforePrepositionContext ctx) {
        return Preposition.BEFORE;
    }

    @Override
    public Object visitAfterPreposition(HypertalkParser.AfterPrepositionContext ctx) {
        return Preposition.AFTER;
    }

    @Override
    public Object visitIntoPreposition(HypertalkParser.IntoPrepositionContext ctx) {
        return Preposition.INTO;
    }

    @Override
    public Object visitVariableDest(HypertalkParser.VariableDestContext ctx) {
        return new DestinationVariable((String) visit(ctx.ID()));
    }

    @Override
    public Object visitChunkVariableDest(HypertalkParser.ChunkVariableDestContext ctx) {
        return new DestinationVariable((String) visit(ctx.ID()), (Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitPartDest(HypertalkParser.PartDestContext ctx) {
        return new DestinationPart((ExpPart) visit(ctx.part()));
    }

    @Override
    public Object visitChunkPartDest(HypertalkParser.ChunkPartDestContext ctx) {
        return new DestinationPart((ExpPart) visit(ctx.part()), (Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitMessageDest(HypertalkParser.MessageDestContext ctx) {
        return new DestinationMsgBox();
    }

    @Override
    public Object visitChunkMessageDest(HypertalkParser.ChunkMessageDestContext ctx) {
        return new DestinationMsgBox((Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitChunkDest(HypertalkParser.ChunkDestContext ctx) {
        return new DestinationMsgBox((Chunk) visit(ctx.chunk()));
    }

    @Override
    public Object visitDefaultDest(HypertalkParser.DefaultDestContext ctx) {
        return new DestinationMsgBox();
    }

    @Override
    public Object visitFieldPart(HypertalkParser.FieldPartContext ctx) {
        return new ExpPartName(PartType.FIELD, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitFieldIdPart(HypertalkParser.FieldIdPartContext ctx) {
        return new ExpPartId(PartType.FIELD, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitButtonPart(HypertalkParser.ButtonPartContext ctx) {
        return new ExpPartName(PartType.BUTTON, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitButtonIdPart(HypertalkParser.ButtonIdPartContext ctx) {
        return new ExpPartId(PartType.BUTTON, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitMePart(HypertalkParser.MePartContext ctx) {
        return new ExpPartMe();
    }

    @Override
    public Object visitOrdinalCharChunk(HypertalkParser.OrdinalCharChunkContext ctx) {
        return new Chunk(ChunkType.CHAR, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeCharChunk(HypertalkParser.RangeCharChunkContext ctx) {
        return new Chunk(ChunkType.CHARRANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitCharCharChunk(HypertalkParser.CharCharChunkContext ctx) {
        return new Chunk(ChunkType.CHAR, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrdinalWordChunk(HypertalkParser.OrdinalWordChunkContext ctx) {
        return new Chunk(ChunkType.WORD, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeWordChunk(HypertalkParser.RangeWordChunkContext ctx) {
        return new Chunk(ChunkType.WORDRANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitWordWordChunk(HypertalkParser.WordWordChunkContext ctx) {
        return new Chunk(ChunkType.WORD, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrdinalItemChunk(HypertalkParser.OrdinalItemChunkContext ctx) {
        return new Chunk(ChunkType.ITEM, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeItemChunk(HypertalkParser.RangeItemChunkContext ctx) {
        return new Chunk(ChunkType.ITEMRANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitItemItemChunk(HypertalkParser.ItemItemChunkContext ctx) {
        return new Chunk(ChunkType.ITEM, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrdinalLineChunk(HypertalkParser.OrdinalLineChunkContext ctx) {
        return new Chunk(ChunkType.LINE, new ExpLiteral(((Ordinal) visit(ctx.ordinal())).stringValue()));
    }

    @Override
    public Object visitRangeLineChunk(HypertalkParser.RangeLineChunkContext ctx) {
        return new Chunk(ChunkType.LINERANGE, (Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitLineLineChunk(HypertalkParser.LineLineChunkContext ctx) {
        return new Chunk(ChunkType.LINE, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitFirstOrd(HypertalkParser.FirstOrdContext ctx) {
        return Ordinal.FIRST;
    }

    @Override
    public Object visitSecondOrd(HypertalkParser.SecondOrdContext ctx) {
        return Ordinal.SECOND;
    }

    @Override
    public Object visitThirdOrd(HypertalkParser.ThirdOrdContext ctx) {
        return Ordinal.THIRD;
    }

    @Override
    public Object visitFourthOrd(HypertalkParser.FourthOrdContext ctx) {
        return Ordinal.FOURTH;
    }

    @Override
    public Object visitFifthOrd(HypertalkParser.FifthOrdContext ctx) {
        return Ordinal.FIFTH;
    }

    @Override
    public Object visitSixthOrd(HypertalkParser.SixthOrdContext ctx) {
        return Ordinal.SIXTH;
    }

    @Override
    public Object visitSeventhOrd(HypertalkParser.SeventhOrdContext ctx) {
        return Ordinal.SEVENTH;
    }

    @Override
    public Object visitEigthOrd(HypertalkParser.EigthOrdContext ctx) {
        return Ordinal.EIGTH;
    }

    @Override
    public Object visitNinthOrd(HypertalkParser.NinthOrdContext ctx) {
        return Ordinal.NINTH;
    }

    @Override
    public Object visitTenthOrd(HypertalkParser.TenthOrdContext ctx) {
        return Ordinal.TENTH;
    }

    @Override
    public Object visitMidOrd(HypertalkParser.MidOrdContext ctx) {
        return Ordinal.MIDDLE;
    }

    @Override
    public Object visitLastOrd(HypertalkParser.LastOrdContext ctx) {
        return Ordinal.LAST;
    }

    @Override
    public Object visitExp(HypertalkParser.ExpContext ctx) {
        return visit(ctx.opLevel10Exp());
    }

    @Override
    public Object visitChunkExp(HypertalkParser.ChunkExpContext ctx) {
        return new ExpChunk((Chunk) visit(ctx.chunk()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitLevel10Exp(HypertalkParser.Level10ExpContext ctx) {
        return visit(ctx.opLevel9Exp());
    }

    @Override
    public Object visitOrExp(HypertalkParser.OrExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel10Exp()), BinaryOperator.OR, (Expression) visit(ctx.opLevel9Exp()));
    }

    @Override
    public Object visitLevel9Exp(HypertalkParser.Level9ExpContext ctx) {
        return visit(ctx.opLevel8Exp());
    }

    @Override
    public Object visitAndExp(HypertalkParser.AndExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel9Exp()), BinaryOperator.AND, (Expression) visit(ctx.opLevel8Exp()));
    }

    @Override
    public Object visitIsNotExp(HypertalkParser.IsNotExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel8Exp()), BinaryOperator.NOTEQUALS, (Expression) visit(ctx.opLevel7Exp()));
    }

    @Override
    public Object visitWackaWackaExp(HypertalkParser.WackaWackaExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel8Exp()), BinaryOperator.NOTEQUALS, (Expression) visit(ctx.opLevel7Exp()));
    }

    @Override
    public Object visitLevel8Exp(HypertalkParser.Level8ExpContext ctx) {
        return visit(ctx.opLevel7Exp());
    }

    @Override
    public Object visitEqualsExp(HypertalkParser.EqualsExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel8Exp()), BinaryOperator.EQUALS, (Expression) visit(ctx.opLevel7Exp()));
    }

    @Override
    public Object visitIsExp(HypertalkParser.IsExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel8Exp()), BinaryOperator.EQUALS, (Expression) visit(ctx.opLevel7Exp()));
    }

    @Override
    public Object visitLevel7Exp(HypertalkParser.Level7ExpContext ctx) {
        return visit(ctx.opLevel6Exp());
    }

    @Override
    public Object visitLessThanEqualsExp(HypertalkParser.LessThanEqualsExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.LESSTHANOREQUALS, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitGreaterThanEqualsExp(HypertalkParser.GreaterThanEqualsExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.GREATERTHANOREQUALS, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitLessThanExp(HypertalkParser.LessThanExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.LESSTHAN, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitIsNotInExp(HypertalkParser.IsNotInExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.NOTCONTAINS, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitContainsExp(HypertalkParser.ContainsExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.CONTAINS, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitIsInExp(HypertalkParser.IsInExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.CONTAINS, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitGreaterThanExp(HypertalkParser.GreaterThanExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel7Exp()), BinaryOperator.GREATERTHAN, (Expression) visit(ctx.opLevel6Exp()));
    }

    @Override
    public Object visitLevel6Exp(HypertalkParser.Level6ExpContext ctx) {
        return visit(ctx.opLevel5Exp());
    }

    @Override
    public Object visitAmpExp(HypertalkParser.AmpExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel6Exp()), BinaryOperator.CONCAT, (Expression) visit(ctx.opLevel5Exp()));
    }

    @Override
    public Object visitAmpAmpExp(HypertalkParser.AmpAmpExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel6Exp()), BinaryOperator.CONCAT, (Expression) visit(ctx.opLevel5Exp()));
    }

    @Override
    public Object visitMinusExp(HypertalkParser.MinusExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel5Exp()), BinaryOperator.MINUS, (Expression) visit(ctx.opLevel4Exp()));
    }

    @Override
    public Object visitLevel5Exp(HypertalkParser.Level5ExpContext ctx) {
        return visit(ctx.opLevel4Exp());
    }

    @Override
    public Object visitPlusExp(HypertalkParser.PlusExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel5Exp()), BinaryOperator.PLUS, (Expression) visit(ctx.opLevel4Exp()));
    }

    @Override
    public Object visitModExp(HypertalkParser.ModExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel4Exp()), BinaryOperator.MOD, (Expression) visit(ctx.opLevel3Exp()));
    }

    @Override
    public Object visitSlashExp(HypertalkParser.SlashExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel4Exp()), BinaryOperator.DIVIDE, (Expression) visit(ctx.opLevel3Exp()));
    }

    @Override
    public Object visitLevel4Exp(HypertalkParser.Level4ExpContext ctx) {
        return visit(ctx.opLevel3Exp());
    }

    @Override
    public Object visitMultiplyExp(HypertalkParser.MultiplyExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel4Exp()), BinaryOperator.MULTIPLY, (Expression) visit(ctx.opLevel3Exp()));
    }

    @Override
    public Object visitDivExp(HypertalkParser.DivExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel4Exp()), BinaryOperator.DIVIDE, (Expression) visit(ctx.opLevel3Exp()));
    }

    @Override
    public Object visitCaratExp(HypertalkParser.CaratExpContext ctx) {
        return new ExpBinaryOperator((Expression) visit(ctx.opLevel3Exp()), BinaryOperator.EXP, (Expression) visit(ctx.opLevel2Exp()));
    }

    @Override
    public Object visitLevel3Exp(HypertalkParser.Level3ExpContext ctx) {
        return visit(ctx.opLevel2Exp());
    }

    @Override
    public Object visitLevel2Exp(HypertalkParser.Level2ExpContext ctx) {
        return visit(ctx.opLevel1Exp());
    }

    @Override
    public Object visitNegateExp(HypertalkParser.NegateExpContext ctx) {
        return new ExpUnaryOperator(UnaryOperator.NEGATE, (Expression) visit(ctx.opLevel2Exp()));
    }

    @Override
    public Object visitNotExp(HypertalkParser.NotExpContext ctx) {
        return new ExpUnaryOperator(UnaryOperator.NOT, (Expression) visit(ctx.opLevel2Exp()));
    }

    @Override
    public Object visitLevel1Exp(HypertalkParser.Level1ExpContext ctx) {
        return visit(ctx.builtin());
    }

    @Override
    public Object visitBuiltinExp(HypertalkParser.BuiltinExpContext ctx) {
        return visit(ctx.builtin());
    }

    @Override
    public Object visitFactorExp(HypertalkParser.FactorExpContext ctx) {
        return visit(ctx.factor());
    }

    @Override
    public Object visitFunctionExp(HypertalkParser.FunctionExpContext ctx) {
        return new ExpUserFunction((String) visit(ctx.ID()), (ArgumentList) visit(ctx.argumentList()));
    }

    @Override
    public Object visitMouseFunc(HypertalkParser.MouseFuncContext ctx) {
        return new ExpMouseFun();
    }

    @Override
    public Object visitMouseLocFunc(HypertalkParser.MouseLocFuncContext ctx) {
        return new ExpMouseLocFun();
    }

    @Override
    public Object visitAverageFunc(HypertalkParser.AverageFuncContext ctx) {
        return new ExpAverageFun((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitResultFunc(HypertalkParser.ResultFuncContext ctx) {
        return new ExpResultFun();
    }

    @Override
    public Object visitMessageFunc(HypertalkParser.MessageFuncContext ctx) {
        return new ExpMessageBoxFun();
    }

    @Override
    public Object visitNumberFunc(HypertalkParser.NumberFuncContext ctx) {
        return new ExpNumberOfFun((ChunkType) visit(ctx.countable()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitMinFunc(HypertalkParser.MinFuncContext ctx) {
        return new ExpMinFun((ArgumentList) visit(ctx.argumentList()));
    }

    @Override
    public Object visitMaxFunc(HypertalkParser.MaxFuncContext ctx) {
        return new ExpMaxFun((ArgumentList) visit(ctx.argumentList()));
    }

    @Override
    public Object visitTicksFunc(HypertalkParser.TicksFuncContext ctx) {
        return new ExpTicksFun();
    }

    @Override
    public Object visitSecondsFunc(HypertalkParser.SecondsFuncContext ctx) {
        return new ExpSecondsFun();
    }

    @Override
    public Object visitDateFormatFunc(HypertalkParser.DateFormatFuncContext ctx) {
        return new ExpDateFun((DateFormat) visit(ctx.dateFormat()));
    }

    @Override
    public Object visitTimeFormatFunc(HypertalkParser.TimeFormatFuncContext ctx) {
        return new ExpTimeFun((DateFormat) visit(ctx.dateFormat()));
    }

    @Override
    public Object visitLongDateFormat(HypertalkParser.LongDateFormatContext ctx) {
        return DateFormat.LONG;
    }

    @Override
    public Object visitShortDateFormat(HypertalkParser.ShortDateFormatContext ctx) {
        return DateFormat.SHORT;
    }

    @Override
    public Object visitAbbrevDateFormat(HypertalkParser.AbbrevDateFormatContext ctx) {
        return DateFormat.ABBREVIATED;
    }

    @Override
    public Object visitAbbreviatedDateFormat(HypertalkParser.AbbreviatedDateFormatContext ctx) {
        return DateFormat.ABBREVIATED;
    }

    @Override
    public Object visitDefaultDateFormat(HypertalkParser.DefaultDateFormatContext ctx) {
        return DateFormat.SHORT;
    }

    @Override
    public Object visitCharsCountable(HypertalkParser.CharsCountableContext ctx) {
        return ChunkType.CHAR;
    }

    @Override
    public Object visitLinesCountable(HypertalkParser.LinesCountableContext ctx) {
        return ChunkType.LINE;
    }

    @Override
    public Object visitWordsCountable(HypertalkParser.WordsCountableContext ctx) {
        return ChunkType.WORD;
    }

    @Override
    public Object visitItemsCountable(HypertalkParser.ItemsCountableContext ctx) {
        return ChunkType.ITEM;
    }

    @Override
    public Object visitLiteralFactor(HypertalkParser.LiteralFactorContext ctx) {
        return new ExpLiteral(visit(ctx.literal()));
    }

    @Override
    public Object visitIdFactor(HypertalkParser.IdFactorContext ctx) {
        return new ExpVariable((String) visit(ctx.ID()));
    }

    @Override
    public Object visitPartFactor(HypertalkParser.PartFactorContext ctx) {
        return visit(ctx.part());
    }

    @Override
    public Object visitExpressionFactor(HypertalkParser.ExpressionFactorContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitIdOfPartFactor(HypertalkParser.IdOfPartFactorContext ctx) {
        return new ExpProperty((String) visit(ctx.ID()), (ExpPart) visit(ctx.part()));
    }

    @Override
    public Object visitStringLiteral(HypertalkParser.StringLiteralContext ctx) {
        String quotedLiteral = ctx.getText();

        if (!quotedLiteral.startsWith("\"") || !quotedLiteral.endsWith("\"")) {
            throw new IllegalStateException("Bug! No quotes around quoted literal.");
        }

        return new Value(String.valueOf(quotedLiteral.substring(1, quotedLiteral.length() - 1)));
    }

    @Override
    public Object visitDotNumberLiteral(HypertalkParser.DotNumberLiteralContext ctx) {
        Object fractional = ctx.INTEGER_LITERAL().getText();
        return new Value("0." + String.valueOf(fractional));
    }

    @Override
    public Object visitNumberDotNumberLiteral(HypertalkParser.NumberDotNumberLiteralContext ctx) {
        Object whole = ctx.INTEGER_LITERAL(0).getText();
        Object fractional = ctx.INTEGER_LITERAL(1).getText();
        return new Value(String.valueOf(whole) + "." + String.valueOf(fractional));
    }

    @Override
    public Object visitNumberDotLiteral(HypertalkParser.NumberDotLiteralContext ctx) {
        return new Value(ctx.getText());
    }

    @Override
    public Object visitNumberLiteral(HypertalkParser.NumberLiteralContext ctx) {
        return new Value(ctx.getText());
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        return node.getText();
    }

}
