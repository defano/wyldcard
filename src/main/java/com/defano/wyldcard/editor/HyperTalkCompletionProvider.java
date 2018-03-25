package com.defano.wyldcard.editor;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;

import java.util.ArrayList;
import java.util.List;

public class HyperTalkCompletionProvider extends DefaultCompletionProvider {

    private final List<Completion> completionList = new ArrayList<>();

    public HyperTalkCompletionProvider() {

        buildCommandCompletions();
        buildConstructCompletions();

        addCompletions(completionList);
    }

    private void buildConstructCompletions() {

        CompletionBuilder.autoComplete("if", "If")
                .to("if ${expression} then ${true-statement}")
                .withSummary("Conditionally executes a single statement based on the logical evaluation of an expression.")
                .withParmeter("expression", "A logical expression (evaluates to `true` or `false`).")
                .withParmeter("true-statement", "A single statement that should execute when the expression is true.")
                .withExample("if x < y then answer \"X is less than Y!\"")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("if", "If-Then")
                .to("if ${expression} then\n\t${true-statement-list}\nend if")
                .withSummary("Conditionally executes a list of statements based on the logical evaluation of an expression.")
                .withParmeter("expression", "A logical expression (evaluates to `true` or `false`).")
                .withParmeter("true-statement-list", "Zero or more statements that should execute when the expression is true.")
                .withExample("if x < y then\n\tanswer \"X is less than Y!\"\nend if")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("if", "If-Then-Else")
                .to("if ${expression} then\n\t${true-statement-list}\nelse\n\t${false-statement-list}\nend if")
                .withSummary("Conditionally executes a list of statements based on the logical evaluation of an expression.")
                .withParmeter("expression", "A logical expression (evaluates to `true` or `false`).")
                .withParmeter("true-statement-list", "Zero or more statements that should execute when the expression is true.")
                .withParmeter("false-statement-list", "Zero or more statements that should execute when the expression is false.")
                .withExample("if x < y then\n\tanswer \"X is less than Y!\"\nelse\n\tanswer \"X is greater than or equal to Y!\"\nend if")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("repeat", "Repeat Forever")
                .to("repeat forever\n\t${statement-list}\nend repeat")
                .withSummary("Repeatedly executes the given list of statements forever (or until the script is terminated by pressing command-period).")
                .withParmeter("statement-list", "The list of statements to be infinitely repeated")
                .withExample("Count forever and ever and ever...",
                        "repeat forever\n" +
                                "\tadd 1 to the message box\n" +
                                "end repeat")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("repeat", "Repeat Until")
                .to("repeat until ${expression}\n\t${statement-list}\nend repeat")
                .withSummary("Repeats the given list of statements until a given expression evaluates to true.")
                .withParmeter("expression", "A logical expression determining if the list of statements should continue repeating.")
                .withParmeter("statement-list", "The list of statements to be repeated")
                .withExample("Drag a part around the card.",
                        "repeat until the mouse is up\n" +
                                "\tset the location of me to the mouseLoc\n" +
                                "end repeat")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("repeat", "Repeat While")
                .to("repeat while ${expression}\n\t${statement-list}\nend repeat")
                .withSummary("Repeats the given list of statements while the given expression evaluates to true.")
                .withParmeter("expression", "A logical expression determining if the list of statements should continue repeating.")
                .withParmeter("statement-list", "The list of statements to be repeated")
                .withExample("Drag a part around the card.",
                        "repeat while the mouse is down\n" +
                                "\tset the location of me to the mouseLoc\n" +
                                "end repeat")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("repeat", "Repeat For")
                .to("repeat for ${expression} times\n\t${statement-list}\nend repeat")
                .withSummary("Repeats the given list of statements for the specified number of times.")
                .withParmeter("expression", "A numerical expression determining the number of times to repeat the list of statements.")
                .withParmeter("statement-list", "The list of statements to be repeated")
                .withExample("Draw attention to this button by blinking it.",
                        "repeat for 4\n" +
                                "\tset the hilite of me to not the hilite of me\n" +
                                "\twait for 10 ticks\n" +
                                "end repeat")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("repeat", "Repeat With")
                .to("repeat with ${variable} = ${start-expression} to ${end-expression} \n\t${statement-list}\nend repeat")
                .withSummary("Repeats a list of statements while incrementing a value during each iteration.")
                .withDescription("When the repeat statement begins executing, the `{variable}` is initialized with the `{start-expression}`. Then, each time the loop executes, the `{variable}` is incremented by one and the loop continues to execute the list of statements as long as the `{variable}` remains less than `{end-expression}`.\n\nNote that `to` may be replaced with `down to` to decrement the index value during each iteration.")
                .withParmeter("variable", "An index variable that is incremented or decremented during each iteration.")
                .withParmeter("start-expression", "A numerical expression representing the beginning value of the loop.")
                .withParmeter("end-expression", "A numerical expression representing the ending value of the loop.")
                .withParmeter("statement-list", "The list of statements to be repeated")
                .withExample("Speak the name of each available speking voice.",
                        "repeat with v = 1 to the number of items in the voices\n" +
                                "\tput item v of the voices into theVoice\n" +
                                "\tspeak \"Hi, my name is \" & theVoice with voice theVoice\n" +
                                "end repeat\n")
                .buildInto(completionList, this);
    }

    private void buildCommandCompletions() {

        CompletionBuilder.autoComplete("add", "Add")
                .to("add ${expression} to ${container}")
                .withSummary("Adds a numerical value to a container.")
                .withParmeter("expression", "A numerical expression to be added to the container.")
                .withParmeter("container", "A container to which the value should be added.")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("ask", "Ask")
                .to("ask ${expression} with ${default-value}")
                .to("ask ${expression}")
                .withSummary("Displays a dialog box with an editable text field containing the provided value.")
                .withParmeter("expression", "A prompt string to display in the dialog")
                .withParmeter("default-value", "The default text to be displayed in the edit field.")
                .withDescription("When no `default-value` is provided, the `empty` string is assumed. The HyperTalk script stops executing until a user has made a choice. The text entered by the user will be put into the the `it` variable.")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("answer", "Answer")
                .to("answer ${expression} with ${button-1} or ${button-2} or ${button-3}")
                .to("answer ${expression} with ${button-1} or ${button-2}")
                .to("answer ${expression} with ${button-1}")
                .to("answer ${expression}")
                .withSummary("Displays a dialog box with up to three user-selectable buttons.")
                .withParmeter("expression", "The prompt string to display in the dialog.")
                .withParmeter("button-1", "The name of the first (default) button.")
                .withParmeter("button-2", "The name of the second button.")
                .withParmeter("button-3", "The name of the third button.")
                .withDescription("When no button names are provided, `OK` and `Cancel` will be assumed by default.\n\nThe HyperTalk script stops executing until a user has made a choice. The name of the button chosen by the user will be put into the the `it` variable.")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("beep", "Beep")
                .to("beep ${expression}")
                .to("beep")
                .withSummary("Emits the system alert sound.")
                .withParmeter("expression", "The number of times to beep.")
                .withExample("Beep once.", "beep")
                .withExample("Beep twice.", "beep 2")
                .withExample("Beep once for every button on the card layer.", "beep the number of card buttons")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("choose", "Choose Tool")
                .to("choose tool ${tool-expression}")
                .to("choose ${tool-expression} tool")
                .withSummary("Chooses the specified tool from the tool palette.")
                .withParmeter("tool-expression", "An expression referring to a tool either by name or number")
                .withDescription("Acceptable tool names and their corresponding numbers are as follows: `browse` (1), `oval` (14), `brush` (7), `pencil` (6), `bucket` (13), `poly[gon]` (18), `button` (2), `rect[angle]` (11), `curve` (15), `reg[ular] poly[gon]` (17), `eraser` (8), `round rect[angle]` (12), `field` (3), `select` (4), `lasso` (5), `spray [can]` (10), `line` (9), or `text` (16).")
                .withExample("Choose the paintbrush tool.", "choose brush tool")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("click", "Click")
                .to("click at ${location}")
                .to("click at ${location} with ${key-expression}")
                .withSummary("Clicks the mouse at the specified location using one or more optional modifier keys.")
                .withParmeter("location", "A point expression (like `10,20`) where the mouse should be clicked.")
                .withParmeter("key-expression", "A list expression containing one or more modifier keys (`shiftKey`, `commandKey`, or `optionKey`)")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("close", "Close")
                .to("close file ${expression}")
                .withSummary("Closes a previously opened file.")
                .withParmeter("expression", "The name of the file to be closed.")
                .buildInto(completionList, this);

        CompletionBuilder.autoComplete("convert", "Convert")
                .to("convert ${expression} from ${format} to ${format}")
                .to("convert ${expression} to ${format}")
                .withSummary("Converts a date and/or time from one format to another.")
                .withParmeter("expression", "A time or date expression to be converted.")
                .withParmeter("format", "One of `seconds` (an integer value equal to the number of seconds since the epoch, Jan. 1, 1970), `dateItems` (a comma-separated list of integers in the form `year, month, day, hour, minute, second, dayNumber`), `[ adjective ] date`, or `[ adjective ] time` where `adjective` is one of `long`, `short`, `abbreviated`, `abbrev` or `english`.")
                .withDescription("When a value is specified (rather than a container), the conversion result is placed into it.")
                .buildInto(completionList, this);
    }

    private void addTemplate(String input, String name, String template, String description) {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(description);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        completionList.add(new TemplateCompletion(this, input, name, template, null, renderer.render(document)));
    }

}

//            | 'convert' container 'to' convertible                                                                              # convertContainerToCmd
//            | 'convert' container 'from' convertible 'to' convertible                                                           # convertContainerFromToCmd
//            | 'convert' expression 'to' convertible                                                                             # convertToCmd
//            | 'convert' expression 'from' convertible 'to' convertible                                                          # convertFromToCmd
//            | 'create' 'menu' expression                                                                                        # createMenuCmdStmt
//            | 'delete' expression                                                                                               # deleteCmdStmt
//            | 'dial' expression                                                                                                 # dialCmdStmt
//            | 'disable' expression                                                                                              # disableExprStmt
//            | 'divide' expression 'by' expression                                                                               # divideCmdStmnt
//            | 'do' expression                                                                                                   # doCmdStmt
//            | 'domenu' expression                                                                                               # doMenuCmdStmt
//            | 'drag' 'from' listExpression 'to' listExpression                                                                  # dragCmdStmt
//            | 'drag' 'from' listExpression 'to' listExpression 'with' listExpression                                            # dragWithKeyCmdStmt
//            | 'edit' 'the'? 'script' of expression                                                                              # editScriptCmdStmt
//            | 'enable' expression                                                                                               # enableExpStmnt
//            | 'exit' handlerName                                                                                                # exitCmdStmt
//            | 'exit' 'repeat'                                                                                                   # exitRepeatCmdStmt
//            | 'exit' 'to' 'hypercard'                                                                                           # exitToHyperCardCmdStmt
//            | 'export' 'paint' 'to' 'file' expression                                                                           # exportPaintCmdStmt
//            | 'find' expression? 'international'? expression of expression of 'marked' cards                                    # findFieldMarkedCards
//            | 'find' expression? 'international'? expression of expression                                                      # findField
//            | 'find' expression? 'international'? expression of 'marked' cards                                                  # findMarkedCards
//            | 'find' expression? 'international'? expression                                                                    # findAnywhere
//            | 'get' expression                                                                                                  # getCmdStmnt
//            | 'go' 'to'? expression 'with' 'visual' expression                                                                  # goVisualEffectCmdStmnd
//            | 'go' 'to'? expression                                                                                             # goCmdStmnt
//            | 'go' 'back'                                                                                                       # goBackCmdStmt
//            | 'go' 'back' 'with' 'visual' expression                                                                            # goBackVisualEffectCmdStmt
//            | 'hide' expression                                                                                                 # hideCmdStmnt
//            | 'hide' card picture                                                                                               # hideThisCardPictCmd
//            | 'hide' background picture                                                                                         # hideThisBkgndPictCmd
//            | 'hide' picture of expression                                                                                      # hidePictCmd
//            | 'hide' 'titlebar'                                                                                                 # hideTitleBar
//            | 'import' 'paint' 'from' 'file' expression                                                                         # importPaintCmdStmt
//            | 'lock' 'screen'                                                                                                   # lockScreenCmdStmt
//            | 'multiply' expression 'by' expression                                                                             # multiplyCmdStmnt
//            | 'next' 'repeat'                                                                                                   # nextRepeatCmdStmt
//            | 'open' 'file' expression                                                                                          # openFileCmdStmt
//            | 'pass' handlerName                                                                                                # passCmdStmt
//            | 'play' musicExpression                                                                                            # playCmdStmt
//            | 'pop' card                                                                                                        # popCardCmdStmt
//            | 'push' card                                                                                                       # pushCardCmdStmt
//            | 'push' expression                                                                                                 # pushDestCmdStmt
//            | 'put' listExpression                                                                                              # putIntoCmd
//            | 'put' listExpression preposition expression                                                                       # putPrepositionCmd
//            | 'read' 'from' 'file' expression                                                                                   # readFileCmd
//            | 'read' 'from' 'file' expression 'for' expression                                                                  # readFileForCmd
//            | 'read' 'from' 'file' expression 'at' expression 'for' expression                                                  # readFileAtCmd
//            | 'read' 'from' 'file' expression 'until' expression                                                                # readFileUntil
//            | 'reset' 'the'? 'menubar'                                                                                          # resetMenuCmdStmt
//            | 'reset' 'paint'                                                                                                   # resetPaintCmdStmt
//            | 'select' 'empty'                                                                                                  # selectEmptyCmd
//            | 'select' 'text' of expression                                                                                     # selectTextCmd
//            | 'select' 'before' 'text' of expression                                                                            # selectBeforeCmd
//            | 'select' 'after' 'text' of expression                                                                             # selectAfterCmd
//            | 'select' expression                                                                                               # selectChunkCmd
//            | 'select' 'before' expression                                                                                      # selectBeforeChunkCmd
//            | 'select' 'after' expression                                                                                       # selectAfterChunkCmd
//            | 'set' property 'to' propertyValue                                                                                 # setCmdStmnt
//            | 'send' listExpression 'to' expression                                                                             # sendCmdStmnt
//            | 'show' expression                                                                                                 # showCmdStmnt
//            | 'show' card picture                                                                                               # showThisCardPictCmd
//            | 'show' background picture                                                                                         # showThisBkgndPictCmd
//            | 'show' picture of expression                                                                                      # showPictCmd
//            | 'show' 'titlebar'                                                                                                 # showTitleBarCmd
//            | 'sort' sortChunkType expression sortDirection sortStyle                                                           # sortDirectionCmd
//            | 'sort' sortChunkType expression sortDirection sortStyle 'by' expression                                           # sortExpressionCmd
//            | 'sort' sortDirection sortStyle 'by' expression                                                                    # sortStackCmd
//            | 'sort' 'this'? 'stack' sortDirection sortStyle 'by' expression                                                    # sortStackCmd
//            | 'sort' 'the'? cards (of 'this' 'stack')? sortDirection sortStyle 'by' expression                                  # sortStackCmd
//            | 'sort' 'the'? 'marked' cards (of 'this' 'stack')? sortDirection sortStyle 'by' expression                         # sortMarkedCardsCmd
//            | 'sort' expression sortDirection sortStyle 'by' expression                                                         # sortBkgndCardsCmd
//            | 'sort' 'the'? cards of expression sortDirection sortStyle 'by' expression                                         # sortBkgndCardsCmd
//            | 'sort' 'the'? 'marked' cards of expression sortDirection sortStyle 'by' expression                                # sortMarkedBkgndCardsCmd
//            | 'speak' expression                                                                                                # speakCmd
//            | 'speak' expression 'with' gender=('male'|'female'|'neuter'|'robotic') 'voice'                                     # speakGenderCmd
//            | 'speak' expression 'with' 'voice' expression                                                                      # speakVoiceCmd
//            | 'subtract' expression 'from' expression                                                                           # subtractCmdStmnt
//            | 'type' expression                                                                                                 # typeCmdStmt
//            | 'type' expression 'with' ('commandkey' | 'cmdkey')                                                                # typeWithCmdKeyCmdStmt
//            | 'unlock' 'screen'                                                                                                 # unlockScreenCmdStmt
//            | 'unlock' 'screen' 'with' 'visual' expression                                                                      # unlockScreenVisualCmdStmt
//            | 'visual' expression                                                                                               # visualEffectCmdStmt
//            | 'wait' expression timeUnit                                                                                        # waitCountCmd
//            | 'wait' 'for' expression timeUnit                                                                                  # waitForCountCmd
//            | 'wait' 'until' expression                                                                                         # waitUntilCmd
//            | 'wait' 'while' expression                                                                                         # waitWhileCmd
//            | 'write' expression 'to' 'file' expression                                                                         # writeFileCmd
//            | 'write' expression 'to' 'file' expression 'at' ('eof' | 'end')                                                    # writeEndFileCmd
//            | 'write' expression 'to' 'file' expression 'at' expression