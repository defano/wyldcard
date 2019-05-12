/*
 * MIT License
 *
 * Copyright (c) 2017-2019 Matt DeFano
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

/* Start symbol accepting only well-formed HyperTalk scripts that consist of handlers, functions, whitespace and
 * comments (representing scipts that are assignable to objects like buttons, fields and cards). Disallows statements or
 * expressions that are not inside of a handler or function block.
 */
script
    : handler script                                                                                                    # handlerScript
    | function script                                                                                                   # functionScript
    | NEWLINE script                                                                                                    # newlineScript
    | EOF                                                                                                               # emptyScript
    ;

/* Start symbol accepting any sequence of HyperTalk statements, expressions, whitespace and comments. Suitable when
 * evaluating the message box or HyperTalk strings via the 'do' command and 'value of' function.
 */
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
    : 'on' symbol NEWLINE+ statementList? 'end' symbol                                                                  # noArgHandler
    | 'on' symbol parameterList NEWLINE+ statementList? 'end' symbol                                                    # argHandler
    ;

function
    : 'function' symbol NEWLINE+ statementList? 'end' symbol                                                            # noArgFunction
    | 'function' symbol parameterList NEWLINE+ statementList? 'end' symbol                                              # argFunction
    ;

parameterList
    : symbol                                                                                                            # singleParamList
    | parameterList ',' symbol                                                                                          # multiParamList
    ;

symbol
    : ID                                                                                                                # idSymbol
    | keyword                                                                                                           # keywordSymbol
    ;

statementList
    : statement NEWLINE+ statementList                                                                                  # multiStmntList
    | statement NEWLINE+                                                                                                # singleStmntList
    ;

statement
    : commandStatement                                                                                                  # commandStmnt
    | functionCall                                                                                                      # funcStmnt
    | messageStatement                                                                                                  # msgStmnt
    | expression                                                                                                        # expStmnt
    | ifStatement                                                                                                       # ifStmnt
    | repeatStatement                                                                                                   # repeatStmnt
    | globalStatement                                                                                                   # globalStmnt
    | returnStatement                                                                                                   # returnStmnt
    ;

globalStatement
    : 'global' parameterList
    ;

returnStatement
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

commandStatement
    : 'add' expression 'to' expression                                                                                  # addCmd
    | 'answer' expression 'with' term 'or' term 'or' term                                                               # answerThreeButtonCmd
    | 'answer' expression 'with' term 'or' term                                                                         # answerTwoButtonCmd
    | 'answer' expression 'with' term                                                                                   # answerOneButtonCmd
    | 'answer' expression                                                                                               # answerDefaultCmd
    | 'answer' 'file' expression                                                                                        # answerFileCmd
    | 'answer' 'file' expression 'of' 'type' expression                                                                 # answerFileTypeCmd
    | 'arrowkey' arrowExpression                                                                                        # arrowKeyCmd
    | 'ask' expression 'with' expression                                                                                # askExpWithCmd
    | 'ask' expression                                                                                                  # askExpCmd
    | 'ask' 'file' expression                                                                                           # askFileCmd
    | 'ask' 'file' expression 'with' expression                                                                         # askFileWithCmd
    | 'ask' 'password' expression                                                                                       # askPasswordCmd
    | 'ask' 'password' expression 'with' expression                                                                     # askPasswordWithCmd
    | 'ask' 'password' 'clear' expression                                                                               # askPasswordClearCmd
    | 'ask' 'password' 'clear' expression 'with' expression                                                             # askPasswordClearWithCmd
    | 'beep'                                                                                                            # beepCmd
    | 'beep' expression                                                                                                 # beepMultipleCmd
    | 'choose' toolExpression 'tool'?                                                                                   # chooseToolCmd
    | 'choose' 'tool' toolExpression                                                                                    # chooseToolNumberCmd
    | 'click' 'at' listExpression                                                                                       # clickCmd
    | 'click' 'at' listExpression 'with' listExpression                                                                 # clickWithKeyCmd
    | 'close' 'file' expression                                                                                         # closeFileCmd
    | 'close' card 'window'                                                                                             # closeCardWindowCmd
    | 'close' 'window' expression                                                                                       # closeWindowCmd
    | 'commandkeydown' expression                                                                                       # commandKeyDownCmd
    | 'controlkey' expression                                                                                           # controlKeyCmd
    | 'optionkey' expression                                                                                            # optionKeyCmd
    | 'convert' expression 'to' convertible                                                                             # convertToCmd
    | 'convert' expression 'from' convertible 'to' convertible                                                          # convertFromToCmd
    | 'create' 'menu' expression                                                                                        # createMenuCmd
    | 'debug' 'checkpoint'                                                                                              # debugCheckpointCmd
    | 'delete' expression                                                                                               # deleteCmd
    | 'dial' expression                                                                                                 # dialCmd
    | 'disable' expression                                                                                              # disableCmd
    | 'divide' expression 'by' expression                                                                               # divideCmd
    | 'do' expression                                                                                                   # doCmd
    | 'domenu' expression                                                                                               # doMenuCmd
    | 'drag' 'from' listExpression 'to' listExpression                                                                  # dragCmd
    | 'drag' 'from' listExpression 'to' listExpression 'with' listExpression                                            # dragWithKeyCmd
    | 'edit' 'the'? 'script' of expression                                                                              # editScriptCmd
    | 'enable' expression                                                                                               # enableCmd
    | 'enterinfield'                                                                                                    # enterInFieldCmd
    | 'enterkey'                                                                                                        # enterKeyCmd
    | 'exit' symbol                                                                                                     # exitCmd
    | 'exit' 'repeat'                                                                                                   # exitRepeatCmd
    | 'exit' 'to' hypercard                                                                                             # exitToHyperCardCmd
    | 'export' 'paint' 'to' 'file' expression                                                                           # exportPaintCmd
    | 'find' expression? 'international'? expression of expression of 'marked' cards                                    # findFieldMarkedCardsCmd
    | 'find' expression? 'international'? expression of expression                                                      # findFieldCmd
    | 'find' expression? 'international'? expression of 'marked' cards                                                  # findMarkedCardsCmd
    | 'find' expression? 'international'? expression                                                                    # findAnywhereCmd
    | 'get' expression                                                                                                  # getCmd
    | 'go' 'to'? position                                                                                               # goPosition
    | 'go' 'to'? ordinal                                                                                                # goOrdinal
    | 'go' 'to'? expression navigationOption                                                                            # goCmd
    | 'hide' 'the'? 'menubar'                                                                                           # hideMenubarCmd
    | 'hide' 'the'? 'titlebar'                                                                                          # hideTitleBarCmd
    | 'hide' card picture                                                                                               # hideThisCardPictCmd
    | 'hide' background picture                                                                                         # hideThisBkgndPictCmd
    | 'hide' picture of expression                                                                                      # hidePictCmd
    | 'hide' expression                                                                                                 # hideCmd
    | 'import' 'paint' 'from' 'file' expression                                                                         # importPaintCmd
    | 'keydown' expression                                                                                              # keydownCmd
    | 'lock' 'screen'                                                                                                   # lockScreenCmd
    | 'lock' 'messages'                                                                                                 # lockMessagesCmd
    | 'mark' 'all' cards                                                                                                # markAllCardsCmd
    | 'mark' expression                                                                                                 # markCardCmd
    | 'mark' cards 'where' expression                                                                                   # markCardsWhereCmd
    | 'mark' cards 'by' 'finding' expression? 'international'? expression of expression                                 # markCardsFindingInFieldCmd
    | 'mark' cards 'by' 'finding' expression? 'international'? expression                                               # markCardsFindingCmd
    | 'multiply' expression 'by' expression                                                                             # multiplyCmd
    | 'next' 'repeat'                                                                                                   # nextRepeatCmd
    | 'open' 'file' expression                                                                                          # openFileCmd
    | 'pass' symbol                                                                                                     # passCmd
    | 'play' musicExpression                                                                                            # playCmd
    | 'pop' card                                                                                                        # popCardCmd
    | 'pop' card preposition expression                                                                                 # popCardIntoCmd
    | 'push' card                                                                                                       # pushCardCmd
    | 'push' expression                                                                                                 # pushDestCmd
    | 'put' listExpression                                                                                              # putIntoCmd
    | 'put' listExpression preposition expression                                                                       # putPrepositionCmd
    | 'put' listExpression preposition expression 'with' menuMessage listExpression                                     # putWithMenuMessagesCmd
    | 'read' 'from' 'file' expression                                                                                   # readFileCmd
    | 'read' 'from' 'file' expression 'for' expression                                                                  # readFileForCmd
    | 'read' 'from' 'file' expression 'at' expression 'for' expression                                                  # readFileAtCmd
    | 'read' 'from' 'file' expression 'until' expression                                                                # readFileUntil
    | 'reset' 'the'? 'menubar'                                                                                          # resetMenuCmd
    | 'reset' 'paint'                                                                                                   # resetPaintCmd
    | 'save' 'this'? 'stack' 'as' 'stack'? expression                                                                   # saveThisStackAsCmd
    | 'save' 'stack' expression 'as' 'stack'? expression                                                                # saveStackAsCmd
    | 'select' 'empty'                                                                                                  # selectEmptyCmd
    | 'select' 'text' of expression                                                                                     # selectTextCmd
    | 'select' 'before' 'text' of expression                                                                            # selectBeforeCmd
    | 'select' 'after' 'text' of expression                                                                             # selectAfterCmd
    | 'select' expression                                                                                               # selectChunkCmd
    | 'select' 'before' expression                                                                                      # selectBeforeChunkCmd
    | 'select' 'after' expression                                                                                       # selectAfterChunkCmd
    | 'set' property 'to' propertyValue                                                                                 # setCmdStmnt
    | 'send' listExpression 'to' expression                                                                             # sendCmdStmnt
    | 'show' 'the'? 'titlebar'                                                                                          # showTitleBarCmd
    | 'show' 'the'? 'menubar'                                                                                           # showMenubarCmd
    | 'show' 'all' cards                                                                                                # showAllCardsCmd
    | 'show' 'marked' cards                                                                                             # showMarkedCardsCmd
    | 'show' expression cards                                                                                           # showCardsCmd
    | 'show' card picture                                                                                               # showThisCardPictCmd
    | 'show' background picture                                                                                         # showThisBkgndPictCmd
    | 'show' picture of expression                                                                                      # showPictCmd
    | 'show' expression                                                                                                 # showCmd
    | 'show' expression 'at' listExpression                                                                             # showAtCmd
    | 'sort' 'this'? 'stack' sortDirection sortStyle 'by' expression                                                    # sortStackCmd
    | 'sort' 'the'? cards (of 'this' 'stack')? sortDirection sortStyle 'by' expression                                  # sortStackCmd
    | 'sort' 'the'? 'marked' cards (of 'this' 'stack')? sortDirection sortStyle 'by' expression                         # sortMarkedCardsCmd
    | 'sort' sortChunkType expression sortDirection sortStyle                                                           # sortDirectionCmd
    | 'sort' sortChunkType expression sortDirection sortStyle 'by' expression                                           # sortExpressionCmd
    | 'sort' sortDirection sortStyle 'by' expression                                                                    # sortStackCmd
    | 'sort' expression sortDirection sortStyle 'by' expression                                                         # sortBkgndCardsCmd
    | 'sort' 'the'? cards of expression sortDirection sortStyle 'by' expression                                         # sortBkgndCardsCmd
    | 'sort' 'the'? 'marked' cards of expression sortDirection sortStyle 'by' expression                                # sortMarkedBkgndCardsCmd
    | 'speak' expression                                                                                                # speakCmd
    | 'speak' expression 'with' gender=('male'|'female'|'neuter'|'robotic') 'voice'                                     # speakGenderCmd
    | 'speak' expression 'with' 'voice' expression                                                                      # speakVoiceCmd
    | 'subtract' expression 'from' expression                                                                           # subtractCmd
    | 'start' 'using' expression                                                                                        # startUsingCmd
    | 'tabkey'                                                                                                          # tabKeyCmd
    | 'type' expression                                                                                                 # typeCmd
    | 'type' expression 'with' listExpression                                                                           # typeWithCmdKeyCmd
    | 'unlock' 'screen'                                                                                                 # unlockScreenCmd
    | 'unlock' 'screen' 'with' 'visual'? 'effect'? expression                                                           # unlockScreenVisualCmd
    | 'unlock' 'messages'                                                                                               # unlockMessagesCmd
    | 'unmark' 'all' cards                                                                                              # unmarkAllCardsCmd
    | 'unmark' expression                                                                                               # unmarkCardCmd
    | 'unmark' cards 'where' expression                                                                                 # unmarkCardsWhereCmd
    | 'unmark' cards 'by' 'finding' expression? 'international'? expression of expression                               # unmarkCardsFindingInFieldCmd
    | 'unmark' cards 'by' 'finding' expression? 'international'? expression                                             # unmarkCardsFindingCmd
    | 'visual' 'effect'? expression                                                                                     # visualEffectCmd
    | 'wait' expression timeUnit                                                                                        # waitCountCmd
    | 'wait' 'for' expression timeUnit                                                                                  # waitForCountCmd
    | 'wait' 'until' expression                                                                                         # waitUntilCmd
    | 'wait' 'while' expression                                                                                         # waitWhileCmd
    | 'write' expression 'to' 'file' expression                                                                         # writeFileCmd
    | 'write' expression 'to' 'file' expression 'at' ('eof' | 'end')                                                    # writeEndFileCmd
    | 'write' expression 'to' 'file' expression 'at' expression                                                         # writeAtFileCmd
    ;

navigationOption
    : IN_A_NEW 'window'                                                                                                 # remoteInNewWindow
    | IN_A_NEW 'window' 'without' 'dialog'                                                                              # remoteInNewWindowWithoutDialog
    | 'without' 'dialog'                                                                                                # remoteWithoutDialog
    |                                                                                                                   # remoteDefault
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
    : 'forever'                                                                                                         # infiniteLoop
    | 'with' ID '=' range                                                                                               # withLoop
    | duration                                                                                                          # durationLoop
    | count                                                                                                             # countLoop
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
    : 'menu' term                                                                                                       # expressionMenu
    | ordinal 'menu'                                                                                                    # ordinalMenu
    ;

menuItem
    : 'menuitem' term of menu                                                                                           # expressionMenuItem
    | ordinal 'menuitem' of menu                                                                                        # ordinalMenuItem
    ;

property
    : partProperty
    | globalProperty
    ;

globalProperty
    : 'the'? symbol                                                                                                     # propertySpecGlobal
    ;

partProperty
    : 'the'? symbol of term                                                                                             # propertySpecPart
    | 'the'? length symbol of term                                                                                      # lengthPropertySpecPart
    ;

part
    : message                                                                                                           # msgPart
    | card 'part' term                                                                                                  # cardPartNumberPart
    | background 'part' term                                                                                            # bkgndPartNumberPart
    | 'me'                                                                                                              # mePart
    | buttonPart                                                                                                        # buttonPartPart
    | fieldPart                                                                                                         # fieldPartPart
    | bkgndPart                                                                                                         # bkgndPartPart
    | windowPart                                                                                                        # windowPartPart
    | cardPart                                                                                                          # cardPartPart
    | stackPart                                                                                                         # stackPartPart
    | hypercard                                                                                                         # hypercardPart
    ;

stackPart
    : 'this'? stack                                                                                                     # thisStackPart
    | stack term                                                                                                        # anotherStackPart
    ;

buttonPart
    : card? button 'id' term                                                                                            # cardButtonIdPart
    | background button 'id' term                                                                                       # bkgndButtonIdPart
    | card? button term                                                                                                 # cardButtonPart
    | background button term                                                                                            # bkgndButtonPart
    | ordinal card? button                                                                                              # cardButtonOrdinalPart
    | ordinal background button                                                                                         # bkgndButtonOrdinalPart
    | buttonPart of cardPart                                                                                            # buttonOfCardPart
    ;

fieldPart
    : card field 'id' term                                                                                              # cardFieldIdPart
    | background? field 'id' term                                                                                       # bkgndFieldIdPart
    | card field term                                                                                                   # cardFieldPart
    | background? field term                                                                                            # bkgndFieldPart
    | ordinal card field                                                                                                # cardFieldOrdinalPart
    | ordinal background? field                                                                                         # bkgndFieldOrdinalPart
    | fieldPart of cardPart                                                                                             # fieldOfCardPart
    ;

cardPart
    : 'this'? card                                                                                                      # thisCardPart
    | card 'id' term                                                                                                    # cardIdPart
    | position card                                                                                                     # positionCardPart
    | position 'marked' card                                                                                            # positionMarkedCardPart
    | ordinal card                                                                                                      # ordinalCardPart
    | ordinal 'marked' card                                                                                             # ordinalMarkedCardPart
    | card term                                                                                                         # expressionCardPart
    | 'marked' card term                                                                                                # markedCardExpressionPart
    | cardPart of bkgndPart                                                                                             # cardOfBkgndPart
    | cardPart of stackPart                                                                                             # cardOfStackPart
    | 'recent' card                                                                                                     # recentCardPart
    | direction                                                                                                         # directionCardPart
    ;

bkgndPart
    : 'this'? background                                                                                                # thisBkgndPart
    | background 'id' term                                                                                              # bkgndIdPart
    | background term                                                                                                   # expressionBkgndPart
    | ordinal background                                                                                                # ordinalBkgndPart
    | position background                                                                                               # positionBkgndPart
    | bkgndPart of stackPart                                                                                            # bkgndOfStackPart
    ;

windowPart
    : 'the'? card 'window'                                                                                              # cardWindowExpr
    | 'the'? 'tool' 'window'                                                                                            # toolWindowExpr
    | 'the'? 'pattern' 'window'                                                                                         # patternWindowExpr
    | 'the'? 'message' 'watcher'                                                                                        # messageWatcherExpr
    | 'the'? 'variable' 'watcher'                                                                                       # variableWatcherExpr
    | 'window' expression                                                                                               # windowNameExpr
    | 'window' 'id' expression                                                                                          # windowIdExpr
    ;

listExpression
    : expression                                                                                                        # singletonListExp
    | expression ',' listExpression                                                                                     # listExp
    ;

expression
    : term                                                                                                              # factorExp
    | 'not' expression                                                                                                  # notExp
    | '-' expression                                                                                                    # negateExp
    | op=(THERE_IS_A|THERE_IS_NO) expression                                                                            # unaryOpExp
    | expression '^' expression                                                                                         # powOpExp
    | expression op=('mod'| 'div'| '/'| '*') expression                                                                 # binaryOpExp
    | expression op=('+'| '-') expression                                                                               # binaryOpExp
    | expression op=('&&'| '&') expression                                                                              # binaryOpExp
    | expression op=('>='|'<='|'≤'|'≥'|'<'|'>'|'contains'|IS_IN|IS_NOT_IN|IS_A|IS_NOT_A|IS_WITHIN|IS_NOT_WITHIN) expression # binaryOpExp
    | expression op=('='|IS_NOT|'is'|'<>'|'≠') expression                                                               # binaryOpExp
    | expression 'and' expression                                                                                       # binaryAndExp
    | expression 'or' expression                                                                                        # binaryOrExp
    ;

term
    : literal                                                                                                           # literalTerm
    | '-' literal                                                                                                       # negativeLiteralTerm
    | '(' expression ')'                                                                                                # expressionTerm
    | effectExpression                                                                                                  # visualEffectTerm
    | functionCall                                                                                                      # functionTerm
    | container                                                                                                         # containerTerm
    | chunk term                                                                                                        # chunkTerm
    ;

container
    : 'the'? 'selection'                                                                                                # selectionContainer
    | 'target'                                                                                                          # targetContainer
    | part                                                                                                              # partContainer
    | menu                                                                                                              # menuContainer
    | menuItem                                                                                                          # menuItemContainer
    | symbol                                                                                                            # variableContainer
    | property                                                                                                          # propertyContainer
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

arrowExpression
    : 'up'                                                                                                              # arrowLiteralExpr
    | 'down'                                                                                                            # arrowLiteralExpr
    | 'left'                                                                                                            # arrowLiteralExpr
    | 'right'                                                                                                           # arrowLiteralExpr
    | expression                                                                                                        # arrowExpr
    ;

functionCall
    : builtInFunc                                                                                                       # builtInFuncCall
    | ID '(' listExpression? ')'                                                                                        # userArgFuncCall
    ;

builtInFunc
    : 'the' zeroArgFunc                                                                                                 # builtinFuncNoArg
    | zeroArgFunc '(' ')'                                                                                               # builtinFuncNoArg
    | 'the'? oneArgFunc of term                                                                                         # builtinFuncOneArgs
    | oneArgFunc '(' listExpression ')'                                                                                 # builtinFuncOneArgs
    | multiArgFunc '(' listExpression ')'                                                                               # builtinFuncArgList
    | 'the'? 'number' of countable                                                                                      # builtinFuncNumber
    | 'number' '(' countable ')'                                                                                        # builtinFuncNumber
    | 'the'? 'selectedbutton' of buttonFamily                                                                           # builtinFuncBtnFamily
    | 'selectedbutton' '(' buttonFamily ')'                                                                             # builtinFuncBtnFamily
    ;

zeroArgFunc
    : 'clickh'                                                                                                          # clickHFunc
    | 'clickchunk'                                                                                                      # clickChunkFunc
    | 'clickloc'                                                                                                        # clickLocFunc
    | 'clickline'                                                                                                       # clickLineFunc
    | 'clicktext'                                                                                                       # clickTextFunc
    | 'clickv'                                                                                                          # clickVFunc
    | ('commandkey' | 'cmdkey')                                                                                         # commandKeyFunc
    | length 'date'                                                                                                     # dateFunc
    | 'diskspace'                                                                                                       # diskSpaceNoArgFunc
    | 'foundchunk'                                                                                                      # foundChunkFunc
    | 'foundfield'                                                                                                      # foundFieldFunc
    | 'foundline'                                                                                                       # foundLineFunc
    | 'foundtext'                                                                                                       # foundTextFunc
    | 'menus'                                                                                                           # menusFunc
    | 'mouse'                                                                                                           # mouseFunc
    | 'mouseh'                                                                                                          # mouseHFunc
    | 'mousev'                                                                                                          # mouseVFunc
    | 'mouseclick'                                                                                                      # mouseClickFunc
    | 'mouseloc'                                                                                                        # mouseLocFunc
    | 'optionkey'                                                                                                       # optionKeyFunc
    | 'result'                                                                                                          # resultFunc
    | 'screenrect'                                                                                                      # screenRectFunc
    | seconds                                                                                                           # secondsFunc
    | 'selectedchunk'                                                                                                   # selectedChunkFunc
    | 'selectedfield'                                                                                                   # selectedFieldFunc
    | 'selectedline'                                                                                                    # selectedLineFunc
    | 'selectedloc'                                                                                                     # selectedLocFunc
    | 'selectedtext'                                                                                                    # selectedTextFunc
    | 'shiftkey'                                                                                                        # shiftKeyFunc
    | 'sound'                                                                                                           # soundFunc
    | 'speech'                                                                                                          # speechFunc
    | 'stacks'                                                                                                          # stacksFunc
    | 'systemversion'                                                                                                   # systemVersionFunc
    | 'ticks'                                                                                                           # ticksFunc
    | length 'time'                                                                                                     # timeFunc
    | 'tool'                                                                                                            # toolFunc
    | 'paramcount'                                                                                                      # paramCountFunc
    | 'params'                                                                                                          # paramsFunc
    | 'target'                                                                                                          # targetFunc
    | 'voices'                                                                                                          # voicesFunc
    | 'windows'                                                                                                         # windowsFunc
    ;

oneArgFunc
    : 'average'                                                                                                         # averageFunc
    | 'min'                                                                                                             # minFunc
    | 'max'                                                                                                             # maxFunc
    | 'sum'                                                                                                             # sumFunc
    | 'random'                                                                                                          # randomFunc
    | 'round'                                                                                                           # roundFunc
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
    : oneArgFunc                                                                                                        # oneArgArgFunc
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
    : 'the'? 'first'
    | 'the'? 'second'
    | 'the'? 'third'
    | 'the'? 'fourth'
    | 'the'? 'fifth'
    | 'the'? 'sixth'
    | 'the'? 'seventh'
    | 'the'? 'eighth'
    | 'the'? 'ninth'
    | 'the'? 'tenth'
    | 'the'? ('mid' | 'middle')
    | 'the'? 'last'
    | 'the'? 'any'
    ;

countable
    : cards (of 'this' 'stack')?                                                                                        # cardsCount
    | cards of term                                                                                                     # cardsOfCount
    | background (of 'this' 'stack')?                                                                                   # backgroundCount
    | background of term                                                                                                # backgroundsOfCount
    | card button                                                                                                       # cardButtonCount
    | card button of term                                                                                               # cardButtonsOfCount
    | card field                                                                                                        # cardFieldCount
    | card field of term                                                                                                # cardFieldsOfCount
    | card? 'parts'                                                                                                     # cardPartCount
    | card 'parts' of term                                                                                              # cardPartsOfCount
    | background button                                                                                                 # bkgndButtonCount
    | background button of term                                                                                         # bkgndButtonsOfCount
    | background? field                                                                                                 # bkgndFieldCount
    | background field of term                                                                                          # bkgndFieldsOfCount
    | background 'parts'                                                                                                # bkgndPartCount
    | background 'parts' of term                                                                                        # bkgndPartsOfCount
    | 'marked' cards (of 'this' 'stack')?                                                                               # markedCardsCount
    | 'marked' cards of term                                                                                            # markedCardsOfCount
    | character of term                                                                                                 # charsOfCount
    | item of term                                                                                                      # itemsOfCount
    | word of term                                                                                                      # wordsOfCount
    | line of term                                                                                                      # linesOfCount
    | 'windows'                                                                                                         # windowsCount
    | 'menus'                                                                                                           # menusCount
    | 'menuitems' of 'menu' term                                                                                        # menuItemsCount
    ;

buttonFamily
    : card 'family' term                                                                                                # cardFamily
    | background 'family' term                                                                                          # bkgndFamily
    ;

mouseState
    : 'up'
    | 'down'
    ;

modifierKey
    : 'commandkey'
    | 'cmdkey'
    | 'optionkey'
    | 'controlkey'
    | 'ctrlkey'
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

propertyValue
    : symbol                                                                                                            # propertySymbolValueExp
    | listExpression                                                                                                    # propertyValueExp
    ;

keyword
    : 'add' | 'to' | 'answer' | 'optionkey' | 'ctrlkey'
    | 'with' | 'arrowkey' | 'ask' | 'file' | 'beep' | 'choose' | 'tool' | 'click' | 'at' | 'close'
    | 'commandkeydown' | 'controlkey' | 'convert' | 'from' | 'create' | 'menu' | 'debug' | 'checkpoint' | 'delete'
    | 'dial' | 'disable' | 'divide' | 'by' | 'domenu' | 'drag' | 'edit' | 'script' | 'enable'
    | 'enterinfield' | 'enterkey' | 'export' | 'paint' | 'find' | 'international' | 'marked'
    | 'get' | 'go' | 'visual' | 'hide' | 'titlebar' | 'menubar' | 'import' | 'keydown' | 'lock' | 'screen'
    | 'mark' | 'all' | 'where' | 'finding' | 'multiply' | 'next' | 'open' | 'pass' | 'play' | 'pop' | 'push' | 'put'
    | 'read' | 'for' | 'until' | 'reset' | 'save' | 'this' | 'stack' | 'as' | 'select' | 'text'
    | 'set' | 'send' | 'show' | 'sort' | 'speak' | 'male' | 'female' | 'neuter' | 'robotic' | 'voice'
    | 'subtract' | 'tabkey' | 'type' | 'commandkey' | 'cmdkey' | 'unlock' | 'unmark' | 'wait' | 'while' | 'write'
    | 'window' | 'without' | 'dialog' | 'dateitems' | 'date' | 'time' | 'english' | 'long' | 'abbreviated'
    | 'abbrev' | 'abbr' | 'short' | 'ascending' | 'descending' | 'numeric' | 'datetime' | 'forever' | 'times'
    | 'down' | 'menuitem' | 'part' | 'id' | 'pattern' | 'watcher' | 'variable' | 'start' | 'using' | 'password'
    | 'selection' | 'tempo' | 'field' | 'button' | 'line' | 'reg' | 'regular'
    | 'poly' | 'polygon' | 'round' | 'rect' | 'rectangle' | 'spray' | 'can' | 'up' | 'left' | 'right' | 'effect'
    | 'number' | 'clickh' | 'clickchunk' | 'clickloc' | 'clickline' | 'clicktext' | 'clickv' | 'diskspace'
    | 'foundchunk' | 'foundfield' | 'foundline' | 'foundtext' | 'menus' | 'mouse' | 'mouseclick' | 'mouseloc'
    | 'optionkey' | 'result' | 'screenrect' | 'selectedchunk' | 'selectedfield' | 'selectedline' | 'selectedloc'
    | 'selectedtext' | 'shiftkey' | 'sound' | 'speech' | 'stacks' | 'systemversion' | 'ticks' | 'paramcount' | 'params'
    | 'voices' | 'windows' | 'average' | 'min' | 'max' | 'sum' | 'random' | 'sqrt' | 'trunc' | 'sin' | 'cos' | 'tan'
    | 'atan' | 'exp' | 'exp1' | 'exp2' | 'ln' | 'ln1' | 'log2' | 'abs' | 'chartonum' | 'numtochar' | 'value' | 'length'
    | 'param' | 'annuity' | 'compound' | 'offset' | 'first' | 'second' | 'third' | 'fourth' | 'fifth' | 'sixth'
    | 'seventh' | 'eighth' | 'ninth' | 'tenth' | 'messages' | 'menumessage' | 'menumsg'
    | 'mid' | 'middle' | 'last' | 'any' | 'parts' | 'menuitems' | 'integer' | 'point' | 'logical' | 'boolean' | 'bool'
    | 'word' | 'chars' | 'whole' | 'string' | 'bottom' | 'top' | 'center' | 'scroll' | 'plain' | 'picture' | 'pict'
    | 'seconds' | 'secs' | 'sec' | 'fast' | 'slow' | 'slowly' | 'very' | 'black' | 'card' | 'gray' | 'grey' | 'inverse'
    | 'white' | 'dissolve' | 'barn' | 'door' | 'checkerboard' | 'iris' | 'shrink' | 'stretch' | 'venetian' | 'blinds'
    | 'wipe' | 'zoom' | 'in' | 'out' | 'tick' | 'prev' | 'previous' | 'msg' | 'box' | 'cards' | 'cds' | 'cd'
    | 'background' | 'backgrounds' | 'bkgnd' | 'bkgnds' | 'bg' | 'bgs' | 'buttons' | 'btn' | 'btns' | 'fields' | 'fld'
    | 'flds' | 'character' | 'characters' | 'char' | 'words' | 'lines' | 'item' | 'items' | 'of'
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

effectExpression
    : 'visual' 'effect'? effectNameExpresssion effectDirection                                                          # visualEffectNameExpr
    | 'visual' 'effect'? effectNameExpresssion effectDirection 'to' effectImage                                         # visualEffectNameImageExpr
    | 'visual' 'effect'? effectNameExpresssion effectDirection effectSpeed                                              # visualEffectNameSpeedExpr
    | 'visual' 'effect'? effectNameExpresssion effectDirection effectSpeed 'to' effectImage                             # visualEffectNameSpeedImageExpr
    | 'visual'? 'effect'? effectName effectDirection                                                                    # visualEffectName
    | 'visual'? 'effect'? effectName effectDirection 'to' effectImage                                                   # visualEffectImageExpr
    | 'visual'? 'effect'? effectName effectDirection effectSpeed                                                        # visualEffectSpeedExpr
    | 'visual'? 'effect'? effectName effectDirection effectSpeed 'to' effectImage                                       # visualEffectSpeedImageExpr
    ;

effectNameExpresssion
    : effectName                                                                                                        # literalEffectNameExpr
    | expression                                                                                                        # exprEffectNameExpr
    ;

effectName
    : 'dissolve'                                                                                                        # literalEffectName
    | 'barn' 'door'                                                                                                     # literalEffectName
    | 'checkerboard'                                                                                                    # literalEffectName
    | 'iris'                                                                                                            # literalEffectName
    | 'plain'                                                                                                           # literalEffectName
    | 'push'                                                                                                            # literalEffectName
    | 'scroll'                                                                                                          # literalEffectName
    | 'shrink' 'to'                                                                                                     # literalEffectName
    | 'stretch' 'from'                                                                                                  # literalEffectName
    | 'venetian' 'blinds'                                                                                               # literalEffectName
    | 'wipe'                                                                                                            # literalEffectName
    | 'zoom'                                                                                                            # literalEffectName
    ;

effectDirection
    : 'open'                                                                                                            # literalEffectDirection
    | 'close'                                                                                                           # literalEffectDirection
    | 'up'                                                                                                              # literalEffectDirection
    | 'down'                                                                                                            # literalEffectDirection
    | 'left'                                                                                                            # literalEffectDirection
    | 'right'                                                                                                           # literalEffectDirection
    | 'top'                                                                                                             # literalEffectDirection
    | 'center'                                                                                                          # literalEffectDirection
    | 'bottom'                                                                                                          # literalEffectDirection
    | 'in'                                                                                                              # literalEffectDirection
    | 'out'                                                                                                             # literalEffectDirection
    |                                                                                                                   # emptyEffectDirection
    | expression                                                                                                        # expressionEffectDirection
    ;

effectSpeed
    : 'fast'                                                                                                            # literalEffectSpeed
    | ('slow' | 'slowly')                                                                                               # literalEffectSpeed
    | 'very' 'fast'                                                                                                     # literalEffectSpeed
    | 'very' ('slow' | 'slowly')                                                                                        # literalEffectSpeed
    | expression                                                                                                        # expressionEffectSpeed
    ;

effectImage
    : 'black'                                                                                                           # literalEffectImage
    | 'card'                                                                                                            # literalEffectImage
    | ('gray' | 'grey')                                                                                                 # literalEffectImage
    | 'inverse'                                                                                                         # literalEffectImage
    | 'white'                                                                                                           # literalEffectImage
    | expression                                                                                                        # expressionEffectImage
    ;

timeUnit
    : 'ticks'                                                                                                           # ticksTimeUnit
    | 'tick'                                                                                                            # tickTimeUnit
    | seconds                                                                                                           # secondsTimeUnit
    |                                                                                                                   # secondsTimeUnit
    ;

position
    : 'the'? 'next'                                                                                                     # nextPosition
    | 'the'? ('prev' | 'previous')                                                                                      # prevPosition
    | 'this'                                                                                                            # thisPosition
    ;

direction
    : 'back'                                                                                                            # backDirection
    | 'forth'                                                                                                           # forthDirection
    ;

message
    : 'the'? ('message' | 'msg') ('box' | 'window')?
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

hypercard
    : 'hypercard'
    | 'wyldcard'
    ;

menuMessage
    : 'menumessage'
    | 'menumessages'
    | 'menumsgs'
    | 'menumsg'
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
    : ('a' .. 'z' | 'A' .. 'Z' | '_' )+
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

IN_A_NEW
    : 'in' WHITESPACE 'a' WHITESPACE 'new'
    | 'in' WHITESPACE 'new'
    ;

THERE_IS_A
    : 'there' WHITESPACE 'is' WHITESPACE 'a'
    | 'there' WHITESPACE 'is' WHITESPACE 'an'
    ;

THERE_IS_NO
    : 'there' WHITESPACE 'is' WHITESPACE 'no'
    | 'there' WHITESPACE 'is' WHITESPACE 'not' WHITESPACE 'a'
    | 'there' WHITESPACE 'is' WHITESPACE 'not' WHITESPACE 'an'
    ;

IS_IN
    : 'is' WHITESPACE 'in'
    ;

IS_NOT_IN
    : 'is' WHITESPACE 'not' WHITESPACE 'in'
    ;

IS_A
    : 'is' WHITESPACE 'a'
    | 'is' WHITESPACE 'an'
    ;

IS_NOT_A
    : 'is' WHITESPACE 'not' WHITESPACE 'a'
    | 'is' WHITESPACE 'not' WHITESPACE 'an'
    ;

IS_WITHIN
    : 'is' WHITESPACE 'within'
    ;

IS_NOT_WITHIN
    : 'is' WHITESPACE 'not' WHITESPACE 'within'
    ;

IS_NOT
    : 'is' WHITESPACE 'not'
    ;

UNLEXED_CHAR
    : .
    ;