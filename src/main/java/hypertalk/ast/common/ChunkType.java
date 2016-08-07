/**
 * ChunkType.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of chunk expression types
 */

package hypertalk.ast.common;

public enum ChunkType
{
	CHAR, WORD, ITEM, LINE, CHARRANGE, WORDRANGE, LINERANGE, ITEMRANGE;

	public boolean isRange() {
		return this == CHARRANGE || this == WORDRANGE || this == LINERANGE || this == ITEMRANGE;
	}
}
