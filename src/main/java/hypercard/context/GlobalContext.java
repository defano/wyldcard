/*
 * GlobalContext.java
 * @author matt.defano@gmail.com
 * 
 * This class provides the global runtime context for HyperCard; implemented as
 * a singleton, the class maintains a list of active parts, global variables and
 * provides a stack of local variables used during execution. 
 */

package hypercard.context;

import hypercard.parts.CardPart;
import hypercard.parts.Part;
import hypercard.parts.PartException;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.*;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import java.io.Serializable;
import java.util.Stack;
import java.util.Vector;

public class GlobalContext implements Serializable {
private static final long serialVersionUID = -6393229377296213396L;

	private static GlobalContext _instance;
	
	private SymbolTable globals;	        
	private Stack<LocalContext> stack;
	private LocalContext locals;
	private PartSpecifier me;
	
	private boolean noMessages = false;
	
	private GlobalContext () {
		globals = new SymbolTable();
		stack = new Stack<LocalContext>();
		locals = new LocalContext(new SymbolTable(), new Vector<String>(), new Value());		
	}
	
	public static GlobalContext getContext () {
		if (_instance == null)
			_instance = new GlobalContext();
		
		return _instance;
	}

	public boolean noMessages () {
		return noMessages;
	}
	
	public void setNoMessages (boolean noMessages) {
		this.noMessages = noMessages;
	}
	
	public void setMe (PartSpecifier me) {
		this.me = me;
	}
	
	public PartSpecifier getMe () {
		return me;
	}
	
	public CardPart getCard () {
		return RuntimeEnv.getRuntimeEnv().getCard();
	}
	
    public void newLocalContext () {
		locals = new LocalContext(new SymbolTable(), new Vector<String>(), new Value());
    }
    
	public void pushContext () {
		stack.push(locals);
		locals = new LocalContext(new SymbolTable(), new Vector<String>(), new Value());
	}
	
	public void popContext () {
		locals = stack.pop();
	}
	
	public void setReturnValue (Value returnValue) {
		locals.setReturnValue(returnValue);
	}
	
	public Value getReturnValue () {
		return locals.getReturnValue();
	}
	
	public void defineGlobal (String id) {
		if (!globals.exists(id))
			globals.put(id, new Value());
        
        locals.globalInScope(id);
	}
	
	public void sendMessage (PartSpecifier ps, String message) 
	throws HtSemanticException, PartException
	{
		getCard().getPart(ps).sendMessage(message);
	}
	
	public void put (Value mutator, Preposition p, DestinationMsgBox d) throws HtSemanticException {

		Chunk chunk = d.chunk();
		Value destValue = new Value(RuntimeEnv.getRuntimeEnv().getMsgBoxText());
		
		// Operating on a chunk of the existing value
		if (chunk != null)
			destValue = Value.setChunk(destValue, p, chunk, mutator);
		else
			destValue = Value.setValue(destValue, p, mutator);
		
		RuntimeEnv.getRuntimeEnv().setMsgBoxText(destValue);
		setIt(destValue);
	}
	
	public void put (Value mutator, Preposition p, DestinationPart d) throws HtSemanticException {
		
		try {
			Part destPart = get(d.part().evaluateAsSpecifier());
			Value destValue = destPart.getValue();
			Chunk chunk = d.chunk();
			
			// Operating on a chunk of the existing value
			if (chunk != null)
				destValue = Value.setChunk(destValue, p, chunk, mutator);
			else
				destValue = Value.setValue(destValue, p, mutator);
		
			destPart.setValue(destValue);
			setIt(destValue);
			
		} catch (PartException e) {
			throw new HtSemanticException(e.getMessage());
		}
	}
	
	public void put (Value mutator, Preposition p, DestinationVariable d) throws HtSemanticException {
		String symbol = d.symbol();
		Chunk chunk = d.chunk();
		Value mutable = get(symbol);
		
		// Operating on a chunk of the existing value
		if (chunk != null)
			mutable = Value.setChunk(mutable, p, chunk, mutator);
		else
			mutable = Value.setValue(mutable, p, mutator);
		
		set(symbol, mutable);
		setIt(mutable);
	}
	
	public void set (String symbol, Value v) {
		
		if (globals.exists(symbol) && locals.isGlobalInScope(symbol))
			globals.put(symbol, v);
		else
			locals.symbols.put(symbol, v);
	}
	
	public Value get (String symbol) {
		Value value;

		if (globals.exists(symbol) && locals.isGlobalInScope(symbol))
			value = globals.get(symbol);
		else if (locals.symbols.exists(symbol))
			value = locals.symbols.get(symbol);
		
		// Allow the user to refer to literals without quotation marks
		else 
			value = new Value(symbol);
		
		return value;
	}
	
	public Part get (PartSpecifier ps) throws PartException {
		return getCard().getPart(ps);
	}
	
	public Value get (String property, PartSpecifier ps) throws NoSuchPropertyException, PartException {
		return getCard().getPart(ps).getProperty(property);
	}

	public void set (String property, PartSpecifier ps, Value value) 
	throws NoSuchPropertyException, PropertyPermissionException, PartException, HtSemanticException
	{
		getCard().getPart(ps).setProperty(property, value);
	}
	
	public void setIt (Object value) {
		globals.put("it", new Value(value.toString()));
	}
	
	public Value getIt () {
		return globals.get("it");
	}
	
}
