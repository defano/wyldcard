grammar Hypertalk;

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

argumentList		: expression                                    # singleExpArgList
                    | argumentList ',' expression                   # multiExpArgList
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

nonEmptyStmnt		: commandStmnt NEWLINE                          # nonEmptyCommandStmnt
                    | globalStmnt NEWLINE                           # nonEmptyGlobalStmnt
                    | ifStatement NEWLINE                           # nonEmptyIfStmnt
                    | repeatStatement NEWLINE                       # nonEmptyRepeatStmnt
                    | doStmnt NEWLINE                               # nonEmptyDoStmnt
                    | returnStmnt NEWLINE                           # nonEmptyReturnStmnt
                    | expression NEWLINE                            # nonEmptyExpStmnt
                    ;

returnStmnt			: 'return'                                      # voidReturnStmnt
                    | 'return' expression                           # eprReturnStmnt
                    ;

doStmnt				: 'do' expression
                    ;

commandStmnt		: answerCmd                                     # answerCmdStmnt
                    | askCmd                                        # askCmdStmnt
                    | putCmd                                        # putCmdStmnt
                    | getCmd                                        # getCmdStmnt
                    | setCmd                                        # setCmdStmnt
                    | sendCmd                                       # sendCmdStmnt
                    | GO destination                                # goCmdStmnt
                    | 'add' expression 'to' container               # addCmdStmnt
                    | 'subtract' expression 'from' container        # subtractCmdStmnt
                    | 'multiply' container 'by' expression          # multiplyCmdStmnt
                    | 'divide' container 'by' expression            # divideCmdStmnt
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

THEN				: NEWLINE 'then'
                    | 'then'
                    ;

singleThen			: nonEmptyStmnt NEWLINE elseBlock
                    | nonEmptyStmnt elseBlock
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

chunk               : ordinal CHAR 'of'                             # ordinalCharChunk
                    | CHAR expression 'to' expression 'of'          # rangeCharChunk
                    | CHAR expression 'of'                          # charCharChunk
                    | ordinal WORD 'of'                             # ordinalWordChunk
                    | WORD expression 'to' expression 'of'          # rangeWordChunk
                    | WORD expression 'of'                          # wordWordChunk
                    | ordinal ITEM 'of'                             # ordinalItemChunk
                    | ITEM expression 'to' expression 'of'          # rangeItemChunk
                    | ITEM expression 'of'                          # itemItemChunk
                    | ordinal 'line of'                             # ordinalLineChunk
                    | LINE expression 'to' expression 'of'          # rangeLineChunk
                    | LINE expression 'of'                          # lineLineChunk
                    ;

container			: ID                                            # variableDest
                    | MESSAGE                                       # messageDest
                    | chunk MESSAGE                                 # chunkMessageDest
                    | chunk ID                                      # chunkVariableDest
                    | part                                          # partDest
                    | chunk part                                    # chunkPartDest
                    | chunk                                         # chunkDest
                    | propertySpec                                  # propertyDest
                    |                                               # defaultDest
                    ;

propertySpec        : 'the' ID 'of' part
                    ;

part                : FIELD factor                                  # fieldPart
                    | FIELD 'id' factor                             # fieldIdPart
                    | BUTTON factor                                 # buttonPart
                    | BUTTON 'id' factor                            # buttonIdPart
                    | 'me'                                          # mePart
                    ;

ordinal             : FIRST                                         # firstOrd
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

expression			: opLevel10Exp                                  # exp
                    ;

opLevel10Exp		: opLevel9Exp                                   # level10Exp
                    | opLevel10Exp 'or' opLevel9Exp                 # orExp
                    ;

opLevel9Exp			: opLevel8Exp                                   # level9Exp
                    | opLevel9Exp 'and' opLevel8Exp                 # andExp
                    ;

opLevel8Exp			: opLevel7Exp                                   # level8Exp
                    | opLevel8Exp '=' opLevel7Exp                   # equalsExp
                    | opLevel8Exp 'is not' opLevel7Exp              # isNotExp
                    | opLevel8Exp 'is' opLevel7Exp                  # isExp
                    | opLevel8Exp '<>' opLevel7Exp                  # wackaWackaExp
                    ;

opLevel7Exp			: opLevel6Exp                                   # level7Exp
                    | opLevel7Exp '>' opLevel6Exp                   # greaterThanExp
                    | opLevel7Exp '<' opLevel6Exp                   # lessThanExp
                    | opLevel7Exp '<=' opLevel6Exp                  # lessThanEqualsExp
                    | opLevel7Exp '>=' opLevel6Exp                  # greaterThanEqualsExp
                    | opLevel7Exp 'contains' opLevel6Exp            # containsExp
                    | opLevel7Exp 'is in' opLevel6Exp               # isInExp
                    | opLevel7Exp 'is not in' opLevel6Exp           # isNotInExp
                    ;

opLevel6Exp			: opLevel5Exp                                   # level6Exp
                    | opLevel6Exp '&' opLevel5Exp                   # ampExp
                    | opLevel6Exp '&&' opLevel5Exp                  # ampAmpExp
                    ;

opLevel5Exp			: opLevel4Exp                                   # level5Exp
                    | opLevel5Exp '+' opLevel4Exp                   # plusExp
                    | opLevel5Exp '-' opLevel4Exp                   # minusExp
                    ;

opLevel4Exp			: opLevel3Exp                                   # level4Exp
                    | opLevel4Exp '*' opLevel3Exp                   # multiplyExp
                    | opLevel4Exp '/' opLevel3Exp                   # slashExp
                    | opLevel4Exp 'div' opLevel3Exp                 # divExp
                    | opLevel4Exp 'mod' opLevel3Exp                 # modExp
                    ;

opLevel3Exp			: opLevel2Exp                                   # level3Exp
                    | opLevel3Exp '^' opLevel2Exp                   # caratExp
                    ;

opLevel2Exp			: opLevel1Exp                                   # level2Exp
                    | '-' opLevel2Exp                               # negateExp
                    | 'not' opLevel2Exp                             # notExp
                    ;

opLevel1Exp         : builtin                                       # level1Exp
                    | chunk expression                              # chunkExp
                    | 'the' builtin                                 # builtinExp
                    | factor                                        # factorExp
                    | ID '(' argumentList ')'                       # functionExp
                    | 'empty'                                       # emptyExp
                    ;

builtin				: 'mouse'                                       # mouseFunc
                    | 'mouseloc'                                    # mouseLocFunc
                    | 'average of' expression                       # averageFunc
                    | 'result'                                      # resultFunc
                    | MESSAGE                                       # messageFunc
                    | 'number of' countable 'in' expression         # numberFunc
                    | 'min' '(' argumentList ')'                    # minFunc
                    | 'max' '(' argumentList ')'                    # maxFunc
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

countable			: 'chars'                                       # charsCountable
                    | 'lines'                                       # linesCountable
                    | 'words'                                       # wordsCountable
                    | 'items'                                       # itemsCountable
                    ;

factor				: literal                                       # literalFactor
                    | ID                                            # idFactor
                    | part                                          # partFactor
                    | '(' expression ')'                            # expressionFactor
                    | propertySpec                                  # idOfPartFactor
                    ;

literal				: STRING_LITERAL                                # stringLiteral
                    | INTEGER_LITERAL                               # numberLiteral
                    | '.' INTEGER_LITERAL                           # dotNumberLiteral
                    | INTEGER_LITERAL '.'                           # numberDotLiteral
                    | INTEGER_LITERAL '.' INTEGER_LITERAL           # numberDotNumberLiteral
                    ;

FIELD               : 'field' | 'card field';
BUTTON              : 'button' | 'card button';
CHAR                : 'char' | 'character' | 'chars' | 'characters' ;
WORD                : 'word' | 'words';
ITEM                : 'item' | 'items';
LINE                : 'line' | 'lines';

GO                  : 'go' | 'go to';
PREV                : 'prev' | 'previous' | 'the prev' | 'the previous';
NEXT                : 'next' | 'the next';

MESSAGE				: 'message' | 'message box' | 'message window'
                    | 'the message' | 'the message box' | 'the message window' ;

FIRST               : 'first'   | 'the first' ;
SECOND              : 'second'  | 'the second' ;
THIRD               : 'third'   | 'the third' ;
FOURTH              : 'fourth'  | 'the fourth' ;
FIFTH               : 'fifth'   | 'the fifth' ;
SIXTH               : 'sixth'   | 'the sixth' ;
SEVENTH             : 'seventh' | 'the seventh' ;
EIGTH               : 'eigth'   | 'the eigth' ;
NINTH               : 'ninth'   | 'the ninth' ;
TENTH               : 'tenth'   | 'the tenth' ;
MIDDLE              : 'mid' | 'the mid' | 'middle' | 'the middle' ;
LAST                : 'last' | 'the last' ;

ID                  : (ALPHA (ALPHA | DIGIT)*) ;

STRING_LITERAL      : '"' ~('"' | '\r' | '\n' )* '"' ;
INTEGER_LITERAL     : DIGIT+ ;

ALPHA               : ('a' .. 'z' | 'A' .. 'Z')+ ;
DIGIT               : ('0' .. '9')+ ;

COMMENT             : '--' ~('\r' | '\n')* -> skip;
NEWLINE             : ('\n' | '\r')+;
WHITESPACE          : (' ' | '\t')+ -> skip;