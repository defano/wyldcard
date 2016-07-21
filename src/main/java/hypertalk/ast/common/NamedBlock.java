/**
 * NamedBlock.java
 * @author matt.defano@gmail.com
 * 
 * Representation of a named block of statements, such as a function
 * definition or event handler. 
 */

package hypertalk.ast.common;

import hypertalk.ast.statements.StatementList;

public class NamedBlock {

	public final String name;
	public final StatementList body;
	
	public NamedBlock (String name, StatementList body) {
		this.name = name;
		this.body = body;
	}
}
