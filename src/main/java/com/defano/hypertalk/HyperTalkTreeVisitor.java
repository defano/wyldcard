/*
 * HyperTalkTreeVisitor
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk;

import com.defano.hypercard.runtime.context.HyperCardProperties;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.*;
import com.defano.hypertalk.ast.constructs.*;
import com.defano.hypertalk.ast.containers.*;
import com.defano.hypertalk.ast.expressions.*;
import com.defano.hypertalk.ast.functions.*;
import com.defano.hypertalk.ast.statements.*;
import com.defano.hypertalk.ast.commands.*;
import com.defano.hypertalk.comparator.SortStyle;
import com.defano.hypertalk.parser.HyperTalkBaseVisitor;
import com.defano.hypertalk.parser.HyperTalkParser;
import com.defano.jsegue.SegueName;
import org.antlr.v4.runtime.tree.TerminalNode;

public class HyperTalkTreeVisitor extends HyperTalkBaseVisitor<Object> {

    @Override
    public Object visitHandlerScript(HyperTalkParser.HandlerScriptContext ctx) {
        return new Script((NamedBlock) visit(ctx.handler()), ctx.handler().getStart().getLine(), ctx.handler().getStop().getLine());
    }

    @Override
    public Object visitWaitCmdStmnt(HyperTalkParser.WaitCmdStmntContext ctx) {
        return visit(ctx.waitCmd());
    }

    @Override
    public Object visitSortCmdStmnt(HyperTalkParser.SortCmdStmntContext ctx) {
        return visit(ctx.sortCmd());
    }

    @Override
    public Object visitGoCmdStmt(HyperTalkParser.GoCmdStmtContext ctx) {
        return visit(ctx.goCmd());
    }

    @Override
    public Object visitHideCmdStmnt(HyperTalkParser.HideCmdStmntContext ctx) {
        return new SetPropertyCmd((PartExp) visit(ctx.part()), PartModel.PROP_VISIBLE, new Value(false));
    }

    @Override
    public Object visitShowCmdStmnt(HyperTalkParser.ShowCmdStmntContext ctx) {
        return new SetPropertyCmd((PartExp) visit(ctx.part()), PartModel.PROP_VISIBLE, new Value(true));
    }

    @Override
    public Object visitDisableCmdStmnt(HyperTalkParser.DisableCmdStmntContext ctx) {
        return visit(ctx.disableCmd());
    }

    @Override
    public Object visitReadCmdStmt(HyperTalkParser.ReadCmdStmtContext ctx) {
        return visit(ctx.readCmd());
    }

    @Override
    public Object visitWriteCmdStmt(HyperTalkParser.WriteCmdStmtContext ctx) {
        return visit(ctx.writeCmd());
    }

    @Override
    public Object visitEnableCmdStmnt(HyperTalkParser.EnableCmdStmntContext ctx) {
        return visit(ctx.enableCmd());
    }

    @Override
    public Object visitWaitCountCmd(HyperTalkParser.WaitCountCmdContext ctx) {
        return new WaitCmd((Expression) visit(ctx.factor()), (TimeUnit) visit(ctx.timeUnit()));
    }

    @Override
    public Object visitWaitUntilCmd(HyperTalkParser.WaitUntilCmdContext ctx) {
        return new WaitCmd((Expression) visit(ctx.expression()), true);
    }

    @Override
    public Object visitWaitWhileCmd(HyperTalkParser.WaitWhileCmdContext ctx) {
        return new WaitCmd((Expression) visit(ctx.expression()), false);
    }

    @Override
    public Object visitSortDirectionCmd(HyperTalkParser.SortDirectionCmdContext ctx) {
        return new SortCmd((Container) visit(ctx.container()), (ChunkType) visit(ctx.sortChunkType()), (SortDirection) visit(ctx.sortDirection()), (SortStyle) visit(ctx.sortStyle()));
    }

    @Override
    public Object visitSortExpressionCmd(HyperTalkParser.SortExpressionCmdContext ctx) {
        return new SortCmd((Container) visit(ctx.container()), (ChunkType) visit(ctx.sortChunkType()), (Expression) visit(ctx.expression()), (SortDirection) visit(ctx.sortDirection()), (SortStyle) visit(ctx.sortStyle()));
    }

    @Override
    public Object visitSortMarkedCardsCmd(HyperTalkParser.SortMarkedCardsCmdContext ctx) {
        return new SortCardsCmd(true, (SortDirection) visit(ctx.sortDirection()), (SortStyle) visit(ctx.sortStyle()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSortStackCmd(HyperTalkParser.SortStackCmdContext ctx) {
        return new SortCardsCmd(false, (SortDirection) visit(ctx.sortDirection()), (SortStyle) visit(ctx.sortStyle()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSortBkgndCardsCmd(HyperTalkParser.SortBkgndCardsCmdContext ctx) {
        return new SortCardsCmd(false, (PartExp) visit(ctx.bkgndPart()), (SortDirection) visit(ctx.sortDirection()), (SortStyle) visit(ctx.sortStyle()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSortMarkedBkgndCardsCmd(HyperTalkParser.SortMarkedBkgndCardsCmdContext ctx) {
        return new SortCardsCmd(true, (PartExp) visit(ctx.bkgndPart()), (SortDirection) visit(ctx.sortDirection()), (SortStyle) visit(ctx.sortStyle()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitConvertContainerFromToCmd(HyperTalkParser.ConvertContainerFromToCmdContext ctx) {
        return new ConvertCmd((Container) visit(ctx.container()), (Convertible) visit(ctx.convertible(0)), (Convertible) visit(ctx.convertible(1)));
    }

    @Override
    public Object visitConvertContainerToCmd(HyperTalkParser.ConvertContainerToCmdContext ctx) {
        return new ConvertCmd((Container) visit(ctx.container()), (Convertible) visit(ctx.convertible()));
    }

    @Override
    public Object visitConvertToCmd(HyperTalkParser.ConvertToCmdContext ctx) {
        return new ConvertCmd((Expression) visit(ctx.expression()), (Convertible) visit(ctx.convertible()));
    }

    @Override
    public Object visitConvertFromToCmd(HyperTalkParser.ConvertFromToCmdContext ctx) {
        return new ConvertCmd((Expression) visit(ctx.expression()), (Convertible) visit(ctx.convertible(0)), (Convertible) visit(ctx.convertible(1)));
    }

    @Override
    public Object visitConvertCmdStmt(HyperTalkParser.ConvertCmdStmtContext ctx) {
        return visit(ctx.convertCmd());
    }

    @Override
    public Object visitSelectCmdStmt(HyperTalkParser.SelectCmdStmtContext ctx) {
        return visit(ctx.selectCmd());
    }

    @Override
    public Object visitDualFormatConvertible(HyperTalkParser.DualFormatConvertibleContext ctx) {
        return new Convertible((ConvertibleDateFormat) visit(ctx.conversionFormat(0)), (ConvertibleDateFormat) visit(ctx.conversionFormat(1)));
    }

    @Override
    public Object visitSingleFormatConvertible(HyperTalkParser.SingleFormatConvertibleContext ctx) {
        return new Convertible((ConvertibleDateFormat) visit(ctx.conversionFormat()));
    }

    @Override
    public Object visitLongTimeConvFormat(HyperTalkParser.LongTimeConvFormatContext ctx) {
        return ConvertibleDateFormat.LONG_TIME;
    }

    @Override
    public Object visitShortTimeConvFormat(HyperTalkParser.ShortTimeConvFormatContext ctx) {
        return ConvertibleDateFormat.SHORT_TIME;
    }

    @Override
    public Object visitDateItemsConvFormat(HyperTalkParser.DateItemsConvFormatContext ctx) {
        return ConvertibleDateFormat.DATE_ITEMS;
    }

    @Override
    public Object visitAbbrevDateConvFormat(HyperTalkParser.AbbrevDateConvFormatContext ctx) {
        return ConvertibleDateFormat.ABBREV_DATE;
    }

    @Override
    public Object visitShortDateConvFormat(HyperTalkParser.ShortDateConvFormatContext ctx) {
        return ConvertibleDateFormat.SHORT_DATE;
    }

    @Override
    public Object visitLongDateConvFormat(HyperTalkParser.LongDateConvFormatContext ctx) {
        return ConvertibleDateFormat.LONG_DATE;
    }

    @Override
    public Object visitSecondsConvFormat(HyperTalkParser.SecondsConvFormatContext ctx) {
        return ConvertibleDateFormat.SECONDS;
    }

    @Override
    public Object visitSortDirectionAsc(HyperTalkParser.SortDirectionAscContext ctx) {
        return SortDirection.ASCENDING;
    }

    @Override
    public Object visitSortDirectionDesc(HyperTalkParser.SortDirectionDescContext ctx) {
        return SortDirection.DESCENDING;
    }

    @Override
    public Object visitSortDirectionDefault(HyperTalkParser.SortDirectionDefaultContext ctx) {
        return SortDirection.ASCENDING;
    }

    @Override
    public Object visitSortChunkLines(HyperTalkParser.SortChunkLinesContext ctx) {
        return ChunkType.LINE;
    }

    @Override
    public Object visitSortChunkItems(HyperTalkParser.SortChunkItemsContext ctx) {
        return ChunkType.ITEM;
    }

    @Override
    public Object visitSortChunkDefault(HyperTalkParser.SortChunkDefaultContext ctx) {
        return ChunkType.LINE;
    }

    @Override
    public Object visitSortStyleText(HyperTalkParser.SortStyleTextContext ctx) {
        return SortStyle.TEXT;
    }

    @Override
    public Object visitSortStyleNumeric(HyperTalkParser.SortStyleNumericContext ctx) {
        return SortStyle.NUMERIC;
    }

    @Override
    public Object visitSortStyleInternational(HyperTalkParser.SortStyleInternationalContext ctx) {
        return SortStyle.INTERNATIONAL;
    }

    @Override
    public Object visitSortStyleDateTime(HyperTalkParser.SortStyleDateTimeContext ctx) {
        return SortStyle.DATE_TIME;
    }

    @Override
    public Object visitSortStyleDefault(HyperTalkParser.SortStyleDefaultContext ctx) {
        return SortStyle.TEXT;
    }

    @Override
    public Object visitEffectDefault(HyperTalkParser.EffectDefaultContext ctx) {
        return new VisualEffectSpecifier((SegueName) visit(ctx.effect()));
    }

    @Override
    public Object visitEffectTo(HyperTalkParser.EffectToContext ctx) {
        return new VisualEffectSpecifier((SegueName) visit(ctx.effect()), (VisualEffectImage) visit(ctx.image()));
    }

    @Override
    public Object visitEffectSpeed(HyperTalkParser.EffectSpeedContext ctx) {
        return new VisualEffectSpecifier((SegueName) visit(ctx.effect()), (VisualEffectSpeed) visit(ctx.speed()));
    }

    @Override
    public Object visitEffectSpeedTo(HyperTalkParser.EffectSpeedToContext ctx) {
        return new VisualEffectSpecifier((SegueName) visit(ctx.effect()), (VisualEffectSpeed) visit(ctx.speed()), (VisualEffectImage) visit(ctx.image()));
    }

    @Override
    public Object visitFastSpeed(HyperTalkParser.FastSpeedContext ctx) {
        return VisualEffectSpeed.FAST;
    }

    @Override
    public Object visitSlowSpeed(HyperTalkParser.SlowSpeedContext ctx) {
        return VisualEffectSpeed.SLOW;
    }

    @Override
    public Object visitVeryFastSpeed(HyperTalkParser.VeryFastSpeedContext ctx) {
        return VisualEffectSpeed.VERY_FAST;
    }

    @Override
    public Object visitVerySlowSpeed(HyperTalkParser.VerySlowSpeedContext ctx) {
        return VisualEffectSpeed.SLOW;
    }

    @Override
    public Object visitBlackImage(HyperTalkParser.BlackImageContext ctx) {
        return VisualEffectImage.BLACK;
    }

    @Override
    public Object visitCardImage(HyperTalkParser.CardImageContext ctx) {
        return VisualEffectImage.CARD;
    }

    @Override
    public Object visitGrayImage(HyperTalkParser.GrayImageContext ctx) {
        return VisualEffectImage.GRAY;
    }

    @Override
    public Object visitInverseImage(HyperTalkParser.InverseImageContext ctx) {
        return VisualEffectImage.INVERSE;
    }

    @Override
    public Object visitWhiteImage(HyperTalkParser.WhiteImageContext ctx) {
        return VisualEffectImage.WHITE;
    }

    @Override
    public Object visitDissolveEffect(HyperTalkParser.DissolveEffectContext ctx) {
        return SegueName.DISSOLVE;
    }

    @Override
    public Object visitBarnDoorOpenEffect(HyperTalkParser.BarnDoorOpenEffectContext ctx) {
        return SegueName.BARN_DOOR_OPEN;
    }

    @Override
    public Object visitBarnDoorCloseEffect(HyperTalkParser.BarnDoorCloseEffectContext ctx) {
        return SegueName.BARN_DOOR_CLOSE;
    }

    @Override
    public Object visitCheckerboardEffect(HyperTalkParser.CheckerboardEffectContext ctx) {
        return SegueName.CHECKERBOARD;
    }

    @Override
    public Object visitIrisOpenEffect(HyperTalkParser.IrisOpenEffectContext ctx) {
        return SegueName.IRIS_OPEN;
    }

    @Override
    public Object visitIrisCloseEffect(HyperTalkParser.IrisCloseEffectContext ctx) {
        return SegueName.IRIS_CLOSE;
    }

    @Override
    public Object visitPlainEffect(HyperTalkParser.PlainEffectContext ctx) {
        return SegueName.PLAIN;
    }

    @Override
    public Object visitScrollDownEffect(HyperTalkParser.ScrollDownEffectContext ctx) {
        return SegueName.SCROLL_DOWN;
    }

    @Override
    public Object visitScrollUpEffect(HyperTalkParser.ScrollUpEffectContext ctx) {
        return SegueName.SCROLL_UP;
    }

    @Override
    public Object visitScrollLeftEffect(HyperTalkParser.ScrollLeftEffectContext ctx) {
        return SegueName.SCROLL_LEFT;
    }

    @Override
    public Object visitScrollRightEffect(HyperTalkParser.ScrollRightEffectContext ctx) {
        return SegueName.SCROLL_RIGHT;
    }

    @Override
    public Object visitShrinkToTopEffect(HyperTalkParser.ShrinkToTopEffectContext ctx) {
        return SegueName.SHRINK_TO_TOP;
    }

    @Override
    public Object visitShrinkToCenterEffect(HyperTalkParser.ShrinkToCenterEffectContext ctx) {
        return SegueName.SHRINK_TO_CENTER;
    }

    @Override
    public Object visitShrinkToBottomEffect(HyperTalkParser.ShrinkToBottomEffectContext ctx) {
        return SegueName.SHRINK_TO_BOTTOM;
    }

    @Override
    public Object visitStretchFromTopEffect(HyperTalkParser.StretchFromTopEffectContext ctx) {
        return SegueName.STRETCH_FROM_TOP;
    }

    @Override
    public Object visitStretchFromCenterEffect(HyperTalkParser.StretchFromCenterEffectContext ctx) {
        return SegueName.STRETCH_FROM_CENTER;
    }

    @Override
    public Object visitStretchFromBottomEffect(HyperTalkParser.StretchFromBottomEffectContext ctx) {
        return SegueName.STRETCH_FROM_BOTTOM;
    }

    @Override
    public Object visitVenitianBlindsEffect(HyperTalkParser.VenitianBlindsEffectContext ctx) {
        return SegueName.VENETIAN_BLINDS;
    }

    @Override
    public Object visitZoomInEffect(HyperTalkParser.ZoomInEffectContext ctx) {
        return SegueName.ZOOM_IN;
    }

    @Override
    public Object visitZoomOutEffect(HyperTalkParser.ZoomOutEffectContext ctx) {
        return SegueName.ZOOM_OUT;
    }

    @Override
    public Object visitWipeUpEffect(HyperTalkParser.WipeUpEffectContext ctx) {
        return SegueName.WIPE_UP;
    }

    @Override
    public Object visitWipeDownEffect(HyperTalkParser.WipeDownEffectContext ctx) {
        return SegueName.WIPE_DOWN;
    }

    @Override
    public Object visitWipeLeftEffect(HyperTalkParser.WipeLeftEffectContext ctx) {
        return SegueName.WIPE_LEFT;
    }

    @Override
    public Object visitWipeRightEffect(HyperTalkParser.WipeRightEffectContext ctx) {
        return SegueName.WIPE_RIGHT;
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
        return new GoCmd((Destination) visit(ctx.destination()));
    }

    @Override
    public Object visitGoBackCmdStmt(HyperTalkParser.GoBackCmdStmtContext ctx) {
        return new GoCmd(null);
    }

    @Override
    public Object visitGoBackVisualEffectCmdStmt(HyperTalkParser.GoBackVisualEffectCmdStmtContext ctx) {
        return new GoCmd(null, (VisualEffectSpecifier) visit(ctx.visualEffect()));
    }

    @Override
    public Object visitGoVisualEffectCmdStmnd(HyperTalkParser.GoVisualEffectCmdStmndContext ctx) {
        return new GoCmd((Destination) visit(ctx.destination()), (VisualEffectSpecifier) visit(ctx.visualEffect()));
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
    public Object visitThisPosition(HyperTalkParser.ThisPositionContext ctx) {
        return Position.THIS;
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
    public Object visitCardDestinationType(HyperTalkParser.CardDestinationTypeContext ctx) {
        return DestinationType.CARD;
    }

    @Override
    public Object visitBkgndDestinationType(HyperTalkParser.BkgndDestinationTypeContext ctx) {
        return DestinationType.BACKGROUND;
    }

    @Override
    public Object visitPropertyDest(HyperTalkParser.PropertyDestContext ctx) {
        return new ContainerProperty((PropertySpecifier) visit(ctx.propertySpec()));
    }

    @Override
    public Object visitChunkPropertyDest(HyperTalkParser.ChunkPropertyDestContext ctx) {
        return new ContainerProperty((PropertySpecifier) visit(ctx.propertySpec()), (Chunk)visit(ctx.chunk()));
    }

    @Override
    public Object visitMenuDest(HyperTalkParser.MenuDestContext ctx) {
        return new ContainerMenu((MenuSpecifier) visit(ctx.menu()));
    }

    @Override
    public Object visitMenuItemDest(HyperTalkParser.MenuItemDestContext ctx) {
        return new ContainerMenu((MenuItemSpecifier) visit(ctx.menuItem()));
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
        script.defineUserFunction((UserFunction) visit(ctx.function()), ctx.function().getStart().getLine(), ctx.function().getStop().getLine());
        return script;
    }

    @Override
    public Object visitFunctionScript(HyperTalkParser.FunctionScriptContext ctx) {
        Script script = new Script();
        script.defineUserFunction((UserFunction) visit(ctx.function()), ctx.function().getStart().getLine(), ctx.function().getStop().getLine());
        return script;
    }

    @Override
    public Object visitScriptHandlerScript(HyperTalkParser.ScriptHandlerScriptContext ctx) {
        Script script = (Script) visit(ctx.script());
        script.defineHandler((NamedBlock) visit(ctx.handler()), ctx.handler().getStart().getLine(), ctx.handler().getStop().getLine());
        return script;
    }

    @Override
    public Object visitCommentScript(HyperTalkParser.CommentScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitStatementListScript(HyperTalkParser.StatementListScriptContext ctx) {
        Script script = new Script();
        script.defineStatementList((StatementList) visit(ctx.statementList()));
        return script;
    }

    @Override
    public Object visitWhitespaceScript(HyperTalkParser.WhitespaceScriptContext ctx) {
        return new Script();
    }

    @Override
    public Object visitPopulatedHandler(HyperTalkParser.PopulatedHandlerContext ctx) {
        return new NamedBlock((String) visit(ctx.blockName(0)), (String) visit(ctx.blockName(1)), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitPopulatedArgHandler(HyperTalkParser.PopulatedArgHandlerContext ctx) {
        return new NamedBlock((String) visit(ctx.blockName(0)), (String) visit(ctx.blockName(1)), (ParameterList) visit(ctx.parameterList()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitEmptyHandler(HyperTalkParser.EmptyHandlerContext ctx) {
        return new NamedBlock((String) visit(ctx.blockName(0)), (String) visit(ctx.blockName(1)), new StatementList());
    }

    @Override
    public Object visitEmptyArgHandler(HyperTalkParser.EmptyArgHandlerContext ctx) {
        return new NamedBlock((String) visit(ctx.blockName(0)), (String) visit(ctx.blockName(1)), (ParameterList) visit(ctx.parameterList()), new StatementList());
    }

    @Override
    public Object visitBlockName(HyperTalkParser.BlockNameContext ctx) {
        return super.visitBlockName(ctx);
    }

    @Override
    public Object visitPopulatedFunction(HyperTalkParser.PopulatedFunctionContext ctx) {
        return new UserFunction((String) visit(ctx.ID(0)), (String) visit(ctx.ID(1)), (ParameterList) visit(ctx.parameterList()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitEmptyFunction(HyperTalkParser.EmptyFunctionContext ctx) {
        return new UserFunction((String) visit(ctx.ID(0)), (String) visit(ctx.ID(1)), (ParameterList) visit(ctx.parameterList()), new StatementList());
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
        return new ExpressionStatement((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitVoidReturnStmnt(HyperTalkParser.VoidReturnStmntContext ctx) {
        return new ReturnStatement();
    }

    @Override
    public Object visitEprReturnStmnt(HyperTalkParser.EprReturnStmntContext ctx) {
        return new ReturnStatement((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitDoStmnt(HyperTalkParser.DoStmntContext ctx) {
        return new DoCmd((Expression) visit(ctx.expression()));
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
        return new GetCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSetCmdStmnt(HyperTalkParser.SetCmdStmntContext ctx) {
        return new SetCmd((PropertySpecifier) visit(ctx.propertySpec()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitSendCmdStmnt(HyperTalkParser.SendCmdStmntContext ctx) {
        return new SendCmd((PartExp) visit(ctx.part()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitAddCmdStmnt(HyperTalkParser.AddCmdStmntContext ctx) {
        return new AddCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitSubtractCmdStmnt(HyperTalkParser.SubtractCmdStmntContext ctx) {
        return new SubtractCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitMultiplyCmdStmnt(HyperTalkParser.MultiplyCmdStmntContext ctx) {
        return new MultiplyCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitDivideCmdStmnt(HyperTalkParser.DivideCmdStmntContext ctx) {
        return new DivideCmd((Expression) visit(ctx.expression()), (Container) visit(ctx.container()));
    }

    @Override
    public Object visitChooseToolCmdStmt(HyperTalkParser.ChooseToolCmdStmtContext ctx) {
        return new ChooseCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitChooseToolNumberCmdStmt(HyperTalkParser.ChooseToolNumberCmdStmtContext ctx) {
        return new ChooseCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitClickCmdStmt(HyperTalkParser.ClickCmdStmtContext ctx) {
        return new ClickCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitClickWithKeyCmdStmt(HyperTalkParser.ClickWithKeyCmdStmtContext ctx) {
        return new ClickCmd((Expression) visit(ctx.expression()), (ExpressionList) visit(ctx.expressionList()));
    }

    @Override
    public Object visitDragCmdStmt(HyperTalkParser.DragCmdStmtContext ctx) {
        return new DragCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitDragWithKeyCmdStmt(HyperTalkParser.DragWithKeyCmdStmtContext ctx) {
        return new DragCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (ExpressionList) visit(ctx.expressionList()));
    }

    @Override
    public Object visitTypeCmdStmt(HyperTalkParser.TypeCmdStmtContext ctx) {
        return new TypeCmd((Expression) visit(ctx.expression()), false);
    }

    @Override
    public Object visitTypeWithCmdKeyCmdStmt(HyperTalkParser.TypeWithCmdKeyCmdStmtContext ctx) {
        return new TypeCmd((Expression) visit(ctx.expression()), true);
    }

    @Override
    public Object visitDeleteCmdStmt(HyperTalkParser.DeleteCmdStmtContext ctx) {
        return new DeletePartCmd((PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitDeleteChunkCmdStmt(HyperTalkParser.DeleteChunkCmdStmtContext ctx) {
        return new DeleteChunkCmd((Container) visit(ctx.container()));
    }

    @Override
    public Object visitPlayCmdStmt(HyperTalkParser.PlayCmdStmtContext ctx) {
        return new PlayCmd((Expression) visit(ctx.expression()), (MusicSpecifier) visit(ctx.music()));
    }

    @Override
    public Object visitDialCmdStmt(HyperTalkParser.DialCmdStmtContext ctx) {
        return new DialCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitBeepCmdStmt(HyperTalkParser.BeepCmdStmtContext ctx) {
        return new BeepCmd();
    }

    @Override
    public Object visitOpenFileCmdStmt(HyperTalkParser.OpenFileCmdStmtContext ctx) {
        return new OpenCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitCloseFileCmdStmt(HyperTalkParser.CloseFileCmdStmtContext ctx) {
        return new CloseCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitNextRepeatCmdStmt(HyperTalkParser.NextRepeatCmdStmtContext ctx) {
        return new NextRepeatStatement();
    }

    @Override
    public Object visitExitRepeatCmdStmt(HyperTalkParser.ExitRepeatCmdStmtContext ctx) {
        return new ExitRepeatStatement();
    }

    @Override
    public Object visitExitCmdStmt(HyperTalkParser.ExitCmdStmtContext ctx) {
        return new ExitStatement((String) visit(ctx.blockName()));
    }

    @Override
    public Object visitNoArgMsgCmdStmt(HyperTalkParser.NoArgMsgCmdStmtContext ctx) {
        return new MessageCmd((String) visit(ctx.ID()), new ExpressionList());
    }

    @Override
    public Object visitArgMsgCmdStmt(HyperTalkParser.ArgMsgCmdStmtContext ctx) {
        return new MessageCmd((String) visit(ctx.ID()), (ExpressionList) visit(ctx.expressionList()));
    }

    @Override
    public Object visitSelectPartCmd(HyperTalkParser.SelectPartCmdContext ctx) {
        return new SelectPartCmd((PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitSelectEmptyCmd(HyperTalkParser.SelectEmptyCmdContext ctx) {
        return new SelectEmptyCmd();
    }

    @Override
    public Object visitSelectTextCmd(HyperTalkParser.SelectTextCmdContext ctx) {
        return new SelectTextCmd(Preposition.INTO, (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitSelectAfterChunkCmd(HyperTalkParser.SelectAfterChunkCmdContext ctx) {
        return new SelectTextCmd(Preposition.AFTER, (Chunk) visit(ctx.chunk()), (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitSelectBeforeChunkCmd(HyperTalkParser.SelectBeforeChunkCmdContext ctx) {
        return new SelectTextCmd(Preposition.BEFORE, (Chunk) visit(ctx.chunk()), (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitSelectAfterCmd(HyperTalkParser.SelectAfterCmdContext ctx) {
        return new SelectTextCmd(Preposition.AFTER, (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitSelectBeforeCmd(HyperTalkParser.SelectBeforeCmdContext ctx) {
        return new SelectTextCmd(Preposition.BEFORE, (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitSelectChunkCmd(HyperTalkParser.SelectChunkCmdContext ctx) {
        return new SelectTextCmd(Preposition.INTO, (Chunk) visit(ctx.chunk()), (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitWriteFileCmd(HyperTalkParser.WriteFileCmdContext ctx) {
        return WriteCmd.writeFile((Expression) visit(ctx.expression()), (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitWriteEndFileCmd(HyperTalkParser.WriteEndFileCmdContext ctx) {
        return WriteCmd.appendFile((Expression) visit(ctx.expression()), (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitWriteAtFileCmd(HyperTalkParser.WriteAtFileCmdContext ctx) {
        return WriteCmd.writeFileAt((Expression) visit(ctx.expression()), (Expression) visit(ctx.factor(0)), (Expression) visit(ctx.factor(1)));
    }

    @Override
    public Object visitReadFileCmd(HyperTalkParser.ReadFileCmdContext ctx) {
        return ReadCmd.ofFile((Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitReadFileForCmd(HyperTalkParser.ReadFileForCmdContext ctx) {
        return ReadCmd.ofFileFor((Expression) visit(ctx.factor(0)), (Expression) visit(ctx.factor(1)));
    }

    @Override
    public Object visitReadFileAtCmd(HyperTalkParser.ReadFileAtCmdContext ctx) {
        return ReadCmd.ofFileAt((Expression) visit(ctx.factor(0)), (Expression) visit(ctx.factor(1)), (Expression) visit(ctx.factor(2)));
    }

    @Override
    public Object visitReadFileUntil(HyperTalkParser.ReadFileUntilContext ctx) {
        return ReadCmd.ofFileUntil((Expression) visit(ctx.factor(0)), (Expression) visit(ctx.factor(1)));
    }

    @Override
    public Object visitMusicNotes(HyperTalkParser.MusicNotesContext ctx) {
        return MusicSpecifier.forNotes((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitMusicNotesTempo(HyperTalkParser.MusicNotesTempoContext ctx) {
        return MusicSpecifier.forNotesAndTempo((Expression) visit(ctx.expression()), (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitMusicTempo(HyperTalkParser.MusicTempoContext ctx) {
        return MusicSpecifier.forTempo((Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitMusicDefault(HyperTalkParser.MusicDefaultContext ctx) {
        return MusicSpecifier.forDefault();
    }

    @Override
    public Object visitLockScreenCmdStmt(HyperTalkParser.LockScreenCmdStmtContext ctx) {
        return new SetPropertyCmd(HyperCardProperties.PROP_LOCKSCREEN, new Value(true));
    }

    @Override
    public Object visitUnlockScreenCmdStmt(HyperTalkParser.UnlockScreenCmdStmtContext ctx) {
        return new SetPropertyCmd(HyperCardProperties.PROP_LOCKSCREEN, new Value(false));
    }

    @Override
    public Object visitUnlockScreenVisualCmdStmt(HyperTalkParser.UnlockScreenVisualCmdStmtContext ctx) {
        return new UnlockScreenCmd((VisualEffectSpecifier) visit(ctx.visualEffect()));
    }

    @Override
    public Object visitPassCmdStmt(HyperTalkParser.PassCmdStmtContext ctx) {
        return new PassCmd((String) visit(ctx.blockName()));
    }

    @Override
    public Object visitDoMenuCmdStmt(HyperTalkParser.DoMenuCmdStmtContext ctx) {
        return new DoMenuCmd((Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitVisualEffectCmdStmt(HyperTalkParser.VisualEffectCmdStmtContext ctx) {
        return new VisualEffectCmd((VisualEffectSpecifier) visit(ctx.visualEffect()));
    }

    @Override
    public Object visitResetMenuCmdStmt(HyperTalkParser.ResetMenuCmdStmtContext ctx) {
        return new ResetMenuCmd();
    }

    @Override
    public Object visitCreateMenuCmdStmt(HyperTalkParser.CreateMenuCmdStmtContext ctx) {
        return new CreateMenuCmd((Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitDeleteMenuCmdStmt(HyperTalkParser.DeleteMenuCmdStmtContext ctx) {
        return new DeleteMenuCmd((MenuSpecifier) visit(ctx.menu()));
    }

    @Override
    public Object visitDeleteMenuItemCmdStmt(HyperTalkParser.DeleteMenuItemCmdStmtContext ctx) {
        return new DeleteMenuItemCmd((MenuItemSpecifier) visit(ctx.menuItem()));
    }

    @Override
    public Object visitEnablePartCmd(HyperTalkParser.EnablePartCmdContext ctx) {
        return new SetPropertyCmd((PartExp) visit(ctx.part()), CardLayerPartModel.PROP_ENABLED, new Value(true));
    }

    @Override
    public Object visitEnableMenuItemCmd(HyperTalkParser.EnableMenuItemCmdContext ctx) {
        return new EnableMenuItemCmd((MenuItemSpecifier) visit(ctx.menuItem()), true);
    }

    @Override
    public Object visitEnableMenuCmd(HyperTalkParser.EnableMenuCmdContext ctx) {
        return new EnableMenuCmd((MenuSpecifier) visit(ctx.menu()), true);
    }

    @Override
    public Object visitDisablePartCmd(HyperTalkParser.DisablePartCmdContext ctx) {
        return new SetPropertyCmd((PartExp) visit(ctx.part()), CardLayerPartModel.PROP_ENABLED, new Value(false));
    }

    @Override
    public Object visitDisableMenuItemCmd(HyperTalkParser.DisableMenuItemCmdContext ctx) {
        return new EnableMenuItemCmd((MenuItemSpecifier) visit(ctx.menuItem()), false);
    }

    @Override
    public Object visitDisableMenuCmd(HyperTalkParser.DisableMenuCmdContext ctx) {
        return new EnableMenuCmd((MenuSpecifier) visit(ctx.menu()), false);
    }

    @Override
    public Object visitIfThenSingleLine(HyperTalkParser.IfThenSingleLineContext ctx) {
        return new IfStatement((Expression) visit(ctx.expression()), (ThenElseBlock) visit(ctx.singleThen()));
    }

    @Override
    public Object visitIfThenMultiline(HyperTalkParser.IfThenMultilineContext ctx) {
        return new IfStatement((Expression) visit(ctx.expression()), (ThenElseBlock) visit(ctx.multiThen()));
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
        return new RepeatStatement((RepeatSpecifier) visit(ctx.repeatRange()), (StatementList) visit(ctx.statementList()));
    }

    @Override
    public Object visitRepeatEmpty(HyperTalkParser.RepeatEmptyContext ctx) {
        return new RepeatStatement((RepeatSpecifier) visit(ctx.repeatRange()), new StatementList());
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
        return new GlobalStatement((String) visit(ctx.ID()));
    }

    @Override
    public Object visitAnswerThreeButtonCmd(HyperTalkParser.AnswerThreeButtonCmdContext ctx) {
        return new AnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (Expression) visit(ctx.expression(2)), (Expression) visit(ctx.expression(3)));
    }

    @Override
    public Object visitAnswerTwoButtonCmd(HyperTalkParser.AnswerTwoButtonCmdContext ctx) {
        return new AnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), (Expression) visit(ctx.expression(2)));
    }

    @Override
    public Object visitAnswerOneButtonCmd(HyperTalkParser.AnswerOneButtonCmdContext ctx) {
        return new AnswerCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAnswerDefaultCmd(HyperTalkParser.AnswerDefaultCmdContext ctx) {
        return new AnswerCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitAskExpWithCmd(HyperTalkParser.AskExpWithCmdContext ctx) {
        return new AskCmd((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAskExpCmd(HyperTalkParser.AskExpCmdContext ctx) {
        return new AskCmd((Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitPutIntoCmd(HyperTalkParser.PutIntoCmdContext ctx) {
        return new PutCmd((Expression) visit(ctx.expression()), Preposition.INTO, new ContainerMsgBox());
    }

    @Override
    public Object visitPutPrepositionCmd(HyperTalkParser.PutPrepositionCmdContext ctx) {
        return new PutCmd((Expression) visit(ctx.expression()), (Preposition) visit(ctx.preposition()), (Container) visit(ctx.container()));
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
        return new ContainerPart((PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitChunkPartDest(HyperTalkParser.ChunkPartDestContext ctx) {
        return new ContainerPart((PartExp) visit(ctx.part()), (Chunk) visit(ctx.chunk()));
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
    public Object visitSelectionDest(HyperTalkParser.SelectionDestContext ctx) {
        return new ContainerSelection();
    }

    @Override
    public Object visitChunkSelectionDest(HyperTalkParser.ChunkSelectionDestContext ctx) {
        return new ContainerSelection((Chunk) visit(ctx.chunk()));
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
    public Object visitExpressionMenu(HyperTalkParser.ExpressionMenuContext ctx) {
        return new MenuSpecifier((Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitOrdinalMenu(HyperTalkParser.OrdinalMenuContext ctx) {
        return new MenuSpecifier((Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitExpressionMenuItem(HyperTalkParser.ExpressionMenuItemContext ctx) {
        return new MenuItemSpecifier((Expression) visit(ctx.factor()), (MenuSpecifier) visit(ctx.menu()));
    }

    @Override
    public Object visitOrdinalMenuItem(HyperTalkParser.OrdinalMenuItemContext ctx) {
        return new MenuItemSpecifier((Ordinal) visit(ctx.ordinal()), (MenuSpecifier) visit(ctx.menu()));
    }

    @Override
    public Object visitPropertySpecGlobal(HyperTalkParser.PropertySpecGlobalContext ctx) {
        return new PropertySpecifier((String) visit(ctx.propertyName()));
    }

    @Override
    public Object visitPropertySpecPart(HyperTalkParser.PropertySpecPartContext ctx) {
        return new PropertySpecifier((String) visit(ctx.propertyName()), (PartExp) visit(ctx.part()));
    }

    @Override
    public Object visitPropertySpecMenuItem(HyperTalkParser.PropertySpecMenuItemContext ctx) {
        return new PropertySpecifier((String) visit(ctx.propertyName()), (MenuItemSpecifier) visit(ctx.menuItem()));
    }

    @Override
    public Object visitPropertyName(HyperTalkParser.PropertyNameContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitButtonPartPart(HyperTalkParser.ButtonPartPartContext ctx) {
        return visit(ctx.buttonPart());
    }

    @Override
    public Object visitFieldPartPart(HyperTalkParser.FieldPartPartContext ctx) {
        return visit(ctx.fieldPart());
    }

    @Override
    public Object visitBkgndPartPart(HyperTalkParser.BkgndPartPartContext ctx) {
        return visit(ctx.bkgndPart());
    }

    @Override
    public Object visitCardPartPart(HyperTalkParser.CardPartPartContext ctx) {
        return visit(ctx.cardPart());
    }

    @Override
    public Object visitBkgndFieldPart(HyperTalkParser.BkgndFieldPartContext ctx) {
        return new PartNameExp(Owner.BACKGROUND, PartType.FIELD, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitBkgndFieldOrdinalPart(HyperTalkParser.BkgndFieldOrdinalPartContext ctx) {
        return new PartNumberExp(Owner.BACKGROUND, PartType.FIELD, (Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitBkgndFieldIdPart(HyperTalkParser.BkgndFieldIdPartContext ctx) {
        return new PartIdExp(Owner.BACKGROUND, PartType.FIELD, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitBkgndButtonPart(HyperTalkParser.BkgndButtonPartContext ctx) {
        return new PartNameExp(Owner.BACKGROUND, PartType.BUTTON, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitBkgndButtonOrdinalPart(HyperTalkParser.BkgndButtonOrdinalPartContext ctx) {
        return new PartNumberExp(Owner.BACKGROUND, PartType.BUTTON, (Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitBkgndButtonIdPart(HyperTalkParser.BkgndButtonIdPartContext ctx) {
        return new PartIdExp(Owner.BACKGROUND, PartType.BUTTON, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitCardFieldPart(HyperTalkParser.CardFieldPartContext ctx) {
        return new PartNameExp(Owner.CARD, PartType.FIELD, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitCardFieldOrdinalPart(HyperTalkParser.CardFieldOrdinalPartContext ctx) {
        return new PartNumberExp(Owner.CARD, PartType.FIELD, (Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitCardFieldIdPart(HyperTalkParser.CardFieldIdPartContext ctx) {
        return new PartIdExp(Owner.CARD, PartType.FIELD, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitCardButtonPart(HyperTalkParser.CardButtonPartContext ctx) {
        return new PartNameExp(Owner.CARD, PartType.BUTTON, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitCardButtonOrdinalPart(HyperTalkParser.CardButtonOrdinalPartContext ctx) {
        return new PartNumberExp(Owner.CARD, PartType.BUTTON, (Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitCardButtonIdPart(HyperTalkParser.CardButtonIdPartContext ctx) {
        return new PartIdExp(Owner.CARD, PartType.BUTTON, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitCardPartNumberPart(HyperTalkParser.CardPartNumberPartContext ctx) {
        return new PartNumberExp(Owner.CARD, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitBkgndPartNumberPart(HyperTalkParser.BkgndPartNumberPartContext ctx) {
        return new PartNumberExp(Owner.BACKGROUND, (Expression)visit(ctx.factor()));
    }

    @Override
    public Object visitMePart(HyperTalkParser.MePartContext ctx) {
        return new PartMeExp();
    }

    @Override
    public Object visitThisCardPart(HyperTalkParser.ThisCardPartContext ctx) {
        return new PartPositionExp(PartType.CARD, Position.THIS);
    }

    @Override
    public Object visitThisBkgndPart(HyperTalkParser.ThisBkgndPartContext ctx) {
        return new PartPositionExp(PartType.BACKGROUND, Position.THIS);
    }

    @Override
    public Object visitPositionCardPart(HyperTalkParser.PositionCardPartContext ctx) {
        return new PartPositionExp(PartType.CARD, (Position) visit(ctx.position()));
    }

    @Override
    public Object visitPositionBkgndPart(HyperTalkParser.PositionBkgndPartContext ctx) {
        return new PartPositionExp(PartType.BACKGROUND, (Position) visit(ctx.position()));
    }

    @Override
    public Object visitOrdinalCardPart(HyperTalkParser.OrdinalCardPartContext ctx) {
        return new PartNumberExp(PartType.CARD, (Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitOrdinalBkgndPart(HyperTalkParser.OrdinalBkgndPartContext ctx) {
        return new PartNumberExp(PartType.BACKGROUND, (Ordinal) visit(ctx.ordinal()));
    }

    @Override
    public Object visitExpressionCardPart(HyperTalkParser.ExpressionCardPartContext ctx) {
        return new PartNameExp(PartType.CARD, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitCardIdPart(HyperTalkParser.CardIdPartContext ctx) {
        return new PartIdExp(PartType.CARD, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitExpressionBkgndPart(HyperTalkParser.ExpressionBkgndPartContext ctx) {
        return new PartNameExp(PartType.BACKGROUND, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitBkgndIdPart(HyperTalkParser.BkgndIdPartContext ctx) {
        return new PartIdExp(PartType.BACKGROUND, (Expression) visit(ctx.factor()));
    }

    @Override
    public Object visitOrdinalCharChunk(HyperTalkParser.OrdinalCharChunkContext ctx) {
        return new Chunk(ChunkType.CHAR, new LiteralExp(((Ordinal) visit(ctx.ordinal())).stringValue()));
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
        return new Chunk(ChunkType.WORD, new LiteralExp(((Ordinal) visit(ctx.ordinal())).stringValue()));
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
        return new Chunk(ChunkType.ITEM, new LiteralExp(((Ordinal) visit(ctx.ordinal())).stringValue()));
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
        return new Chunk(ChunkType.LINE, new LiteralExp(((Ordinal) visit(ctx.ordinal())).stringValue()));
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
        return Ordinal.EIGHTH;
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
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitWithinExp(HyperTalkParser.WithinExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitChunkExp(HyperTalkParser.ChunkExpContext ctx) {
        return new ChunkExp((Chunk) visit(ctx.chunk()), (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitOrExp(HyperTalkParser.OrExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.OR, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAndExp(HyperTalkParser.AndExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.AND, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitEqualityExp(HyperTalkParser.EqualityExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitCaratExp(HyperTalkParser.CaratExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.EXP, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitComparisonExp(HyperTalkParser.ComparisonExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitAdditionExp(HyperTalkParser.AdditionExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitNegateExp(HyperTalkParser.NegateExpContext ctx) {
        return new UnaryOperatorExp(UnaryOperator.NEGATE, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitConstantExp(HyperTalkParser.ConstantExpContext ctx) {
        return visit(ctx.constant());
    }

    @Override
    public Object visitNotExp(HyperTalkParser.NotExpContext ctx) {
        return new UnaryOperatorExp(UnaryOperator.NOT, (Expression) visit(ctx.expression()));
    }

    @Override
    public Object visitFactorExp(HyperTalkParser.FactorExpContext ctx) {
        return visit(ctx.factor());
    }

    @Override
    public Object visitFunctionExp(HyperTalkParser.FunctionExpContext ctx) {
        return new UserFunctionExp((String) visit(ctx.ID()), (ExpressionList) visit(ctx.expressionList()));
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
    public Object visitOptionKeyFunc(HyperTalkParser.OptionKeyFuncContext ctx) {
        return BuiltInFunction.OPTION_KEY;
    }

    @Override
    public Object visitSelectionFunc(HyperTalkParser.SelectionFuncContext ctx) {
        return BuiltInFunction.SELECTION;
    }

    @Override
    public Object visitShiftKeyFunc(HyperTalkParser.ShiftKeyFuncContext ctx) {
        return BuiltInFunction.SHIFT_KEY;
    }

    @Override
    public Object visitCommandKeyFunc(HyperTalkParser.CommandKeyFuncContext ctx) {
        return BuiltInFunction.COMMAND_KEY;
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
    public Object visitLongDateFormatFunc(HyperTalkParser.LongDateFormatFuncContext ctx) {
        return BuiltInFunction.LONG_DATE;
    }

    @Override
    public Object visitAbbrevTimeFormatFunc(HyperTalkParser.AbbrevTimeFormatFuncContext ctx) {
        return BuiltInFunction.ABBREV_TIME;
    }

    @Override
    public Object visitToolFunc(HyperTalkParser.ToolFuncContext ctx) {
        return BuiltInFunction.TOOL;
    }

    @Override
    public Object visitLongTimeFormatFunc(HyperTalkParser.LongTimeFormatFuncContext ctx) {
        return BuiltInFunction.LONG_TIME;
    }

    @Override
    public Object visitAbbrevDateFormatFunc(HyperTalkParser.AbbrevDateFormatFuncContext ctx) {
        return BuiltInFunction.ABBREV_DATE;
    }

    @Override
    public Object visitShortDateFormatFunc(HyperTalkParser.ShortDateFormatFuncContext ctx) {
        return BuiltInFunction.SHORT_DATE;
    }

    @Override
    public Object visitLiteralFactor(HyperTalkParser.LiteralFactorContext ctx) {
        return new LiteralExp(visit(ctx.literal()));
    }

    @Override
    public Object visitIdFactor(HyperTalkParser.IdFactorContext ctx) {
        return new VariableExp((String) visit(ctx.ID()));
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
        return new PropertyExp((PropertySpecifier) visit(ctx.propertySpec()));
    }

    @Override
    public Object visitMenuFactor(HyperTalkParser.MenuFactorContext ctx) {
        return new MenuExp((MenuSpecifier) visit(ctx.menu()));
    }

    @Override
    public Object visitMenuItemFactor(HyperTalkParser.MenuItemFactorContext ctx) {
        return new MenuExp((MenuItemSpecifier) visit(ctx.menuItem()));
    }

    @Override
    public Object visitTruncFunc(HyperTalkParser.TruncFuncContext ctx) {
        return BuiltInFunction.TRUNC;
    }

    @Override
    public Object visitEmptyExp(HyperTalkParser.EmptyExpContext ctx) {
        return new LiteralExp("");
    }

    @Override
    public Object visitPiExp(HyperTalkParser.PiExpContext ctx) {
        return new LiteralExp("3.14159265358979323846");
    }

    @Override
    public Object visitQuoteExp(HyperTalkParser.QuoteExpContext ctx) {
        return new LiteralExp("\"");
    }

    @Override
    public Object visitReturnExp(HyperTalkParser.ReturnExpContext ctx) {
        return new LiteralExp("\n");
    }

    @Override
    public Object visitSpaceExp(HyperTalkParser.SpaceExpContext ctx) {
        return new LiteralExp(" ");
    }

    @Override
    public Object visitTabExp(HyperTalkParser.TabExpContext ctx) {
        return new LiteralExp("\t");
    }

    @Override
    public Object visitFormFeedExp(HyperTalkParser.FormFeedExpContext ctx) {
        return new LiteralExp("\f");
    }

    @Override
    public Object visitLineFeedExp(HyperTalkParser.LineFeedExpContext ctx) {
        return new LiteralExp("\n");
    }

    @Override
    public Object visitCommaExp(HyperTalkParser.CommaExpContext ctx) {
        return new LiteralExp(",");
    }

    @Override
    public Object visitColonExp(HyperTalkParser.ColonExpContext ctx) {
        return new LiteralExp(":");
    }

    @Override
    public Object visitConcatExp(HyperTalkParser.ConcatExpContext ctx) {
        return new BinaryOperatorExp((Expression) visit(ctx.expression(0)), BinaryOperator.fromName(ctx.op.getText()), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitWaitForCountCmd(HyperTalkParser.WaitForCountCmdContext ctx) {
        return new WaitCmd((Expression) visit(ctx.factor()), (TimeUnit) visit(ctx.timeUnit()));
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
        return visit(ctx.builtInFunc());
    }

    @Override
    public Object visitBuiltinFuncOneArgs(HyperTalkParser.BuiltinFuncOneArgsContext ctx) {
        switch ((BuiltInFunction) visit(ctx.oneArgFunc())) {
            case MIN: return new MinFunc((Expression) visit(ctx.factor()));
            case MAX: return new MaxFunc((Expression) visit(ctx.factor()));
            case SUM: return new SumFunc((Expression) visit(ctx.factor()));
            case AVERAGE: return new AverageFunc((Expression) visit(ctx.factor()));
            case NUMBER_CHARS: return new NumberOfFunc(Countable.CHAR, (Expression) visit(ctx.factor()));
            case NUMBER_ITEMS: return new NumberOfFunc(Countable.ITEM, (Expression) visit(ctx.factor()));
            case NUMBER_LINES: return new NumberOfFunc(Countable.LINE, (Expression) visit(ctx.factor()));
            case NUMBER_WORDS: return new NumberOfFunc(Countable.WORD, (Expression) visit(ctx.factor()));
            case NUMBER_MENUITEMS: return new NumberOfFunc(Countable.MENU_ITEMS, (Expression) visit(ctx.factor()));
            case NUMBER_BKGND_CARDS: return new NumberOfFunc(Countable.BKGND_CARDS, (Expression) visit(ctx.factor()));
            case RANDOM: return new RandomFunc((Expression) visit(ctx.factor()));
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
                return new MathFunc((BuiltInFunction) visit(ctx.oneArgFunc()), (Expression) visit(ctx.factor()));
            case CHAR_TO_NUM: return new CharToNumFunc((Expression) visit(ctx.factor()));
            case VALUE: return new ValueFunc((Expression) visit(ctx.factor()));
            case LENGTH: return new NumberOfFunc(Countable.CHAR, (Expression) visit(ctx.factor()));
            case DISK_SPACE: return new DiskSpaceFunc((Expression) visit(ctx.factor()));
            case PARAM: return new ParamFunc((Expression) visit(ctx.factor()));

            default: throw new RuntimeException("Bug! Unimplemented one-arg function: " + ctx.oneArgFunc().getText());
        }
    }

    @Override
    public Object visitBuiltinFuncNoArg(HyperTalkParser.BuiltinFuncNoArgContext ctx) {
        switch ((BuiltInFunction) visit(ctx.noArgFunc())) {
            case MOUSE: return new MouseFunc();
            case MOUSELOC: return new MouseLocFunc();
            case RESULT: return new ResultFunc();
            case MESSAGE: return new MessageBoxFunc();
            case SELECTION: return new SelectionFunc();
            case TICKS: return new TicksFunc();
            case SECONDS: return new SecondsFunc();
            case ABBREV_DATE: return new DateFunc(DateLength.ABBREVIATED);
            case SHORT_DATE: return new DateFunc(DateLength.SHORT);
            case LONG_DATE: return new DateFunc(DateLength.LONG);
            case ABBREV_TIME: return new TimeFunc(DateLength.ABBREVIATED);
            case LONG_TIME: return new TimeFunc(DateLength.LONG);
            case SHORT_TIME: return new TimeFunc(DateLength.SHORT);
            case OPTION_KEY: return new ModifierKeyFunc(ModifierKey.OPTION);
            case COMMAND_KEY: return new ModifierKeyFunc(ModifierKey.COMMAND);
            case SHIFT_KEY: return new ModifierKeyFunc(ModifierKey.SHIFT);
            case TOOL: return new ToolFunc();
            case NUMBER_CARD_PARTS: return new NumberOfFunc(Countable.CARD_PARTS);
            case NUMBER_BKGND_PARTS: return new NumberOfFunc(Countable.BKGND_PARTS);
            case NUMBER_CARD_BUTTONS: return new NumberOfFunc(Countable.CARD_BUTTONS);
            case NUMBER_BKGND_BUTTONS: return new NumberOfFunc(Countable.BKGND_BUTTONS);
            case NUMBER_CARD_FIELDS: return new NumberOfFunc(Countable.CARD_FIELDS);
            case NUMBER_BKGND_FIELDS: return new NumberOfFunc(Countable.BKGND_FIELDS);
            case NUMBER_MENUS: return new NumberOfFunc(Countable.MENUS);
            case NUMBER_CARDS: return new NumberOfFunc(Countable.CARDS);
            case NUMBER_MARKED_CARDS: return new NumberOfFunc(Countable.MARKED_CARDS);
            case NUMBER_BKGNDS: return new NumberOfFunc(Countable.BKGNDS);
            case MENUS: return new MenusFunc();
            case DISK_SPACE: return new DiskSpaceFunc();
            case PARAM_COUNT: return new ParamCountFunc();
            case PARAMS: return new ParamsFunc();

            default: throw new RuntimeException("Bug! Unimplemented no-arg function: " + ctx.noArgFunc().getText());
        }
    }

    @Override
    public Object visitBuiltinFuncArgList(HyperTalkParser.BuiltinFuncArgListContext ctx) {
        switch ((BuiltInFunction) visit(ctx.argFunc())) {
            case MIN: return new MinFunc((ExpressionList) visit(ctx.expressionList()));
            case MAX: return new MaxFunc((ExpressionList) visit(ctx.expressionList()));
            case SUM: return new SumFunc((ExpressionList) visit(ctx.expressionList()));
            case AVERAGE: return new AverageFunc((ExpressionList) visit(ctx.expressionList()));
            case RANDOM: return new RandomFunc((ExpressionList) visit(ctx.expressionList()));
            case ANNUITY: return new AnnuityFunc((ExpressionList) visit(ctx.expressionList()));
            case COMPOUND: return new CompoundFunc((ExpressionList) visit(ctx.expressionList()));
            case OFFSET: return new OffsetFunc((ExpressionList) visit(ctx.expressionList()));
            default: throw new RuntimeException("Bug! Unimplemented arg-list function: " + ctx.argFunc().getText());
        }
    }

    @Override
    public Object visitOneArgArgFunc(HyperTalkParser.OneArgArgFuncContext ctx) {
        return visit(ctx.oneArgFunc());
    }

    @Override
    public Object visitAnnuityArgFunc(HyperTalkParser.AnnuityArgFuncContext ctx) {
        return BuiltInFunction.ANNUITY;
    }

    @Override
    public Object visitCompoundArgFunc(HyperTalkParser.CompoundArgFuncContext ctx) {
        return BuiltInFunction.COMPOUND;
    }

    @Override
    public Object visitOffsetArgFunc(HyperTalkParser.OffsetArgFuncContext ctx) {
        return BuiltInFunction.OFFSET;
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
    public Object visitSumFunc(HyperTalkParser.SumFuncContext ctx) {
        return BuiltInFunction.SUM;
    }

    @Override
    public Object visitNumberOfCardButtons(HyperTalkParser.NumberOfCardButtonsContext ctx) {
        return BuiltInFunction.NUMBER_CARD_BUTTONS;
    }

    @Override
    public Object visitNumberOfBkgndButtons(HyperTalkParser.NumberOfBkgndButtonsContext ctx) {
        return BuiltInFunction.NUMBER_BKGND_BUTTONS;
    }

    @Override
    public Object visitNumberOfCardFields(HyperTalkParser.NumberOfCardFieldsContext ctx) {
        return BuiltInFunction.NUMBER_CARD_FIELDS;
    }

    @Override
    public Object visitNumberOfBkgndFields(HyperTalkParser.NumberOfBkgndFieldsContext ctx) {
        return BuiltInFunction.NUMBER_BKGND_FIELDS;
    }

    @Override
    public Object visitMenusFunc(HyperTalkParser.MenusFuncContext ctx) {
        return BuiltInFunction.MENUS;
    }

    @Override
    public Object visitLiteral(HyperTalkParser.LiteralContext ctx) {
        String literal = ctx.getText();

        // Drop quotes from quoted string literal when converting a value
        if (literal.startsWith("\"") && literal.endsWith("\"")) {
            return new Value(String.valueOf(literal.substring(1, literal.length() - 1)));
        }

        return new Value(ctx.getText());
    }

    @Override
    public Object visitDiskSpaceNoArgFunc(HyperTalkParser.DiskSpaceNoArgFuncContext ctx) {
        return BuiltInFunction.DISK_SPACE;
    }

    @Override
    public Object visitParamsFunc(HyperTalkParser.ParamsFuncContext ctx) {
        return BuiltInFunction.PARAMS;
    }

    @Override
    public Object visitParamCountFunc(HyperTalkParser.ParamCountFuncContext ctx) {
        return BuiltInFunction.PARAM_COUNT;
    }

    @Override
    public Object visitDiskSpaceFunc(HyperTalkParser.DiskSpaceFuncContext ctx) {
        return BuiltInFunction.DISK_SPACE;
    }

    @Override
    public Object visitParamFunc(HyperTalkParser.ParamFuncContext ctx) {
        return BuiltInFunction.PARAM;
    }

    @Override
    public Object visitNumberOfCardParts(HyperTalkParser.NumberOfCardPartsContext ctx) {
        return BuiltInFunction.NUMBER_CARD_PARTS;
    }

    @Override
    public Object visitNumberOfBkgndParts(HyperTalkParser.NumberOfBkgndPartsContext ctx) {
        return BuiltInFunction.NUMBER_BKGND_PARTS;
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
    public Object visitNumberOfMenuItemsFunc(HyperTalkParser.NumberOfMenuItemsFuncContext ctx) {
        return BuiltInFunction.NUMBER_MENUITEMS;
    }

    @Override
    public Object visitNumberOfBkgndCardsFunc(HyperTalkParser.NumberOfBkgndCardsFuncContext ctx) {
        return BuiltInFunction.NUMBER_BKGND_CARDS;
    }

    @Override
    public Object visitNumberOfMenusFunc(HyperTalkParser.NumberOfMenusFuncContext ctx) {
        return BuiltInFunction.NUMBER_MENUS;
    }

    @Override
    public Object visitNumberOfCardsFunc(HyperTalkParser.NumberOfCardsFuncContext ctx) {
        return BuiltInFunction.NUMBER_CARDS;
    }

    @Override
    public Object visitNumberOfMarkedCards(HyperTalkParser.NumberOfMarkedCardsContext ctx) {
        return BuiltInFunction.NUMBER_MARKED_CARDS;
    }

    @Override
    public Object visitNumberOfBackgrounds(HyperTalkParser.NumberOfBackgroundsContext ctx) {
        return BuiltInFunction.NUMBER_BKGNDS;
    }

    @Override
    public Object visitRandomFunc(HyperTalkParser.RandomFuncContext ctx) {
        return BuiltInFunction.RANDOM;
    }

    @Override
    public Object visitSqrtFunc(HyperTalkParser.SqrtFuncContext ctx) {
        return BuiltInFunction.SQRT;
    }

    @Override
    public Object visitSinFunc(HyperTalkParser.SinFuncContext ctx) {
        return BuiltInFunction.SIN;
    }

    @Override
    public Object visitCosFunc(HyperTalkParser.CosFuncContext ctx) {
        return BuiltInFunction.COS;
    }

    @Override
    public Object visitTanFunc(HyperTalkParser.TanFuncContext ctx) {
        return BuiltInFunction.TAN;
    }

    @Override
    public Object visitAtanFunc(HyperTalkParser.AtanFuncContext ctx) {
        return BuiltInFunction.ATAN;
    }

    @Override
    public Object visitExpFunc(HyperTalkParser.ExpFuncContext ctx) {
        return BuiltInFunction.EXP;
    }

    @Override
    public Object visitExp1Func(HyperTalkParser.Exp1FuncContext ctx) {
        return BuiltInFunction.EXP1;
    }

    @Override
    public Object visitExp2Func(HyperTalkParser.Exp2FuncContext ctx) {
        return BuiltInFunction.EXP2;
    }

    @Override
    public Object visitLnFunc(HyperTalkParser.LnFuncContext ctx) {
        return BuiltInFunction.LN;
    }

    @Override
    public Object visitLn1Func(HyperTalkParser.Ln1FuncContext ctx) {
        return BuiltInFunction.LN1;
    }

    @Override
    public Object visitLog2Func(HyperTalkParser.Log2FuncContext ctx) {
        return BuiltInFunction.LOG2;
    }

    @Override
    public Object visitNumToCharFunc(HyperTalkParser.NumToCharFuncContext ctx) {
        return BuiltInFunction.NUM_TO_CHAR;
    }

    @Override
    public Object visitValueFunc(HyperTalkParser.ValueFuncContext ctx) {
        return BuiltInFunction.VALUE;
    }

    @Override
    public Object visitLengthFunc(HyperTalkParser.LengthFuncContext ctx) {
        return BuiltInFunction.LENGTH;
    }

    @Override
    public Object visitCharToNumFunc(HyperTalkParser.CharToNumFuncContext ctx) {
        return BuiltInFunction.CHAR_TO_NUM;
    }

    @Override
    public Object visitAbsFunc(HyperTalkParser.AbsFuncContext ctx) {
        return BuiltInFunction.ABS;
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        return node.getText();
    }
}
