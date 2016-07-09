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
                    | 'add' factor 'to' destination                 # addCmdStmnt
                    | 'subtract' factor 'from' destination          # subtractCmdStmnt
                    | 'multiply' destination 'by' factor            # multiplyCmdStmnt
                    | 'divide' destination 'by' factor              # divideCmdStmnt
                    ;

answerCmd			: 'answer' factor 'with' factor 'or' factor 'or' factor     # answerThreeButtonCmd
                    | 'answer' factor 'with' factor 'or' factor                 # answerTwoButtonCmd
                    | 'answer' factor 'with' factor                             # answerOneButtonCmd
                    | 'answer' factor                                           # answerDefaultCmd
                    ;

askCmd				: 'ask' expression 'with' factor                # askExpWithCmd
                    | 'ask' expression                              # askExpCmd
                    ;

putCmd				: 'put' expression destination                  # putIntoCmd
                    | 'put' expression preposition destination      # putPrepositionCmd
                    ;

getCmd				: 'get' expression
                    ;

setCmd				: 'set the' ID 'of' part 'to' expression
                    ;

sendCmd				: 'send' expression 'to' part
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

destination			: ID                                            # variableDest
                    | MESSAGE                                       # messageDest
                    | chunk MESSAGE                                 # chunkMessageDest
                    | chunk ID                                      # chunkVariableDest
                    | part                                          # partDest
                    | chunk part                                    # chunkPartDest
                    | chunk                                         # chunkDest
                    |                                               # defaultDest
                    ;

part                : 'field' factor                                # fieldPart
                    | 'field id' factor                             # fieldIdPart
                    | 'button' factor                               # buttonPart
                    | 'button id' factor                            # buttonIdPart
                    | 'me'                                          # mePart
                    ;

chunk               : ordinal CHAR 'of'                             # ordinalCharChunk
                    | CHAR expression 'to' expression 'of'          # rangeCharChunk
                    | CHAR expression 'of'                          # charCharChunk
                    | ordinal 'word of'                             # ordinalWordChunk
                    | 'word' expression 'to' expression 'of'        # rangeWordChunk
                    | 'word' expression 'of'                        # wordWordChunk
                    | ordinal 'item of'                             # ordinalItemChunk
                    | 'item' expression 'to' expression 'of'        # rangeItemChunk
                    | 'item' expression 'of'                        # itemItemChunk
                    | ordinal 'line of'                             # ordinalLineChunk
                    | 'line' expression 'to' expression 'of'        # rangeLineChunk
                    | 'line' expression 'of'                        # lineLineChunk
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
                    | chunk expression                              # chunkExp
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
                    | 'the' builtin                                 # builtinExp
                    | factor                                        # factorExp
                    | ID '(' argumentList ')'                       # functionExp
                    ;

builtin				: 'mouse'                                       # mouseFunc
                    | 'mouseloc'                                    # mouseLocFunc
                    | 'average of' factor                           # averageFunc
                    | 'result'                                      # resultFunc
                    | MESSAGE                                       # messageFunc
                    | 'number of' countable 'in' factor             # numberFunc
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
                    | 'the' ID 'of' part                            # idOfPartFactor
                    ;

literal				: STRING_LITERAL                                # stringLiteral
                    | INTEGER_LITERAL                               # numberLiteral
                    | '.' INTEGER_LITERAL                           # dotNumberLiteral
                    | INTEGER_LITERAL '.'                           # numberDotLiteral
                    | INTEGER_LITERAL '.' INTEGER_LITERAL           # numberDotNumberLiteral
                    ;

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

CHAR                : 'char' | 'character' ;

STRING_LITERAL      : '"' ~('"' | '\r' | '\n' )* '"' ;
INTEGER_LITERAL     : DIGIT+ ;

ALPHA               : ('a' .. 'z' | 'A' .. 'Z')+ ;
DIGIT               : ('0' .. '9')+ ;

COMMENT             : '--' ~('\r' | '\n')* -> skip;
NEWLINE             : ('\n' | '\r')+;
WHITESPACE          : (' ' | '\t')+ -> skip;