package com.defano.wyldcard.runtime.executor.task;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.preemption.PassPreemption;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.preemption.TerminateHandlerPreemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.debug.message.HandlerInvocation;
import com.defano.wyldcard.debug.message.HandlerInvocationCache;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;

public class MessageHandlerExecutionTask implements HandlerExecutionTask {

    private final ExecutionContext context;
    private final ASTNode callingNode;
    private final NamedBlock handler;
    private final PartSpecifier me;
    private final Message message;

    public MessageHandlerExecutionTask(ExecutionContext context, ASTNode callingNode, PartSpecifier me, NamedBlock handler, Message message) {
        this.context = context;
        this.callingNode = callingNode;
        this.handler = handler;
        this.me = me;
        this.message = message;
    }

    @Override
    public Boolean call() throws HtException {
        boolean trapped = true;

        List<Value> arguments = message.getArguments(context);

        HandlerInvocationCache.getInstance().notifyMessageHandled(new HandlerInvocation(Thread.currentThread().getName(), handler.name, arguments, me, context.getTarget() == null, context.getStackDepth(), true));

        // Push a new context
        context.pushStackFrame(callingNode, handler.name, me, arguments);

        // Target refers to the part first receiving the message
        if (context.getTarget() == null) {
            context.setTarget(me);
        }

        // Bind argument values to parameter variables in this context
        for (int index = 0; index < handler.parameters.list.size(); index++) {
            String theParam = handler.parameters.list.get(index);

            // Handlers may be invoked with missing arguments; assume empty for missing args
            Value theArg = index >= arguments.size() ? new Value() : arguments.get(index);
            context.setVariable(theParam, theArg);
        }

        // Execute handler
        try {
            handler.statements.execute(context);
            context.popStackFrame();
        }

        // Script invoked 'pass {handlerName}' command; check semantics
        catch (PassPreemption e) {
            trapped = false;
            context.popStackFrame();
        }

        // Script invoked 'exit {handlerName}' command; check semantics
        catch (TerminateHandlerPreemption e) {
            context.popStackFrame();
        }

        // Script invoked some other (illegal) form of exit (like 'exit repeat')
        catch (Preemption e) {
            throw new HtSemanticException("Cannot exit from here.");
        }

        return trapped;
    }
}
