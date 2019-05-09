package com.defano.wyldcard.menubar.dispatcher;

import com.defano.wyldcard.message.EvaluatedMessage;
import com.defano.wyldcard.message.MessageHandler;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.HashMap;

public class MenuMessageDispatcher implements MessageHandler {

    private final static MenuMessageDispatcher instance = new MenuMessageDispatcher();
    private final HashMap<String, MenuMessageHandler> handlers = new HashMap<>();

    private MenuMessageDispatcher() {}

    public static MenuMessageDispatcher getInstance() {
        return instance;
    }

    @Override
    public boolean test(EvaluatedMessage m) {
        return m.getMessageName().equalsIgnoreCase(SystemMessage.DO_MENU.messageName) &&
                m.getArguments().size() == 1 &&
                handlers.containsKey(m.getArguments().get(0).toString().toLowerCase());
    }

    @Override
    public void handleMessage(ExecutionContext context, EvaluatedMessage m) {
        handlers.get(m.getArguments().get(0).toString().toLowerCase()).handleMessage(context, m);
    }

    public void addHandler(MenuMessageHandler handler) {
        handlers.put(handler.getMenuItem().toLowerCase(), handler);
    }
}
