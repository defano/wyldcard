# HyperTalk Java
The HyperCard Scripting Environment

## What is this?

… a Java implementation of Apple’s HyperCard. 

Originally released in 1987 as part of System Software 6, HyperCard was an application that largely defied classification: part database, part programming language, part “paint” program. In true Apple fashion, HyperCard was a new paradigm. Other toy languages of the time made it possible to write simple console-mode programs (boring!), but with HyperCard a novice could script an application with a full graphical user interface using a very expressive syntax that mimicked natural language. Apple called it “programming for the rest of us.”

## What Was Done

I successfully implemented enough of the runtime environment and HyperTalk scripting language to demonstrate each of the core aspects of HyperCard, including: parts, attributes, event messaging, local and global variables, built-in and user defined functions, and complex prepositional chunk expressions.

I used CUP and JFLEX as the parser generator, and Sun’s NetBeans IDE for much of the Swing UI development. With my application, you can create buttons and fields in the UI and attach scripts to them for controlling their presentation and behavior. About 95% of the HyperTalk’s expression language is implemented, as is the ability for one object to send messages to another, or to dynamically execute or evaluate code (i.e., any string of text can be executed as a script using the do command). 

## Getting Started

The folder containing this document is a Java Eclipse project. It can be imported directly into Eclipse Europa for easy compilation. As I described on the class discussion board, the project implements automatic building of the parser and lexer; any modification to the CUP/FLEX input files will cause the parser and lexer to be regenerated. 

You may choose to build the project yourself (RuntimeEnv.java contains the main entry point) or, alternately, I’ve included an executable JAR file (in the distribution/ directory) which you can run without having to compile anything.  

Once the program is running, you’ll be presented with the application’s main window. From here, you may:

*	Begin adding your own user interface elements by right clicking within the “Card” panel and selecting either “New Button” or “New Field” from the context sensitive menu.
*	Open a previously saved card document (“File” -> “Open Card…”). I’ve included a few example cards in the “Examples” folder; each contains several scripted buttons and fields to experiment with.
*	Enter a statement or expression in the message box. Press enter or click the adjacent button to execute or evaluate your message.

To start scripting:

1.	Create a new button or field, or select an existing one.
2.	Right-click on the part and choose “Edit Script…” or “Edit Part Properties…” Both buttons and fields have several user-editable properties that can be inspected and modified within the GUI.

## The Language

HyperCard’s native language, called HyperTalk, is an event-driven scripting language. Scripts are associated with user interface elements (called parts) and are triggered by user actions called events. There is no singular “main” script that executes at runtime.

A script consists of zero or more handlers and function definitions. A handler is a list of statements that execute when the handler’s name is passed as a message to the part containing it. A function definition, like its counterpart in other imperative languages accepts zero or more arguments, executes one or more statements and optionally returns a single value.

For example, a button might contain the script:

```
on mouseUp
	answer “Hello World” with “Why, thank you”
end mouseUp
```

When the user clicks the button containing this script, the action of the mouse button being released over the part causes HyperCard to send the message “mouseUp” to the affected button. Upon receipt of this message, the button executes its mouseUp handler. (In our example, this generates a “hello world” dialog box). 

My HyperCard implementation automatically sends the following event messages:

Event Message	Description
mouseUp	Sent when the mouse is pressed and released over a part
mouseDown	Sent when the mouse is pressed over a part
mouseEnter	Sent when the cursor enters the region of a part
mouseExit	Sent when the cursor leaves the region of a part
keyDown	Sent only to in-focus fields when the user presses a key
keyUp	Sent only to in-focus fields when the user presses then releases a key

Not all messages need originate from HyperCard. A script may send a message to itself or to another part using the send command. The message need not be a known HyperCard message (i.e., from the table above); it’s acceptable to send a message of the scripter’s own creation. 

Also note that parts do not need to implement a handler for every message they might receive. Messages for which no handler exists are simply ignored.

For example:

```
send mouseExit to button id 0
send doSomethingCool to field “myField”
send keyDown to me
```

### Parts and their properties

A part is a scriptable user interface element in HyperCard. Apple’s implementation provided a wide range of parts and styles, but for simplicity, my version supports only two parts: simple push buttons and scrollable text fields. In HyperCard, these parts live within a document called a card (somewhat analogous to a window or panel).

In Apple’s HyperCard, cards contain two layers of user interface elements, a foreground and a background and are grouped into a document called a stack (like a stack of index cards). Each card had an individual foreground, but the background could be shared between two or more cards. Each of these elements—backgrounds, cards, stacks, etc—could contain their own scripts and act upon event messages from HyperCard. 

For simplification, this implementation treats cards as standalone documents (there is no concept of a stack), and further, the card cannot itself be scripted, nor does it support the concept of a foreground and background.

In addition to containing scripts, a part also maintains a set of properties. Properties control various aspects of the part, typically including its name, id, size and location. A part can be programmatically modified by way of its properties, and different types of parts have different properties.

A button has these properties:

Property	Description
script	Retrieves or replaces the current script of the button
id	Returns the buttons id. Each part has a globally unique id that is assigned at creation and cannot be changed.
name	Returns or sets the script-addressable name of the button
title	Returns or sets the title of this button  
left	Returns or sets the left-most border of the button’s location
top	Returns or sets the top-most border of the button’s location
width	Returns or sets the width of the button (in pixels)
height	Returns or sets the height of the button (in pixels)
visible	Returns or sets the visibility of the button (a Boolean value). When invisible, the button is not drawn on the screen and receives no messages from the UI. 
showtitle	Returns or sets the visibility of the button’s title (a Boolean value). When not true, the button is drawn without a name.
enabled	Returns or sets whether the button is enabled (a Boolean value). When disabled, the button appears “grayed out”. Note that it continues to receive user interface generated messages.

A field has these properties:

Property	Description
script	Retrieves or replaces the current script of the field
id	Returns the field’s id. Each part has a globally unique id that is assigned at creation and cannot be changed.
name	Returns or sets the script-addressable name of the field
text	Returns or sets the text contained within this field 
left	Returns or sets the left-most border of the field’s location
top	Returns or sets the top-most border of the field’s location
width	Returns or sets the width of the field (in pixels)
height	Returns or sets the height of the field (in pixels)
visible	Returns or sets the visibility of the field (a Boolean value). When invisible, the field is not drawn on the screen and receives no messages from the UI. 
wraptext	Returns or sets whether the text contained by the field will automatically wrap at end of line.
locktext	Returns or sets whether the text contained by the field can be edited by the user. 

Parts may be addressed in HyperTalk by name or id, and a part can refer to itself as me. Properties are read using the get command, and modified with the set command. Some examples include:

```
set the visible of me to true
set the left of button myButton to item 1 of the mouseLoc
get the name of button id 0
```

### Variables and containers 

A container is any entity in HyperCard that can hold a value; all parts, variables and the message box are containers. 
HyperCard is dynamically typed. Internally, each value is stored as a string and converted to an integer, float, Boolean, or list depending on the context of its use. Unlike Perl, however, HyperCard does not allow nonsensical conversions; adding 5 to “hello,” for example, produces a syntax error. 
Local variables in HyperTalk are lexically scoped. That is, they retain their value only within the handler or function in which they’re used. A variable may be made global by explicitly declaring it as such. Variables that are not declared as global are considered local, even when a global variable of the same name exists. All variables, global and local, are implicitly initialized with the empty string. 

HyperTalk uses “--” to initiate a single-line comment (there are no multi-line comments). Comments can appear on their own line, or following a statement inline. It’s also legal for comments to appear outside of function definitions and handlers. 
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
	put aVar	-- puts the empty string (“”) into the message box
end f

function y
	global aVar
	put aVar	-- puts “5” into the message box
end y
```

Like variables, parts and the message box can also be used to store value. For example:

```
put 35 + 27 into field id 12
put the text of button myButton into the message box
```

Note that HyperTalk contains an implicit variable named it; most expressions and some commands mutate the value of this variable so that it always contains the most recent evaluated result. The value of it may also be retrieved using the result function. For example:

```
on mouseUp
	ask “How are you, fine sir?”
	put it into responseVar  -- ‘it’ contains the user’s input
	put the result into responseVar -- same effect as previous line
end mouseUp
```

### Expressions

HyperCard contains a rich expression language that includes support for complex prepositional chunk operations. A script can access or mutate a range of words, characters, lines, or comma-delimited items in a value. Chunks may be specified numerically (line 3 of), by ordinal (the third line of), or relatively (the last line of; the middle word of).

Chunk expressions follow the form:

::= [the] { <ordinal> | <relation> } <chunk> of <value>
|   <chunk> <integer> of <value>
|   <chunk> <integer> to <integer> of <value>

Where:

<chunk>    ::= char | character | word | item | line
<relation> ::= middle | last
<ordinal>  ::= first | second | third | … | tenth

Consider the following chunked expressions:

`the first character of the second word of the last line of field id 24`
`character 19 to 27 of the message box`
`the second item of “Hello,Goodbye” -- yields “Goodbye”`
`the middle word of “one two three” -- yields “two”`

When mutating a chunk of text within a container (using the put command), a preposition (before, into, or after) may be included in the expression. For example:

`put word 2 of “Hello Goodbye” into the first word of field id 0`
`put “blah” after the third character of the middle item of myVar`
`put 29 before the message box`

In addition to chunk expressions, HyperTalk supports a typical suite of operators, including:
	
Precedence	Operator	Description all operators are binary, excepted where otherwise noted
1 (highest)	( )	Grouping
2	-	Negation for numbers (unary)
	not	Negation for boolean values (unary)
3	^	Exponentiation for numbers
4	*	Multiplication for numbers
	/	Division for numbers
	div	Division for numbers
	mod	Modulus division for numbers; returns the remainder
5	+	Addition for numbers
	-	Subtraction for numbers
6	&, &&	Text concatenation; & and && are synonymous
7	>	Greater than comparison for numbers and text
	<	Less than comparison for numbers and text
	<=	Less than or equal to comparison for numbers and text
	>=	Greater than or equal to comparison for numbers and text
	contains	Substring comparison for text
8	=	Equality comparison for text
	is	Equality comparison for text
	is not	Negative equality comparison for text
	<>	Synonym for is not
9	and	Logical AND for boolean values
10 (lowest) 	or	Logical OR for boolean values

My implementation supports nearly the full expression language (all of the aforementioned operators), and follows the same order of precedence as HyperTalk.  Valid expressions include:

`4 * (2 + 3) -- yields 20`
`“hello” contains “el” and “goodbye” contains “bye” -- true`
`3 * 5 is not 15 -- false`
`“Hello” && “ World” -- produces “Hello World”`
`“Hyper” > “Card” – true, “Hyper” is alphabetically after “Card”`
`not “nonsense” -- syntax error, “nonsense” is not a boolean`

### Control Structures

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
	answer “This is true!”
end if
```

```
if the first line of field id 0 contains “hello” then
	put “Hello” into the message box
else 
	put “Goodbye” into the message box
end if
```

HyperTalk also provides this self-explanatory set of looping constructs:

`repeat forever`
`repeat until <expression>`
`repeat while <expression>`
`repeat [for] <expression> [times]`
`repeat with <container> = <expression> down to <expression>`
`repeat with <container> = <expression> to <expression>`

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

### Functions

HyperCard provides both a suite of built-in functions as well as the ability for a user to script new ones. Note that the calling syntax differs between built-in and user-defined functions.

When executing a built-in function, the syntax is [the] <function> [of <argument> [in <factor>]]. This implementation includes the following built-in functions:

Function	Description
average	Returns the statistical mean of a list of numerical items. Example: the average of “1,2,3,4,5” returns “3.0”
mouse	Returns the current state of the left mouse button; either “up” or “down”
mouseLoc	Returns the current location of the cursor (in coordinates relative the top-left corner of the card panel), for example: the mouseLoc returns “123,55”
number	Returns the number of words, characters, items or lines in a given factor. For example: the number of characters in “hello” returns “5”
result	Returns the current value of the implicit variable it, for example: the result

A user may define a function of their own creation anywhere inside of a script, but keep in mind that functions cannot be nested and cannot be accessed outside of the script in which they’re defined. 

The syntax for defining a function is:

```
function <functionName> [<arg1> [, <arg2>] … [, <argN>]]]
	<statementList>
	[return <expression>]
end <functionName>
```

When calling a user defined function, the syntax is <functionName>(<arg1>, <arg2>, ..., <argN>). Note that the number of arguments passed to the function must match the number declared in the definition. HyperTalk does not support function overloading; each function defined in a script must have a unique identifier.

For example:

```
on mouseUp
	-- factorial(5) returns 120
	answer “The factorial of 5 is ” && factorial(5)
end mouseUp

function factorial fact
	if fact = 1 then
		return 1
	else
		return fact * factorial(fact – 1)
	end if
end factorial
```

### Commands 

This version of HyperCard implements the following set of commands:

Command	Description
put	Places a value into a container or a chunk of a container; put “hello” into the third item of mylist. When no container is specified, the message box is implied as a default container.
get	Get the value of a part’s property and places it into the implicit variable it; get the visible of button id 0 
set	Sets the property of a part to a value; set the wraptext of field id 3 to (5 > 3)
answer	Produces a dialog box with a message and up to three user-defined buttons. Follows the syntax answer <message> [with <button1> [or <button2>] [or <button3>]]]. Upon completion, it contains the text of the button selected by the user, or the empty string if answer is used without an optional button specifier. 
ask	Similar to answer, ask produces a dialog box with a message and a user-editable response string. Follows the syntax ask <message> [with <answer>]. Upon completion, it contains the value of the user-editable text field, or the empty string if the user cancelled the dialog.
do	Executes a value as if it were a list of statements; do “put 2+3 into the message window” or do the text of field myscript
send	Send a message to a part on the current card; send “mouseUp” to field id 3

