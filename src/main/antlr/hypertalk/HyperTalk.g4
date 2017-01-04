grammar HyperTalk;

script				: handler                                       # handlerScript
                    | script handler                                # scriptHandlerScript
                    | function                                      # functionScript
                    | script function                               # scriptFunctionScript
                    | nonEmptyStmnt                                 # statementScript
                    | COMMENT                                       # commentScript
                    | NEWLINE                                       # whitespaceScript
                    | <EOF>                                         # eofScript
                    | script NEWLINE                                # scriptNewlineScript
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

statementList		: statementList nonEmptyStmnt                   # multiStmntList
                    | nonEmptyStmnt                                 # singleStmntList
                    | statementList NEWLINE                         # stmntListNewlineStmntList
                    | NEWLINE                                       # newlineStmntList
                    ;

nonEmptyStmnt		: commandStmnt stmntTerminator                  # nonEmptyCommandStmnt
                    | globalStmnt stmntTerminator                   # nonEmptyGlobalStmnt
                    | ifStatement stmntTerminator                   # nonEmptyIfStmnt
                    | repeatStatement stmntTerminator               # nonEmptyRepeatStmnt
                    | doStmnt stmntTerminator                       # nonEmptyDoStmnt
                    | returnStmnt stmntTerminator                   # nonEmptyReturnStmnt
                    | expression stmntTerminator                    # nonEmptyExpStmnt
                    ;

stmntTerminator     : NEWLINE | <EOF>;

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

putCmd				: 'put' expression container                    # putIntoCmd
                    | 'put' expression preposition container        # putPrepositionCmd
                    ;

getCmd				: 'get' expression
                    ;

setCmd				: 'set' propertySpec 'to' expression
                    ;

sendCmd				: 'send' expression 'to' part
                    ;

waitCmd             : 'wait' factor timeUnit                        # waitCountCmd
                    | 'wait for' factor timeUnit                    # waitForCountCmd
                    | 'wait until' expression                       # waitUntilCmd
                    | 'wait while' expression                       # waitWhileCmd
                    ;

// Can't use a lexer rule for synonyms here because it creates ambiguity with ordinals and built-in function names. :(
timeUnit            : 'ticks'                                       # ticksTimeUnit
                    | 'tick'                                        # tickTimeUnit
                    | 'seconds'                                     # secondsTimeUnit
                    | 'sec'                                         # secTimeUnit
                    | 'second'                                      # secondTimeUnit
                    ;

position            : NEXT                                          # nextPosition
                    | PREV                                          # prevPosition
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
                    | nonEmptyStmnt NEWLINE                         # singleThenNoElse
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
                    | ordinal CHAR OF_IN                            # ordinalCharChunk
                    | CHAR expression 'to' expression OF_IN         # rangeCharChunk
                    | CHAR expression OF_IN                         # charCharChunk
                    | ordinal WORD OF_IN                            # ordinalWordChunk
                    | WORD expression 'to' expression OF_IN         # rangeWordChunk
                    | WORD expression OF_IN                         # wordWordChunk
                    | ordinal ITEM OF_IN                            # ordinalItemChunk
                    | ITEM expression 'to' expression OF_IN         # rangeItemChunk
                    | ITEM expression OF_IN                         # itemItemChunk
                    | ordinal LINE OF_IN                            # ordinalLineChunk
                    | LINE expression 'to' expression OF_IN         # rangeLineChunk
                    | LINE expression OF_IN                         # lineLineChunk
                    ;

container			: ID                                            # variableDest
                    | 'the'? MESSAGE                                # messageDest
                    | chunk 'the'? MESSAGE                          # chunkMessageDest
                    | chunk ID                                      # chunkVariableDest
                    | part                                          # partDest
                    | chunk part                                    # chunkPartDest
                    | chunk                                         # chunkDest
                    | propertySpec                                  # propertyDest
                    |                                               # defaultDest
                    ;

propertySpec        : 'the'? ID                                     # propertySpecGlobal
                    | 'the'? ID OF_IN part                          # propertySpecPart
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

ordinalValue        : FIRST                                         # firstOrd
                    | SECOND                                        # secondOrd
                    | THIRD                                         # thirdOrd
                    | FOURTH                                        # fourthOrd
                    | FIFTH                                         # fifthOrd
                    | SIXTH                                         # sixthOrd
                    | SEVENTH                                       # seventhOrd
                    | EIGTH                                         # eigthOrd
                    | NINTH                                         # ninthOrd
                    | TENTH                                         # tenthOrd
                    | MIDDLE                                        # midOrd
                    | LAST                                          # lastOrd
                    ;

expression          : 'empty'                                       # emptyExp
                    | builtinFunc                                   # builtinFuncExp
                    | ID '(' expressionList ')'                       # functionExp
                    | factor                                        # factorExp
                    | chunk expression                              # chunkExp
                    | 'not' expression                              # notExp
                    | '-' expression                                # negateExp
                    | expression '^' expression                     # caratExp
                    | expression 'mod' expression                   # modExp
                    | expression 'div' expression                   # divExp
                    | expression '/' expression                     # slashExp
                    | expression '*' expression                     # multiplyExp
                    | expression '-' expression                     # minusExp
                    | expression '+' expression                     # plusExp
                    | expression '&&' expression                    # ampAmpExp
                    | expression '&' expression                     # ampExp
                    | expression 'is not in' expression             # isNotInExp
                    | expression 'is in' expression                 # isInExp
                    | expression 'contains' expression              # containsExp
                    | expression '>=' expression                    # greaterThanEqualsExp
                    | expression '<=' expression                    # lessThanEqualsExp
                    | expression '<' expression                     # lessThanExp
                    | expression '>' expression                     # greaterThanExp
                    | expression '<>' expression                    # wackaWackaExp
                    | expression 'is' expression                    # isExp
                    | expression 'is not' expression                # isNotExp
                    | expression '=' expression                     # equalsExp
                    | expression 'and' expression                   # andExp
                    | expression 'or' expression                    # orExp
                    ;

factor				: literal                                       # literalFactor
                    | ID                                            # idFactor
                    | part                                          # partFactor
                    | '(' expression ')'                            # expressionFactor
                    | propertySpec                                  # idOfPartFactor
                    ;

builtinFunc         : 'the'? oneArgFunc OF_IN factor                # builtinFuncOneArgs
                    | 'the' noArgFunc                               # builtinFuncNoArg
                    | oneArgFunc '(' expressionList ')'               # builtinFuncArgList
                    ;

oneArgFunc          : 'average'                                     # averageFunc
                    | 'min'                                         # minFunc
                    | 'max'                                         # maxFunc
                    | 'number' OF_IN CHAR                           # numberOfCharsFunc
                    | 'number' OF_IN WORD                           # numberOfWordsFunc
                    | 'number' OF_IN ITEM                           # numberOfItemsFunc
                    | 'number' OF_IN LINE                           # numberOfLinesFunc
                    | 'random'                                      # randomFunc
                    ;

noArgFunc           : 'mouse'                                       # mouseFunc
                    | 'mouseloc'                                    # mouseLocFunc
                    | 'result'                                      # resultFunc
                    | MESSAGE                                       # messageFunc
                    | 'ticks'                                       # ticksFunc
                    | 'seconds'                                     # secondsFunc
                    | dateFormat 'date'                             # dateFormatFunc
                    | dateFormat 'time'                             # timeFormatFunc
                    ;

dateFormat          : 'long'                                        # longDateFormat
                    | 'short'                                       # shortDateFormat
                    | 'abbrev'                                      # abbrevDateFormat
                    | 'abbreviated'                                 # abbreviatedDateFormat
                    |                                               # defaultDateFormat
                    ;

literal				: STRING_LITERAL                                # stringLiteral
                    | INTEGER_LITERAL                               # numberLiteral
                    | '.' INTEGER_LITERAL                           # dotNumberLiteral
                    | INTEGER_LITERAL '.'                           # numberDotLiteral
                    | INTEGER_LITERAL '.' INTEGER_LITERAL           # numberDotNumberLiteral
                    ;

THEN				: NEWLINE 'then' | 'then';
OF_IN               : 'of' | 'in';

FIELD               : 'field'  | 'card field';
BUTTON              : 'button' | 'card button';
CHAR                : 'char'   | 'character' | 'chars' | 'characters' ;
WORD                : 'word'   | 'words';
ITEM                : 'item'   | 'items';
LINE                : 'line'   | 'lines';

PREV                : 'prev' | 'previous' | 'the prev' | 'the previous';
NEXT                : 'next' | 'the next';

MESSAGE				: 'message' | 'message box' | 'message window';

FIRST               : 'first';
SECOND              : 'second';
THIRD               : 'third';
FOURTH              : 'fourth';
FIFTH               : 'fifth';
SIXTH               : 'sixth';
SEVENTH             : 'seventh';
EIGTH               : 'eigth';
NINTH               : 'ninth';
TENTH               : 'tenth';
MIDDLE              : 'mid' | 'middle';
LAST                : 'last';

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