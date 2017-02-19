# HyperTalk Java

A toy implementation of Apple's HyperCard written in Java. Originally created as a class project for a graduate-level compiler design course at DePaul University, Chicago.

## What's HyperCard?

HyperCard was born in 1987 as part of Apple's System Software 6. It was an application that largely defied classification: part database, part programming language, part "paint" program. HyperCard was something entirely new. Other "teaching" languages of the time made it possible to write dull, console-mode programs, but with HyperCard, a novice could draw a graphical user interface with [MacPaint](https://en.wikipedia.org/wiki/MacPaint)-like tools, then apply scripts using an expressive syntax that mimicked natural language. Apple called it "programming for the rest of us."

[Watch an interview of HyperCard's creators](https://www.youtube.com/watch?v=BeMRoYDc2z8) Bill Atkinson and Dan Winkler on The Computer Chronicles, circa 1987.

### I want to run the _real_ HyperCard

Use the SheepShaver emulator to run Macintosh System Software on newer Macs or Windows machines. See [this tutorial](https://jamesfriend.com.au/running-hypercard-stack-2014) for details.

### I'd like to sue

This project represents a homework assignment and is in no way associated with Apple's long-obsolete, HyperCard application program. HyperCard&trade;, HyperTalk&trade; and any other trademarks used within are the property of Apple, Inc. and/or their rightful owner(s).

## What can I do with this?

You can create stacks of cards containing graphics, buttons and fields. Buttons and fields are scriptable, and the full suite of HyperCard paint tools and patterns (with color!) are available.

Much of the HyperTalk language has been implemented including parts, attributes, event messaging, local and global variables, built-in and user-defined functions, and complex prepositional chunk expressions.

## What can't I do?

This is not a HyperCard replacement nor is it an open-sourced version of Apple's software.

It won't import your old stacks and it's missing many foundational aspects of the real software. Among its limitations, this implementation lacks support for multiple backgrounds; user levels; card, background or stack-level scripts; externals (XCMDs/XFCNs) and home stack script inheritance.

# Building the application

The project is built with Gradle (and should import easily into any IDE with Gradle integration like Eclipse or IntelliJ). The following Gradle tasks are defined by the project's build file:

Task | Description
--------|----------------------------
`run`   | Build, test and run the application
`generateGrammarSource`  | Re-generate the HyperTalk parser using Antlr4 (executes automatically as part of the `gradle build` task)

The project uses Antlr4 as the parser generator and the IntelliJ GUI Designer for much of the Swing UI development (see the section below for information about modifying UI components). With this implementation you can create buttons and fields in the UI and attach scripts to them for controlling their presentation and behavior. About 95% of the HyperTalk's expression language is implemented, as is the ability for one object to send messages to another, or to dynamically execute or evaluate code (i.e., any string of text can be executed as a script using the `do` command).

Note that this project was originally implemented with the JCup/JFlex LALR parser generator tools and was converted to Antlr in July, 2016. The JCup implementation can be found in the (abandoned) `jcup` branch.

### Running the program

Execute the `gradle run` task to build and start the program, or simply execute the `HyperCard` class. Once the program is running, you'll be presented with the application's main window. From here you may:

*	Begin adding your own user interface elements by choosing "New Button" or "New Field" from the "Objects" menu.
* Use the paint tools (choose "Tools Palette" from the "Tools" menu) to draw on the card.
*	Open a previously saved stack document ("File" -> "Open Stack...").
*	Enter a statement or expression in the message box. Press enter to execute or evaluate your message.

To start scripting:

1.	Create a new button or field, or, choose the button tool or field tool from the tools palette, then select an existing one.
2.	Double-click on the selected part to show the part's property editor, then click the "Script..." button.

### Modifying the UI components

The UI forms were generated using the GUI Designer built into IntelliJ's IDEA (Community Edition). Do not modify the generated source code by hand, as doing so will render those files incompatible with the GUI Designer tool.

Be aware that by default IntelliJ "hides" the generated code it creates inside of the `.class` files that it compiles. While this technique is quite elegant, it produces source code that is incomplete and which cannot be imported and built with other tools.

To correct this, you need to configure IntelliJ to generate its GUI boilerplate code in Java source files, not in the `.class` files:

1. From IntelliJ IDEA menu, choose "Preferences..."
2. Navigate to "Editor" -> GUI Designer
3. Select the "Java source code" option for GUI generation.
4. Apply the changes and "Rebuild project" from the "Build" menu.

# The HyperTalk Language

HyperCard's native language, called HyperTalk, is an event-driven scripting language. Scripts are associated with user interface elements called `parts` and are triggered by user actions called `events`. There is no singular "main" script that executes at runtime.

HyperTalk is a [duck-typed](https://en.wikipedia.org/wiki/Duck_typing) language. Internally, each value is stored as a string and converted to an integer, float, boolean, or list depending on the context of its use. Unlike Perl, however, HyperCard does not allow nonsensical conversions; adding 5 to "hello," for example, produces a syntax error.

A simple script to prompt the user to enter their name then greet them might look like:

```
-- This is my first script

on mouseUp
  ask "Tell me your name" with "I'd rather not"

  if it is "I'd rather not" or it is empty then
    answer "Then I can't very well say hello, can I?"
  else
    put "Hello " && it into greeting
    answer greeting with "Why, thanks!"
  end if

end mouseUp

```

Note that comments are preceded by `--` and, unlike C or Java, newlines have meaning in the syntax (although other whitespace does not). Newlines are somewhat analogous to semicolons in C-like languages; statements must be separated by a newline, and a single statement cannot break across multiple lines.

For example, this is legal:

```
if x < y then
  put x
else
  put y
end if
```

... but this is not:

```
if x < 10 then
  put x
else put y
end if
```

... nor is this:

```
answer "How are you today" with
  "Pretty good" or
  "Stinky!"
```

Apple's HyperCard supported a newline character (_logical negation_ symbol, [Unicode U+00AC](https://en.wikipedia.org/wiki/Latin-1_Supplement_(Unicode_block)) that could be used to break a long statement across multiple lines; this implementation does not.

As you enter script text into the script editor, this implementation will flag syntax errors as you type by underlining the offending text with a red squiggle. A (typically useless) error message from the Antlr parser will also appear at the bottom of the editor.

## Stacks of Cards

A HyperCard stack consists of one or more cards grouped together in an ordered list (analogous to a stack of index cards or a Rolodex). Only one card is ever visible to the user inside the stack window. When the current card changes as result of navigating away, the contents of the new card appear in the window in place of the old card. While cards can be "pushed" and "popped" from view, one should not confuse HyperCard's concept of a stack with the data structure known in Computer Science. (A student of Computer Science would model HyperCard's collection of cards as an ordered list; not a stack.)

The `go` command is used to navigate between cards:

```
go [to] <destination>
```

Where:

```
<destination>   :== { card | } <expression>
                | [the] <ordinal> { card | }
                | [the] <position> { card | }
<ordinal>       :== first | second | third | ... | tenth
<position>      :== first | last | next | prev | previous

```

For example:

```
go to myCard  -- myCard is a variable holding the number of a card
go to card 13 -- no effect if there are fewer than 13 cards
go 1          -- first card; cards are numbered from 1, not 0
go next
go to the third card

-- Lots of different ways to say the same thing:
go previous card
go to the previous card
go to the prev card
```

## Scripts and Handlers

A script consists of zero or more handlers and/or function definitions.

A _handler_ is a list of statements that execute when the handler's name is passed as a message to the part containing it (you'll note [Smalltalk](https://en.wikipedia.org/wiki/Smalltalk)'s influence, here). A _function definition_, like its counterpart in other imperative languages, accepts zero or more arguments, executes one or more statements and optionally returns a single value.

For example, a button might contain the script:

```
on mouseUp
  answer "Hello World" with "Why, thank you"
end mouseUp
```

In this example, when the user clicks the button containing this script, the action of the mouse button being released over the part causes the HyperCard runtime environment to send the message `mouseUp` to the affected button. Upon receipt of this message, the button executes its `mouseUp` handler. (In our example, this generates a "hello world" dialog box).

This HyperCard implementation automatically sends the following event messages:

 Event Message      | Description
--------------------|-----------------------------------------------------------------------------
 `mouseUp`          | Sent when the mouse is pressed and released over a part
 `mouseDown`        | Sent when the mouse is pressed over a part
 `mouseDoubleClick` | Sent when the mouse is double-clicked over a part
 `mouseEnter`       | Sent when the cursor enters the region of a part
 `mouseLeave`       | Sent when the cursor leaves the region of a part
 `keyDown`          | Sent only to in-focus fields when the user presses a key
 `keyUp`            | Sent only to in-focus fields when the user presses then releases a key

Not all messages need originate from HyperCard. A script may send a message to itself or to another part using the `send` command. Moreover, the message need not be a known HyperCard message (i.e., one listed in the table above); it's acceptable to send a message of the scripter's own creation.

For example:

```
send mouseExit to button id 0
send doSomethingCool to field "myField"
send keyDown to me
```

Parts do not need to implement a handler for every message they might receive. Messages for which no handler exists are simply ignored.

## Containers

A _container_ is any entity in HyperCard that can hold a value; all parts, variables, properties and the message box are containers.

Local variables in HyperTalk are lexically scoped and implicitly declared. That is, they retain their value only within the handler or function in which they're used. A variable may be made global by explicitly declaring it as such with the `global` keyword. Variables that are not declared as global are considered local, even when a global variable of the same name exists. All variables, global and local, are implicitly initialized with the empty string.

HyperTalk uses `--` to initiate a single-line comment (there are no multi-line comments). Comments can appear on their own line, or following a statement inline. It's also legal for comments to appear outside of function definitions and handlers.
For example:

```
--
-- Global variable example script
--

on mouseUp
	global aVar
	put 5 into aVar

	f() -- call function f with no arguments
	y() -- call function y with no arguments
end mouseUp

function f
	put aVar	-- puts the empty string ("") into the message box
end f

function y
	global aVar
	put aVar	-- puts "5" into the message box
end y
```

Like variables, parts, properties and the message box can also be used to store value. When placing a value into a field, the text contents of the field are changed. When placing a value into a button, the button's `contents` property is changed (the contents property does not affect the button's appearance in any way).

For example:

```
put 35 + 27 into field id 12          -- Changes the text of this field to "62"
put 35 + 26 into button "My Button"   -- Changes the contents of this button to "62"
put the name of button myButton into the message box
put "Button " && buttonTitle into the name of button id 1
```

Note that HyperTalk contains an implicit variable named `it`; most expressions and some commands mutate the value of this variable so that it always contains the most recently evaluated result. In this implementation, the value of `it` may also be retrieved using `the result` function (this is not true in Apple's HyperCard).

For example:

```
on mouseUp
	ask "How are you, fine sir?"
	put it into responseVar            -- ‘it' contains the user's input
	put the result into responseVar    -- same effect as previous line
end mouseUp
```

## Parts and Properties

A _part_ is a scriptable user interface element in HyperCard. Apple's implementation provided a wide range of parts and styles. This implementation provides a similar (but not identical) set of styled parts.

In Apple's HyperCard, cards contain two layers of user interface elements: a foreground and a background. Each card has an individual foreground, but the background could be shared between cards. Each of these elements--backgrounds, cards, stacks, etc--could contain their own scripts and act upon event messages from HyperCard.

For simplification, this implementation does not allow scripting of cards, stacks or backgrounds. Currently, this implementation supports a single background shared across all cards but only for the purpose of graphics (button or field parts cannot yet be added to the background--coming soon).

In addition to containing scripts, a part also maintains a set of _properties_. Properties describe various aspects of the part like its name, id, size and location on the card. A part can be programmatically modified by way of its properties. Different types of parts have different properties.

Parts may be addressed in HyperTalk by name or id, and a part can refer to itself as `me`. Properties are treated as "first class" containers in HyperTalk and may be read with the `get` command, or mutated using the `set` or `put` commands.

For example:

```
set the visible of me to true
set the left of button myButton to item 1 of the mouseLoc
get the name of button id 0      -- puts the name into the implicit variable, 'it'
set the rect of button "Hi" to "10,10,100,100"
```

Note that part names need not be unique and this fact may lead to ambiguity when addressing a part by name. If more than one part exists with the addressed name, one of the parts will be accessed (which one is indeterminate).

All parts have these properties:

Property      | Description
--------------|------------
`script`      | Retrieves or replaces the current script of the part
`id`          | Returns the part's id. Each part has a globally unique id that is assigned by HyperCard at creation and cannot be changed.
`name`        | Returns or sets the script-addressable name of the part (on buttons, this value determines the label or text that appears drawn on the button)
`location`    | Returns or sets the center point of the part. Also available as the `loc` property.
`left`        | Returns or sets the left-most border of the part's location, moving the part horizontally but not affecting its width.
`right`       | Returns or sets the right-most border of the part's location, moving the part horizontally but not affecting its width.
`top`	        | Returns or sets the top-most border of the part's location, moving the part vertically but not affecting its height.
`bottom`      | Returns or sets the bottom-most border of the part's location, moving the part vertically but not affecting its height.
`topLeft`     | Returns or sets the top-left coordinate of the part. When set, this property adjusts the part's position on the card but does not affect its `height` or `width`. This property only accepts a _point_ value consisting of a comma-separated _x_ and _y_ coordinate, for example, `"10, 100"`
`bottomRight` | Returns or sets the bottom-right coordinate of the part. When set, this property adjusts the part's position on the card but does not affect its `height` or `width`. This property only accepts a _point_ value consisting of a comma-separated _x_ and _y_ coordinate, for example, `"10, 100"`
`width`       | Returns or sets the width of the part (in pixels)
`height`      | Returns or sets the height of the part (in pixels)
`rectangle`   | Returns or sets the rectangle of the part, equivalent to getting or setting the `top`, `left`, `height` and `width` properties together. This property only accepts a _rectangle_ value, consisting of two, comma-separated point coordinates representing the top-left and bottom-right positions of the part, for example `"10, 10, 100, 100"`. This value is also accessible as `rect`.
`visible`     | Returns or sets the visibility of the button (a Boolean value). When invisible, the button is not drawn on the screen and receives no messages from the UI.
`enabled`     | Returns or sets whether the part is enabled (a Boolean value). When disabled, the part appears "grayed out", but note that it continues to receive HyperCard-generated messages (like `mouseEnter`).
`textSize`    | Returns or sets the size (in points) of the part's text.
`textFont`    | Returns or sets the font (family) of the part. Uses the system default font if the specified font family does not exist.
`textAlign`   | Returns or sets the text alignment of the part; one of `left`, `right` or `center`. Assumes `center` if any other value is provided.
`textStyle`   | Returns or sets the text style attributes of the part. Valid style attributes include `plain`, `bold`, `italic`. Provide a list to set multiple attributes together (i.e., `set the textStyle of me to "bold, italic"`)

### Buttons

Buttons come in a variety of _styles_ which affects their look-and-feel as well as their behavior. This implementation supports the following button styles:

Style                                    | Name          | Notes
-----------------------------------------|---------------|------------
![Default](doc/images/default.png)       | `default`     | A typical push button whose style matches that of the operating system.
![Default](doc/images/classic.png)       | `classic`     | A push button drawn in the style of Mac OS Classic.
![Default](doc/images/shadow.png)        | `shadow`      | A push button drawn with a drop-shadow decoration.
![Default](doc/images/oval.png) | `oval` | A push button drawn with an oval border.
![Default](doc/images/rectangular.png)   | `rectangular` | A push button drawn with a rectangular border.
![Default](doc/images/transparent.png)   | `transparent` | A push button drawn without any decoration or border; can be placed atop of graphics on the card to make any region of the card "clickable"
![Opaque](doc/images/opaque.png)         | `opaque`      | A rectangular push button drawn without a border.
![Default](doc/images/checkbox.png)      | `checkbox`    | A checkbox drawn in the style provided by the operating system. When `autohilite` is true and the `family` property is an integer value, then clicking this button will cause the `hilite` of all other buttons in the family to become `false` and the `hilite` of this button to become true.
![Default](doc/images/radio.png)         | `radio`       | A radio button drawn in the style provided by the operating system. When `autohilite` is true and the `family` property is an integer value, then clicking this button will cause the `hilite` of all other buttons in the family to become `false` and the `hilite` of this button to become true.
![Default](doc/images/menu.png)          | `menu`        | A drop-down (_combo box_) menu drawn in the style provided by the operating system. Each line of the button's contents are rendered as a selectable menu item.

In addition to the properties common to all parts, a button has these additional properties:

Property    | Description
------------|------------
`style`     | Sets or retrieves the button style. Button styles are described in the table above.
`contents`  | Returns or sets the value of this object, as set or retrieved via HyperTalk's `put` and `get` commands. For example, `put "hello" into button id 0` sets the contents of the button to "Hello". This value could be retrieved with `get the contents of button id 0`.
`showname`  | Returns or sets the visibility of the button's name (a Boolean value). When false, the button is drawn without a name.
`enabled`   | Returns or sets whether the button is enabled (a Boolean value). When disabled, the button appears "grayed out". Note that disabled buttons continue to receive user interface generated messages such as `mouseUp` or `mouseEnter`.
`hilite`    | (Sic) Returns or sets whether the button is drawn "highlighted"; for checkbox and radio styles, hilite describes whether the checkbox is checked or the radio button is selected; for other styles, `hilite` describes a "pressed" state--a highlight typically drawn while the user holds the mouse down over the part. This property has no effect on menu buttons.
`autohilite`| Returns or sets whether the button's `hilite` property is managed by HyperCard. When `autohilite` is `true`, checkbox and radio buttons automatically check/uncheck when clicked, and other styles of buttons highlight when the mouse is down within their bounds.

### Fields

Fields in this version come in two flavors, transparent and opaque. This implementation provides no ability to show lines or prevent text-wrapping.

Style                                        | Name          | Notes
---------------------------------------------|---------------|------------
![Opaque](doc/images/opaque_field.png)       | `opaque`      | An opaque, etched-border editable text field that can be filled with formatted text.
![Default](doc/images/transparent_field.png) | `transparent` | Functionally identical to an opaque field, but whose background and border is transparent.

A field has these properties:

Property   | Description
-----------|------------
`text`     | Returns or sets the text contained within this field
`visible`  | Returns or sets the visibility of the field (a Boolean value). When invisible, the field is not drawn on the screen and receives no messages from the UI.
`locktext` | Returns or sets whether the text contained by the field can be edited by the user.

### Global Properties

Some properties apply to HyperCard at large instead of an individual part. The syntax for setting or getting a global property is similar to part properties, sans the `of` clause. For example:

```
set the itemDelimiter to ","
get the itemDelimiter
```

This implementation supports only a single global property:

Global Property | Description
----------------|---------------
`itemDelimiter` | A character or string used to mark the separation between items in a list. HyperCard will use this value anywhere it needs to treat a value as a list. For example, `set the itemDelimiter to "***" \n get the second item of "item 1***item 2***item 3" -- yeilds 'item 2'`. Note that this value has no effect on _point_ or _rectangle_ list items (i.e., when getting or setting the `rect`, `topLeft` or `bottomRight` of a part, the coordinates will always be separated by a comma irrespective of the current `itemDelimiter`).

## Chunk Expressions

HyperCard contains a rich expression language that includes support for complex prepositional chunk operations. A script can access or mutate a range of words, characters, lines, or comma-delimited items in a value. Chunks may be specified numerically (`line 3 of`), by ordinal (`the third line of`), or relatively (`the last line of`; `the middle word of`).

Chunk expressions follow the form:

```
::= [the] { <ordinal> | <relation> } <chunk> of <value>
|   <chunk> <integer> of <value>
|   <chunk> <integer> to <integer> of <value>
```

Where:

```
<chunk>    ::= char | character | word | item | line
<relation> ::= middle | last
<ordinal>  ::= first | second | third | ... | tenth
```

Consider the following chunked expressions:

```
the first character of the second word of the last line of field id 24
character 19 to 27 of the message box
the second item of "Hello,Goodbye" -- yields "Goodbye"
the middle word of "one two three" -- yields "two"
```

When mutating a chunk of text within a container (using the `put` command), a preposition (`before`, `into`, or `after`) may be included in the expression. For example:

```
put word 2 of "Hello Goodbye" into the first word of field id 0
put "blah" after the third character of the middle item of myVar
put 29 before the message box
```

Want to impress your friends? HyperTalk lets you to modify a chunk-of-a-chunk of text within a container. For example:

```
put x into the second character of the third word of the fourth line of field id 1
put the first char of the second word of x into the middle item of the last line of y
```

## Arithmetic and Logical Expressions

In addition to chunk expressions, HyperTalk supports a standard suite of mathematical, text and logical operators, including the following (all operators are binary, excepted where otherwise noted):

Precedence  | Operator        | Description
------------| ----------------|-------------
1 (highest) | `( )`           | Grouping
2           | `-`             | Negation for numbers (unary)
            | `not`	          | Negation for boolean values (unary)
3           | `^`             | Exponentiation for numbers
4           | `*`             | Multiplication for numbers
            | `/`             | Division for numbers
            | `div`	          | Division for numbers
            | `mod`	          | Modulus division for numbers; returns the remainder
5           | `+`             | Addition for numbers
            | `-`             | Subtraction for numbers
6           | `&`, `&&`       | Text concatenation; `&&` adds a space between operands; `&` does not
7           | `>`             | Greater than comparison for numbers and text
            | `<`             | Less than comparison for numbers and text
            | `<=`            | Less than or equal to comparison for numbers and text
            | `>=`            | Greater than or equal to comparison for numbers and text
            | `contains`      | Substring comparison for text
            | `is a`, `is an` | Determines if the left-hand value is a `number`, `integer`, `date`, `point`, `rect` (or `rectangle`), `logical` (or `boolean`, `bool`). Returns an error if the right-hand value is not an expression yielding one of these types.
            | `is not a`, `is not an` | The logical inverse of `is a`, `is an`
8           | `=`             | Equality comparison for text
            | `is`            | Equality comparison for text
            | `is not`        | Negative equality comparison for text
            | `<>`	          | Synonym for is not
9           | `is within`     | Determines if the left-hand point value is contained within the right-hand rectangle value.
            | `is not within` | Determines if the left-hand point value is not contained within the right-hand rectangle value.
10          | `and`           | Logical AND for boolean values
11 (lowest) |  `or`           | Logical OR for boolean values

HyperCard uses the following order (top to bottom) to evaluate factors and terms in an expression:

Precedence  | Term                    | Description
------------|------------------------|------------
1 (highest) | `empty`                 | Keyword expression representing the empty string (e.g., `if x is empty`)
2           | _Built-in Function_     | Evaluation of a built-in function (e.g., `the mouse`)
3           | _User-defined Function_ | Evaluation of a user-defined function (e.g., `fact(10)`)
4           | _Literal_               | Evaluation of a literal value (e.g., `"Hello world!"`)
5           | _Variable Container_    | Evaluation of a variable container (e.g., `x` in `get x + 3`)
6           | _Part_                  | Evaluation of a part specifier (e.g., `card field id 0`)
7 (lowest)  | _Property_              | Evaluation of a property of a part (e.g., `the width of me`)

This implementation supports nearly the full expression language (all of the aforementioned operators), and follows the same order of precedence as real HyperTalk.  

Valid expressions include:

```
item 1 of the mouseloc < 100 -- true if the mouse is towards the left of the card
4 * (2 + 3) -- yields 20
"hello" contains "el" and "goodbye" contains "bye" -- true
3 * 5 is not 15 -- false
"Hello" && " World" -- produces "Hello World"
"Hyper" > "Card" -- true, "Hyper" is alphabetically after "Card"
not "nonsense" -- syntax error, "nonsense" is not a boolean
false is not "tr" && "ue" -- true, concatenating 'tr' with 'ue' produces a logical value
```

## Commands

This version of HyperCard implements the following set of commands:

Command	   | Description
-----------|------------
`put`      | Places a value into a container or into a chunk of a container; `put "hello" into the third item of mylist`. When no container is specified, the message box is implied as the default container. Note that HyperCard does not allow "putting" a value into a property, but this implementation does, for example: `put item 1 of the mouseloc into item 1 of the location of me`.
`get`	     | Get the value of a part's property and places it into the implicit variable it; `get the visible of button id 0`
`set`	     | Sets the property of a part to a value (`set the wraptext of field id 3 to (5 > 3)`) or sets a global HyperCard property (`set the itemDelim to "*"`). If no such property exists, the given expression is placed into a container (variable) of that name.
`go`       | Transitions to a new card; `go to card 1` or `go next` or `go to the last card`
`wait`     | Waits for the specified condition or for the given amount of time. Follows the syntax `wait { [for] <count> { ticks `&#124;` seconds } `&#124;` until <condition> `&#124;` while <condition> }`. Valid examples include: `wait for 3 seconds`, `wait until the mouse is down`, `wait while the message box contains "hello"`
`answer`   | Produces a dialog box with a message and up to three user-defined buttons. Follows the syntax `answer <message> [with <button1> [or <button2>] [or <button3>]]]`. Upon completion, it contains the text of the button selected by the user, or the empty string if answer is used without an optional button specifier.
`ask`	     | Similar to answer, but produces a dialog box with a message and a user-editable response string. Follows the syntax `ask <message> [with <answer>]`. Upon completion, it contains the value of the user-editable text field, or the empty string if the user cancelled the dialog.
`do`       | Executes a value as if it were a list of statements; `do "put 2+3 into the message window"` or `do the text of field myscript`
`send`     | Send a message to a part on the current card; `send "mouseUp" to field id 3`
`add`      | Adds a value to a container; for example `add 3 to x` or `add card field id 0 to card field id 1`
`subtract` | Subtracts a value from a container; `subtract (10 * 3) from item 2 of field "items"`
`multiply` | Multiplies a container by a value; `multiply x by 3`
`divide`   | Divides a container by a value; `divide x by it`
`choose`   | Selects a tool from the tool palette; `choose brush tool` or `choose tool 7`. Acceptable tool names and their corresponding numbers are as follows: `browse` (1), `oval` (14), `brush` (7), `pencil` (6), `bucket` (13), `poly[gon]` (18), `button` (2), `rect[angle]` (11), `curve` (15), `reg[ular] poly[gon]` (17), `eraser` (8), `round rect[angle]` (12), `field` (3), `select` (4), `lasso` (5), `spray [can]` (10), `line` (9), or `text` (16).
`click`    | Clicks the mouse at a provided location on the card, while optionally holding down one or more modifier keys; `click at "10, 10"` or `click at "130,220" with shiftKey, commandKey`. Valid modifier keys are `shiftKey`, `optionKey` and `commandKey`.
`drag`     | Drags the mouse from one point to another while optionally holding down one or more modifier keys; `drag from "35,70" to "200,180" with shiftKey`
`sort`     | Sorts the `lines` or `items` of a container based on value or expression using the syntax `sort [[the] {items | lines} of] <container> [{{ascending | descending} | by <expression>}]` For example, `sort field id 0` or `sort the items of myContainer descending` or `sort lines of myField by the third character of each`. In the last syntax form, a local variable called `each` is implicitly declared and contains the chunk (the line or item) that is being compared.

## Functions

HyperCard provides both a suite of built-in functions as well as the ability for a user to script new ones. Note that the calling syntax differs between built-in and user-defined functions.

### Built-in Functions

There are several equivalent syntax forms that can be used when invoking a built in function:

* For built-in functions that accept a single argument, use `[the] <function> { of | in } <argument>` or `<function> ( <argument> )`
* For built-in functions that don't accept any arguments, use `[the] <function>`. Note that you cannot invoke a no-argument built-in as `<function>()` as you might in C or Java.
* For built-in functions that accept multiple arguments, use `function(<arg1>, <arg2>, ...)`.

This implementation includes the following built-in functions:

Function | Description
---------|-------------
`average`	| Returns the statistical mean of a list of numerical items. Example: `the average of "1,2,3,4,5"` (returns 3) or `average (93, 26, 77)` returns 65.33.
`mouse` | Returns the current state of the left mouse button; either "up" or "down"
`mouseLoc` | Returns the current location of the cursor (in coordinates relative the top-left corner of the card panel), for example: `the mouseLoc` returns "123,55"
`tool` | Returns the name of the currently selected tool. Example: `if the tool is "brush" then answer "You chose the paint brush!"`
`shiftKey` | Returns the current state of the shift key, either `up` or `down`. For example, `wait until the shiftKey is down`
`commandKey` | Returns the current state of the command key (and/or 'ctrl' key on PC hardware), either `up` or `down`. Also available as `the cmdKey`
`optionKey` | Returns the current state of the option key (and/or 'meta' key on Unix hardware), either `up` or `down`. For example, `repeat while the optionKey is up`
`number of words` <br> `number of chars` <br> `number of lines` <br> `number of items` | Returns the number of words, characters, items or lines in a given factor. For example: `the number of characters in "hello"` returns "5"
`result` | Returns the current value of the implicit variable `it`, for example: `the result`
`message`<br>`message box`<br>`message window` | Returns the contents of the message box. For example: `put the message box into aVar`
`min` | Returns the minimum number passed to the function. For example: `min(3,5,7.24,9)` evaluates to 3.
`max` | Returns the maximum number passed to the function. For example: `min(3,5,7.24,9)` evaluates to 9.
`date`<br>`short date` | Returns the current date in _dd/mm/yy_ format. For example `put the date` yields 07/04/16.
`long date` | Returns the current date fully spelled out. For example, Saturday, July 02, 2016.
`abbreviated date`<br>`abbrev date` | Returns the current date  spelled out using abbreviations. For example, Sat, Jul 02, 2016.
`seconds` | Returns the number of seconds since midnight, January 1, 1970 UTC.
`ticks` | Returns the number of ticks (1/60th second) since the JVM was started.
`random` | Returns a random integer between 0 and the given argument. Example: `the random of 100` or `random(10)`.
`sqrt` | Returns the square root of the given argument or `NaN` of the argument is negative.
`sin` | Returns the trigonometric sine of the given argument, represented in radians.
`cos` | Returns the trigonometric cosine of the given argument, represented in radians.
`tan` | Returns the trigonometric tangent of the given argument, represented in radians.
`atan` | Returns the trigonometric arctangent of the given argument, represented in radians.
`exp` | Returns the value of _e_ raised to the power of the given argument.
`exp1` | Returns the value of _1-e_ raised to the number of the given argument.
`exp2` | Returns the value of _2_ raised to the given argument; for example `the exp2 of 3` is equivalent to `2^3`.
`ln` | Returns the natural logarithm of the given argument.
`ln1` | Returns the natural logarithm of the given argument plus one.
`log2` | Returns the base-2 logarithm of the given argument.
`trunc` | Returns the integer portion of the given numerical argument; for example `the trunc of 8.99` yields `8`.
`abs` | Returns the absolute value of the given numerical argument.
`numToChar` | Returns the character value associated with the given character _codepoint_. The actual mapping between numbers and characters will depend on the character encoding used by the system, but `numToChar` is always assured to be the inverse of `charToNum`
`charToNum` | Return the numerical _codepoint_ associated with the given character. The actual mapping between characters and numbers will depend on the character encoding used by the system, but `charToNum` is always assured to be the inverse of `numToChar`
`value` | Evaluates the given factor as a HyperTalk expression and returns the result. Example: `the value of ("3" & "*4")` yields 12.
`length` | Returns the number of characters in the value of the given expression. Example: `the length of "Hello World!"` yields 12.

### User-defined Functions

A user may define a function of their own creation anywhere inside of a script. Note that user-defined functions cannot be nested; cannot be accessed outside of the script in which they're defined; and cannot be invoked using the `[the] <function> of ...` syntax.

There is no distinction between a function that returns a value and one that does not (there is no requirement that a function call `return`). If a user-defined function does not invoke `return`, the empty string is implicitly returned.

The syntax for defining a function is:

```
function <functionName> [<arg1> [, <arg2>] ... [, <argN>]]]
	<statementList>
	[return <expression>]
end <functionName>
```

When calling a user defined function, use the calling syntax `<functionName>(<arg1>, <arg2>, ...)`. Note that the number of arguments passed to the function must match the number declared in the definition. HyperTalk does not support function overloading; each function defined in a script must have a unique name.

Consider this recursive function for generating the Fibonacci sequence:

```
on mouseUp
  answer fibonacci("", 0, 1, 200)
end mouseUp

function fibonacci sequence, lastValue, thisValue, maxValue
	if sequence is empty then put "0" into sequence
	put thisValue + lastValue after the last item of sequence

	if thisValue + lastValue <= maxValue then
		return fibonacci(sequence, thisValue, thisValue + lastValue, maxValue)
	else
		return sequence
	end if

end fibonacci
```

## Control Structures

HyperTalk supports simple conditional branching (if-then-else), plus a very flexible syntax for looping. Conditionals have the following syntax:

```
if <expression> then
   <statementList>
[else
   <statementList>]
end if
```

Some examples of conditional branching:

```
if 1 < 2 and 3 < 4 then
	answer "This is true!"
end if
```

```
if the first line of field id 0 contains "hello" then
	put "Hello" into the message box
else
	put "Goodbye" into the message box
end if
```

To address the [dangling else problem](https://en.wikipedia.org/wiki/Dangling_else), HyperTalk does not support a multi-line else-if construct. That said, nesting complex conditional logic can be achieved by nesting `if` statements. For example:

```
ask "Yes, no or maybe?" with ""

if it is "yes" then
  answer "Thank you for your support."
else
  if it is "maybe" then
    answer "Make up your mind already."
  else
    answer "We never liked you anyway."
  end if
end if
```

### Loop Constructs

HyperTalk provides a variety of looping constructs. The overall syntax for each of them is

```
<repeat-construct>
  <statement-list>
end repeat
```

The list of available repeat constructs is defined in the table below:

Repeat Construct | Description
-----------------|------------
`repeat forever` | Executes the enclosed statement-list forever. Sort of. Type `cmd-.` or `ctrl-.` at anytime to break execution of the loop.
`repeat until <expression>` | Executes the enclosed statement-list until the Boolean expression is true; if the expression is initially true, the statement-list will not be executed.
`repeat while <expression>` | Executes the enclosed statement-list as long as the Boolean expression remains true; if the expression is initially false, the statement-list will not be executed.
`repeat [for] <expression> [times]` | Executes the enclosed statement-list a pre-determined number of times.
`repeat with <container> = <expression> down to <expression>` | Executes the enclosed statement-list for as long as the first expression remains numerically greater than the second expression. Decrements the first expression by one each time the loop executes and places the decremented value into the given container.
`repeat with <container> = <expression> to <expression>` | Executes the enclosed statement-list for as long as the first expression remains numerically less than the second expression. Increments the first expression by one each time the loop executes and places the incremented value into the given container.

For example:

```
repeat with myVar = 1 to 10
	answer myVar
end repeat
```

```
repeat while the mouse is down
	set the top of me to item 2 of the mouseLoc
end repeat
```
