package com.defano.hypercard.runtime;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.stack.StackObservable;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.ExpressionList;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mechanism for sending periodic messages to parts. Periodic messages include 'idle' and 'mouseWithin'.
 */
public class PeriodicMessageManager implements Runnable, StackObservable {

    private final static int IDLE_PERIOD_MS = 200;              // Frequency that periodic messages are sent
    private final static int IDLE_DEFERRAL_CYCLES = 50;         // Number of cycles we defer if error is encountered

    private final static PeriodicMessageManager instance = new PeriodicMessageManager();

    private int deferCycles = 0;
    private final ScheduledExecutorService idleTimeExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("periodic-executor-%d").build());
    private final Vector<PartModel> withinParts = new Vector<>();

    private PeriodicMessageManager() {

        // Stop tracking 'within' when not in browse mode
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> {
            if (arg != ToolMode.BROWSE) {
                withinParts.clear();
            }
        });

    }

    public static PeriodicMessageManager getInstance() {
        return instance;
    }

    public void start() {
        idleTimeExecutor.scheduleAtFixedRate(this, 0, IDLE_PERIOD_MS, TimeUnit.MILLISECONDS);

        // Stop tracking 'within' when card goes away
        HyperCard.getInstance().getStack().addObserver(this);
    }

    public void addWithin(PartModel part) {
        withinParts.add(part);
    }

    public void removeWithin(PartModel part) {
        withinParts.remove(part);
    }

    @Override
    public void run() {
        try {
            // Send 'idle' message to card if no other scripts are pending
            if (Interpreter.getPendingScriptCount() == 0) {
                ExecutionContext.getContext().getGlobalProperties().resetProperties();
                send(SystemMessage.IDLE, ExecutionContext.getContext().getCurrentCard().getCardModel());
            }

            // Send 'within' message to any parts whose bounds the mouse is within
            send(SystemMessage.MOUSE_WITHIN, withinParts.toArray(new PartModel[] {}));

            if (deferCycles > 0) {
                --deferCycles;
            }

        } catch (Exception e) {
            // Nothing to do
        }
    }

    @Override
    public void onCardClosed(CardPart oldCard) {
        withinParts.clear();
    }

    private void send(SystemMessage message, PartModel... models) {
        for (PartModel model : models) {
            if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE && deferCycles < 1) {
                model.receiveMessage(message.messageName, new ExpressionList(), (command, wasTrapped, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        deferCycles = IDLE_DEFERRAL_CYCLES;
                    }
                });
            }
        }
    }
}
