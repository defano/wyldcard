package com.defano.hypertalk.ast.model;

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
 * Representation of a value in HyperTalk; all values are stored internally as Strings and converted to integers, floats
 * or booleans as requested at runtime.
 * <p>
 * The value held within a Value object is immutable.
 */
public class Value implements StyledComparable<Value> {

    private final String value;

    // A flag to indicate value represents a quoted literal (useful when disambiguating 'card button 1' from 'card
    // button "1"'. The latter refers to a card button _named_ "1"; the former refers to card button number 1)
    private boolean isQuotedLiteral;

    // Cache for known value types (all are effectively final)
    private Long longValue;
    private Double floatValue;
    private Boolean booleanValue;

    public Value() {
        this("");
    }

    public Value(Point p) {
        this(p.x + "," + p.y);
    }

    public Value(Rectangle r) {
        this(r.x + "," + r.y + "," + (r.x + r.width) + "," + (r.y + r.height));
    }

    public Value(Object v) {
        this(v == null ? "" : String.valueOf(v));

        if (v instanceof Value) {
            this.isQuotedLiteral = ((Value) v).isQuotedLiteral;
        }
    }

    public Value(long v) {
        this(String.valueOf(v));
        longValue = v;
    }

    public Value(double f) {
        this(String.valueOf(f));
        floatValue = f;
    }

    public Value(boolean v) {
        this(String.valueOf(v));
        booleanValue = v;
    }

    public Value(int x, int y) {
        this(String.valueOf(x) + "," + String.valueOf(y));
    }

    public Value(int x1, int y1, int x2, int y2) {
        this(String.valueOf(x1) + "," + String.valueOf(y1) + "," + String.valueOf(x2) + "," + String.valueOf(y2));
    }

    public Value(char c) {
        this(String.valueOf(c));
    }

    public Value(String value) {
        this.value = value == null ? "" : value;

        // Special case: empty string is a valid int and float
        if (value == null || value.trim().equals("")) {
            longValue = 0L;
            floatValue = 0.0;
            booleanValue = null;
        }
    }

    /**
     * Creates a new value of the given string and flags the value as a quoted literal.
     *
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

    public static Value ofLines(List<Value> lines) {
        return ofValues(lines, "\n");
    }

    public static Value ofItems(List<Value> items) {
        return ofValues(items, ",");
    }

    public static Value ofWords(List<Value> words) {
        return ofValues(words, " ");
    }

    public static Value ofChars(List<Value> chars) {
        return ofValues(chars, "");
    }

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

    public static Value setChunk(ExecutionContext context, Value mutable, Preposition p, Chunk c, Object mutator) throws HtException {

        if (c instanceof CompositeChunk) {
            return new Value(ChunkUtils.putCompositeChunk(context, (CompositeChunk) c, p, mutable.stringValue(), String.valueOf(mutator)));
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

        if (startVal == null || !startVal.isNatural() && !Ordinal.reservedValue(startVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + startVal + "' instead.");
        if (endVal != null && !endVal.isNatural() && !Ordinal.reservedValue(endVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + endVal + "' instead.");

        startIdx = startVal.integerValue();
        if (endVal != null)
            endIdx = endVal.integerValue();

        return new Value(ChunkUtils.putChunk(context, c.type, p, mutableString, startIdx, endIdx, mutatorString));
    }

    public static Value setValue(Value mutable, Preposition p, Value mutator) {

        switch (p) {
            case BEFORE:
                return new Value(mutator.toString() + mutable.toString());
            case INTO:
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
     * @return True if the value represents a natural number.
     */
    public boolean isNatural() {
        return isInteger() && longValue() >= 0;
    }

    /**
     * Determines if this value represents a boolean (or 'logical') value, that is, the value 'true' or 'false'.
     * @return True if this value is either 'true' or 'false'.
     */
    public boolean isBoolean() {
        return booleanValue != null || parseBoolean() != null;
    }

    /**
     * Determines if this value represents a numerical value, integer or floating point.
     * @return True if this value is a number.
     */
    public boolean isNumber() {
        return floatValue != null || parseFloat() != null;
    }

    /**
     * Determines if this value was flagged as being a quoted literal. Used when disambiguating part names and numbers.
     *
     * Note that the parser removes quotes from quoted literals appearing in script text; this flag attempts to preserve
     * whether this value originally had quotes around it.
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

    private Long parseLong() {
        try {
            longValue = Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            longValue = null;
        }

        return longValue;
    }

    private Double parseFloat() {
        try {
            floatValue = Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            floatValue = null;
        }

        return floatValue;
    }

    private Boolean parseBoolean() {
        if (value.trim().equalsIgnoreCase("true")) {
            booleanValue = true;
        } else if (value.trim().equalsIgnoreCase("false")) {
            booleanValue = false;
        } else {
            booleanValue = null;
        }

        return booleanValue;
    }

    public String stringValue() {
        return value;
    }

    /**
     * Gets this value's integer representation as an int, or 0 if this value is not an integer.
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
     * Gets this value's integer representation as a long, or 0 if this value is not an integer.
     * @return The integer representation of this value.
     */
    public long longValue() {
        if (longValue != null || parseLong() != null)
            return longValue;
        else
            return 0;
    }

    /**
     * Gets this value's floating-point representation as a double, or 0.0 if this value is not a number.
     * @return The floating-point representation of this value.
     */
    public double doubleValue() {
        if (floatValue != null || parseFloat() != null)
            return floatValue;
        else
            return 0.0;
    }

    public double doubleValueOrError(HtException error) throws HtException {
        if (isNumber()) {
            return doubleValue();
        } else {
            throw error;
        }
    }

    /**
     * Gets this value's boolean representation, or false if this value is not a logical value.
     * @return The boolean representaion of this value.
     */
    public boolean booleanValue() {
        if (booleanValue != null || parseBoolean() != null)
            return booleanValue;
        else
            return false;
    }

    /**
     *
     * @return
     * @throws HtException
     */
    public boolean checkedBooleanValue() throws HtException {
        if (isBoolean()) {
            return booleanValue();
        } else {
            throw new HtSemanticException("Expected true or false here.");
        }
    }

    public Rectangle rectangleValue(ExecutionContext context) {
        if (isRect()) {
            int left = getItemAt(context, 0).integerValue();
            int top = getItemAt(context, 1).integerValue();
            int height = getItemAt(context, 3).integerValue() - getItemAt(context, 1).integerValue();
            int width = getItemAt(context, 2).integerValue() - getItemAt(context, 0).integerValue();

            return new Rectangle(left, top, width, height);
        }

        return new Rectangle();
    }

    public Point pointValue(ExecutionContext context) {
        if (isPoint()) {
            int left = getItemAt(context, 0).integerValue();
            int top = getItemAt(context, 1).integerValue();

            return new Point(left, top);
        }

        return new Point();
    }

    /**
     * Gets a list of comma-separated items contained in this value. This function ignores the itemDelimiter HyperCard
     * property when splitting the value into items. Useful for parsing argument lists. See {@link #getItems(ExecutionContext)} for a
     * method whose behavior respects the itemDelimiter.
     *
     * @return A list of zero or
     */
    public List<Value> getListItems() {
        ArrayList<Value> items = new ArrayList<>();
        for (String thisItem : value.split(",")) {
            items.add(new Value(thisItem));
        }
        return items;
    }

    public List<Value> getItems(ExecutionContext context) {
        return getChunks(context, ChunkType.ITEM);
    }

    public Value getItemAt(ExecutionContext context, int index) {
        List<Value> items = getItems(context);
        if (items.size() > index) {
            return new Value(items.get(index));
        } else {
            return new Value();
        }
    }

    public List<Value> getLines(ExecutionContext context) {
        return getChunks(context, ChunkType.LINE);
    }

    public List<Value> getWords(ExecutionContext context) {
        return getChunks(context, ChunkType.WORD);
    }

    public List<Value> getChars(ExecutionContext context) {
        return getChunks(context, ChunkType.CHAR);
    }

    public List<Value> getChunks(ExecutionContext context, ChunkType type) {
        Matcher matcher = ChunkUtils.getRegexForChunkType(context, type).matcher(value);
        ArrayList<Value> chunks = new ArrayList<>();

        while (matcher.find()) {
            chunks.add(new Value(matcher.group()));
        }

        return chunks;
    }

    public int itemCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.ITEM, value);
    }

    public int wordCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.WORD, value);
    }

    public int charCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.CHAR, value);
    }

    public int lineCount(ExecutionContext context) {
        return ChunkUtils.getCount(context, ChunkType.LINE, value);
    }

    public Value getChunk(ExecutionContext context, Chunk c) throws HtException {

        Value startVal = null;
        Value endVal = null;

        int startIdx = 0;
        int endIdx = 0;

        if (c.start != null)
            startVal = c.start.evaluate(context);
        if (c.end != null)
            endVal = c.end.evaluate(context);

        if (startVal == null || !startVal.isNatural() && !Ordinal.reservedValue(startVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + startVal + "' instead.");
        if (endVal != null && !endVal.isNatural() && !Ordinal.reservedValue(endVal.integerValue()))
            throw new HtSemanticException("Chunk specifier requires natural integer value, but got '" + endVal + "' instead.");

        startIdx = startVal.integerValue();
        if (endVal != null)
            endIdx = endVal.integerValue();

        Value chunkValue = new Value(ChunkUtils.getChunk(context, c.type, value, startIdx, endIdx));

        // If a composite chunk; evaluate right hand of the expression first
        if (c instanceof CompositeChunk) {
            return chunkValue.getChunk(context, ((CompositeChunk) c).chunkOf);
        } else {
            return chunkValue;
        }
    }

    public boolean isEmpty() {
        return value.equals("");
    }

    public Value lessThan(Value v) {
        if (isNumber() && v.isNumber())
            return new Value(doubleValue() < v.doubleValue());
        else
            return new Value(toString().compareTo(v.toString()) < 0);
    }

    public Value greaterThan(Value v) {
        if (isNumber() && v.isNumber())
            return new Value(doubleValue() > v.doubleValue());
        else
            return new Value(toString().compareTo(v.toString()) > 0);
    }

    public Value greaterThanOrEqualTo(Value v) {
        if (isNumber() && v.isNumber())
            return new Value(doubleValue() >= v.doubleValue());
        else
            return new Value(toString().compareTo(v.toString()) >= 0);
    }

    public Value lessThanOrEqualTo(Value v) {
        if (isNumber() && v.isNumber())
            return new Value(doubleValue() <= v.doubleValue());
        else
            return new Value(toString().compareTo(v.toString()) <= 0);
    }

    public Value multiply(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + value + "' cannot be multiplied by '" + v + "'.");
        }

        try {
            if (isInteger() && v.isInteger())
                return new Value(Math.multiplyExact(longValue(), v.longValue()));
            else
                return new Value(doubleValue() * v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Overflow when trying to multiply " + stringValue() + " by " + v.stringValue() + ".");
        }
    }

    public Value divide(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + value + "' cannot be divided by " + v + '.');
        }

        try {
            return new Value(doubleValue() / v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Cannot divide " + stringValue() + " by zero.");
        }
    }

    public Value add(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + v + "' cannot be added to '" + value + "'.");
        }

        try {
            if (isInteger() && v.isInteger())
                return new Value(Math.addExact(longValue(), v.longValue()));
            else
                return new Value(doubleValue() + v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Overflow when trying to add " + stringValue() + " to " + v.stringValue() + ".");
        }
    }

    public Value subtract(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + v + "' cannot be subtracted from '" + value + "'.");
        }

        try {
            if (isInteger() && v.isInteger())
                return new Value(Math.subtractExact(longValue(), v.longValue()));
            else
                return new Value(doubleValue() - v.doubleValue());
        } catch (ArithmeticException e) {
            throw new HtSemanticException("Overflow when trying to subtract " + v.stringValue() + " from " + stringValue() + ".");
        }
    }

    public Value exponentiate(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + value + "' cannot be raised to the power of '" + v + "'.");
        }

        return new Value(Math.pow(doubleValue(), v.doubleValue()));
    }

    public Value mod(Value v) throws HtSemanticException {
        if (!isNumber() || !v.isNumber()) {
            throw new HtSemanticException("The value '" + v + "' cannot be mod by '" + value + "'.");
        }

        if (isInteger() && v.isInteger())
            return new Value(longValue() % v.longValue());
        else
            return new Value(doubleValue() % v.doubleValue());
    }

    public Value not() throws HtSemanticException {
        if (!isBoolean())
            throw new HtSemanticException("Expected a logical value here, but got '" + value + "'.");

        return new Value(!booleanValue());
    }

    public Value negate() throws HtSemanticException {
        if (isInteger())
            return new Value(longValue() * -1);
        else if (isNumber())
            return new Value(doubleValue() * -1);
        else {
            throw new HtSemanticException("Expected a number here, but got '" + value + "'.");
        }
    }

    public Value and(Value v) throws HtSemanticException {

        // Allow for short circuit evaluation
        if (!isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + value + "'.");
        }

        if (!booleanValue) {
            return new Value(false);
        }

        if (!v.isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + v + "'.");
        }

        return new Value(booleanValue() && v.booleanValue());
    }

    public Value or(Value v) throws HtSemanticException {

        if (!isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + value + "'.");
        }

        if (booleanValue) {
            return new Value(true);
        }

        if (!v.isBoolean()) {
            throw new HtSemanticException("Expected a logical value here, but got '" + v + "'.");
        }

        return new Value(booleanValue() || v.booleanValue());
    }

    public Value concat(Value v) {
        return new Value(value + v.toString());
    }

    public Value within(ExecutionContext context, Value v) throws HtSemanticException {
        if (!isPoint() || !v.isRect()) {
            throw new HtSemanticException("Cannot determine if '" + value + "' is within the bounds of '" + v.stringValue() + "'.");
        }

        return new Value(v.rectangleValue(context).contains(pointValue(context)));
    }

    public Value trunc() throws HtSemanticException {
        if (isInteger()) {
            return new Value(integerValue());
        } else if (isNumber()) {
            return new Value((int) doubleValue());
        }

        throw new HtSemanticException("Cannot trunc the value '" + stringValue() + "' because it is not a number.");
    }

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
                throw new HtSemanticException("Bug! Unimplemented type comparison for: " + type);
        }
    }

    public boolean contains(Value v) {
        return value.toLowerCase().contains(v.stringValue().toLowerCase());
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value otherValue = (Value) o;

        if (isBoolean() && otherValue.isBoolean()) {
            return this.booleanValue() == otherValue.booleanValue();
        } else if (isInteger() && otherValue.isInteger()) {
            // Weird special case: "" is a valid number (zero), but is not equal to 0
            // Thus, '2 * "" = 0', but '0 <> ""' -- don't believe me, try it in HyperCard!
            if (value.equals("") || otherValue.value.equals("")) {
                return value.equals(otherValue.value);
            } else {
                return this.integerValue() == otherValue.integerValue();
            }
        } else if ((isInteger() || isNumber()) && (otherValue.isInteger() || otherValue.isNumber())) {
            return this.doubleValue() == otherValue.doubleValue();
        } else {
            return this.stringValue().equalsIgnoreCase(otherValue.stringValue());
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(Value o) {
        if (this.isInteger() && o.isInteger()) {
            return Integer.compare(this.integerValue(), o.integerValue());
        } else if (this.isNumber() && o.isNumber()) {
            return Double.compare(this.doubleValue(), o.doubleValue());
        } else {
            return this.stringValue().toLowerCase().trim().compareTo(o.stringValue().toLowerCase().trim());
        }
    }

    @Override
    public int compareTo(Value to, SortStyle style) {
        switch (style) {
            case INTERNATIONAL:
            case TEXT:
                return this.stringValue().compareTo(to.stringValue());
            case NUMERIC:
                return Double.compare(this.doubleValue(), to.doubleValue());
            case DATE_TIME:
                Date thisDate = DateUtils.dateOf(this);
                if (thisDate == null) {
                    return 1;
                }

                Date toDateTime = DateUtils.dateOf(to);
                if (toDateTime == null) {
                    return -1;
                }

                return thisDate.compareTo(toDateTime);
        }

        throw new IllegalArgumentException("Bug! Unimplemented comparison style.");
    }
}
