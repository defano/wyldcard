/**
 * RuntimeEnv.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package hypercard.runtime;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import hypercard.context.GlobalContext;
import hypercard.gui.menu.HyperCardMenuBar;
import hypercard.gui.util.ModifierKeyListener;
import hypercard.gui.util.MouseListener;
import hypercard.gui.window.MessageWindow;
import hypercard.gui.window.PaintToolsPalette;
import hypercard.gui.window.StackWindow;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModel;
import hypercard.parts.model.StackModelObserver;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.StatementList;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RuntimeEnv implements StackModelObserver {

    private static RuntimeEnv _instance;

    private StackWindow stackWindow;
    private MessageWindow messageWindow;
    private PaintToolsPalette paintToolsPalette;

    private StackModel stack;

    private ExecutorService scriptExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());
    private ExecutorService messageBoxExecutor = Executors.newSingleThreadExecutor();

    public static void main(String argv[]) {
        // Display the frame's menu as the Mac OS menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true" );

        _instance = new RuntimeEnv();
    }

    private RuntimeEnv() {
        /*
         * Use this operating systems look and feel for our user interface. If
         * this causes an exception, just ignore it (it's not the end of the
         * world if we can't use the native look anyway).
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to set the UI look and feel");
        }

        stack = StackModel.newStack("Untitled");
        stack.addObserver(this);

        // Create the main window, center it on the screen and display it
        stackWindow = new StackWindow();
        JFrame stackFrame = WindowBuilder.make(stackWindow)
                .withTitle("HyperCard")
                .resizeable(true)
                .quitOnClose()
                .withMenuBar(HyperCardMenuBar.instance)
                .withModel(stack.getCurrentCard())
                .build();

        messageWindow = new MessageWindow();
        WindowBuilder.make(messageWindow)
                .withTitle("Message Box")
                .resizeable(false)
                .withLocationUnderneath(stackFrame)
                .withMenuBar(HyperCardMenuBar.instance)
                .notInitiallyVisible()
                .build();

        paintToolsPalette = new PaintToolsPalette();
        WindowBuilder.make(paintToolsPalette)
                .resizeable(false)
                .notFocusable()
                .withTitle("Tools Palette")
                .withMenuBar(HyperCardMenuBar.instance)
                .withLocationLeftOf(stackFrame)
                .notInitiallyVisible()
                .build();

        ModifierKeyListener.start();
        MouseListener.start();
    }

    public static RuntimeEnv getRuntimeEnv() {
        return _instance;
    }

    public void executeHandler(PartSpecifier me, Script script, String handler) {
        executeStatementList(me, script.getHandler(handler), true);
    }

    public Future executeStatementList(PartSpecifier me, StatementList handler, boolean onNewThread) {
        HandlerExecutionTask handlerTask = new HandlerExecutionTask(me, handler);
        if (SwingUtilities.isEventDispatchThread() || onNewThread)
            return scriptExecutor.submit(handlerTask);
        else {
            handlerTask.run();
            return Futures.immediateFuture(null);
        }
    }

    public Value executeUserFunction(PartSpecifier me, UserFunction function, ArgumentList arguments) {
        FunctionExecutionTask functionTask = new FunctionExecutionTask(me, function, arguments);
        
        try {
            // Not normally possible user functions are always executed in the context of a handler
            if (SwingUtilities.isEventDispatchThread())
                return scriptExecutor.submit(functionTask).get();
            else
                return functionTask.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Component getStackPanel() {
        return stackWindow.getWindowPanel();
    }

    public StackModel getStack () { return stack; }

    public void setStack (StackModel model) {
        SwingUtilities.invokeLater(() -> {
            stack = model;
            stack.addObserver(RuntimeEnv.this);
            stackWindow.setDisplayedCard(stack.getCurrentCard());
        });
    }

    public CardPart getCard () {
        return stack.getCurrentCard();
    }

    public Point getTheMouseLoc() {
        CardPart theCard = stackWindow.getDisplayedCard();
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLoc, theCard);

        return mouseLoc;
    }

    public Value getTheMouseLocValue() {
        Point mouseLoc = getTheMouseLoc();
        return new Value(String.valueOf(mouseLoc.x) + ","
                + String.valueOf(mouseLoc.y));
    }

    public Value getTheMouse() {
        return MouseListener.isMouseDown() ? new Value("down") : new Value("up");
    }

    public boolean isMessageBoxVisible () {
        return messageWindow.isVisible();
    }

    public void setMessageBoxVisible (boolean visible) {
        SwingUtilities.invokeLater(() -> messageWindow.setVisible(visible));
    }

    public void setMsgBoxText(Object theMsg) {
        SwingUtilities.invokeLater(() -> messageWindow.setMsgBoxText(theMsg.toString()));
    }

    public String getMsgBoxText() {
        return messageWindow.getMsgBoxText();
    }

    public void setPaintToolsPaletteVisible (boolean visible) {
        paintToolsPalette.setVisible(visible);
    }

    public boolean isPaintToolsPaletteVisible() {
        return paintToolsPalette.isVisible();
    }

    public void doMsgBoxText() {
        messageBoxExecutor.submit(() -> {
            try {
                if (!getMsgBoxText().trim().isEmpty()) {
                    Interpreter.execute(null, getMsgBoxText()).get();
                    RuntimeEnv.getRuntimeEnv().setMsgBoxText(GlobalContext.getContext().getIt());
                }
            } catch (Exception e) {
                RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
            }
        });
    }

    public void dialogSyntaxError(Exception e) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(stackWindow.getWindowPanel(), e.getMessage()));
        e.printStackTrace();
    }

    @Override
    public void onCurrentCardChanged(CardPart newCard) {
        SwingUtilities.invokeLater(() -> stackWindow.setDisplayedCard(newCard));
    }
}
