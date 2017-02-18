grammar HyperTalk;

script			    : handler (NEWLINE+ | EOF)                      # handlerScript
                    | script handler (NEWLINE+ | EOF)               # scriptHandlerScript
                    | function (NEWLINE+ | EOF)                     # functionScript
                    | script function (NEWLINE+ | EOF)              # scriptFunctionScript
                    | nonEmptyStmnt (NEWLINE+ | EOF)                # statementScript
                    | statementList (NEWLINE+ | EOF)                # statementListScript
                    | (NEWLINE+ | EOF)                              # whitespaceScript
                    | COMMENT                                       # commentScript
                    ;

handler				: 'on' ID NEWLINE statementList 'end' ID        # populatedHandler
                    | 'on' ID NEWLINE 'end' ID                      # emptyHandler
                    ;

function			: 'function' ID parameterList NEWLINE statementList 'end' ID    # populatedFunction
                    | 'function' ID parameterList NEWLINE 'end' ID                  # emptyFunction
                    ;

expressionList		: expression                                    # singleExpArgList
                    | expressionList ',' expression                 # multiExpArgList
                    |                                               # emptyArgList
                    ;

parameterList		: ID                                            # singleParamList
                    | parameterList ',' ID                          # multiParamList
                    |                                               # emptyParamList
                    ;

statementList		: statementList nonEmptyStmnt NEWLINE           # multiStmntList
                    | nonEmptyStmnt NEWLINE                         # singleStmntList
                    | statementList NEWLINE                         # stmntListNewlineStmntList
                    | NEWLINE                                       # newlineStmntList
                    ;

nonEmptyStmnt		: commandStmnt                                  # nonEmptyCommandStmnt
                    | globalStmnt                                   # nonEmptyGlobalStmnt
                    | ifStatement                                   # nonEmptyIfStmnt
                    | repeatStatement                               # nonEmptyRepeatStmnt
                    | doStmnt                                       # nonEmptyDoStmnt
                    | returnStmnt                                   # nonEmptyReturnStmnt
                    | expression                                    # nonEmptyExpStmnt
                    ;

returnStmnt			: 'return' expression                           # eprReturnStmnt
                    | 'return'                                      # voidReturnStmnt
                    ;

doStmnt				: 'do' expression
                    ;

commandStmnt		: answerCmd                                     # answerCmdStmnt
                    | askCmd                                        # askCmdStmnt
                    | putCmd                                        # putCmdStmnt
                    | getCmd                                        # getCmdStmnt
                    | setCmd                                        # setCmdStmnt
                    | sendCmd                                       # sendCmdStmnt
                    | waitCmd                                       # waitCmdStmnt
                    | sortCmd                                       # sortCmdStmnt
                    | 'go' 'to'? destination                        # goCmdStmnt
                    | 'add' expression 'to' container               # addCmdStmnt
                    | 'subtract' expression 'from' container        # subtractCmdStmnt
                    | 'multiply' container 'by' expression          # multiplyCmdStmnt
                    | 'divide' container 'by' expression            # divideCmdStmnt
                    | 'choose' expression 'tool'                    # chooseToolCmdStmt
                    | 'choose' 'tool' expression                    # chooseToolNumberCmdStmt
                    | 'click' 'at' expression                       # clickCmdStmt
                    | 'click' 'at' expression 'with' expressionList # clickWithKeyCmdStmt
                    | 'drag' 'from' expression 'to' expression      # dragCmdStmt
                    | 'drag' 'from' expression 'to' expression 'with' expressionList            # dragWithKeyCmdStmt
                    ;

answerCmd			: 'answer' expression 'with' expression 'or' expression 'or' expression     # answerThreeButtonCmd
                    | 'answer' expression 'with' expression 'or' expression                     # answerTwoButtonCmd
                    | 'answer' expression 'with' expression                                     # answerOneButtonCmd
                    | 'answer' expression                                                       # answerDefaultCmd
                    ;

askCmd				: 'ask' expression 'with' expression            # askExpWithCmd
                    | 'ask' expression                              # askExpCmd
                    ;

putCmd				: 'put' expression                              # putIntoCmd
                    | 'put' expression preposition container        # putPrepositionCmd
                    ;

getCmd				: 'get' expression
                    ;

setCmd				: 'set' propertySpec 'to' expression            # setPropertyCmd
                    ;

sendCmd				: 'send' expression 'to' part
                    ;

waitCmd             : 'wait' factor timeUnit                        # waitCountCmd
                    | 'wait for' factor timeUnit                    # waitForCountCmd
                    | 'wait until' expression                       # waitUntilCmd
                    | 'wait while' expression                       # waitWhileCmd
                    ;

sortCmd             : 'sort' sortChunkType container sortDirection      # sortDirectionCmd
                    | 'sort' sortChunkType container 'by' expression    # sortExpressionCmd
                    ;

sortDirection       : 'ascending'                                   # sortDirectionAsc
                    | 'descending'                                  # sortDirectionDesc
                    |                                               # sortDirectionDefault
                    ;

sortChunkType       : 'the'? LINE ('of' | 'in')                     # sortChunkLines
                    | 'the'? ITEM ('of' | 'in')                     # sortChunkItems
                    |                                               # sortChunkDefault
                    ;

// Can't use a lexer rule for synonyms here because it creates ambiguity with ordinals and built-in function names. :(
timeUnit            : 'ticks'                                       # ticksTimeUnit
                    | 'tick'                                        # tickTimeUnit
                    | 'seconds'                                     # secondsTimeUnit
                    | 'sec'                                         # secTimeUnit
                    | 'second'                                      # secondTimeUnit
                    ;

position            : 'the'? 'next'                                 # nextPosition
                    | 'the'? ('prev' | 'previous')                  # prevPosition
                    ;

destination         : destinationType expression                    # cardNumber
                    | ordinal destinationType                       # cardOrdinal
                    | position destinationType                      # cardPosition
                    ;

destinationType     : 'card'
                    |
                    ;

ifStatement			: 'if' expression THEN singleThen               # ifThenSingleLine
                    | 'if' expression THEN NEWLINE multiThen        # ifThenMultiline
                    ;

singleThen			: nonEmptyStmnt NEWLINE elseBlock               # singleThenNewlineElse
                    | nonEmptyStmnt elseBlock                       # singleThenElse
                    | nonEmptyStmnt                                 # singleThenNoElse
                    ;

multiThen			: statementList 'end if'                        # emptyElse
                    | 'end if'                                      # emptyThenEmptyElse
                    | statementList elseBlock                       # thenElse
                    ;

elseBlock			: 'else' nonEmptyStmnt                          # elseStmntBlock
                    | 'else' NEWLINE statementList 'end if'         # elseStmntListBlock
                    | 'else' NEWLINE 'end if'                       # elseEmptyBlock
                    ;

repeatStatement		: 'repeat' repeatRange NEWLINE statementList 'end repeat'       # repeatStmntList
                    | 'repeat' repeatRange NEWLINE 'end repeat'                     # repeatEmpty
                    ;

repeatRange			: 'forever'                                     # infiniteLoop
                    | duration                                      # durationLoop
                    | count                                         # countLoop
                    | 'with' ID '=' range                           # withLoop
                    ;

duration			: 'until' expression                            # untilDuration
                    | 'while' expression                            # whileDuration
                    ;

count				: 'for' expression 'times'
                    | 'for' expression
                    | expression 'times'
                    | expression
                    ;

range				: expression 'down to' expression               # rangeDownTo
                    | expression 'to' expression                    # rangeUpTo
                    ;

globalStmnt			: 'global' ID
                    ;

preposition			: 'before'                                      # beforePreposition
                    | 'after'                                       # afterPreposition
                    | 'into'                                        # intoPreposition
                    ;

chunk               : chunk chunk                                   # compositeChunk
                    | ordinal CHAR ('of' | 'in')                    # ordinalCharChunk
                    | CHAR expression 'to' expression ('of' | 'in') # rangeCharChunk
                    | CHAR expression ('of' | 'in')                 # charCharChunk
                    | ordinal WORD ('of' | 'in')                    # ordinalWordChunk
                    | WORD expression 'to' expression ('of' | 'in') # rangeWordChunk
                    | WORD expression ('of' | 'in')                 # wordWordChunk
                    | ordinal ITEM ('of' | 'in')                    # ordinalItemChunk
                    | ITEM expression 'to' expression ('of' | 'in') # rangeItemChunk
                    | ITEM expression ('of' | 'in')                 # itemItemChunk
                    | ordinal LINE ('of' | 'in')                    # ordinalLineChunk
                    | LINE expression 'to' expression ('of' | 'in') # rangeLineChunk
                    | LINE expression ('of' | 'in')                 # lineLineChunk
                    ;

container			: ID                                                                # variableDest
                    | 'the'? ('message' | 'message' 'box' | 'message' 'window')         # messageDest
                    | chunk 'the'? ('message' | 'message' 'box' | 'message' 'window')   # chunkMessageDest
                    | chunk ID                                                          # chunkVariableDest
                    | part                                                              # partDest
                    | chunk part                                                        # chunkPartDest
                    | chunk                                                             # chunkDest
                    | propertySpec                                                      # propertyDest
                    | chunk propertySpec                                                # chunkPropertyDest
                    |                                                                   # defaultDest
                    ;

propertySpec        : 'the'? ID                                     # propertySpecGlobal
                    | 'the'? ID ('of' | 'in') part                  # propertySpecPart
                    ;

part                : FIELD factor                                  # fieldPart
                    | FIELD 'id' factor                             # fieldIdPart
                    | BUTTON factor                                 # buttonPart
                    | BUTTON 'id' factor                            # buttonIdPart
                    | 'card' factor                                 # cardPart
                    | 'card' 'id' factor                            # cardIdPart
                    | 'me'                                          # mePart
                    ;

ordinal             : 'the'? ordinalValue                           # theOrdinalVal
                    ;

ordinalValue        : 'first'                                       # firstOrd
                    | 'second'                                      # secondOrd
                    | 'third'                                       # thirdOrd
                    | 'fourth'                                      # fourthOrd
                    | 'fifth'                                       # fifthOrd
                    | 'sixth'                                       # sixthOrd
                    | 'seventh'                                     # seventhOrd
                    | 'eigth'                                       # eigthOrd
                    | 'ninth'                                       # ninthOrd
                    | 'tenth'                                       # tenthOrd
                    | ('mid' | 'middle')                            # midOrd
                    | 'last'                                        # lastOrd
                    ;

expression          : 'empty'                                                           # emptyExp
                    | builtinFunc                                                       # builtinFuncExp
                    | ID '(' expressionList ')'                                         # functionExp
                    | factor                                                            # factorExp
                    | chunk expression                                                  # chunkExp
                    | 'not' expression                                                  # notExp
                    | '-' expression                                                    # negateExp
                    | expression '^' expression                                         # caratExp
                    | expression op=('mod'|'div'|'/'|'*') expression                    # multiplicationExp
                    | expression op=('+'|'-') expression                                # additionExp
                    | expression op=('&&'|'&') expression                               # concatExp
                    | expression op=('>='|'<='|'<'|'>'|'contains'|'is in'|'is a' | 'is an' | 'is not a' | 'is not an') expression   # equalityExp
                    | expression op=('='|'is not'|'is'|'<>'|'is not in') expression     # comparisonExp
                    | expression op=('is within' | 'is not within') expression          # withinExp
                    | expression 'and' expression                                       # andExp
                    | expression 'or' expression                                        # orExp
                    ;

factor				: literal                                       # literalFactor
                    | ID                                            # idFactor
                    | part                                          # partFactor
                    | '(' expression ')'                            # expressionFactor
                    | propertySpec                                  # idOfPartFactor
                    ;

builtinFunc         : 'the'? oneArgFunc ('of' | 'in') factor        # builtinFuncOneArgs
                    | 'the' noArgFunc                               # builtinFuncNoArg
                    | oneArgFunc '(' expressionList ')'             # builtinFuncArgList
                    ;

oneArgFunc          : 'average'                                     # averageFunc
                    | 'min'                                         # minFunc
                    | 'max'                                         # maxFunc
                    | 'number of' CHAR                              # numberOfCharsFunc
                    | 'number of' WORD                              # numberOfWordsFunc
                    | 'number of' ITEM                              # numberOfItemsFunc
                    | 'number of' LINE                              # numberOfLinesFunc
                    | 'random'                                      # randomFunc
                    | 'sqrt'                                        # sqrtFunc
                    | 'trunc'                                       # truncFunc
                    | 'sin'                                         # sinFunc
                    | 'cos'                                         # cosFunc
                    | 'tan'                                         # tanFunc
                    | 'atan'                                        # atanFunc
                    | 'exp'                                         # expFunc
                    | 'exp1'                                        # exp1Func
                    | 'exp2'                                        # exp2Func
                    | 'ln'                                          # lnFunc
                    | 'ln1'                                         # ln1Func
                    | 'log2'                                        # log2Func
                    | 'abs'                                         # absFunc
                    | 'charToNum'                                   # charToNumFunc
                    | 'numToChar'                                   # numToCharFunc
                    ;

noArgFunc           : 'mouse'                                               # mouseFunc
                    | 'mouseloc'                                            # mouseLocFunc
                    | 'result'                                              # resultFunc
                    | ('message' | 'message' 'box' | 'message' 'window')    # messageFunc
                    | 'ticks'                                               # ticksFunc
                    | 'seconds'                                             # secondsFunc
                    | 'long date'                                           # longDateFormatFunc
                    | 'short date'                                          # shortDateFormatFunc
                    | ('abbrev date' | 'abbreviated date')                  # abbrevDateFormatFunc
                    | 'long time'                                           # longTimeFormatFunc
                    | 'short time'                                          # shortTimeFormatFunc
                    | ('abbrev time' | 'abbreviated time')                  # abbrevTimeFormatFunc
                    ;

literal				: STRING_LITERAL                                # stringLiteral
                    | INTEGER_LITERAL                               # numberLiteral
                    | '-' INTEGER_LITERAL                           # negNumberLiteral
                    | '.' INTEGER_LITERAL                           # dotNumberLiteral
                    | '-' '.' INTEGER_LITERAL                       # negDotNumberLiteral
                    | INTEGER_LITERAL '.'                           # numberDotLiteral
                    | '-' INTEGER_LITERAL '.'                       # negNumberDotLiteral
                    | INTEGER_LITERAL '.' INTEGER_LITERAL           # numberDotNumberLiteral
                    | '-' INTEGER_LITERAL '.' INTEGER_LITERAL       # negNumberDotNumberLiteral
                    ;

THEN				: NEWLINE 'then' | 'then';

FIELD               : 'field'  | 'card field';
BUTTON              : 'button' | 'card button';
CHAR                : 'char'   | 'character' | 'chars' | 'characters' ;
WORD                : 'word'   | 'words';
ITEM                : 'item'   | 'items';
LINE                : 'line'   | 'lines';

ID                  : POINT | RECT | (ALPHA (ALPHA | DIGIT)*) ;

STRING_LITERAL      : '"' ~('"' | '\r' | '\n' )* '"' ;
INTEGER_LITERAL     : DIGIT+ ;

ALPHA               : ('a' .. 'z' | 'A' .. 'Z')+ ;
DIGIT               : ('0' .. '9')+ ;
POINT               : (DIGIT ',' DIGIT);
RECT                : (DIGIT ',' DIGIT ',' DIGIT ',' DIGIT);

COMMENT             : '--' ~('\r' | '\n')* -> skip;
NEWLINE             : ('\n' | '\r')+;
WHITESPACE          : (' ' | '\t')+ -> skip;