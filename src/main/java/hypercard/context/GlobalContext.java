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

import java.util.Stack;
import java.util.Vector;

public class GlobalContext {

    private static GlobalContext instance = new GlobalContext();
    
    private SymbolTable globals;

    /*
     * In this implementation, when HyperCard sends a system-defined message to a part
     * (i.e., "mouseEnter") the reaction to that message exists in its own thread; therefore
     * the entire local context needs to be specific to each thread.
     */
    private ThreadLocal<Stack<Frame>> stack = new ThreadLocal<>();
    private ThreadLocal<Frame> frame = new ThreadLocal<>();
    private ThreadLocal<PartSpecifier> me = new ThreadLocal<>();

    private GlobalContext () {
        globals = new SymbolTable();
    }

    public static GlobalContext getContext () {
        return instance;
    }

    public void newLocalContext () {
        setFrame(new Frame(new SymbolTable(), new Vector<>(), new Value()));
    }

    public void pushContext () {
        getStack().push(getFrame());
        setFrame(new Frame(new SymbolTable(), new Vector<>(), new Value()));
    }

    public void popContext () {
        setFrame(getStack().pop());
    }

    public void setReturnValue (Value returnValue) {
        getFrame().setReturnValue(returnValue);
    }

    public Value getReturnValue () {
        return getFrame().getReturnValue();
    }

    public void defineGlobal (String id) {
        if (!globals.exists(id))
            globals.put(id, new Value());

        getFrame().globalInScope(id);
    }

    public void set (String symbol, Value v) {
        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            globals.put(symbol, v);
        else
            getFrame().symbols.put(symbol, v);
    }

    public Value get (String symbol) {
        Value value;

        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            value = globals.get(symbol);
        else if (getFrame().symbols.exists(symbol))
            value = getFrame().symbols.get(symbol);

            // Allow the user to refer to literals without quotation marks
        else
            value = new Value(symbol);

        return value;
    }

    public CardPart getCard () {
        return RuntimeEnv.getRuntimeEnv().getCard();
    }

    public void sendMessage (PartSpecifier ps, String message) 
    throws HtSemanticException, PartException
    {
        getCard().getPart(ps).sendMessage(message);
    }
    
    public void put (Value mutator, Preposition p, ContainerMsgBox d) throws HtSemanticException {

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
    
    public void put (Value mutator, Preposition p, ContainerPart d) throws HtSemanticException {
        
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
    
    public void put (Value mutator, Preposition p, ContainerVariable d) throws HtSemanticException {
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

    public void setMe (PartSpecifier me) {
        this.me.set(me);
    }

    public PartSpecifier getMe () {
        return this.me.get();
    }

    private Stack<Frame> getStack() {
        if (this.stack.get() == null) {
            this.stack.set(new Stack<>());
        }

        return this.stack.get();
    }

    private void setFrame(Frame frame) {
        this.frame.set(frame);
    }

    private Frame getFrame() {
        return frame.get();
    }
}
