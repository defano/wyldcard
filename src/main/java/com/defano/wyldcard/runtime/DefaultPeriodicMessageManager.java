package com.defano.wyldcard.runtime;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Singleton;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mechanism for sending periodic messages to parts. Periodic messages include 'idle' and 'mouseWithin'.
 */
@Singleton
public class DefaultPeriodicMessageManager implements PeriodicMessageManager {

    private final static int IDLE_PERIOD_MS = 200;              // Frequency that periodic messages are sent
    private final static int IDLE_DEFERRAL_CYCLES = 50;         // Number of cycles we defer if error is encountered

    private int deferCycles = 0;
    private final ScheduledExecutorService idleTimeExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("periodic-executor-%d").build());
    private final Vector<PartModel> withinParts = new Vector<>();

    @Override
    public void start() {
        idleTimeExecutor.scheduleAtFixedRate(this, 0, IDLE_PERIOD_MS, TimeUnit.MILLISECONDS);

        // Stop tracking 'within' when card goes away
        WyldCard.getInstance().getFocusedStackProvider().subscribe(stackPart -> stackPart.addNavigationObserver(DefaultPeriodicMessageManager.this));

        // Stop tracking 'within' when not in browse mode
        WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolMode -> {
            if (toolMode != ToolMode.BROWSE) {
                withinParts.clear();
            }
        });
    }

    @Override
    public void addWithin(PartModel part) {
        withinParts.add(part);
    }

    @Override
    public void removeWithin(PartModel part) {
        withinParts.remove(part);
    }

    @Override
    public void run() {
        try {
            // Send 'within' message to any parts whose bounds the mouse is within
            send(SystemMessage.MOUSE_WITHIN, withinParts.toArray(new PartModel[] {}));

            // Send 'idle' message to card if no other scripts are pending
            if (Interpreter.getPendingScriptCount() == 0) {
                WyldCard.getInstance().getWyldCardProperties().resetProperties();
                DebugContext.getInstance().resume();
                send(SystemMessage.IDLE, new ExecutionContext().getCurrentCard().getCardModel());
            }

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
            if (WyldCard.getInstance().getToolsManager().getToolMode() == ToolMode.BROWSE && deferCycles < 1) {
                model.receiveMessage(new ExecutionContext(model), message.messageName, new ListExp(null), (command, wasTrapped, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        deferCycles = IDLE_DEFERRAL_CYCLES;
                    }
                });
            }
        }
    }
}
