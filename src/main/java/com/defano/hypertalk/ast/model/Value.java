package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.hypertalk.ast.model.chunk.ChunkType;
import com.defano.hypertalk.ast.model.chunk.CompositeChunk;
import com.defano.hypertalk.comparator.StyledComparable;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.ChunkUtils;
import com.defano.hypertalk.utils.DateUtils;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Representation of a value in HyperTalk; all script values (literals, variables, properties, etc.) are represented by
 * this type. Every value is stored internally as a Java String and converted to {@link Integer}, {@link Double},
 * {@link Boolean}, {@link Point} or {@link Rectangle} as requested at runtime.
 * <p>
 * Instances of this class are effectively immutable.
 */
public class Value implements StyledComparable<Value> {

    private final String stringValue;

    // A flag to indicate value represents a quoted literal (useful when disambiguating 'card button 1' from 'card
    // button "1"'. The latter refers to a card button _named_ "1"; the former refers to card button number 1)
    private boolean isQuotedLiteral;

    // Cache for known value types (all are effectively final)
    private Long longValue;
    private Double floatValue;
    private Boolean booleanValue;

    /**
     * Creates a new Value representing the empty string, equivalent to `new Value("")`
     */
    public Value() {
        this("");
    }

    /**
     * Creates a new Value representing a {@link Point} (x,y coordinate). Equivalent to `new Value(p.x + "," + p.y)`
     *
     * @param p The initial point value
     */
    public Value(Point p) {
        this(p.x + "," + p.y);
    }

    /**
     * Creates a new Value representing a {@link Rectangle} (x1, y1, x2, y2).
     *
     * @param r The initial rectangle value
     */
    public Value(Rectangle r) {
        this(r.x + "," + r.y + "," + (r.x + r.width) + "," + (r.y + r.height));
    }

    /**
     * Creates a new Value representing the String value of the argument. If the argument is null, creates a Value
     * initialized with the empty string.
     *
     * @param v The initial value
     */
    public Value(Object v) {
        this(v == null ? "" : String.valueOf(v));

        if (v instanceof Value) {
            this.isQuotedLiteral = ((Value) v).isQuotedLiteral;
        }
    }

    /**
     * Creates a new Value representing a long integer.
     *
     * @param v The initial value
     */
    public Value(long v) {
        this(String.valueOf(v));
        longValue = v;
    }

    /**
     * Creates a new Value representing a double.
     *
     * @param f The initial value
     */
    public Value(double f) {
        this(String.valueOf(f));
        floatValue = f;
    }

    /**
     * Creates a new Value representing a boolean.
     *
     * @param v The initial value
     */
    public Value(boolean v) {
        this(String.valueOf(v));
        booleanValue = v;
    }

    /**
     * Creates a new Value representing a point (coordinate).
     *
     * @param x The initial x coordinate
     * @param y The initial y coordinate
     */
    public Value(int x, int y) {
        this(x + "," + y);
    }

    /**
     * Creates a new Value representing a rectangle.
     *
     * @param x1 The top-left x coordinate
     * @param y1 The top-left y coordinate
     * @param x2 The bottom-right x coordinate
     * @param y2 The bottom-right y coordinate
     */
    public Value(int x1, int y1, int x2, int y2) {
        this(x1 + "," + y1 + "," + x2 + "," + y2);
    }

    /**
     * Creates a new Value representing a character.
     *
     * @param c The initial value.
     */
    public Value(char c) {
        this(String.valueOf(c));
    }

    /**
     * Creates a new Value representing a String.
     *
     * @param v The initial value.
     */
    public Value(String v) {
        this.stringValue = v == null ? "" : v;

        // Special case: empty string is a valid int and float
        if (v == null || v.trim().equals("")) {
            longValue = 0L;
            floatValue = 0.0;
            booleanValue = null;
        }
    }

    /**
     * Creates a new value of the given string and flags the value as a quoted literal.
     * <p>
     * Useful when disambiguating 'card button 1' from 'card button "1"'. The latter refers to a card button _named_
     * "1"; the former refers to card button number 1.
     *
     * @param literal The quoted literal (with the quotes already removed; this method does not remove quotes)
     * @return A value of the given string.
     */
    public static Value ofQuotedLiteral(String literal) {
        Value v = new Value(literal);
        v.isQuotedLiteral = true;
        return v;
    }

    /**
     * Creates a line-delimited Value of Values. For example, "1\n2\n3".
     *
     * @param lines The values to be separated by newlines.
     * @return The new Value
     */
    public static Value ofLines(List<Value> lines) {
        return ofValues(lines, "\n");
    }

    /**
     * Creates a comma-delimited Value of Values. For example, "1,2,3".
     *
     * @param items The values to be separated by commas.
     * @return The new Value
     */
    public static Value ofItems(List<Value> items) {
        return ofValues(items, ",");
    }

    /**
     * Creates a space-delimited Value of Values. For example, "1 2 3".
     *
     * @param words The values to be separated by spaces.
     * @return The new Value
     */
    public static Value ofWords(List<Value> words) {
        return ofValues(words, " ");
    }

    /**
     * Creates a null-delimited Value of Values. For example, "123".
     *
     * @param chars The values to be concatenated into a string.
     * @return The new Value
     */
    public static Value ofChars(List<Value> chars) {
        return ofValues(chars, "");
    }

    /**
     * Creates a delimited Value of Values using a provided delimiter.
     *
     * @param values    The values to be concatenated.
     * @param delimiter The string used to delimit values
     * @return The new Value
     */
    private static Value ofValues(List<Value> values, String delimiter) {
        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < values.size(); index++) {
            builder.append(values.get(index));

            if (index < values.size() - 1) {
                builder.append(delimiter);
            }
        }

        return new Value(builder.toString());
    }

    /**
     * Creates a new Value by taking an existing value and mutating a chunk of its text. For example, `put x into the
     * middle char of 'hello'` creates a new Value equivalent to new Value("hexlo")
     *
     * @param context The execution context
     * @param mutable The existing value from which the new value will be created (this argument is unmodified by this
     *                method).
     * @param p       The mutation preposition (i.e., before, into, after)
     * @param c       The chunk specifier (i.e., 'the first word of', 'character 17')
     * @param mutator An object whose string value will be added. In the example, `put 10 after 20`, "10" is the
     *                mutator value.
     * @return A new Value equal to the mutable argument modified by applying the chunk operation to it.
     * @throws HtException Thrown if an error occurs applying the chunk.
     */
    @SuppressWarnings("UnusedAssignment")
    public static Value ofMutatedChunk(ExecutionContext context, Value mutable, Preposition p, Chunk c, Object mutator) throws HtException {

        if (c instanceof CompositeChunk) {
            return new Value(ChunkUtils.putCompositeChunk(context, (CompositeChunk) c, p, mutable.toString(), String.valueOf(mutator)));
        }

        String mutatorString = mutator.toString();
        String mutableString = mutable.toString();

        Value startVal = null;
        Value endVal = null;

        int startIdx = 0;
        int endIdx = 0;

        if (c.start != null)
            startVal = c.start.evaluate(context);
        if (c.end != null)
            endVal = c.end.evaluate(context);

        if (startVal == null || !startVal.isNatural() && !Ordinal.isReservedValue(startVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + startVal + "' instead.");
        if (endVal != null && !endVal.isNatural() && !Ordinal.isReservedValue(endVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + endVal + "' instead.");

        startIdx = startVal.integerValue();
        if (endVal != null)
            endIdx = endVal.integerValue();

        return new Value(ChunkUtils.putChunk(context, c.type, p, mutableString, startIdx, endIdx, mutatorString));
    }

    /**
     * Creates a new Value by taking an existing Value and placing a new string before, after or into (replacing) it.
     *
     * @param mutable The existing value
     * @param p       A preposition representing where the mutator value should be inserted (before, after or into).
     * @param mutator The mutator string.
     * @return The new value
     */
    public static Value ofValue(Value mutable, Preposition p, Value mutator) {

        switch (p) {
            case BEFORE:
                return new Value(mutator.toString() + mutable.toString());
            case INTO:
            case REPLACING:
                return new Value(mutator.toString());
            case AFTER:
                return new Value(mutable.toString() + mutator.toString());
            default:
                throw new RuntimeException("Bug! Unhandled preposition.");
        }
    }

    /**
     * Determines if this value represents an integer (whole number). All integers are numbers, but not all numbers are
     * integers.
     *
     * @return True if the value represents a whole number
     */
    public boolean isInteger() {
        return longValue != null || parseLong() != null;
    }

    /**
     * Determines if this value represents a non-negative, non-zero integer.
     *
     * @return True if the value represents a natural number.
     */
    public boolean isNatural() {
        return isInteger() && longValue() > 0;
    }

    /**
     * Determines if this value represents a boolean (or 'logical') value, that is, the value 'true' or 'false'.
     *
     * @return True if this value is either 'true' or 'false'.
     */
    public boolean isBoolean() {
        return booleanValue != null || parseBoolean() != null;
    }

    /**
     * Determines if this value represents a numerical, integer or floating point value.
     *
     * @return True if this value is a number.
     */
    public boolean isNumber() {
        return floatValue != null || parseFloat() != null;
    }

    /**
     * Determines if this value was flagged as being a quoted literal. Used when disambiguating part names and numbers.
     * A value is flagged as a quoted literal only when created using the {@link #ofQuotedLiteral(String)} creator
     * method.
     * <p>
     * Note that Antlr removes quotes from quoted literals appearing in script text; this flag attempts to preserve
     * whether this value originally had quotes around it.
     *
     * @return True if this value was marked as having originated from a quoted literal value in script.
     */
    public boolean isQuotedLiteral() {
        return isQuotedLiteral;
    }

    /**
     * Determines if this value represents a point, that is, two comma-separated integer values representing an
     * x-coordinate and a y-coordinate.
     *
     * @return True if this value is a point
     */
    public boolean isPoint() {
        List<Value> listValue = getListItems();

        return listValue.size() == 2 &&
                new Value(listValue.get(0)).isInteger() &&
                new Value(listValue.get(1)).isInteger();
    }

    /**
     * Determines if this value represents a rectangle, that is, four comma-separated integer values representing the
     * top-left point and bottom-right point of the rectangle's boundary.
     *
     * @return True if this value is a rectangle.
     */
    public boolean isRect() {
        List<Value> listValue = getListItems();

        return listValue.size() == 4 &&
                new Value(listValue.get(0)).isInteger() &&
                new Value(listValue.get(1)).isInteger() &&
                new Value(listValue.get(2)).isInteger() &&
                new Value(listValue.get(3)).isInteger();
    }

    /**
     * Attempts to cast this value as a long integer.
     *
     * @return The long integer representation of this value, or null if it cannot be cast as a long.
     */
    private Long parseLong() {
        try {
            longValue = Long.parseLong(stringValue.trim());
        } catch (NumberFormatException e) {
            longValue = null;
        }

        return longValue;
    }

    /**
     * Attempts to cast this value as a double-precision floating point value.
     *
     * @return The double representation of this value, or null if it cannot be cast as a double.
     */
    private Double parseFloat() {
        try {
            floatValue = Double.parseDouble(stringValue.trim());
        } catch (NumberFormatException e) {
            floatValue = null;
        }

        return floatValue;
    }

    /**
     * Attempts to cast this value as a Boolean.
     *
     * @return The boolean representation of this value, or null, if it cannot be cast as a Boolean.
     */
    private Boolean parseBoolean() {
        if (stringValue.trim().equalsIgnoreCase("true")) {
            booleanValue = true;
        } else if (stringValue.trim().equalsIgnoreCase("false")) {
            booleanValue = false;
        } else {
            booleanValue = null;
        }

        return booleanValue;
    }

    /**
     * Returns this value's representation as an integer, or 0 if this value cannot be cast as an integer.
     *
     * @return The integer representation of this value.
     */
    public int integerValue() {
        if (longValue != null || parseLong() != null) {
            return longValue.intValue();
        } else {
            return 0;
        }
    }

    /**
     * Returns this value's representation as a long integer, or 0 if this value cannot be cast as an integer.
     *
     * @return The integer representation of this value.
     */
    public long longValue() {
        if (longValue != null || parseLong() != null)
            return longValue;
        else
            return 0;
    }

    /**
     * Returns this value's representation as a double-precision floating point value, or 0.0 if this value cannot be
     * cast as a number.
     *
     * @return The floating-point representation of this value.
     */
    public double doubleValue() {
        if (floatValue != null || parseFloat() != null)
            return floatValue;
        else
            return 0;
    }

    /**
     * Returns this value's representation as a double-precision floating point value, throwing a provided exception if
     * the value cannot be cast as a double.
     *
     * @param error The error to be thrown when this value cannot be cast as a double.
     * @return The double representation of this value.
     * @throws HtException Thrown when this value cannot be cast
     */
    public double doubleValueOrError(HtException error) throws HtException {
        if (isNumber()) {
            return doubleValue();
        } else {
            throw error;
        }
    }

    /**
     * Returns this value's boolean representation, or false if this value is not a logical value.
     *
     * @return The boolean representation of this value.
     */
    public boolean booleanValue() {
        if (booleanValue != null || parseBoolean() != null)
            return booleanValue;
        else
            return false;
    }

    /**
     * Returns this value's representation as a boolean, throwing a given exception if the value cannot be cast.
     *
     * @param error The exception to be thrown if this value cannot be cast to a boolean value
     * @return The boolean representation of this value
     * @throws HtException Thrown if this value cannot be cast.
     */
    public boolean booleanValueOrError(HtException error) throws HtException {
        if (isBoolean()) {
            return booleanValue();
        } else {
            throw error;
        }
    }

    /**
     * Returns this value's representation as a rectangle.
     *
     * @return This value's Rectangle representation, or an empty Rectangle (i.e., 0,0,0,0) if this value cannot be
     * interpreted as a Rectangle.
     */
    @SuppressWarnings("WeakerAccess")
    public Rectangle rectangleValue() {
        if (isRect()) {
            int left = getListItemAt(0).integerValue();
            int top = getListItemAt(1).integerValue();
            int height = getListItemAt(3).integerValue() - getListItemAt(1).integerValue();
            int width = getListItemAt(2).integerValue() - getListItemAt(0).integerValue();

            return new Rectangle(left, top, width, height);
        }

        return new Rectangle();
    }

    /**
     * Returns this value's representation as a Point (coordinate)
     *
     * @return This value's Point representation, or an empty Point (i.e. 0,0) if this value cannot be interpreted as a
     * Point.
     */
    @SuppressWarnings("WeakerAccess")
    public Point pointValue() {
        if (isPoint()) {
            int left = getListItemAt(0).integerValue();
            int top = getListItemAt(1).integerValue();

            return new Point(left, top);
        }

        return new Point();
    }

    /**
     * Gets a list of comma-separated items contained in this value. This function ignores the itemDelimiter HyperCard
     * property when splitting the value into items. Useful for parsing argument lists.
     * <p>
     * See {@link #getItems(ExecutionContext)} for a method whose behavior respects the itemDelimiter.
     *
     * @return A list of zero or
     */
    public List<Value> getListItems() {
        ArrayList<Value> items = new ArrayList<>();

        if (!stringValue.isEmpty()) {
            for (String thisItem : stringValue.split(",")) {
                items.add(new Value(thisItem));
            }
        }

        return items;
    }

    /**
     * Returns this value as a List of items (typically delimited by comma, but may be overridden using the
     * `itemDelimiter` WyldCard property.
     *
     * @param context The execution context.
     * @return The list of items represented by the value. Every value has at least one item.
     */
    public List<Value> getItems(ExecutionContext context) {
        return getChunks(context, ChunkType.ITEM);
    }

    /**
     * Returns the item in this value identified by index, or an empty Value if no such item exists.
     *
     * @param context The execution context.
     * @param index   The zero-based index of the item to be retrieved.
     * @return A new Value representing the requested item, or an empty Value if no such item exists.
     */
    public Value getItemAt(ExecutionContext context, int index) {
        List<Value> items = getItems(context);
        if (items.size() > index) {
            return new Value(items.get(index));
        } else {
            return new Value();
        }
    }

    /**
     * Returns the comma-delimited item in this value (irrespective of what the itemDelimiter property is). Useful for
     * parsing coordinates from points and rectangle values.
     *
     * @param index The zero-based index of the item to be retrieved.
     * @return A new Value representing the requested item, or an empty Value if no such item exists.
     */
    @SuppressWarnings("WeakerAccess")
    public Value getListItemAt(int index) {
        List<Value> items = getListItems();
        if (items.size() > index) {
            return new Value(items.get(index));
        } else {
            return new Value();
        }
    }

    /**
     * Returns this value as a List of line-delimited chunks.
     *
     * @param context The execution context
     * @return A List of Values containing each line in the value. Every value has at least one line.
     */
    public List<Value> getLines(ExecutionContext context) {
        return getChunks(context, ChunkType.LINE);
    }

    /**
     * Returns this value as a List of whitespace-delimited chunks.
     *
     * @param context The execution context
     * @return A List of Values containing each word in the value. Every value has at least one word.
     */
    public List<Value> getWords(ExecutionContext context) {
        return getChunks(context, ChunkType.WORD);
    }

    /**
     * Returns this value as a List of null-delimited chunks.
     *
     * @param context The execution context
     * @return A List of Values containing each character in the value. An empty Value returns a singleton list of a
     * empty Value.
     */
    public List<Value> getChars(ExecutionContext context) {
        return getChunks(context, ChunkType.CHAR);
    }

    /**
     * Returns this value as a list of chunks.
     *
     * @param context The execution context.
     * @param type    The chunk type (i.e., word, item, line)
     * @return A list of zero or more chunks of the requested type
     */
    @SuppressWarnings("WeakerAccess")
    public List<Value> getChunks(ExecutionContext context, ChunkType type) {
        Matcher matcher = ChunkUtils.getRegexForChunkType(context, type).matcher(stringValue);
        ArrayList<Value> chunks = new ArrayList<>();

        while (matcher.find()) {
            chunks.add(new Value(matcher.group()));
        }

        return chunks;
    }

    /**
     * Returns the number of items held in this value.
     *
     * @param context The execution context.
     * @return The number of items held in this value.
     */
    public int itemCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.ITEM, stringValue);
    }

    /**
     * Returns the number of words held in this value.
     *
     * @param context The execution context.
     * @return The number of words held in this value.
     */
    public int wordCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.WORD, stringValue);
    }

    /**
     * Returns the number of chars held in this value.
     *
     * @param context The execution context.
     * @return The number of chars held in this value.
     */
    public int charCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.CHAR, stringValue);
    }

    /**
     * Returns the number of lines held in this value.
     *
     * @param context The execution context.
     * @return The number of lines held in this value.
     */
    public int lineCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.LINE, stringValue);
    }

    /**
     * Returns a chunk of this value.
     *
     * @param context The execution context.
     * @param c       The chunk specifier (i.e., `the first word` or `char 13`)
     * @return The extracted chunk
     * @throws HtException Thrown if an error occurs getting the chunk
     */
    @SuppressWarnings("UnusedAssignment")
    public Value getChunk(ExecutionContext context, Chunk c) throws HtException {

        Value startVal = null;
        Value endVal = null;

        int startIdx = 0;
        int endIdx = 0;

        if (c.start != null)
            startVal = c.start.evaluate(context);
        if (c.end != null)
            endVal = c.end.evaluate(context);

        if (startVal == null || !startVal.isNatural() && !Ordinal.isReservedValue(startVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + startVal + "' instead.");
        if (endVal != null && !endVal.isNatural() && !Ordinal.isReservedValue(endVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + endVal + "' instead.");

        startIdx = startVal.integerValue();
        if (endVal != null)
            endIdx = endVal.integerValue();

        Value chunkValue = new Value(ChunkUtils.getChunk(context, c.type, stringValue, startIdx, endIdx));

        // If a composite chunk; evaluate right hand of the expression first
        if (c instanceof CompositeChunk) {
            return chunkValue.getChunk(context, ((CompositeChunk) c).chunkOf);
        } else {
            return chunkValue;
        }
    }

    /**
     * Returns a new logical value representing if this value contains the empty string.
     *
     * @return True if the value is empty, false otherwise.
     */
    public boolean isEmpty() {
        return stringValue.equals("");
    }

    /**
     * Returns a logical value representing if this value represents the number 0.
     * @return True if this value is numerically equal to zero, false otherwise.
     */
    public boolean isZero() {
        return isEmpty() || (isInteger() && integerValue() == 0) || (isNumber() && doubleValue() == 0.0);
    }

    /**
     * Returns a new logical value representing if this value is less than a given value. Non-numeric values are
     * compared alphabetically.
     *
     * @param v The value being compared
     * @return True if this value is less than v, false otherwise
     */
    public Value isLessThan(Value v) {
        if (isNumber() && v.isNumber()) {
            return new Value(doubleValue() < v.doubleValue());
        } else {
            return new Value(toString().compareTo(v.toString()) < 0);
        }
    }

    /**
     * Returns a new logical value representing if this value is greater than a given value. Non-numeric values are
     * compared alphabetically.
     *
     * @param v The value being compared
     * @return True if this value is greater than v, false otherwise
     */
    public Value isGreaterThan(Value v) {
        if (isNumber() && v.isNumber()) {
            return new Value(doubleValue() > v.doubleValue());
        } else {
            return new Value(toString().compareTo(v.toString()) > 0);
        }
    }

    /**
     * Returns a new logical value representing if this value is greater than or equal to a given value. Non-numeric
     * values are compared alphabetically.
     *
     * @param v The value being compared
     * @return True if this value is greater than or equal to v, false otherwise
     */
    public Value isGreaterThanOrEqualTo(Value v) {
        if (isNumber() && v.isNumber()) {
            return new Value(doubleValue() >= v.doubleValue());
        } else {
            return new Value(toString().compareTo(v.toString()) >= 0);
        }
    }

    /**
     * Returns a new logical value representing if this value is less than or equal to a given value. Non-numeric values
     * are compared alphabetically.
     *
     * @param v The value being compared
     * @return True if this value is less than or equal to v, false otherwise
     */
    public Value isLessThanOrEqualTo(Value v) {
        if (isNumber() && v.isNumber()) {
            return new Value(doubleValue() <= v.doubleValue());
        } else {
            return new Value(toString().compareTo(v.toString()) <= 0);
        }
    }

    /**
     * Returns a new value equal to multiplying this value by a given value.
     *
     * @param v The value by which to multiply this value.
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number, or if an overflow occurs while performing the
     *                             multiplication
     */
    public Value multipliedBy(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + stringValue + "' cannot be multiplied by '" + v + "'.");
        }

        try {
            if (isInteger() && v.isInteger()) {
                return new Value(Math.multiplyExact(longValue(), v.longValue()));
            } else {
                return new Value(doubleValue() * v.doubleValue());
            }
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Overflow when trying to multiply " + toString() + " by " + v.toString() + ".");
        }
    }

    /**
     * Returns a new value equal to dividing this value by another value. See {@link #divBy(Value)} for integer
     * division.
     *
     * @param v The value by which this value should be divided
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number, or if the divisor is zero.
     */
    public Value dividedBy(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + stringValue + "' cannot be divided by " + v + '.');
        }

        if (v.isZero()) {
            throw new HtSemanticException("Cannot divide by zero.");
        }

        try {
            return new Value(doubleValue() / v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Cannot divide " + toString() + " by zero.");
        }
    }

    /**
     * Returns a new value equal to dividing this value by another value, ignoring any fractional remainder (i.e.,
     * performs integer division).
     *
     * @param v The value that this value should be div'd by
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number, or if the divisor is 0.
     */
    public Value divBy(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("Expected a number here.");
        }

        if (v.isZero()) {
            throw new HtSemanticException("Cannot divide by zero.");
        }

        try {
            return new Value((int) (doubleValue() / v.doubleValue()));
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Cannot divide " + toString() + " by zero.");
        }
    }

    /**
     * Returns a new value equal to adding this value to another value.
     *
     * @param v The value that should be added to this value
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number or if an overflow occurs
     */
    public Value add(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + v + "' cannot be added to '" + stringValue + "'.");
        }

        try {
            if (isInteger() && v.isInteger())
                return new Value(Math.addExact(longValue(), v.longValue()));
            else
                return new Value(doubleValue() + v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Overflow when trying to add " + toString() + " to " + v.toString() + ".");
        }
    }

    /**
     * Returns a new value equal to subtracting another value from this value.
     *
     * @param v The value that should be subtracted from this value
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number or if an overflow occurs
     */
    public Value subtract(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + v + "' cannot be subtracted from '" + stringValue + "'.");
        }

        try {
            if (isInteger() && v.isInteger())
                return new Value(Math.subtractExact(longValue(), v.longValue()));
            else
                return new Value(doubleValue() - v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Overflow when trying to subtract " + v.toString() + " from " + toString() + ".");
        }
    }

    /**
     * Returns a new value equal to raising this value to the power of another value.
     *
     * @param v A value representing the power that this value should be raised to.
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number
     */
    public Value exponentiate(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + stringValue + "' cannot be raised to the power of '" + v + "'.");
        }

        return new Value(Math.pow(doubleValue(), v.doubleValue()));
    }

    /**
     * Returns a new value equal modulo-dividing this value by another value.
     *
     * @param v The value that this value should be modulo-divided by.
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a number
     */
    public Value mod(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + v + "' cannot be mod by '" + stringValue + "'.");
        }

        if (isInteger() && v.isInteger())
            return new Value(longValue() % v.longValue());
        else
            return new Value(doubleValue() % v.doubleValue());
    }

    /**
     * Returns a new value equal to the logical negation of this value (i.e., true becomes false, false becomes true).
     *
     * @return The logical negation of this value
     * @throws HtSemanticException Thrown if this value is not a logical (boolean) value
     */
    public Value not() throws HtSemanticException {
        if (!isBoolean())
            throw new HtSemanticException("Expected a logical value here, but got '" + stringValue + "'.");

        return new Value(!booleanValue());
    }

    /**
     * Returns a new value equal to the numeric negation of this value (i.e., 20 becomes -20).
     *
     * @return The numerical negation of this value
     * @throws HtSemanticException Thrown if this value is not a number
     */
    public Value negate() throws HtSemanticException {
        if (isInteger())
            return new Value(longValue() * -1);
        else if (isNumber())
            return new Value(doubleValue() * -1);
        else {
            throw new HtSemanticException("Expected a number here, but got '" + stringValue + "'.");
        }
    }

    /**
     * Returns a new value equal to performing a logical 'and' operation of this value and another value.
     *
     * @param v The value that should be and'ed with this value
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a logical (boolean) value.
     */
    public Value and(Value v) throws HtSemanticException {

        // Allow for short circuit evaluation
        if (!isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + stringValue + "'.");
        }

        if (!v.isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + v + "'.");
        }

        return new Value(booleanValue() && v.booleanValue());
    }

    /**
     * Returns a new value equal to performing a logical 'or' operation of this value and another value.
     *
     * @param v The value that should be or'ed with this value
     * @return The resultant value
     * @throws HtSemanticException Thrown if either value is not a logical (boolean) value.
     */
    public Value or(Value v) throws HtSemanticException {

        if (!isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + stringValue + "'.");
        }

        if (!v.isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + v + "'.");
        }

        return new Value(booleanValue() || v.booleanValue());
    }

    /**
     * Returns a new value equal to concatenating the string equivalent of this value with that of another value.
     *
     * @param v The value that should be concatenated to this value
     * @return The resultant value
     */
    public Value concat(Value v) {
        return new Value(stringValue + v.toString());
    }

    /**
     * Returns a new logical value indicating whether this value is a coordinate that lies inside the rectangle bound
     * by the given value.
     *
     * @param v A rectangle value
     * @return True if this value lies inside the specified rectangle value, false otherwise
     * @throws HtSemanticException Thrown if this value is not a point or if v is not a rectangle.
     */
    public Value isWithin(Value v) throws HtSemanticException {
        if (!isPoint() || !v.isRect()) {
            throw new HtSemanticException("Cannot determine if '" + stringValue + "' is within the bounds of '" + v.toString() + "'.");
        }

        return new Value(v.rectangleValue().contains(pointValue()));
    }

    /**
     * Returns a new value equal to the numerical truncation of this value. That is, any fractional/floating point
     * portion of the value is removed without rounding or modifying the while portion.
     *
     * @return The resultant value
     * @throws HtSemanticException Thrown if this value is not a number
     */
    public Value trunc() throws HtSemanticException {
        if (isInteger()) {
            return new Value(integerValue());
        } else if (isNumber()) {
            return new Value((int) doubleValue());
        }

        throw new HtSemanticException("Cannot trunc the value '" + toString() + "' because it is not a number.");
    }

    /**
     * Rounds the value to the nearest whole number.
     *
     * @return The resulting value
     * @throws HtSemanticException Thrown if this value is not a number.
     */
    public Value round() throws HtSemanticException {
        if (isNumber()) {
            return new Value(Math.round(doubleValue()));
        }

        throw new HtSemanticException("Expected a number, but got " + this);
    }

    /**
     * Returns a logical value indicating whether this value can be interpreted as the given type (one of 'number',
     * 'integer', 'point', 'rect'/'rectangle', 'date', 'logical'/'boolean'/'bool')
     *
     * @param val A value containing the name of a known HyperTalk type
     * @return True if this value can be interpreted as the given type
     * @throws HtSemanticException Thrown if the given value does not specify a known HyperTalk type
     */
    public Value isA(Value val) throws HtSemanticException {
        KnownType type = KnownType.getTypeByName(val.toString());
        switch (type) {
            case NUMBER:
                return new Value(this.isNumber());
            case INTEGER:
                return new Value(this.isInteger());
            case POINT:
                return new Value(this.isPoint());
            case RECT:
                return new Value(this.isRect());
            case DATE:
                return new Value(DateUtils.dateOf(this) != null);
            case LOGICAL:
                return new Value(this.isBoolean());

            default:
                throw new RuntimeException("Bug! Unimplemented type comparison for: " + type);
        }
    }

    /**
     * Determines whether the given value can be found as a substring of the current value.
     *
     * @param v The substring to search for
     * @return True if the given value can be found within this value
     */
    public boolean contains(Value v) {
        return stringValue.toLowerCase().contains(v.toString().toLowerCase());
    }

    /**
     * Returns the string representation of this value.
     *
     * @return The string representation of this value.
     */
    public String toString() {
        return stringValue;
    }

    /**
     * Determines if one Value is equal to another, per HyperTalk's definition of equality. In general, two values are
     * equal if their case insensitive {@link #toString()} values are equal, with a few caveats:
     * <p>
     * If either value is empty (""), then the two values are equal only if both values are empty. In general, an empty
     * value is numerically equivalent to zero, but not when comparing. That is, `3 + ""` equals 3, but "" is not equal
     * to 0.
     * <p>
     * When comparing values that can be interpreted as booleans, a boolean evaluation will occur instead of a string
     * evaluation. For example, `false = "  false  "` (true) and `"true" = " true "` (also true), but `"abc" = " abc "`
     * is false.
     *
     * @param o The value that should be compared to this value
     * @return True if the values are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value otherValue = (Value) o;

        // Logical comparisons: Ignore whitespace that may be present in value
        if (isBoolean() && otherValue.isBoolean()) {
            return this.booleanValue() == otherValue.booleanValue();
        }

        // Integer comparisons: Empty string is zero, except for equality
        else if (isInteger() && otherValue.isInteger()) {
            // Weird special case: "" is a valid number (zero), but is not equal to 0
            // Thus, '2 * "" = 0', but '0 <> ""' -- don't believe me, try it in HyperCard!
            if (stringValue.equals("") || otherValue.stringValue.equals("")) {
                return stringValue.equals(otherValue.stringValue);
            } else {
                return this.integerValue() == otherValue.integerValue();
            }
        }

        // Numeric comparisons: Compare numeric values (i.e., 1.2 should equal 1.2000)
        else if ((isInteger() || isNumber()) && (otherValue.isInteger() || otherValue.isNumber())) {
            return this.doubleValue() == otherValue.doubleValue();
        }

        // All others: Case insensitive string equality
        else {
            return toString().equalsIgnoreCase(otherValue.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return stringValue.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Value o) {
        if (this.isInteger() && o.isInteger()) {
            return Integer.compare(this.integerValue(), o.integerValue());
        } else if (this.isNumber() && o.isNumber()) {
            return Double.compare(this.doubleValue(), o.doubleValue());
        } else {
            return toString().toLowerCase().trim().compareTo(o.toString().toLowerCase().trim());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Value to, SortStyle style) {
        switch (style) {
            case INTERNATIONAL:
            case TEXT:
                return toString().compareTo(to.toString());
            case NUMERIC:
                return Double.compare(this.doubleValue(), to.doubleValue());
            case DATE_TIME:
                Date thisDate = DateUtils.dateOf(this);
                Date toDateTime = DateUtils.dateOf(to);

                if (thisDate == null && toDateTime == null) {
                    return 0;
                }

                if (thisDate == null) {
                    return 1;
                }

                if (toDateTime == null) {
                    return -1;
                }

                return thisDate.compareTo(toDateTime);
        }

        throw new IllegalArgumentException("Bug! Unimplemented comparison style.");
    }
}
