/**
 * ChunkUtils.java
 * @author matt.defano@gmail.com
 * 
 * A library of static methods used in performing chunked operations.
 */

package hypertalk.utils;

import hypertalk.ast.common.ChunkType;
import hypertalk.ast.common.Ordinal;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.Preposition;
import java.io.Serializable;

public class ChunkUtils implements Serializable {
private static final long serialVersionUID = -2055882808268916413L;

	public static String setChar (Preposition p, String value, int charIdx, String replacement) {
		switch (p) {
		case BEFORE: return insert(value, ChunkType.CHAR, charIdx + 1, replacement);
		case INTO: return replace(value, ChunkType.CHAR, charIdx + 1, charIdx + 1, replacement);
		case AFTER: return insert(value, ChunkType.CHAR, charIdx + 2, replacement);
		default: throw new RuntimeException("Unhandled preposition in ChunkUtils.setChar()");
		}		
	}
	
	public static String setCharRange (Preposition p, String value, int start, int end, String replacement) {
		switch (p) {
		case BEFORE: return replace(value, ChunkType.CHAR, start, end + 1, replacement);
		case INTO: return replace(value, ChunkType.CHAR, start + 1, end + 1, replacement);
		case AFTER: return replace(value, ChunkType.CHAR, start + 1, end + 2, replacement);
		default: throw new RuntimeException("Unhandled preposition in ChunkUtils.setCharRange()");
		}
	}
	
	public static String setWord (Preposition p, String value, int wordIdx, String replacement) {
		switch (p) {
		case BEFORE: return insert(value, ChunkType.WORD, wordIdx - 1, replacement);
		case INTO: return replace(value, ChunkType.WORD, wordIdx, wordIdx, replacement);
		case AFTER: return insert(value, ChunkType.WORD, wordIdx + 1, replacement);
		default: throw new RuntimeException("Unhandled preposition in ChunkUtils.setWord()");
		}
	}

	public static String setWordRange (Preposition p, String value, int start, int end, String replacement) {
		switch (p) {
		case BEFORE: return replace(value, ChunkType.WORD, start - 1, end, replacement);
		case INTO: return replace(value, ChunkType.WORD, start, end, replacement);
		case AFTER: return replace(value, ChunkType.WORD, start, end + 1, replacement);
		default: throw new RuntimeException("Unhandled preposition in ChunkUtils.setWordRange()");
		}		
	}
	
	public static String setItem (Preposition p, Value value, int itemIdx, String replacement) {
		switch(p) {
		case BEFORE: return insert(value.stringValue(), ChunkType.ITEM, itemIdx - 1, replacement);
		case INTO: return replace(value.stringValue(), ChunkType.ITEM, itemIdx, itemIdx, replacement);
		case AFTER: return insert(value.stringValue(), ChunkType.ITEM, itemIdx + 1, replacement);
		default: throw new RuntimeException("Unhandeled preposition in ChunkUtils.setItem");
		}
	}
	
	public static String setItemRange (Preposition p, Value value, int start, int end, String replacement) {
		switch(p) {
		case BEFORE: return replace(value.stringValue(), ChunkType.ITEM, start - 1, end, replacement);
		case INTO: return replace(value.stringValue(), ChunkType.ITEM, start, end, replacement);
		case AFTER: return replace(value.stringValue(), ChunkType.ITEM, start, end + 1, replacement);
		default: throw new RuntimeException("Unhandeled preposition in ChunkUtils.setItemRange");
		}
	}
	
	public static String setLine (Preposition p, String value, int lineIdx, String replacement) {
		switch (p) {
		case BEFORE: return insert(value, ChunkType.LINE, lineIdx - 1, replacement);
		case INTO: return replace(value, ChunkType.LINE, lineIdx, lineIdx, replacement);
		case AFTER: return insert(value, ChunkType.LINE, lineIdx + 1, replacement);
		default: throw new RuntimeException("Unhandled preposition in ChunkUtils.setLine()");
		}
	}

	public static String setLineRange (Preposition p, String value, int start, int end, String replacement) {
		switch (p) {
		case BEFORE: return replace(value, ChunkType.LINE, start - 1, end, replacement);
		case INTO: return replace(value, ChunkType.LINE, start, end, replacement);
		case AFTER: return replace(value, ChunkType.LINE, start, end + 1, replacement);
		default: throw new RuntimeException("Unhandled preposition in ChunkUtils.setLineRange()");
		}		
	}	
	
	public static String getChar (String value, int charIdx) {
		return get(value, ChunkType.CHAR, charIdx + 1);
	}
	
	public static String getCharRange (String value, int start, int end) {
		return get(value, ChunkType.CHAR, start + 1, end + 1);
	}
	
	public static String getWord (String value, int wordIdx) {
		return get(value, ChunkType.WORD, wordIdx);
	}
	
	public static String getWordRange (String value, int start, int end) {
		return get(value, ChunkType.WORD, start, end);
	}
	
	public static String getItem (String value, int itemIdx) {
		return get(value, ChunkType.ITEM, itemIdx);
	}

	public static String getItemRange (String value, int start, int end) {
		return get(value, ChunkType.ITEM, start, end);
	}
	
	public static String getLine (String value, int lineIdx) {
		return get(value, ChunkType.LINE, lineIdx);
	}
	
	public static String getLineRange (String value, int start, int end) {
		return get(value, ChunkType.LINE, start, end);
	}	
	
	private static String getDelimiterRegex (ChunkType delim) {
		switch (delim) {
		case CHAR: return "";
		case WORD: return "\\s";
		case LINE: return "\n";
		case ITEM: return Value.ITEM_DELIMITER;
		default: throw new RuntimeException("Unhandeled delimiter");
		}
	}
	
	private static String getDelimiterSeperator (ChunkType delim) {
		switch (delim) {
		case CHAR: return "";
		case WORD: return " ";
		case LINE: return "\n";
		case ITEM: return Value.ITEM_DELIMITER;
		default: throw new RuntimeException("Unhandeled delimiter");
		}
	}	
	
	private static int getTokenIndex (int index, int tokenCount) {

		// User means second to last ("before the last xxx of yyy")
		if (index == Ordinal.LAST.intValue() - 1)
			return tokenCount - 1;
		
		// User means the middle item ("the middle xxx of yyy")
		else if (index == Ordinal.MIDDLE.intValue() ||
				 index == 0)
			return tokenCount / 2;
		
		// User means "before the first xxx of yyy"
		else if (index < 0)
			return 0;
		
		// User means "after the last xxx of yyy"
		else if (index > tokenCount)
			return tokenCount;
		
		// Default case: Subtract one from index to match user's count 
		// from 1 perspective
		else
			return index - 1;
	}
	
	private static String replace (String value, ChunkType delimiter, int start, int end, String replacement) {

		String delimRegex = getDelimiterRegex(delimiter);
		String delimString = getDelimiterSeperator(delimiter);
		String[] tokens = value.split(delimRegex);
		
		start = getTokenIndex(start, tokens.length);
		end = getTokenIndex(end, tokens.length);

		int tokenIdx = 0;
		StringBuffer result = new StringBuffer();
		
		for (String token : tokens) {
			if (tokenIdx == start)
				result.append(replacement + delimString);
			
			if (tokenIdx < start || tokenIdx > end)
				result.append(token + delimString);	
			
			tokenIdx++;
		}
		
		return result.toString();		
	}
	
	private static String insert (String value, ChunkType delimiter, int index, String replacement) {

		String delimRegex = getDelimiterRegex(delimiter);
		String delimString = getDelimiterSeperator(delimiter);
		String[] tokens = value.split(delimRegex);
		
		index = getTokenIndex(index, tokens.length);
		int tokenIdx = 0;
		StringBuffer result = new StringBuffer();
		
		if (index < 0)
			result.append(replacement + delimString);
		
		for (String token : tokens) {
			if (tokenIdx == index) 
				result.append(replacement + delimString);
			
			result.append(token + delimString);			
			tokenIdx++;
		}

		if (index >= tokens.length)
			result.append(replacement);
			
		return result.toString();
	}
	
	private static String get (String value, ChunkType delimiter, int index) {
		String[] tokens = value.split(getDelimiterRegex(delimiter));
		
		int tokenIdx = getTokenIndex(index, tokens.length);
		if (tokenIdx >= tokens.length)
			return tokens[tokens.length - 1];
			              
		return tokens[tokenIdx];
	}
	
	private static String get (String value, ChunkType delimiter, int start, int end) {
		String[] tokens = value.split(getDelimiterRegex(delimiter));
		StringBuffer buffer = new StringBuffer();
		
		for (int index = start; index < end; index++) {
			buffer.append(tokens[getTokenIndex(index, tokens.length)]);
		}
		
		return buffer.toString();
	}	
}
