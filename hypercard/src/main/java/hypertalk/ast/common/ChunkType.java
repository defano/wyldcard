/**
 * ChunkType.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of chunk expression types
 */

package hypertalk.ast.common;

import java.io.Serializable;

public enum ChunkType implements Serializable
{
	CHAR, WORD, ITEM, LINE, CHARRANGE, WORDRANGE, LINERANGE, ITEMRANGE;
}
