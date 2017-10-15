grammar HyperTalk;

script              : handler (NEWLINE+ | EOF)                                                                          # handlerScript
                    | script handler (NEWLINE+ | EOF)                                                                   # scriptHandlerScript
                    | function (NEWLINE+ | EOF)                                                                         # functionScript
                    | script function (NEWLINE+ | EOF)                                                                  # scriptFunctionScript
                    | nonEmptyStmnt (NEWLINE+ | EOF)                                                                    # statementScript
                    | statementList (NEWLINE+ | EOF)                                                                    # statementListScript
                    | (NEWLINE+ | EOF)                                                                                  # whitespaceScript
                    | COMMENT                                                                                           # commentScript
                    ;

handler             : 'on' blockName NEWLINE statementList 'end' blockName                                              # populatedHandler
                    | 'on' blockName parameterList NEWLINE statementList 'end' blockName                                # populatedArgHandler
                    | 'on' blockName NEWLINE 'end' blockName                                                            # emptyHandler
                    | 'on' blockName parameterList NEWLINE 'end' blockName                                              # emptyArgHandler
                    ;

blockName           : ID
                    // Need a special rules here to handle command names because they're also lexed keywords
                    | 'answer' | 'ask' | 'put' | 'get' | 'set' | 'send' | 'wait' | 'sort' | 'go' | 'enable' | 'disable'
                    | 'read' | 'write' | 'hide' | 'show' | 'add' | 'subtract' | 'multiply' | 'divide' | 'choose'
                    | 'click' | 'drag' | 'type' | 'lock' | 'unlock' | 'pass' | 'domenu' | 'visual' | 'reset' | 'create'
                    | 'delete' | 'play' | 'dial' | 'beep' | 'open' | 'close' | 'select'
                    ;

function            : 'function' ID parameterList NEWLINE statementList 'end' ID                                        # populatedFunction
                    | 'function' ID parameterList NEWLINE 'end' ID                                                      # emptyFunction
                    ;

expressionList      : expression                                                                                        # singleExpArgList
                    | expressionList ',' expression                                                                     # multiExpArgList
                    |                                                                                                   # emptyArgList
                    ;

parameterList       : ID                                                                                                # singleParamList
                    | parameterList ',' ID                                                                              # multiParamList
                    |                                                                                                   # emptyParamList
                    ;

statementList       : statementList nonEmptyStmnt NEWLINE                                                               # multiStmntList
                    | nonEmptyStmnt NEWLINE                                                                             # singleStmntList
                    | statementList NEWLINE                                                                             # stmntListNewlineStmntList
                    | NEWLINE                                                                                           # newlineStmntList
                    ;

nonEmptyStmnt       : ifStatement                                                                                       # nonEmptyIfStmnt
                    | repeatStatement                                                                                   # nonEmptyRepeatStmnt
                    | doStmnt                                                                                           # nonEmptyDoStmnt
                    | globalStmnt                                                                                       # nonEmptyGlobalStmnt
                    | returnStmnt                                                                                       # nonEmptyReturnStmnt
                    | expression                                                                                        # nonEmptyExpStmnt
                    | commandStmnt                                                                                      # nonEmptyCommandStmnt
                    ;

returnStmnt         : 'return' expression                                                                               # eprReturnStmnt
                    | 'return'                                                                                          # voidReturnStmnt
                    ;

doStmnt             : 'do' expression
                    ;

commandStmnt        : answerCmd                                                                                         # answerCmdStmnt
                    | askCmd                                                                                            # askCmdStmnt
                    | putCmd                                                                                            # putCmdStmnt
                    | 'get' expression                                                                                  # getCmdStmnt
                    | 'set' propertySpec 'to' propertyValue                                                             # setCmdStmnt
                    | 'send' expression 'to' part                                                                       # sendCmdStmnt
                    | waitCmd                                                                                           # waitCmdStmnt
                    | sortCmd                                                                                           # sortCmdStmnt
                    | goCmd                                                                                             # goCmdStmt
                    | enableCmd                                                                                         # enableCmdStmnt
                    | disableCmd                                                                                        # disableCmdStmnt
                    | readCmd                                                                                           # readCmdStmt
                    | writeCmd                                                                                          # writeCmdStmt
                    | convertCmd                                                                                        # convertCmdStmt
                    | selectCmd                                                                                         # selectCmdStmt
                    | 'hide' part                                                                                       # hideCmdStmnt
                    | 'show' part                                                                                       # showCmdStmnt
                    | 'add' expression 'to' container                                                                   # addCmdStmnt
                    | 'subtract' expression 'from' container                                                            # subtractCmdStmnt
                    | 'multiply' container 'by' expression                                                              # multiplyCmdStmnt
                    | 'divide' container 'by' expression                                                                # divideCmdStmnt
                    | 'choose' expression 'tool'                                                                        # chooseToolCmdStmt
                    | 'choose' 'tool' expression                                                                        # chooseToolNumberCmdStmt
                    | 'click' 'at' expression                                                                           # clickCmdStmt
                    | 'click' 'at' expression 'with' expressionList                                                     # clickWithKeyCmdStmt
                    | 'drag' 'from' expression 'to' expression                                                          # dragCmdStmt
                    | 'drag' 'from' expression 'to' expression 'with' expressionList                                    # dragWithKeyCmdStmt
                    | 'type' expression                                                                                 # typeCmdStmt
                    | 'type' expression 'with' 'commandkey'                                                             # typeWithCmdKeyCmdStmt
                    | 'lock' 'screen'                                                                                   # lockScreenCmdStmt
                    | 'unlock' 'screen'                                                                                 # unlockScreenCmdStmt
                    | 'unlock' 'screen' 'with' 'visual' visualEffect                                                    # unlockScreenVisualCmdStmt
                    | 'pass' blockName                                                                                  # passCmdStmt
                    | 'domenu' factor                                                                                   # doMenuCmdStmt
                    | 'visual' visualEffect                                                                             # visualEffectCmdStmt
                    | 'reset' 'the'? 'menubar'                                                                          # resetMenuCmdStmt
                    | 'create' 'menu' factor                                                                            # createMenuCmdStmt
                    | 'delete' menu                                                                                     # deleteMenuCmdStmt
                    | 'delete' menuItem                                                                                 # deleteMenuItemCmdStmt
                    | 'delete' part                                                                                     # deleteCmdStmt
                    | 'delete' container                                                                                # deleteChunkCmdStmt
                    | 'play' expression music                                                                           # playCmdStmt
                    | 'dial' expression                                                                                 # dialCmdStmt
                    | 'beep'                                                                                            # beepCmdStmt
                    | 'open' 'file' expression                                                                          # openFileCmdStmt
                    | 'close' 'file' expression                                                                         # closeFileCmdStmt
                    | 'exit' 'repeat'                                                                                   # exitRepeatCmdStmt
                    | 'next' 'repeat'                                                                                   # nextRepeatCmdStmt
                    | 'exit' blockName                                                                                  # exitCmdStmt
                    | 'exit' 'to' 'hypercard'                                                                           # exitToHyperCardCmdStmt
                    | ID                                                                                                # noArgMsgCmdStmt
                    | ID expressionList                                                                                 # argMsgCmdStmt
                    ;

selectCmd           : 'select' part                                                                                     # selectPartCmd
                    | 'select' 'empty'                                                                                  # selectEmptyCmd
                    | 'select' 'text' 'of' part                                                                         # selectTextCmd
                    | 'select' 'before' 'text' 'of' part                                                                # selectBeforeCmd
                    | 'select' 'after' 'text' 'of' part                                                                 # selectAfterCmd
                    | 'select' chunk part                                                                               # selectChunkCmd
                    | 'select' 'before' chunk part                                                                      # selectBeforeChunkCmd
                    | 'select' 'after' chunk part                                                                       # selectAfterChunkCmd
                    ;

convertCmd          : 'convert' container 'to' convertible                                                              # convertContainerToCmd
                    | 'convert' container 'from' convertible 'to' convertible                                           # convertContainerFromToCmd
                    | 'convert' expression 'to' convertible                                                             # convertToCmd
                    | 'convert' expression 'from' convertible 'to' convertible                                          # convertFromToCmd
                    ;

writeCmd            : 'write' expression 'to' 'file' factor                                                             # writeFileCmd
                    | 'write' expression 'to' 'file' factor 'at' ('eof' | 'end')                                        # writeEndFileCmd
                    | 'write' expression 'to' 'file' factor 'at' factor                                                 # writeAtFileCmd
                    ;

readCmd             : 'read' 'from' 'file' factor                                                                       # readFileCmd
                    | 'read' 'from' 'file' factor 'for' factor                                                          # readFileForCmd
                    | 'read' 'from' 'file' factor 'at' factor 'for' factor                                              # readFileAtCmd
                    | 'read' 'from' 'file' factor 'until' factor                                                        # readFileUntil
                    ;

music               : expression                                                                                        # musicNotes
                    | 'tempo' factor expression                                                                         # musicNotesTempo
                    | 'tempo' factor                                                                                    # musicTempo
                    |                                                                                                   # musicDefault
                    ;

enableCmd           : 'enable' part                                                                                     # enablePartCmd
                    | 'enable' menuItem                                                                                 # enableMenuItemCmd
                    | 'enable' menu                                                                                     # enableMenuCmd
                    ;

disableCmd          : 'disable' part                                                                                    # disablePartCmd
                    | 'disable' menuItem                                                                                # disableMenuItemCmd
                    | 'disable' menu                                                                                    # disableMenuCmd
                    ;

goCmd               : 'go' 'to'? destination 'with' 'visual' visualEffect                                               # goVisualEffectCmdStmnd
                    | 'go' 'to'? destination                                                                            # goCmdStmnt
                    | 'go' 'back'                                                                                       # goBackCmdStmt
                    | 'go' 'back' 'with' 'visual' visualEffect                                                          # goBackVisualEffectCmdStmt
                    ;


answerCmd           : 'answer' expression 'with' expression 'or' expression 'or' expression                             # answerThreeButtonCmd
                    | 'answer' expression 'with' expression 'or' expression                                             # answerTwoButtonCmd
                    | 'answer' expression 'with' expression                                                             # answerOneButtonCmd
                    | 'answer' expression                                                                               # answerDefaultCmd
                    ;

askCmd              : 'ask' expression 'with' expression                                                                # askExpWithCmd
                    | 'ask' expression                                                                                  # askExpCmd
                    ;

putCmd              : 'put' expression                                                                                  # putIntoCmd
                    | 'put' expression preposition container                                                            # putPrepositionCmd
                    ;

waitCmd             : 'wait' factor timeUnit                                                                            # waitCountCmd
                    | 'wait for' factor timeUnit                                                                        # waitForCountCmd
                    | 'wait until' expression                                                                           # waitUntilCmd
                    | 'wait while' expression                                                                           # waitWhileCmd
                    ;

sortCmd             : 'sort' sortChunkType container sortDirection sortStyle                                            # sortDirectionCmd
                    | 'sort' sortChunkType container sortDirection sortStyle 'by' expression                            # sortExpressionCmd
                    | 'sort' sortDirection sortStyle 'by' expression                                                    # sortStackCmd
                    | 'sort' 'this'? 'stack' sortDirection sortStyle 'by' expression                                    # sortStackCmd
                    | 'sort' 'the'? 'cards' ('of' 'this' 'stack')? sortDirection sortStyle 'by' expression              # sortStackCmd
                    | 'sort' 'the'? 'marked' 'cards' ('of' 'this' 'stack')? sortDirection sortStyle 'by' expression     # sortMarkedCardsCmd
                    | 'sort' bkgndPart sortDirection sortStyle 'by' expression                                          # sortBkgndCardsCmd
                    | 'sort' 'the'? 'cards' 'of' bkgndPart sortDirection sortStyle 'by' expression                      # sortBkgndCardsCmd
                    | 'sort' 'the'? 'marked' 'cards' 'of' bkgndPart sortDirection sortStyle 'by' expression             # sortMarkedBkgndCardsCmd
                    ;

convertible         : conversionFormat                                                                                  # singleFormatConvertible
                    | conversionFormat 'and' conversionFormat                                                           # dualFormatConvertible
                    ;

conversionFormat    : 'seconds'                                                                                         # secondsConvFormat
                    | 'dateitems'                                                                                       # dateItemsConvFormat
                    | ('long date' | 'english date')                                                                    # longDateConvFormat
                    | ('date' | 'short date')                                                                           # shortDateConvFormat
                    | ('abbrev date' | 'abbreviated date')                                                              # abbrevDateConvFormat
                    | ('time' | 'short time' | 'abbrev time' | 'abbreviated time')                                      # shortTimeConvFormat
                    | ('english time' | 'long time')                                                                    # longTimeConvFormat
                    ;

sortDirection       : 'ascending'                                                                                       # sortDirectionAsc
                    | 'descending'                                                                                      # sortDirectionDesc
                    |                                                                                                   # sortDirectionDefault
                    ;

sortChunkType       : 'the'? ('line' | 'lines') ('of' | 'in')                                                           # sortChunkLines
                    | 'the'? ('item' | 'items') ('of' | 'in')                                                           # sortChunkItems
                    |                                                                                                   # sortChunkDefault
                    ;

sortStyle           : 'text'                                                                                            # sortStyleText
                    | 'numeric'                                                                                         # sortStyleNumeric
                    | 'international'                                                                                   # sortStyleInternational
                    | 'datetime'                                                                                        # sortStyleDateTime
                    |                                                                                                   # sortStyleDefault
                    ;

visualEffect        : 'effect'? effect                                                                                  # effectDefault
                    | 'effect'? effect 'to' image                                                                       # effectTo
                    | 'effect'? effect speed                                                                            # effectSpeed
                    | 'effect'? effect speed 'to' image                                                                 # effectSpeedTo
                    ;

speed               : 'fast'                                                                                            # fastSpeed
                    | ('slow' | 'slowly')                                                                               # slowSpeed
                    | 'very' 'fast'                                                                                     # veryFastSpeed
                    | 'very' ('slow' | 'slowly')                                                                        # verySlowSpeed
                    ;

image               : 'black'                                                                                           # blackImage
                    | 'card'                                                                                            # cardImage
                    | ('gray' | 'grey')                                                                                 # grayImage
                    | 'inverse'                                                                                         # inverseImage
                    | 'white'                                                                                           # whiteImage
                    ;

effect              : 'dissolve'                                                                                        # dissolveEffect
                    | 'barn door open'                                                                                  # barnDoorOpenEffect
                    | 'barn door close'                                                                                 # barnDoorCloseEffect
                    | 'checkerboard'                                                                                    # checkerboardEffect
                    | 'iris open'                                                                                       # irisOpenEffect
                    | 'iris close'                                                                                      # irisCloseEffect
                    | 'plain'                                                                                           # plainEffect
                    | 'scroll down'                                                                                     # scrollDownEffect
                    | 'scroll up'                                                                                       # scrollUpEffect
                    | 'scroll left'                                                                                     # scrollLeftEffect
                    | 'scroll right'                                                                                    # scrollRightEffect
                    | 'shrink to top'                                                                                   # shrinkToTopEffect
                    | 'shrink to center'                                                                                # shrinkToCenterEffect
                    | 'shrink to bottom'                                                                                # shrinkToBottomEffect
                    | 'stretch from top'                                                                                # stretchFromTopEffect
                    | 'stretch from center'                                                                             # stretchFromCenterEffect
                    | 'stretch from bottom'                                                                             # stretchFromBottomEffect
                    | 'venetian blinds'                                                                                 # venitianBlindsEffect
                    | 'wipe up'                                                                                         # wipeUpEffect
                    | 'wipe down'                                                                                       # wipeDownEffect
                    | 'wipe left'                                                                                       # wipeLeftEffect
                    | 'wipe right'                                                                                      # wipeRightEffect
                    | 'zoom in'                                                                                         # zoomInEffect
                    | 'zoom out'                                                                                        # zoomOutEffect
                    ;

// Can't use a lexer rule for synonyms here because it creates ambiguity with ordinals and built-in function names. :(
timeUnit            : 'ticks'                                                                                           # ticksTimeUnit
                    | 'tick'                                                                                            # tickTimeUnit
                    | 'seconds'                                                                                         # secondsTimeUnit
                    | 'sec'                                                                                             # secTimeUnit
                    | 'second'                                                                                          # secondTimeUnit
                    ;

position            : 'the'? 'next'                                                                                     # nextPosition
                    | 'the'? ('prev' | 'previous')                                                                      # prevPosition
                    | 'this'                                                                                            # thisPosition
                    ;

destination         : destinationType expression                                                                        # cardNumber
                    | ordinal destinationType                                                                           # cardOrdinal
                    | position destinationType                                                                          # cardPosition
                    | factor                                                                                            # destinationRef
                    ;

destinationType     : ('card' | 'cd')                                                                                   # cardDestinationType
                    | ('background' | 'bkgnd' | 'bg')                                                                   # bkgndDestinationType
                    ;

ifStatement         : 'if' expression NEWLINE? singleThen NEWLINE?                                                      # ifThenSingleLine
                    | 'if' expression NEWLINE? multiThen NEWLINE?                                                       # ifThenMultiline
                    ;

singleThen          : 'then' NEWLINE? nonEmptyStmnt NEWLINE? elseBlock                                                  # singleThenNewlineElse
                    | 'then' NEWLINE? nonEmptyStmnt elseBlock                                                           # singleThenElse
                    | 'then' NEWLINE? nonEmptyStmnt                                                                     # singleThenNoElse
                    ;

multiThen           : 'then' statementList 'end if'                                                                     # emptyElse
                    | 'then' 'end if'                                                                                   # emptyThenEmptyElse
                    | 'then' statementList elseBlock                                                                    # thenElse
                    ;

elseBlock           : 'else' nonEmptyStmnt                                                                              # elseStmntBlock
                    | 'else' NEWLINE? statementList 'end if'                                                            # elseStmntListBlock
                    | 'else' NEWLINE 'end if'                                                                           # elseEmptyBlock
                    ;

repeatStatement     : 'repeat' repeatRange NEWLINE statementList 'end repeat'                                           # repeatStmntList
                    | 'repeat' repeatRange NEWLINE 'end repeat'                                                         # repeatEmpty
                    ;

repeatRange         : 'forever'                                                                                         # infiniteLoop
                    |                                                                                                   # infiniteLoop
                    | duration                                                                                          # durationLoop
                    | count                                                                                             # countLoop
                    | 'with' ID '=' range                                                                               # withLoop
                    ;

duration            : 'until' expression                                                                                # untilDuration
                    | 'while' expression                                                                                # whileDuration
                    ;

count               : 'for' expression 'times'
                    | 'for' expression
                    | expression 'times'
                    | expression
                    ;

range               : expression 'down to' expression                                                                   # rangeDownTo
                    | expression 'to' expression                                                                        # rangeUpTo
                    ;

globalStmnt         : 'global' ID
                    ;

preposition         : 'before'                                                                                          # beforePreposition
                    | 'after'                                                                                           # afterPreposition
                    | 'into'                                                                                            # intoPreposition
                    ;

chunk               : chunk chunk                                                                                       # compositeChunk
                    | ordinal ('char' | 'character' | 'chars' | 'characters') ('of' | 'in')                             # ordinalCharChunk
                    | ('char' | 'character' | 'chars' | 'characters') expression 'to' expression ('of' | 'in')          # rangeCharChunk
                    | ('char' | 'character' | 'chars' | 'characters') expression ('of' | 'in')                          # charCharChunk
                    | ordinal ('word' | 'words') ('of' | 'in')                                                          # ordinalWordChunk
                    | ('word' | 'words') expression 'to' expression ('of' | 'in')                                       # rangeWordChunk
                    | ('word' | 'words') expression ('of' | 'in')                                                       # wordWordChunk
                    | ordinal ('item' | 'items') ('of' | 'in')                                                          # ordinalItemChunk
                    | ('item' | 'items') expression 'to' expression ('of' | 'in')                                       # rangeItemChunk
                    | ('item' | 'items') expression ('of' | 'in')                                                       # itemItemChunk
                    | ordinal ('line' | 'lines') ('of' | 'in')                                                          # ordinalLineChunk
                    | ('line' | 'lines') expression 'to' expression ('of' | 'in')                                       # rangeLineChunk
                    | ('line' | 'lines') expression ('of' | 'in')                                                       # lineLineChunk
                    ;

container           : ID                                                                                                # variableDest
                    | 'the'? ('message' | 'message' 'box' | 'message' 'window')                                         # messageDest
                    | chunk 'the'? ('message' | 'message' 'box' | 'message' 'window')                                   # chunkMessageDest
                    | 'the' 'selection'                                                                                 # selectionDest
                    | chunk 'the' 'selection'                                                                           # chunkSelectionDest
                    | chunk ID                                                                                          # chunkVariableDest
                    | part                                                                                              # partDest
                    | chunk part                                                                                        # chunkPartDest
                    | chunk                                                                                             # chunkDest
                    | propertySpec                                                                                      # propertyDest
                    | chunk propertySpec                                                                                # chunkPropertyDest
                    | menu                                                                                              # menuDest
                    | menuItem                                                                                          # menuItemDest
                    |                                                                                                   # defaultDest
                    ;

menu                : 'menu' factor                                                                                     # expressionMenu
                    | ordinal 'menu'                                                                                    # ordinalMenu
                    ;

menuItem            : 'menuitem' factor ('of' | 'from') menu                                                            # expressionMenuItem
                    | ordinal 'menuitem' ('of' | 'from') menu                                                           # ordinalMenuItem
                    ;

propertyValue       : 'plain'                                                                                           # propertyValueLiteral
                    | expression                                                                                        # propertyValueExp
                    ;

propertySpec        : 'the'? propertyName                                                                               # propertySpecGlobal
                    | 'the'? propertyName ('of' | 'in') part                                                            # propertySpecPart
                    | 'the'? propertyName ('of' | 'in') chunk part                                                      # propertySpecChunkPart
                    | 'the'? propertyName 'of' menuItem                                                                 # propertySpecMenuItem
                    ;

propertyName        : 'marked'          // Requires special rule because 'marked' is also a lexed token
                    | 'id'
                    | 'rect' | 'rectangle'
                    | ID
                    ;

part                : buttonPart                                                                                        # buttonPartPart
                    | fieldPart                                                                                         # fieldPartPart
                    | bkgndPart                                                                                         # bkgndPartPart
                    | cardPart                                                                                          # cardPartPart
                    | ('card' | 'cd') 'part' factor                                                                     # cardPartNumberPart
                    | ('background' | 'bkgnd' | 'bg') 'part' factor                                                     # bkgndPartNumberPart
                    | 'me'                                                                                              # mePart
                    | 'the'? ('message' | 'message' 'box' | 'message' 'window')                                         # msgPart
                    | ID                                                                                                # partRef
                    ;

buttonPart          : ('background' | 'bkgnd' | 'bg')? 'button' factor                                                  # bkgndButtonPart
                    | ordinal ('background' | 'bkgnd' | 'bg')? 'button'                                                 # bkgndButtonOrdinalPart
                    | ('background' | 'bkgnd' | 'bg')? 'button' 'id' factor                                             # bkgndButtonIdPart
                    | ('card' | 'cd')? 'button' factor                                                                  # cardButtonPart
                    | ordinal ('card' | 'cd')? 'button'                                                                 # cardButtonOrdinalPart
                    | ('card' | 'cd')? 'button' 'id' factor                                                             # cardButtonIdPart
                    ;

fieldPart           : ('background' | 'bkgnd' | 'bg')? 'field' factor                                                   # bkgndFieldPart
                    | ordinal ('background' | 'bkgnd' | 'bg')? 'field'                                                  # bkgndFieldOrdinalPart
                    | ('background' | 'bkgnd' | 'bg')? 'field' 'id' factor                                              # bkgndFieldIdPart
                    | ('card' | 'cd')? 'field' factor                                                                   # cardFieldPart
                    | ordinal ('card' | 'cd')? 'field'                                                                  # cardFieldOrdinalPart
                    | ('card' | 'cd')? 'field' 'id' factor                                                              # cardFieldIdPart
                    ;

cardPart            : 'this' ('card' | 'cd')                                                                            # thisCardPart
                    | position ('card' | 'cd')                                                                          # positionCardPart
                    | ordinal ('card' | 'cd')                                                                           # ordinalCardPart
                    | ('card' | 'cd') factor                                                                            # expressionCardPart
                    | ('card' | 'cd') 'id' factor                                                                       # cardIdPart
                    ;

bkgndPart           : ('background' | 'bkgnd' | 'bg') factor                                                            # expressionBkgndPart
                    | ('background' | 'bkgnd' | 'bg') 'id' factor                                                       # bkgndIdPart
                    | ordinal ('background' | 'bkgnd' | 'bg')                                                           # ordinalBkgndPart
                    | position ('background' | 'bkgnd' | 'bg')                                                          # positionBkgndPart
                    | 'this' ('background' | 'bkgnd' | 'bg')                                                            # thisBkgndPart
                    ;

ordinal             : 'the'? ordinalValue                                                                               # theOrdinalVal
                    ;

ordinalValue        : 'first'                                                                                           # firstOrd
                    | 'second'                                                                                          # secondOrd
                    | 'third'                                                                                           # thirdOrd
                    | 'fourth'                                                                                          # fourthOrd
                    | 'fifth'                                                                                           # fifthOrd
                    | 'sixth'                                                                                           # sixthOrd
                    | 'seventh'                                                                                         # seventhOrd
                    | 'eigth'                                                                                           # eigthOrd
                    | 'ninth'                                                                                           # ninthOrd
                    | 'tenth'                                                                                           # tenthOrd
                    | ('mid' | 'middle')                                                                                # midOrd
                    | 'last'                                                                                            # lastOrd
                    ;

expression          : constant                                                                                          # constantExp
                    | builtInFunc                                                                                       # builtinFuncExp
                    | ID '(' expressionList ')'                                                                         # functionExp
                    | factor                                                                                            # factorExp
                    | chunk expression                                                                                  # chunkExp
                    | 'not' expression                                                                                  # notExp
                    | '-' expression                                                                                    # negateExp
                    | expression '^' expression                                                                         # caratExp
                    | expression op=('mod'|'div'|'/'|'*') expression                                                    # multiplicationExp
                    | expression op=('+'|'-') expression                                                                # additionExp
                    | expression op=('&&'|'&') expression                                                               # concatExp
                    | expression op=('>='|'<='|'≤'|'≥'|'<'|'>'|'contains'|'is in'|'is a' | 'is an' | 'is not a' | 'is not an') expression   # equalityExp
                    | expression op=('='|'is not'|'is'|'<>'|'≠'|'is not in') expression                              # comparisonExp
                    | expression op=('is within' | 'is not within') expression                                          # withinExp
                    | expression 'and' expression                                                                       # andExp
                    | expression 'or' expression                                                                        # orExp
                    ;

constant            : 'empty'                                                                                           # emptyExp
                    | 'pi'                                                                                              # piExp
                    | 'quote'                                                                                           # quoteExp
                    | 'return'                                                                                          # returnExp
                    | 'space'                                                                                           # spaceExp
                    | 'tab'                                                                                             # tabExp
                    | 'formfeed'                                                                                        # formFeedExp
                    | 'linefeed'                                                                                        # lineFeedExp
                    | 'comma'                                                                                           # commaExp
                    | 'colon'                                                                                           # colonExp
                    | ('one' | 'two' | 'three' | 'four' | 'five' | 'six' | 'seven' | 'eight' | 'nine' | 'ten')          # cardninalExp
                    ;

factor              : literal                                                                                           # literalFactor
                    | ID                                                                                                # idFactor
                    | part                                                                                              # partFactor
                    | 'the'? 'selection'                                                                                # selectionFactor
                    | '(' expression ')'                                                                                # expressionFactor
                    | propertySpec                                                                                      # idOfPartFactor
                    | menu                                                                                              # menuFactor
                    | menuItem                                                                                          # menuItemFactor
                    | chunk factor                                                                                      # chunkFactorChunk
                    ;

builtInFunc         : 'the'? oneArgFunc ('of' | 'in') factor                                                            # builtinFuncOneArgs
                    | oneArgFunc '(' factor ')'                                                                         # builtinFuncOneArgs
                    | 'the' noArgFunc                                                                                   # builtinFuncNoArg
                    | argFunc '(' expressionList ')'                                                                    # builtinFuncArgList
                    ;

argFunc             : oneArgFunc                                                                                        # oneArgArgFunc
                    | 'annuity'                                                                                         # annuityArgFunc
                    | 'compound'                                                                                        # compoundArgFunc
                    | 'offset'                                                                                          # offsetArgFunc
                    ;

oneArgFunc          : 'average'                                                                                         # averageFunc
                    | 'min'                                                                                             # minFunc
                    | 'max'                                                                                             # maxFunc
                    | 'sum'                                                                                             # sumFunc
                    | 'number of' ('char' | 'character' | 'chars' | 'characters')                                       # numberOfCharsFunc
                    | 'number of' ('word' | 'words')                                                                    # numberOfWordsFunc
                    | 'number of' ('item' | 'items')                                                                    # numberOfItemsFunc
                    | 'number of' ('line' | 'lines')                                                                    # numberOfLinesFunc
                    | 'number of' 'menuitems'                                                                           # numberOfMenuItemsFunc
                    | 'number of' 'cards'                                                                               # numberOfBkgndCardsFunc
                    | 'random'                                                                                          # randomFunc
                    | 'sqrt'                                                                                            # sqrtFunc
                    | 'trunc'                                                                                           # truncFunc
                    | 'sin'                                                                                             # sinFunc
                    | 'cos'                                                                                             # cosFunc
                    | 'tan'                                                                                             # tanFunc
                    | 'atan'                                                                                            # atanFunc
                    | 'exp'                                                                                             # expFunc
                    | 'exp1'                                                                                            # exp1Func
                    | 'exp2'                                                                                            # exp2Func
                    | 'ln'                                                                                              # lnFunc
                    | 'ln1'                                                                                             # ln1Func
                    | 'log2'                                                                                            # log2Func
                    | 'abs'                                                                                             # absFunc
                    | 'chartonum'                                                                                       # charToNumFunc
                    | 'numtochar'                                                                                       # numToCharFunc
                    | 'value'                                                                                           # valueFunc
                    | 'length'                                                                                          # lengthFunc
                    | 'diskspace'                                                                                       # diskSpaceFunc
                    | 'param'                                                                                           # paramFunc
                    ;

noArgFunc           : 'mouse'                                                                                           # mouseFunc
                    | 'mouseloc'                                                                                        # mouseLocFunc
                    | 'result'                                                                                          # resultFunc
                    | ('commandkey' | 'cmdkey')                                                                         # commandKeyFunc
                    | 'shiftkey'                                                                                        # shiftKeyFunc
                    | 'optionkey'                                                                                       # optionKeyFunc
                    | 'ticks'                                                                                           # ticksFunc
                    | 'seconds'                                                                                         # secondsFunc
                    | ('english date' | 'long date')                                                                    # longDateFormatFunc
                    | ('date' | 'short date')                                                                           # shortDateFormatFunc
                    | ('abbrev date' | 'abbreviated date')                                                              # abbrevDateFormatFunc
                    | ('english time' | 'long time')                                                                    # longTimeFormatFunc
                    | ('time' | 'short time' | 'abbrev time' | 'abbreviated time')                                      # abbrevTimeFormatFunc
                    | 'tool'                                                                                            # toolFunc
                    | 'number of' ('card' | 'cd')? 'parts'                                                              # numberOfCardParts
                    | 'number of' ('background' | 'bkgnd' | 'bg') 'parts'                                               # numberOfBkgndParts
                    | 'number of' ('card' | 'cd')? 'buttons'                                                            # numberOfCardButtons
                    | 'number of' ('background' | 'bkgnd' | 'bg') 'buttons'                                             # numberOfBkgndButtons
                    | 'number of' ('card' | 'cd') 'fields'                                                              # numberOfCardFields
                    | 'number of' ('background' | 'bkgnd' | 'bg')? 'fields'                                             # numberOfBkgndFields
                    | 'number of' 'menus'                                                                               # numberOfMenusFunc
                    | 'number of' 'cards'                                                                               # numberOfCardsFunc
                    | 'number of' 'marked' 'cards'                                                                      # numberOfMarkedCards
                    | 'number of' ('backgrounds' | 'bkgnds' | 'bgs') ('in' 'this' 'stack')?                             # numberOfBackgrounds
                    | 'menus'                                                                                           # menusFunc
                    | 'diskspace'                                                                                       # diskSpaceNoArgFunc
                    | 'params'                                                                                          # paramsFunc
                    | 'paramcount'                                                                                      # paramCountFunc
                    ;

literal             : knownType
                    | LITERAL
                    | TWO_ITEM_LIST
                    | FOUR_ITEM_LIST
                    ;

knownType           : 'number'
                    | 'integer'
                    | 'point'
                    | 'rect' | 'rectangle'
                    | 'date'
                    | 'logical' | 'boolean' | 'bool'
                    ;

ID                  : (ALPHA (ALPHA | DIGIT)*) ;
LITERAL             : STRING_LITERAL | NUMBER_LITERAL;

INTEGER_LITERAL     : DIGIT+ ;

NUMBER_LITERAL      : INTEGER_LITERAL
                    | '-' INTEGER_LITERAL
                    | '.' INTEGER_LITERAL
                    | '-' '.' INTEGER_LITERAL
                    | INTEGER_LITERAL '.'
                    | '-' INTEGER_LITERAL '.'
                    | INTEGER_LITERAL '.' INTEGER_LITERAL
                    | '-' INTEGER_LITERAL '.' INTEGER_LITERAL
                    ;

STRING_LITERAL      : '"' ~('"' | '\r' | '\n' )* '"' ;
TWO_ITEM_LIST       : (LITERAL ',' LITERAL);
FOUR_ITEM_LIST      : (LITERAL ',' LITERAL ',' LITERAL ',' LITERAL);

ALPHA               : ('a' .. 'z' | 'A' .. 'Z')+ ;
DIGIT               : ('0' .. '9')+ ;

COMMENT             : '--' ~('\r' | '\n' | '|')* -> skip;
BREAK               : ('|' | '¬') NEWLINE -> skip;
NEWLINE             : ('\n' | '\r')+;
WHITESPACE          : (' ' | '\t')+ -> skip;
UNLEXED_CHAR        : . ;