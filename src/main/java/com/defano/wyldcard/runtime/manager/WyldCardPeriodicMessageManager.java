package com.defano.wyldcard.runtime.manager;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.part.button.HyperCardButton;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.card.CardPart;
import com.defano.wyldcard.part.field.styles.HyperCardTextField;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.executor.ScriptExecutor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mechanism for sending periodic messages to parts. Periodic messages include 'idle' and 'mouseWithin'.
 */
@Singleton
public class WyldCardPeriodicMessageManager implements PeriodicMessageManager {

    private static final int IDLE_PERIOD_MS = 200;              // Frequency that periodic messages are sent
    private static final int IDLE_DEFERRAL_CYCLES = 50;         // Number of cycles we defer if error is encountered

    private final ScheduledExecutorService idleTimeExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("periodic-executor-%d").build());
    private final ArrayList<PartModel> withinParts = new ArrayList<>();
    private final Set<IdleObserver> idleObservers = new HashSet<>();
    private int deferCycles = 0;

    @Override
    public void start() {
        idleTimeExecutor.scheduleAtFixedRate(this, 0, IDLE_PERIOD_MS, TimeUnit.MILLISECONDS);

        // Stop tracking 'within' when card goes away
        WyldCard.getInstance().getStackManager().getFocusedStackProvider().subscribe(stackPart -> stackPart.addNavigationObserver(WyldCardPeriodicMessageManager.this));

        // Stop tracking 'within' when not in browse mode
        WyldCard.getInstance().getPaintManager().getToolModeProvider().subscribe(toolMode -> {
            if (toolMode != ToolMode.BROWSE) {
                withinParts.clear();
            }
        });
    }

    @Override
    public void addIdleObserver(IdleObserver observer) {
        idleObservers.add(observer);
    }

    @Override
    public void run() {
        try {

            // Send 'idle' message to card if no other scripts are pending
            if (ScriptExecutor.getPendingScriptCount() == 0) {
                // Notify debugger that system is idle
                DebugContext.getInstance().resume();

                // Notify other listeners
                fireIdleListeners();

                // Notify card that system is idle
                send(SystemMessage.IDLE, new ExecutionContext().getCurrentCard().getPartModel());
            }

            // Send 'within' message to any parts whose bounds the mouse is within
            send(SystemMessage.MOUSE_WITHIN, findPartsUnderMouse());

            if (deferCycles > 0) {
                --deferCycles;
            }

        } catch (Exception e) {
            // Nothing to do
        }
    }

    @Override
    public void onDisplayedCardChanged(CardModel prevCard, CardPart nextCard) {
        withinParts.clear();
    }

    private void fireIdleListeners() {
        for (IdleObserver thisObserver : idleObservers.toArray(new IdleObserver[0])) {
            thisObserver.onIdle();
        }
    }

    private void send(SystemMessage message, PartModel... models) {
        for (PartModel model : models) {
            if (WyldCard.getInstance().getPaintManager().getToolMode() == ToolMode.BROWSE && deferCycles < 1) {
                model.receiveMessage(new ExecutionContext(model), null, message, (command, wasTrapped, error) -> {
                    if (error != null) {
                        error.getStackTrace();
                        deferCycles = IDLE_DEFERRAL_CYCLES;
                    }
                });
            }
        }
    }

    private PartModel[] findPartsUnderMouse() {
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();

        if (WyldCard.getInstance().getWindowManager().getFocusedStackWindow() != null) {
            CardPart theCard = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getDisplayedCard();
            SwingUtilities.convertPointFromScreen(mouseLoc, theCard);

            return partsAt(mouseLoc, theCard).toArray(new PartModel[0]);
        }

        return new PartModel[0];
    }

    private ArrayList<PartModel> partsAt(Point p, Component c) {
        ArrayList<PartModel> parts = new ArrayList<>();

        if (c.getBounds().contains(p)) {

            if (c instanceof HyperCardButton) {
                parts.add(((HyperCardButton)c).getToolEditablePart().getPartModel());
            }

            if (c instanceof HyperCardTextField) {
                parts.add(((HyperCardTextField)c).getToolEditablePart().getPartModel());
            }

            if (c instanceof Container) {
                for (Component child : ((Container) c).getComponents()) {
                    parts.addAll(partsAt(p, child));
                }
            }
        }

        return parts;
    }

}
