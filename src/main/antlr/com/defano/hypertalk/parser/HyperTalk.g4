/*
 * MIT License
 *
 * Copyright (c) 2017 Matt DeFano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

grammar HyperTalk;

// Start symbol accepting only well-formed HyperTalk scripts that consist of handlers, functions, whitespace and
// comments (representing scipts that are assignable to objects like buttons, fields and cards). Disallows statements or
// expressions that are not inside of a handler or function block.
script
    : handler script                                                                                                    # handlerScript
    | function script                                                                                                   # functionScript
    | NEWLINE script                                                                                                    # newlineScript
    | EOF                                                                                                               # emptyScript
    ;

// Start symbol accepting any sequence of HyperTalk statements, expressions, whitespace and comments. Suitable when
// evaluating the message box or HyperTalk strings via the 'do' command and 'value of' function.
scriptlet
    : statement EOF                                                                                                     # singleScriptlet
    | multilineScriptlet                                                                                                # mutliScriptlet
    ;

multilineScriptlet
    : statement NEWLINE multilineScriptlet                                                                              # stmntMultiScriptlet
    | statement EOF                                                                                                     # stmntScriptlet
    | NEWLINE multilineScriptlet                                                                                        # whitespaceScriptlet
    | EOF                                                                                                               # eofScriptlet
    ;

handler
    : 'on' handlerName NEWLINE+ statementList? 'end' handlerName                                                        # noArgHandler
    | 'on' handlerName parameterList NEWLINE+ statementList? 'end' handlerName                                          # argHandler
    ;

function
    : 'function' ID NEWLINE+ statementList? 'end' ID                                                                    # noArgFunction
    | 'function' ID parameterList NEWLINE+ statementList? 'end' ID                                                      # argFunction
    ;

handlerName
    : ID
    | commandName   // Handlers can take the name of a command keyword (other keywords are disallowed)
    ;

parameterList
    : ID                                                                                                                # singleParamList
    | parameterList ',' ID                                                                                              # multiParamList
    ;

statementList
    : statement NEWLINE+ statementList                                                                                  # multiStmntList
    | statement NEWLINE+                                                                                                # singleStmntList
    ;

statement
    : commandStmnt                                                                                                      # nonEmptyCommandStmnt
    | functionCall                                                                                                      # nonEmptyFuncStmnt
    | messageStatement                                                                                                  # nonEmptyMsgStmnt
    | expression                                                                                                        # nonEmptyExpStmnt
    | ifStatement                                                                                                       # nonEmptyIfStmnt
    | repeatStatement                                                                                                   # nonEmptyRepeatStmnt
    | globalStmnt                                                                                                       # nonEmptyGlobalStmnt
    | returnStmnt                                                                                                       # nonEmptyReturnStmnt
    ;

globalStmnt
    : 'global' parameterList
    ;

returnStmnt
    : 'return' expression                                                                                               # eprReturnStmnt
    | 'return'                                                                                                          # voidReturnStmnt
    ;

ifStatement
    : 'if' expression thenStatement
    ;

thenStatement
    : NEWLINE? 'then' statement                                                                                         # thenSingleLineStmnt
    | NEWLINE? 'then' statement NEWLINE? elseStatement?                                                                 # thenSingleStmnt
    | NEWLINE? 'then' NEWLINE+ statementList? (elseStatement | 'end' 'if')                                              # thenStmntList
    ;

elseStatement
    : 'else' statement (NEWLINE+ 'end' 'if')?                                                                           # elseSingleStmt
    | 'else' NEWLINE+ statementList? 'end' 'if'                                                                         # elseStmntList
    ;

repeatStatement
    : 'repeat' repeatRange NEWLINE+ statementList 'end' 'repeat'                                                        # repeatStmntList
    | 'repeat' repeatRange NEWLINE+ 'end' 'repeat'                                                                      # repeatEmpty
    ;

messageStatement
    : ID                                                                                                                # noArgMsgCmdStmt
    | ID listExpression                                                                                                 # argMsgCmdStmt
    ;

commandStmnt
    : 'add' expression 'to' expression                                                                                  # addCmdStmnt
    | 'answer' expression 'with' factor 'or' factor 'or' factor                                                         # answerThreeButtonCmd
    | 'answer' expression 'with' factor 'or' factor                                                                     # answerTwoButtonCmd
    | 'answer' expression 'with' factor                                                                                 # answerOneButtonCmd
    | 'answer' expression                                                                                               # answerDefaultCmd
    | 'ask' expression 'with' expression                                                                                # askExpWithCmd
    | 'ask' expression                                                                                                  # askExpCmd
    | 'beep'                                                                                                            # beepCmdStmt
    | 'beep' expression                                                                                                 # beepMultipleStmt
    | 'choose' toolExpression 'tool'?                                                                                   # chooseToolCmdStmt
    | 'choose' 'tool' toolExpression                                                                                    # chooseToolNumberCmdStmt
    | 'click' 'at' listExpression                                                                                       # clickCmdStmt
    | 'click' 'at' listExpression 'with' listExpression                                                                 # clickWithKeyCmdStmt
    | 'close' 'file' expression                                                                                         # closeFileCmdStmt
    | 'convert' container 'to' convertible                                                                              # convertContainerToCmd
    | 'convert' container 'from' convertible 'to' convertible                                                           # convertContainerFromToCmd
    | 'convert' expression 'to' convertible                                                                             # convertToCmd
    | 'convert' expression 'from' convertible 'to' convertible                                                          # convertFromToCmd
    | 'create' 'menu' expression                                                                                        # createMenuCmdStmt
    | 'delete' expression                                                                                               # deleteCmdStmt
    | 'dial' expression                                                                                                 # dialCmdStmt
    | 'disable' expression                                                                                              # disableExprStmt
    | 'divide' expression 'by' expression                                                                               # divideCmdStmnt
    | 'do' expression                                                                                                   # doCmdStmt
    | 'domenu' expression                                                                                               # doMenuCmdStmt
    | 'drag' 'from' listExpression 'to' listExpression                                                                  # dragCmdStmt
    | 'drag' 'from' listExpression 'to' listExpression 'with' listExpression                                            # dragWithKeyCmdStmt
    | 'edit' 'the'? 'script' of expression                                                                              # editScriptCmdStmt
    | 'enable' expression                                                                                               # enableExpStmnt
    | 'exit' handlerName                                                                                                # exitCmdStmt
    | 'exit' 'repeat'                                                                                                   # exitRepeatCmdStmt
    | 'exit' 'to' 'hypercard'                                                                                           # exitToHyperCardCmdStmt
    | 'export' 'paint' 'to' 'file' expression                                                                           # exportPaintCmdStmt
    | 'find' expression? 'international'? expression of expression of 'marked' cards                                    # findFieldMarkedCards
    | 'find' expression? 'international'? expression of expression                                                      # findField
    | 'find' expression? 'international'? expression of 'marked' cards                                                  # findMarkedCards
    | 'find' expression? 'international'? expression                                                                    # findAnywhere
    | 'get' expression                                                                                                  # getCmdStmnt
    | 'go' 'to'? expression 'with' 'visual' expression                                                                  # goVisualEffectCmdStmnd
    | 'go' 'to'? expression                                                                                             # goCmdStmnt
    | 'go' 'back'                                                                                                       # goBackCmdStmt
    | 'go' 'back' 'with' 'visual' expression                                                                            # goBackVisualEffectCmdStmt
    | 'hide' expression                                                                                                 # hideCmdStmnt
    | 'hide' card picture                                                                                               # hideThisCardPictCmd
    | 'hide' background picture                                                                                         # hideThisBkgndPictCmd
    | 'hide' picture of expression                                                                                      # hidePictCmd
    | 'hide' 'titlebar'                                                                                                 # hideTitleBar
    | 'import' 'paint' 'from' 'file' expression                                                                         # importPaintCmdStmt
    | 'lock' 'screen'                                                                                                   # lockScreenCmdStmt
    | 'multiply' expression 'by' expression                                                                             # multiplyCmdStmnt
    | 'next' 'repeat'                                                                                                   # nextRepeatCmdStmt
    | 'open' 'file' expression                                                                                          # openFileCmdStmt
    | 'pass' handlerName                                                                                                # passCmdStmt
    | 'play' musicExpression                                                                                            # playCmdStmt
    | 'pop' card                                                                                                        # popCardCmdStmt
    | 'push' card                                                                                                       # pushCardCmdStmt
    | 'push' expression                                                                                                 # pushDestCmdStmt
    | 'put' listExpression                                                                                              # putIntoCmd
    | 'put' listExpression preposition expression                                                                       # putPrepositionCmd
    | 'read' 'from' 'file' expression                                                                                   # readFileCmd
    | 'read' 'from' 'file' expression 'for' expression                                                                  # readFileForCmd
    | 'read' 'from' 'file' expression 'at' expression 'for' expression                                                  # readFileAtCmd
    | 'read' 'from' 'file' expression 'until' expression                                                                # readFileUntil
    | 'reset' 'the'? 'menubar'                                                                                          # resetMenuCmdStmt
    | 'reset' 'paint'                                                                                                   # resetPaintCmdStmt
    | 'select' 'empty'                                                                                                  # selectEmptyCmd
    | 'select' 'text' of expression                                                                                     # selectTextCmd
    | 'select' 'before' 'text' of expression                                                                            # selectBeforeCmd
    | 'select' 'after' 'text' of expression                                                                             # selectAfterCmd
    | 'select' expression                                                                                               # selectChunkCmd
    | 'select' 'before' expression                                                                                      # selectBeforeChunkCmd
    | 'select' 'after' expression                                                                                       # selectAfterChunkCmd
    | 'set' property 'to' propertyValue                                                                                 # setCmdStmnt
    | 'send' listExpression 'to' expression                                                                             # sendCmdStmnt
    | 'show' expression                                                                                                 # showCmdStmnt
    | 'show' card picture                                                                                               # showThisCardPictCmd
    | 'show' background picture                                                                                         # showThisBkgndPictCmd
    | 'show' picture of expression                                                                                      # showPictCmd
    | 'show' 'titlebar'                                                                                                 # showTitleBarCmd
    | 'sort' sortChunkType expression sortDirection sortStyle                                                           # sortDirectionCmd
    | 'sort' sortChunkType expression sortDirection sortStyle 'by' expression                                           # sortExpressionCmd
    | 'sort' sortDirection sortStyle 'by' expression                                                                    # sortStackCmd
    | 'sort' 'this'? 'stack' sortDirection sortStyle 'by' expression                                                    # sortStackCmd
    | 'sort' 'the'? cards (of 'this' 'stack')? sortDirection sortStyle 'by' expression                                  # sortStackCmd
    | 'sort' 'the'? 'marked' cards (of 'this' 'stack')? sortDirection sortStyle 'by' expression                         # sortMarkedCardsCmd
    | 'sort' expression sortDirection sortStyle 'by' expression                                                         # sortBkgndCardsCmd
    | 'sort' 'the'? cards of expression sortDirection sortStyle 'by' expression                                         # sortBkgndCardsCmd
    | 'sort' 'the'? 'marked' cards of expression sortDirection sortStyle 'by' expression                                # sortMarkedBkgndCardsCmd
    | 'speak' expression                                                                                                # speakCmd
    | 'speak' expression 'with' gender=('male'|'female'|'neuter'|'robotic') 'voice'                                     # speakGenderCmd
    | 'speak' expression 'with' 'voice' expression                                                                      # speakVoiceCmd
    | 'subtract' expression 'from' expression                                                                           # subtractCmdStmnt
    | 'type' expression                                                                                                 # typeCmdStmt
    | 'type' expression 'with' ('commandkey' | 'cmdkey')                                                                # typeWithCmdKeyCmdStmt
    | 'unlock' 'screen'                                                                                                 # unlockScreenCmdStmt
    | 'unlock' 'screen' 'with' 'visual' expression                                                                      # unlockScreenVisualCmdStmt
    | 'visual' expression                                                                                               # visualEffectCmdStmt
    | 'wait' expression timeUnit                                                                                        # waitCountCmd
    | 'wait' 'for' expression timeUnit                                                                                  # waitForCountCmd
    | 'wait' 'until' expression                                                                                         # waitUntilCmd
    | 'wait' 'while' expression                                                                                         # waitWhileCmd
    | 'write' expression 'to' 'file' expression                                                                         # writeFileCmd
    | 'write' expression 'to' 'file' expression 'at' ('eof' | 'end')                                                    # writeEndFileCmd
    | 'write' expression 'to' 'file' expression 'at' expression                                                         # writeAtFileCmd
    ;

convertible
    : conversionFormat                                                                                                  # singleFormatConvertible
    | conversionFormat 'and' conversionFormat                                                                           # dualFormatConvertible
    ;

conversionFormat
    : seconds                                                                                                           # secondsConvFormat
    | 'dateitems'                                                                                                       # dateItemsConvFormat
    | length 'date'                                                                                                     # dateConvFormat
    | length 'time'                                                                                                     # timeConvFormat
    ;

length
    : ('english' | 'long')                                                                                              # longTimeFormat
    | ('abbreviated' | 'abbrev' | 'abbr')                                                                               # abbreviatedTimeFormat
    | 'short'                                                                                                           # shortTimeFormat
    |                                                                                                                   # defaultTimeFormat
    ;

sortDirection
    : 'ascending'                                                                                                       # sortDirectionAsc
    | 'descending'                                                                                                      # sortDirectionDesc
    |                                                                                                                   # sortDirectionDefault
    ;

sortChunkType
    : 'the'? line of                                                                                                    # sortChunkLines
    | 'the'? item of                                                                                                    # sortChunkItems
    | 'the'? word of                                                                                                    # sortChunkWords
    | 'the'? character of                                                                                               # sortChunkChars
    |                                                                                                                   # sortChunkDefault
    ;

sortStyle
    : 'text'                                                                                                            # sortStyleText
    | 'numeric'                                                                                                         # sortStyleNumeric
    | 'international'                                                                                                   # sortStyleInternational
    | 'datetime'                                                                                                        # sortStyleDateTime
    |                                                                                                                   # sortStyleDefault
    ;

repeatRange
    : duration                                                                                                          # durationLoop
    | count                                                                                                             # countLoop
    | 'with' ID '=' range                                                                                               # withLoop
    | 'forever'                                                                                                         # infiniteLoop
    |                                                                                                                   # infiniteLoop
    ;

duration
    : 'until' expression                                                                                                # untilDuration
    | 'while' expression                                                                                                # whileDuration
    ;

count
    : 'for' expression 'times'
    | 'for' expression
    | expression 'times'
    | expression
    ;

range
    : expression 'down' 'to' expression                                                                                 # rangeDownTo
    | expression 'to' expression                                                                                        # rangeUpTo
    ;

chunk
    : chunk chunk                                                                                                       # compositeChunk
    | ordinal character of                                                                                              # ordinalCharChunk
    | character expression 'to' expression of                                                                           # rangeCharChunk
    | character expression of                                                                                           # charCharChunk
    | ordinal word of                                                                                                   # ordinalWordChunk
    | word expression 'to' expression of                                                                                # rangeWordChunk
    | word expression of                                                                                                # wordWordChunk
    | ordinal item of                                                                                                   # ordinalItemChunk
    | item expression 'to' expression of                                                                                # rangeItemChunk
    | item expression of                                                                                                # itemItemChunk
    | ordinal line of                                                                                                   # ordinalLineChunk
    | line expression 'to' expression of                                                                                # rangeLineChunk
    | line expression of                                                                                                # lineLineChunk
    ;

menu
    : 'menu' factor                                                                                                     # expressionMenu
    | ordinal 'menu'                                                                                                    # ordinalMenu
    ;

menuItem
    : 'menuitem' factor of menu                                                                                         # expressionMenuItem
    | ordinal 'menuitem' of menu                                                                                        # ordinalMenuItem
    ;

property
    : partProperty
    | globalProperty
    ;

globalProperty
    : 'the'? propertyName                                                                                               # propertySpecGlobal
    ;

partProperty
    : 'the'? propertyName of factor                                                                                     # propertySpecPart
    | 'the'? length propertyName of factor                                                                              # lengthPropertySpecPart
    ;

part
    : message                                                                                                           # msgPart
    | card 'part' factor                                                                                                # cardPartNumberPart
    | background 'part' factor                                                                                          # bkgndPartNumberPart
    | 'me'                                                                                                              # mePart
    | buttonPart                                                                                                        # buttonPartPart
    | fieldPart                                                                                                         # fieldPartPart
    | bkgndPart                                                                                                         # bkgndPartPart
    | cardPart                                                                                                          # cardPartPart
    | stackPart                                                                                                         # stackPartPart
    | windowPart                                                                                                        # windowPartPart
    ;

stackPart
    : 'this'? stack                                                                                                     # thisStackPart
    | stack factor                                                                                                      # anotherStackPart
    ;

buttonPart
    : card? button 'id' factor                                                                                          # cardButtonIdPart
    | background button 'id' factor                                                                                     # bkgndButtonIdPart
    | card? button factor                                                                                               # cardButtonPart
    | background button factor                                                                                          # bkgndButtonPart
    | ordinal card? button                                                                                              # cardButtonOrdinalPart
    | ordinal background button                                                                                         # bkgndButtonOrdinalPart
    | buttonPart of cardPart                                                                                            # buttonOfCardPart
    ;

fieldPart
    : card field 'id' factor                                                                                            # cardFieldIdPart
    | background? field 'id' factor                                                                                     # bkgndFieldIdPart
    | card field factor                                                                                                 # cardFieldPart
    | background? field factor                                                                                          # bkgndFieldPart
    | ordinal card field                                                                                                # cardFieldOrdinalPart
    | ordinal background? field                                                                                         # bkgndFieldOrdinalPart
    | fieldPart of cardPart                                                                                             # fieldOfCardPart
    ;

cardPart
    : 'this'? card                                                                                                      # thisCardPart
    | card 'id' factor                                                                                                  # cardIdPart
    | position card                                                                                                     # positionCardPart
    | ordinal card                                                                                                      # ordinalCardPart
    | card factor                                                                                                       # expressionCardPart
    | cardPart of bkgndPart                                                                                             # cardOfBkgndPart
    ;

bkgndPart
    : 'this'? background                                                                                                # thisBkgndPart
    | background 'id' factor                                                                                            # bkgndIdPart
    | background factor                                                                                                 # expressionBkgndPart
    | ordinal background                                                                                                # ordinalBkgndPart
    | position background                                                                                               # positionBkgndPart
    ;

windowPart
    : 'the'? card 'window'                                                                                              # cardWindowExpr
    | 'the'? 'tool' 'window'                                                                                            # toolWindowExpr
    | 'the'? 'pattern' 'window'                                                                                         # patternWindowExpr
    | 'the'? 'message' 'watcher'                                                                                        # messageWatcherExpr
    | 'the'? 'variable' 'watcher'                                                                                       # variableWatcherExpr
    | 'window' expression                                                                                               # windowExprExpr
    ;

listExpression
    : expression                                                                                                        # singletonListExp
    | expression ',' listExpression                                                                                     # listExp
    ;

expression
    : factor                                                                                                            # factorExp
    | 'not' expression                                                                                                  # notExp
    | '-' expression                                                                                                    # negateExp
    | op=('there is a'|'there is an'|'there is no'|'there is not a'|'there is not an') expression                       # unaryOpExp
    | expression '^' expression                                                                                         # powOpExp
    | expression op=('mod'| 'div'| '/'| '*') expression                                                                 # binaryOpExp
    | expression op=('+'| '-') expression                                                                               # binaryOpExp
    | expression op=('&&'| '&') expression                                                                              # binaryOpExp
    | expression op=('>='|'<='|'≤'|'≥'|'<'|'>'|'contains'|'is in'|'is not in'|'is a'|'is an'|'is not a'|'is not an'|'is within'|'is not within') expression # binaryOpExp
    | expression op=('='|'is not'|'is'|'<>'|'≠') expression                                                             # binaryOpExp
    | expression 'and' expression                                                                                       # binaryAndExp
    | expression 'or' expression                                                                                        # binaryOrExp
    ;

factor
    : literal                                                                                                           # literalFactor
    | '-' literal                                                                                                       # negativeLiteralFactor
    | '(' expression ')'                                                                                                # expressionFactor
    | effectExpression                                                                                                  # visualEffectFactor
    | functionCall                                                                                                      # functionExp
    | container                                                                                                         # containerFactor
    | chunk factor                                                                                                      # chunkFactorChunk
    ;

container
    : ID                                                                                                                # variableDest
    | 'the'? 'selection'                                                                                                # selectionDest
    | 'target'                                                                                                          # targetDest
    | property                                                                                                          # propertyDest
    | menu                                                                                                              # menuDest
    | menuItem                                                                                                          # menuItemDest
    | message                                                                                                           # messageDest
    | part                                                                                                              # partDest
    | chunk container                                                                                                   # chunkContainerDest
    ;

musicExpression
    : expression expression                                                                                             # musicInstrumentNotes
    | expression 'tempo' expression expression                                                                          # musicInstrumentNotesTempo
    | expression 'tempo' expression                                                                                     # musicInstrumentTempo
    | expression                                                                                                        # musicInstrument
    ;

toolExpression
    : 'text'                                                                                                            # keywordToolExpr
    | 'select'                                                                                                          # keywordToolExpr
    | 'field'                                                                                                           # keywordToolExpr
    | 'button'                                                                                                          # keywordToolExpr
    | 'line'                                                                                                            # keywordToolExpr
    | ('reg' | 'regular')? ('poly' | 'polygon')                                                                         # keywordToolExpr
    | 'round'? ('rect' | 'rectangle')                                                                                   # keywordToolExpr
    | 'spray' 'can'?                                                                                                    # keywordToolExpr
    | expression                                                                                                        # toolExpr
    ;

effectExpression
    : 'effect'? effect                                                                                                  # effectDefault
    | 'effect'? effect 'to' image                                                                                       # effectTo
    | 'effect'? effect speed                                                                                            # effectSpeed
    | 'effect'? effect speed 'to' image                                                                                 # effectSpeedTo
    ;

functionCall
    : builtInFunc                                                                                                       # builtInFuncCall
    | ID '(' listExpression? ')'                                                                                        # userArgFuncCall
    ;

builtInFunc
    : 'the' zeroArgFunc                                                                                                 # builtinFuncNoArg
    | 'the'? singleArgFunc of factor                                                                                    # builtinFuncOneArgs
    | singleArgFunc '(' listExpression ')'                                                                              # builtinFuncOneArgs
    | multiArgFunc '(' listExpression ')'                                                                               # builtinFuncArgList
    ;

zeroArgFunc
    : 'mouse'                                                                                                           # mouseFunc
    | 'mouseloc'                                                                                                        # mouseLocFunc
    | 'result'                                                                                                          # resultFunc
    | ('commandkey' | 'cmdkey')                                                                                         # commandKeyFunc
    | 'shiftkey'                                                                                                        # shiftKeyFunc
    | 'optionkey'                                                                                                       # optionKeyFunc
    | 'ticks'                                                                                                           # ticksFunc
    | seconds                                                                                                           # secondsFunc
    | length 'time'                                                                                                     # timeFunc
    | length 'date'                                                                                                     # dateFunc
    | 'tool'                                                                                                            # toolFunc
    | 'mouseclick'                                                                                                      # mouseClickFunc
    | 'number' 'of' card? 'parts'                                                                                       # numberOfCardParts
    | 'number' 'of' background 'parts'                                                                                  # numberOfBkgndParts
    | 'number' 'of' card? button                                                                                        # numberOfCardButtons
    | 'number' 'of' background button                                                                                   # numberOfBkgndButtons
    | 'number' 'of' card field                                                                                          # numberOfCardFields
    | 'number' 'of' background? field                                                                                   # numberOfBkgndFields
    | 'number' 'of' 'menus'                                                                                             # numberOfMenusFunc
    | 'number' 'of' cards (of 'this' 'stack')?                                                                          # numberOfCardsFunc
    | 'number' 'of' 'marked' cards                                                                                      # numberOfMarkedCards
    | 'number' 'of' background (of 'this' 'stack')?                                                                     # numberOfBackgrounds
    | 'menus'                                                                                                           # menusFunc
    | 'diskspace'                                                                                                       # diskSpaceNoArgFunc
    | 'params'                                                                                                          # paramsFunc
    | 'paramcount'                                                                                                      # paramCountFunc
    | 'sound'                                                                                                           # propDelegatedFunc
    | 'selectedtext'                                                                                                    # propDelegatedFunc
    | 'selectedchunk'                                                                                                   # propDelegatedFunc
    | 'selectedfield'                                                                                                   # propDelegatedFunc
    | 'selectedline'                                                                                                    # propDelegatedFunc
    | 'target'                                                                                                          # targetFunc
    | 'speech'                                                                                                          # speechFunc
    | 'voices'                                                                                                          # voicesFunc
    | 'clicktext'                                                                                                       # propDelegatedFunc
    | 'mouseh'                                                                                                          # propDelegatedFunc
    | 'mousev'                                                                                                          # propDelegatedFunc
    | 'screenrect'                                                                                                      # propDelegatedFunc
    | 'clickloc'                                                                                                        # propDelegatedFunc
    | 'clickh'                                                                                                          # propDelegatedFunc
    | 'clickv'                                                                                                          # propDelegatedFunc
    | 'foundchunk'                                                                                                      # propDelegatedFunc
    | 'foundfield'                                                                                                      # propDelegatedFunc
    | 'foundline'                                                                                                       # propDelegatedFunc
    | 'foundtext'                                                                                                       # propDelegatedFunc
    | 'windows'                                                                                                         # windowsFunc
    ;

singleArgFunc
    : 'average'                                                                                                         # averageFunc
    | 'min'                                                                                                             # minFunc
    | 'max'                                                                                                             # maxFunc
    | 'sum'                                                                                                             # sumFunc
    | 'number' 'of' character                                                                                           # numberOfCharsFunc
    | 'number' 'of' word                                                                                                # numberOfWordsFunc
    | 'number' 'of' item                                                                                                # numberOfItemsFunc
    | 'number' 'of' line                                                                                                # numberOfLinesFunc
    | 'number' 'of' 'menuitems'                                                                                         # numberOfMenuItemsFunc
    | 'number' 'of' cards                                                                                               # numberOfBkgndCardsFunc
    | 'number'                                                                                                          # numberOfPart
    | 'random'                                                                                                          # randomFunc
    | 'sqrt'                                                                                                            # sqrtFunc
    | 'trunc'                                                                                                           # truncFunc
    | 'sin'                                                                                                             # sinFunc
    | 'cos'                                                                                                             # cosFunc
    | 'tan'                                                                                                             # tanFunc
    | 'atan'                                                                                                            # atanFunc
    | 'exp'                                                                                                             # expFunc
    | 'exp1'                                                                                                            # exp1Func
    | 'exp2'                                                                                                            # exp2Func
    | 'ln'                                                                                                              # lnFunc
    | 'ln1'                                                                                                             # ln1Func
    | 'log2'                                                                                                            # log2Func
    | 'abs'                                                                                                             # absFunc
    | 'chartonum'                                                                                                       # charToNumFunc
    | 'numtochar'                                                                                                       # numToCharFunc
    | 'value'                                                                                                           # valueFunc
    | 'length'                                                                                                          # lengthFunc
    | 'diskspace'                                                                                                       # diskSpaceFunc
    | 'param'                                                                                                           # paramFunc
    ;

multiArgFunc
    : singleArgFunc                                                                                                     # oneArgArgFunc
    | 'annuity'                                                                                                         # annuityArgFunc
    | 'compound'                                                                                                        # compoundArgFunc
    | 'offset'                                                                                                          # offsetArgFunc
    ;

literal
    : constant                                                                                                          # constantExp
    | modifierKey                                                                                                       # literalExp
    | mouseState                                                                                                        # literalExp
    | knownType                                                                                                         # literalExp
    | findType                                                                                                          # literalExp
    | LITERAL                                                                                                           # literalExp
    ;

preposition
    : 'before'                                                                                                          # beforePreposition
    | 'after'                                                                                                           # afterPreposition
    | 'into'                                                                                                            # intoPreposition
    ;

constant
    : cardinalValue                                                                                                     # cardninalExp
    | 'empty'                                                                                                           # emptyExp
    | 'pi'                                                                                                              # piExp
    | 'quote'                                                                                                           # quoteExp
    | 'return'                                                                                                          # returnExp
    | 'space'                                                                                                           # spaceExp
    | 'tab'                                                                                                             # tabExp
    | 'formfeed'                                                                                                        # formFeedExp
    | 'linefeed'                                                                                                        # lineFeedExp
    | 'comma'                                                                                                           # commaExp
    | 'colon'                                                                                                           # colonExp
    ;

cardinalValue
    : 'zero'
    | 'one'
    | 'two'
    | 'three'
    | 'four'
    | 'five'
    | 'six'
    | 'seven'
    | 'eight'
    | 'nine'
    | 'ten'
    ;

ordinal
    : 'the'? ordinalValue                                                                                               # theOrdinalVal
    ;

ordinalValue
    : 'first'
    | 'second'
    | 'third'
    | 'fourth'
    | 'fifth'
    | 'sixth'
    | 'seventh'
    | 'eighth'
    | 'ninth'
    | 'tenth'
    | ('mid' | 'middle')
    | 'last'
    | 'any'
    ;

mouseState
    : 'up'
    | 'down'
    ;

modifierKey
    : 'commandkey'
    | 'cmdkey'
    | 'optionkey'
    | 'shiftkey'
    ;

knownType
    : 'number'
    | 'integer'
    | 'point'
    | 'rect'
    | 'rectangle'
    | 'date'
    | 'logical'
    | 'boolean'
    | 'bool'
    ;

findType
    : 'word'
    | 'chars'
    | 'whole'
    | 'string'
    ;

// Not all properties need to be enumerated here, only those sharing a name with another keyword.
propertyName
    : 'marked'
    | 'selectedtext'
    | 'selectedchunk'
    | 'selectedfield'
    | 'selectedline'
    | 'number'
    | 'id'
    | 'rect'
    | 'rectangle'
    | 'bottom'
    | 'left'
    | 'right'
    | 'top'
    | 'center'
    | 'scroll'
    | 'script'
    | 'pattern'
    | ID
    ;

// Not all property values need to be enumerated here, only known values sharing a name with another keyword.
propertyValue
    : 'plain'                                                                                                           # propertyValueLiteral
    | 'menu'                                                                                                            # propertyValueLiteral
    | 'bottom'                                                                                                          # propertyValueLiteral
    | 'left'                                                                                                            # propertyValueLiteral
    | 'right'                                                                                                           # propertyValueLiteral
    | 'top'                                                                                                             # propertyValueLiteral
    | 'center'                                                                                                          # propertyValueLiteral
    | listExpression                                                                                                    # propertyValueExp
    ;

commandName
    : 'answer'
    | 'ask'
    | 'put'
    | 'get'
    | 'set'
    | 'send'
    | 'wait'
    | 'sort'
    | 'go'
    | 'enable'
    | 'disable'
    | 'read'
    | 'write'
    | 'hide'
    | 'show'
    | 'add'
    | 'subtract'
    | 'multiply'
    | 'divide'
    | 'choose'
    | 'click'
    | 'drag'
    | 'type'
    | 'lock'
    | 'unlock'
    | 'pass'
    | 'domenu'
    | 'visual'
    | 'reset'
    | 'create'
    | 'delete'
    | 'play'
    | 'dial'
    | 'beep'
    | 'open'
    | 'close'
    | 'select'
    | 'find'
    | 'import'
    | 'export'
    ;

picture
    : 'picture'
    | 'pict'
    ;

seconds
    : 'seconds'
    | 'secs'
    | 'second'
    | 'sec'
    ;

speed
    : 'fast'                                                                                                            # fastSpeed
    | ('slow' | 'slowly')                                                                                               # slowSpeed
    | 'very' 'fast'                                                                                                     # veryFastSpeed
    | 'very' ('slow' | 'slowly')                                                                                        # verySlowSpeed
    ;

image
    : 'black'                                                                                                           # blackImage
    | 'card'                                                                                                            # cardImage
    | ('gray' | 'grey')                                                                                                 # grayImage
    | 'inverse'                                                                                                         # inverseImage
    | 'white'                                                                                                           # whiteImage
    ;

effect
    : 'dissolve'                                                                                                        # dissolveEffect
    | 'barn' 'door' 'open'                                                                                              # barnDoorOpenEffect
    | 'barn' 'door' 'close'                                                                                             # barnDoorCloseEffect
    | 'checkerboard'                                                                                                    # checkerboardEffect
    | 'iris' 'open'                                                                                                     # irisOpenEffect
    | 'iris' 'close'                                                                                                    # irisCloseEffect
    | 'plain'                                                                                                           # plainEffect
    | 'push' 'up'                                                                                                       # pushUpEffect
    | 'push' 'down'                                                                                                     # pushDownEffect
    | 'push' 'left'                                                                                                     # pushLeftEffect
    | 'push' 'right'                                                                                                    # pushRightEffect
    | 'scroll' 'down'                                                                                                   # scrollDownEffect
    | 'scroll' 'up'                                                                                                     # scrollUpEffect
    | 'scroll' 'left'                                                                                                   # scrollLeftEffect
    | 'scroll' 'right'                                                                                                  # scrollRightEffect
    | 'shrink' 'to' 'top'                                                                                               # shrinkToTopEffect
    | 'shrink' 'to' 'center'                                                                                            # shrinkToCenterEffect
    | 'shrink' 'to' 'bottom'                                                                                            # shrinkToBottomEffect
    | 'stretch' 'from' 'top'                                                                                            # stretchFromTopEffect
    | 'stretch' 'from' 'center'                                                                                         # stretchFromCenterEffect
    | 'stretch' 'from' 'bottom'                                                                                         # stretchFromBottomEffect
    | 'venetian' 'blinds'                                                                                               # venitianBlindsEffect
    | 'wipe' 'up'                                                                                                       # wipeUpEffect
    | 'wipe' 'down'                                                                                                     # wipeDownEffect
    | 'wipe' 'left'                                                                                                     # wipeLeftEffect
    | 'wipe' 'right'                                                                                                    # wipeRightEffect
    | 'zoom' 'in'                                                                                                       # zoomInEffect
    | 'zoom' 'out'                                                                                                      # zoomOutEffect
    | 'zoom' 'open'                                                                                                     # zoomOpenEffect
    | 'zoom' 'close'                                                                                                    # zoomCloseEffect
    ;

timeUnit
    : 'ticks'                                                                                                           # ticksTimeUnit
    | 'tick'                                                                                                            # tickTimeUnit
    | seconds                                                                                                           # secondsTimeUnit
    ;

position
    : 'the'? 'next'                                                                                                     # nextPosition
    | 'the'? ('prev' | 'previous')                                                                                      # prevPosition
    | 'this'                                                                                                            # thisPosition
    ;

message
    : 'the'? ('message' | 'msg') ('box' | 'window' | )
    ;

cards
    : 'cards'
    | 'cds'
    ;

card
    : 'card'
    | 'cd'
    ;

background
    : 'background'
    | 'backgrounds'
    | 'bkgnd'
    | 'bkgnds'
    | 'bg'
    | 'bgs'
    ;

button
    : 'button'
    | 'buttons'
    | 'btn'
    | 'btns'
    ;

field
    : 'field'
    | 'fields'
    | 'fld'
    | 'flds'
    ;

stack
    : 'stack'
    ;

character
    : 'character'
    | 'characters'
    | 'char'
    | 'chars'
    ;

word
    : 'word'
    | 'words'
    ;

line
    : 'line'
    | 'lines'
    ;

item
    : 'item'
    | 'items'
    ;

of
    : 'of'
    | 'from'
    | 'in'
    ;

ID
    : (ALPHA (ALPHA | DIGIT)*)
    ;

BREAK
    : ('|' | '¬') WHITESPACE? COMMENT? WHITESPACE? NEWLINE -> skip
    ;

LITERAL
    : STRING_LITERAL
    | NUMBER_LITERAL
    ;

INTEGER_LITERAL
    : DIGIT+
    ;

NUMBER_LITERAL
    : INTEGER_LITERAL
    | '.' INTEGER_LITERAL
    | INTEGER_LITERAL '.'
    | INTEGER_LITERAL '.' INTEGER_LITERAL
    ;

STRING_LITERAL
    : '"' ~('"' | '\r' | '\n')* '"'
    ;

ALPHA
    : ('a' .. 'z' | 'A' .. 'Z')+
    ;

DIGIT
    : ('0' .. '9')+
    ;

COMMENT
    : ('--' ~('\r' | '\n' | '|')*) -> channel(HIDDEN)
    ;

NEWLINE
    : ('\n' | '\r')+
    ;

WHITESPACE
    : (' ' | '\t')+ -> channel(HIDDEN)
    ;

UNLEXED_CHAR
    : .
    ;