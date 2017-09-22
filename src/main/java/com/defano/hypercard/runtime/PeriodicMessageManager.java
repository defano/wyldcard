package com.defano.hypercard.runtime;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.SystemMessage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mechanism for sending periodic messages to parts. Periodic messages include 'idle' and 'mouseWithin'.
 */
public class PeriodicMessageManager implements Runnable {

    private final static int IDLE_PERIOD_MS = 200;              // Frequency that periodic messages are sent
    private final static int IDLE_DEFERRAL_CYCLES = 50;         // Number of cycles we defer if error is encountered

    private final static PeriodicMessageManager instance = new PeriodicMessageManager();

    private int deferCycles = 0;
    private final ScheduledExecutorService idleTimeExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("periodic-executor-%d").build());
    private final Vector<PartModel> withinParts = new Vector<>();

    private PeriodicMessageManager() {}

    public static PeriodicMessageManager getInstance() {
        return instance;
    }

    public void start() {
        idleTimeExecutor.scheduleAtFixedRate(this, 0, IDLE_PERIOD_MS, TimeUnit.MILLISECONDS);
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
            if (Interpreter.getPendingScriptCount() == 0) {
                send(SystemMessage.IDLE, ExecutionContext.getContext().getCurrentCard().getCardModel());
            }

            send(SystemMessage.MOUSE_WITHIN, withinParts.toArray(new PartModel[] {}));

            if (deferCycles > 0) {
                --deferCycles;
            }

        } catch (Exception e) {
            // Nothing to do
        }
    }

    private void send(SystemMessage message, PartModel... models) {
        for (PartModel model : models) {
            if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE && deferCycles < 1) {
                model.receiveMessage(message.messageName, new ExpressionList(), (command, wasTrapped, error) -> {
                    if (error != null) {
                        deferCycles = IDLE_DEFERRAL_CYCLES;
                    }
                });
            }
        }
    }
}
