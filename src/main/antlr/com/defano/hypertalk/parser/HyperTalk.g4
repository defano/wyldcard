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

// Accepts only well-formed HyperTalk scripts consisting handlers, functions, whitespace and comments representing
// scipts assignable to parts (like buttons and fields). Disallows statements or expressions not inside a handler or
// function block.
script
    : handler script                                                                                                    # handlerScript
    | function script                                                                                                   # functionScript
    | NEWLINE script                                                                                                    # newlineScript
    | COMMENT script                                                                                                    # commentScript
    | EOF                                                                                                               # emptyScript
    ;

// Accepts any sequence of HyperTalk statements, expressions, whitespace and comments. Suitable when evaluating the
// message box or via the 'do' command / 'value of' function.
scriptlet
    : nonEmptyStmnt scriptlet                                                                                           # statementScriptlet
    | NEWLINE scriptlet                                                                                                 # whitespaceScriptlet
    | COMMENT scriptlet                                                                                                 # commentScriptlet
    | EOF                                                                                                               # emptyScriptlet
    ;

handler
    : 'on' handlerName NEWLINE statementList 'end' handlerName                                                          # populatedHandler
    | 'on' handlerName parameterList NEWLINE statementList 'end' handlerName                                            # populatedArgHandler
    ;

function
    : 'function' ID parameterList NEWLINE statementList 'end' ID                                                        # populatedFunction
    ;

handlerName
    : ID
    // Need a special rules here to handle command names because they're also keywords (tokens)
    | 'answer' | 'ask' | 'put' | 'get' | 'set' | 'send' | 'wait' | 'sort' | 'go' | 'enable' | 'disable'
    | 'read' | 'write' | 'hide' | 'show' | 'add' | 'subtract' | 'multiply' | 'divide' | 'choose'
    | 'click' | 'drag' | 'type' | 'lock' | 'unlock' | 'pass' | 'domenu' | 'visual' | 'reset' | 'create'
    | 'delete' | 'play' | 'dial' | 'beep' | 'open' | 'close' | 'select' | 'find'
    ;

expressionList
    : factor                                                                                                            # singleExpArgList
    | expressionList ',' factor                                                                                         # multiExpArgList
    ;

parameterList
    : ID                                                                                                                # singleParamList
    | parameterList ',' ID                                                                                              # multiParamList
    ;

statementList
    : statementList nonEmptyStmnt                                                                                       # multiStmntList
    | nonEmptyStmnt                                                                                                     # singleStmntList
    | statementList NEWLINE                                                                                             # stmntListNewlineStmntList
    |                                                                                                                   # emptyStmntList
    ;

nonEmptyStmnt
    : ifStatement                                                                                                       # nonEmptyIfStmnt
    | repeatStatement                                                                                                   # nonEmptyRepeatStmnt
    | globalStmnt                                                                                                       # nonEmptyGlobalStmnt
    | returnStmnt                                                                                                       # nonEmptyReturnStmnt
    | expression                                                                                                        # nonEmptyExpStmnt
    | commandStmnt                                                                                                      # nonEmptyCommandStmnt
    ;

returnStmnt
    : 'return' expression                                                                                               # eprReturnStmnt
    | 'return'                                                                                                          # voidReturnStmnt
    ;

commandStmnt
    : 'add' expression 'to' container                                                                                   # addCmdStmnt
    | 'answer' expression 'with' expression 'or' expression 'or' expression                                             # answerThreeButtonCmd
    | 'answer' expression 'with' expression 'or' expression                                                             # answerTwoButtonCmd
    | 'answer' expression 'with' expression                                                                             # answerOneButtonCmd
    | 'answer' expression                                                                                               # answerDefaultCmd
    | 'ask' expression 'with' expression                                                                                # askExpWithCmd
    | 'ask' expression                                                                                                  # askExpCmd
    | 'beep'                                                                                                            # beepCmdStmt
    | 'beep' expression                                                                                                 # beepMultipleStmt
    | 'choose' toolExpression 'tool'                                                                                    # chooseToolCmdStmt
    | 'choose' 'tool' toolExpression                                                                                    # chooseToolNumberCmdStmt
    | 'click' 'at' expression                                                                                           # clickCmdStmt
    | 'click' 'at' expression 'with' expressionList                                                                     # clickWithKeyCmdStmt
    | 'close' 'file' expression                                                                                         # closeFileCmdStmt
    | 'convert' container 'to' convertible                                                                              # convertContainerToCmd
    | 'convert' container 'from' convertible 'to' convertible                                                           # convertContainerFromToCmd
    | 'convert' expression 'to' convertible                                                                             # convertToCmd
    | 'convert' expression 'from' convertible 'to' convertible                                                          # convertFromToCmd
    | 'create' 'menu' factor                                                                                            # createMenuCmdStmt
    | 'delete' menu                                                                                                     # deleteMenuCmdStmt
    | 'delete' menuItem                                                                                                 # deleteMenuItemCmdStmt
    | 'delete' part                                                                                                     # deleteCmdStmt
    | 'delete' container                                                                                                # deleteChunkCmdStmt
    | 'dial' expression                                                                                                 # dialCmdStmt
    | 'disable' part                                                                                                    # disablePartCmd
    | 'disable' menuItem                                                                                                # disableMenuItemCmd
    | 'disable' menu                                                                                                    # disableMenuCmd
    | 'divide' container 'by' expression                                                                                # divideCmdStmnt
    | 'do' expression                                                                                                   # doCmdStmt
    | 'domenu' factor                                                                                                   # doMenuCmdStmt
    | 'drag' 'from' expression 'to' expression                                                                          # dragCmdStmt
    | 'drag' 'from' expression 'to' expression 'with' expressionList                                                    # dragWithKeyCmdStmt
    | 'enable' part                                                                                                     # enablePartCmd
    | 'enable' menuItem                                                                                                 # enableMenuItemCmd
    | 'enable' menu                                                                                                     # enableMenuCmd
    | 'exit' handlerName                                                                                                # exitCmdStmt
    | 'exit' 'repeat'                                                                                                   # exitRepeatCmdStmt
    | 'exit' 'to' 'hypercard'                                                                                           # exitToHyperCardCmdStmt
    | find factor                                                                                                       # findAnywhere
    | find factor of fieldPart                                                                                          # findField
    | find factor of 'marked' 'cards'                                                                                   # findMarkedCards
    | find factor of fieldPart of 'marked' 'cards'                                                                      # findFieldMarkedCards
    | 'get' expression                                                                                                  # getCmdStmnt
    | 'go' 'to'? destination 'with' 'visual' visualEffect                                                               # goVisualEffectCmdStmnd
    | 'go' 'to'? destination                                                                                            # goCmdStmnt
    | 'go' 'back'                                                                                                       # goBackCmdStmt
    | 'go' 'back' 'with' 'visual' visualEffect                                                                          # goBackVisualEffectCmdStmt
    | 'hide' part                                                                                                       # hideCmdStmnt
    | 'lock' 'screen'                                                                                                   # lockScreenCmdStmt
    | 'multiply' container 'by' expression                                                                              # multiplyCmdStmnt
    | 'next' 'repeat'                                                                                                   # nextRepeatCmdStmt
    | 'open' 'file' expression                                                                                          # openFileCmdStmt
    | 'pass' handlerName                                                                                                # passCmdStmt
    | 'play' music                                                                                                      # playCmdStmt
    | 'pop' card                                                                                                        # popCardCmdStmt
    | 'push' card                                                                                                       # pushCardCmdStmt
    | 'push' destination                                                                                                # pushDestCmdStmt
    | 'put' expression                                                                                                  # putIntoCmd
    | 'put' expression preposition container                                                                            # putPrepositionCmd
    | 'read' 'from' 'file' factor                                                                                       # readFileCmd
    | 'read' 'from' 'file' factor 'for' factor                                                                          # readFileForCmd
    | 'read' 'from' 'file' factor 'at' factor 'for' factor                                                              # readFileAtCmd
    | 'read' 'from' 'file' factor 'until' factor                                                                        # readFileUntil
    | 'reset' 'the'? 'menubar'                                                                                          # resetMenuCmdStmt
    | 'reset' 'paint'                                                                                                   # resetPaintCmdStmt
    | 'select' part                                                                                                     # selectPartCmd
    | 'select' 'empty'                                                                                                  # selectEmptyCmd
    | 'select' 'text' of part                                                                                           # selectTextCmd
    | 'select' 'before' 'text' of part                                                                                  # selectBeforeCmd
    | 'select' 'after' 'text' of part                                                                                   # selectAfterCmd
    | 'select' chunk part                                                                                               # selectChunkCmd
    | 'select' 'before' chunk part                                                                                      # selectBeforeChunkCmd
    | 'select' 'after' chunk part                                                                                       # selectAfterChunkCmd
    | 'set' propertySpec 'to' propertyValue                                                                             # setCmdStmnt
    | 'send' expression 'to' part                                                                                       # sendCmdStmnt
    | 'show' part                                                                                                       # showCmdStmnt
    | 'sort' sortChunkType container sortDirection sortStyle                                                            # sortDirectionCmd
    | 'sort' sortChunkType container sortDirection sortStyle 'by' expression                                            # sortExpressionCmd
    | 'sort' sortDirection sortStyle 'by' expression                                                                    # sortStackCmd
    | 'sort' 'this'? 'stack' sortDirection sortStyle 'by' expression                                                    # sortStackCmd
    | 'sort' 'the'? 'cards' (of 'this' 'stack')? sortDirection sortStyle 'by' expression                                # sortStackCmd
    | 'sort' 'the'? 'marked' 'cards' (of 'this' 'stack')? sortDirection sortStyle 'by' expression                       # sortMarkedCardsCmd
    | 'sort' bkgndPart sortDirection sortStyle 'by' expression                                                          # sortBkgndCardsCmd
    | 'sort' 'the'? 'cards' of bkgndPart sortDirection sortStyle 'by' expression                                        # sortBkgndCardsCmd
    | 'sort' 'the'? 'marked' 'cards' of bkgndPart sortDirection sortStyle 'by' expression                               # sortMarkedBkgndCardsCmd
    | 'subtract' expression 'from' container                                                                            # subtractCmdStmnt
    | 'type' expression                                                                                                 # typeCmdStmt
    | 'type' expression 'with' ('commandkey' | 'cmdkey')                                                                # typeWithCmdKeyCmdStmt
    | 'unlock' 'screen'                                                                                                 # unlockScreenCmdStmt
    | 'unlock' 'screen' 'with' 'visual' visualEffect                                                                    # unlockScreenVisualCmdStmt
    | 'visual' visualEffect                                                                                             # visualEffectCmdStmt
    | 'wait' factor timeUnit                                                                                            # waitCountCmd
    | 'wait for' factor timeUnit                                                                                        # waitForCountCmd
    | 'wait until' expression                                                                                           # waitUntilCmd
    | 'wait while' expression                                                                                           # waitWhileCmd
    | 'write' expression 'to' 'file' factor                                                                             # writeFileCmd
    | 'write' expression 'to' 'file' factor 'at' ('eof' | 'end')                                                        # writeEndFileCmd
    | 'write' expression 'to' 'file' factor 'at' factor                                                                 # writeAtFileCmd
    | ID                                                                                                                # noArgMsgCmdStmt
    | ID expressionList                                                                                                 # argMsgCmdStmt
    ;

find
    : 'find word' 'international'?                                                                                      # searchableWord
    | 'find' 'chars' 'international'?                                                                                   # searchableChars
    | 'find' 'whole' 'international'?                                                                                   # searchableWhole
    | 'find' 'string' 'international'?                                                                                  # searchableString
    | 'find' 'international'?                                                                                           # searchableSubstring
    ;

music
    : factor expression                                                                                                 # musicInstrumentNotes
    | factor 'tempo' factor expression                                                                                  # musicInstrumentNotesTempo
    | factor 'tempo' expression                                                                                         # musicInstrumentTempo
    | factor                                                                                                            # musicInstrument
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

convertible
    : conversionFormat                                                                                                  # singleFormatConvertible
    | conversionFormat 'and' conversionFormat                                                                           # dualFormatConvertible
    ;

conversionFormat
    : 'seconds'                                                                                                         # secondsConvFormat
    | 'dateitems'                                                                                                       # dateItemsConvFormat
    | ('long date' | 'english date')                                                                                    # longDateConvFormat
    | ('date' | 'short date')                                                                                           # shortDateConvFormat
    | ('abbrev date' | 'abbreviated date')                                                                              # abbrevDateConvFormat
    | ('time' | 'short time' | 'abbrev time' | 'abbreviated time')                                                      # shortTimeConvFormat
    | ('english time' | 'long time')                                                                                    # longTimeConvFormat
    ;

sortDirection
    : 'ascending'                                                                                                       # sortDirectionAsc
    | 'descending'                                                                                                      # sortDirectionDesc
    |                                                                                                                   # sortDirectionDefault
    ;

sortChunkType
    : 'the'? line of                                                                                                    # sortChunkLines
    | 'the'? item of                                                                                                    # sortChunkItems
    |                                                                                                                   # sortChunkDefault
    ;

sortStyle
    : 'text'                                                                                                            # sortStyleText
    | 'numeric'                                                                                                         # sortStyleNumeric
    | 'international'                                                                                                   # sortStyleInternational
    | 'datetime'                                                                                                        # sortStyleDateTime
    |                                                                                                                   # sortStyleDefault
    ;

visualEffect
    : 'effect'? effect                                                                                                  # effectDefault
    | 'effect'? effect 'to' image                                                                                       # effectTo
    | 'effect'? effect speed                                                                                            # effectSpeed
    | 'effect'? effect speed 'to' image                                                                                 # effectSpeedTo
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
    ;

timeUnit
    : 'ticks'                                                                                                           # ticksTimeUnit
    | 'tick'                                                                                                            # tickTimeUnit
    | 'seconds'                                                                                                         # secondsTimeUnit
    | 'sec'                                                                                                             # secTimeUnit
    | 'second'                                                                                                          # secondTimeUnit
    ;

position
    : 'the'? 'next'                                                                                                     # nextPosition
    | 'the'? ('prev' | 'previous')                                                                                      # prevPosition
    | 'this'                                                                                                            # thisPosition
    ;

destination
    : destinationType expression                                                                                        # cardNumber
    | ordinal destinationType                                                                                           # cardOrdinal
    | position destinationType                                                                                          # cardPosition
    | factor                                                                                                            # destinationRef
    ;

destinationType
    : card                                                                                                              # cardDestinationType
    | background                                                                                                        # bkgndDestinationType
    ;

ifStatement
    : 'if' expression thenStatement
    ;

thenStatement
    : 'then' nonEmptyStmnt NEWLINE? elseStatement?                                                                      # thenSingleStmnt
    | 'then' NEWLINE statementList NEWLINE (elseStatement | 'end' 'if')                                                 # thenStmntList
    ;

elseStatement
    : 'else' nonEmptyStmnt (NEWLINE 'end' 'if')?                                                                        # elseSingleStmt
    | 'else' NEWLINE statementList NEWLINE 'end' 'if'                                                                   # elseStmntList
    ;

repeatStatement
    : 'repeat' repeatRange NEWLINE statementList 'end' 'repeat'                                                         # repeatStmntList
    | 'repeat' repeatRange NEWLINE 'end' 'repeat'                                                                       # repeatEmpty
    ;

repeatRange
    : 'forever'                                                                                                         # infiniteLoop
    |                                                                                                                   # infiniteLoop
    | duration                                                                                                          # durationLoop
    | count                                                                                                             # countLoop
    | 'with' ID '=' range                                                                                               # withLoop
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

globalStmnt
    : 'global' ID
    ;

preposition
    : 'before'                                                                                                          # beforePreposition
    | 'after'                                                                                                           # afterPreposition
    | 'into'                                                                                                            # intoPreposition
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

container
    : ID                                                                                                                # variableDest
    | messagePart                                                                                                       # messageDest
    | chunk messagePart                                                                                                 # chunkMessageDest
    | 'the' 'selection'                                                                                                 # selectionDest
    | chunk 'the' 'selection'                                                                                           # chunkSelectionDest
    | chunk ID                                                                                                          # chunkVariableDest
    | part                                                                                                              # partDest
    | chunk part                                                                                                        # chunkPartDest
    | chunk                                                                                                             # chunkDest
    | propertySpec                                                                                                      # propertyDest
    | chunk propertySpec                                                                                                # chunkPropertyDest
    | menu                                                                                                              # menuDest
    | menuItem                                                                                                          # menuItemDest
    |                                                                                                                   # defaultDest
    ;

menu
    : 'menu' factor                                                                                                     # expressionMenu
    | ordinal 'menu'                                                                                                    # ordinalMenu
    ;

menuItem
    : 'menuitem' factor of menu                                                                                         # expressionMenuItem
    | ordinal 'menuitem' of menu                                                                                        # ordinalMenuItem
    ;

propertyValue
    : 'plain'                                                                                                           # propertyValueLiteral
    | 'menu'                                                                                                            # propertyValueLiteral
    | expression                                                                                                        # propertyValueExp
    ;

propertySpec
    : 'the'? propertyName                                                                                               # propertySpecGlobal
    | 'the'? propertyName of part                                                                                       # propertySpecPart
    | 'the'? propertyName of chunk part                                                                                 # propertySpecChunkPart
    | 'the'? propertyName of menuItem                                                                                   # propertySpecMenuItem
    ;

propertyName
    : 'marked'          // Requires special rule because 'marked' is also a lexed token
    | 'id'
    | 'rect' | 'rectangle'
    | 'bottom' | 'left' | 'right' | 'top' | 'center' | 'scroll'
    | ID
    ;

part
    : buttonPart                                                                                                        # buttonPartPart
    | fieldPart                                                                                                         # fieldPartPart
    | bkgndPart                                                                                                         # bkgndPartPart
    | cardPart                                                                                                          # cardPartPart
    | card 'part' factor                                                                                                # cardPartNumberPart
    | background 'part' factor                                                                                          # bkgndPartNumberPart
    | 'me'                                                                                                              # mePart
    | messagePart                                                                                                       # msgPart
    | ID                                                                                                                # partRef
    ;

messagePart
    : 'the'? 'message'
    | 'the'? 'message' 'box'
    | 'the'? 'message' 'window'
    ;

card
    : 'card'
    | 'cards'
    | 'cd'
    | 'cds'
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
    | 'in'
    | 'from'
    ;

buttonPart
    : background? button factor                                                                                         # bkgndButtonPart
    | ordinal background? button                                                                                        # bkgndButtonOrdinalPart
    | background? button 'id' factor                                                                                    # bkgndButtonIdPart
    | card? button factor                                                                                               # cardButtonPart
    | ordinal card? button                                                                                              # cardButtonOrdinalPart
    | card? button 'id' factor                                                                                          # cardButtonIdPart
    | buttonPart of cardPart                                                                                            # buttonOfCardPart
    ;

fieldPart
    : background? field factor                                                                                          # bkgndFieldPart
    | ordinal background? field                                                                                         # bkgndFieldOrdinalPart
    | background? field 'id' factor                                                                                     # bkgndFieldIdPart
    | card? field factor                                                                                                # cardFieldPart
    | ordinal card? field                                                                                               # cardFieldOrdinalPart
    | card? field 'id' factor                                                                                           # cardFieldIdPart
    | fieldPart of cardPart                                                                                             # fieldOfCardPart
    ;

cardPart
    : 'this' card                                                                                                       # thisCardPart
    | position card                                                                                                     # positionCardPart
    | ordinal card                                                                                                      # ordinalCardPart
    | card factor                                                                                                       # expressionCardPart
    | card 'id' factor                                                                                                  # cardIdPart
    ;

bkgndPart
    : background factor                                                                                                 # expressionBkgndPart
    | background 'id' factor                                                                                            # bkgndIdPart
    | ordinal background                                                                                                # ordinalBkgndPart
    | position background                                                                                               # positionBkgndPart
    | 'this' background                                                                                                 # thisBkgndPart
    ;

ordinal
    : 'the'? ordinalValue                                                                                               # theOrdinalVal
    ;

ordinalValue
    : 'first'                                                                                                           # firstOrd
    | 'second'                                                                                                          # secondOrd
    | 'third'                                                                                                           # thirdOrd
    | 'fourth'                                                                                                          # fourthOrd
    | 'fifth'                                                                                                           # fifthOrd
    | 'sixth'                                                                                                           # sixthOrd
    | 'seventh'                                                                                                         # seventhOrd
    | 'eigth'                                                                                                           # eigthOrd
    | 'ninth'                                                                                                           # ninthOrd
    | 'tenth'                                                                                                           # tenthOrd
    | ('mid' | 'middle')                                                                                                # midOrd
    | 'last'                                                                                                            # lastOrd
    ;

expression
    : constant                                                                                                          # constantExp
    | builtInFunc                                                                                                       # builtinFuncExp
    | ID '(' expressionList ')'                                                                                         # functionExp
    | factor                                                                                                            # factorExp
    | chunk expression                                                                                                  # chunkExp
    | 'not' expression                                                                                                  # notExp
    | '-' expression                                                                                                    # negateExp
    | expression '^' expression                                                                                         # caratExp
    | expression op=('mod'|'div'|'/'|'*') expression                                                                    # multiplicationExp
    | expression op=('+'|'-') expression                                                                                # additionExp
    | expression op=('&&'|'&') expression                                                                               # concatExp
    | expression op=('>='|'<='|'≤'|'≥'|'<'|'>'|'contains'|'is in'|'is a'|'is an'|'is not a'|'is not an') expression     # equalityExp
    | expression op=('='|'is not'|'is'|'<>'|'≠'|'is not in') expression                                                 # comparisonExp
    | expression op=('is within' | 'is not within') expression                                                          # withinExp
    | expression 'and' expression                                                                                       # andExp
    | expression 'or' expression                                                                                        # orExp
    ;

constant
    : 'empty'                                                                                                           # emptyExp
    | 'pi'                                                                                                              # piExp
    | 'quote'                                                                                                           # quoteExp
    | 'return'                                                                                                          # returnExp
    | 'space'                                                                                                           # spaceExp
    | 'tab'                                                                                                             # tabExp
    | 'formfeed'                                                                                                        # formFeedExp
    | 'linefeed'                                                                                                        # lineFeedExp
    | 'comma'                                                                                                           # commaExp
    | 'colon'                                                                                                           # colonExp
    | ('zero' | 'one' | 'two' | 'three' | 'four' | 'five' | 'six' | 'seven' | 'eight' | 'nine' | 'ten')                 # cardninalExp
    | ('commandkey' | 'cmdkey' | 'optionkey' | 'shiftkey')                                                              # modifierKeyExp
    ;

factor
    : literal                                                                                                           # literalFactor
    | ID                                                                                                                # idFactor
    | part                                                                                                              # partFactor
    | 'the'? 'selection'                                                                                                # selectionFactor
    | '(' expression ')'                                                                                                # expressionFactor
    | propertySpec                                                                                                      # idOfPartFactor
    | menu                                                                                                              # menuFactor
    | menuItem                                                                                                          # menuItemFactor
    | chunk factor                                                                                                      # chunkFactorChunk
    ;

builtInFunc
    : 'the'? singleArgFunc of factor                                                                                    # builtinFuncOneArgs
    | singleArgFunc '(' factor ')'                                                                                      # builtinFuncOneArgs
    | 'the' zeroArgFunc                                                                                                 # builtinFuncNoArg
    | multiArgFunc '(' expressionList ')'                                                                               # builtinFuncArgList
    ;

multiArgFunc
    : singleArgFunc                                                                                                     # oneArgArgFunc
    | 'annuity'                                                                                                         # annuityArgFunc
    | 'compound'                                                                                                        # compoundArgFunc
    | 'offset'                                                                                                          # offsetArgFunc
    ;

singleArgFunc
    : 'average'                                                                                                         # averageFunc
    | 'min'                                                                                                             # minFunc
    | 'max'                                                                                                             # maxFunc
    | 'sum'                                                                                                             # sumFunc
    | 'number of' character                                                                                             # numberOfCharsFunc
    | 'number of' word                                                                                                  # numberOfWordsFunc
    | 'number of' item                                                                                                  # numberOfItemsFunc
    | 'number of' line                                                                                                  # numberOfLinesFunc
    | 'number of' 'menuitems'                                                                                           # numberOfMenuItemsFunc
    | 'number of' card                                                                                                  # numberOfBkgndCardsFunc
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

zeroArgFunc
    : 'mouse'                                                                                                           # mouseFunc
    | 'mouseloc'                                                                                                        # mouseLocFunc
    | 'result'                                                                                                          # resultFunc
    | ('commandkey' | 'cmdkey')                                                                                         # commandKeyFunc
    | 'shiftkey'                                                                                                        # shiftKeyFunc
    | 'optionkey'                                                                                                       # optionKeyFunc
    | 'ticks'                                                                                                           # ticksFunc
    | 'seconds'                                                                                                         # secondsFunc
    | ('english date' | 'long date')                                                                                    # longDateFormatFunc
    | ('date' | 'short date')                                                                                           # shortDateFormatFunc
    | ('abbrev date' | 'abbreviated date')                                                                              # abbrevDateFormatFunc
    | ('english time' | 'long time')                                                                                    # longTimeFormatFunc
    | ('time' | 'short time' | 'abbrev time' | 'abbreviated time')                                                      # abbrevTimeFormatFunc
    | 'tool'                                                                                                            # toolFunc
    | 'number of' card? 'parts'                                                                                         # numberOfCardParts
    | 'number of' background 'parts'                                                                                    # numberOfBkgndParts
    | 'number of' card? button                                                                                          # numberOfCardButtons
    | 'number of' background button                                                                                     # numberOfBkgndButtons
    | 'number of' card field                                                                                            # numberOfCardFields
    | 'number of' background? field                                                                                     # numberOfBkgndFields
    | 'number of' 'menus'                                                                                               # numberOfMenusFunc
    | 'number of' card                                                                                                  # numberOfCardsFunc
    | 'number of' 'marked' card                                                                                         # numberOfMarkedCards
    | 'number of' background ('in' 'this' 'stack')?                                                                     # numberOfBackgrounds
    | 'menus'                                                                                                           # menusFunc
    | 'diskspace'                                                                                                       # diskSpaceNoArgFunc
    | 'params'                                                                                                          # paramsFunc
    | 'paramcount'                                                                                                      # paramCountFunc
    ;

literal
    : knownType
    | constant
    | LITERAL
    | TWO_ITEM_LIST
    | FOUR_ITEM_LIST
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

ID
    : (ALPHA (ALPHA | DIGIT)*)
    ;

LITERAL
    : STRING_LITERAL
    | NUMBER_LITERAL
    ;

fragment INTEGER_LITERAL
    : DIGIT+
    ;

fragment NUMBER_LITERAL
    : INTEGER_LITERAL
    | '-' INTEGER_LITERAL
    | '.' INTEGER_LITERAL
    | '-' '.' INTEGER_LITERAL
    | INTEGER_LITERAL '.'
    | '-' INTEGER_LITERAL '.'
    | INTEGER_LITERAL '.' INTEGER_LITERAL
    | '-' INTEGER_LITERAL '.' INTEGER_LITERAL
    ;

fragment STRING_LITERAL
    : '"' ~('"' | '\r' | '\n' )* '"'
    ;

TWO_ITEM_LIST
    : (LITERAL ',' LITERAL)
    ;

FOUR_ITEM_LIST
    : (LITERAL ',' LITERAL ',' LITERAL ',' LITERAL)
    ;

fragment ALPHA
    : ('a' .. 'z' | 'A' .. 'Z')+
    ;

fragment DIGIT
    : ('0' .. '9')+
    ;

COMMENT
    : '--' ~('\r' | '\n' | '|')* -> skip
    ;

BREAK
    : ('|' | '¬') NEWLINE -> skip
    ;

NEWLINE
    : ('\n' | '\r')+
    ;

WHITESPACE
    : (' ' | '\t')+ -> skip
    ;

UNLEXED_CHAR
    : .
    ;