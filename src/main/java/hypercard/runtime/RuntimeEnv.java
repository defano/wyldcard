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
import hypercard.gui.menu.HyperCardMenuBar;
import hypercard.gui.window.MessageWindow;
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
	private JFrame messageFrame;
	private StackModel stack;
	private boolean mouseIsDown;

	private ExecutorService scriptExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("script-executor-%d").build());

	public static void main(String argv[]) {
		// Display the frame's menu as the Mac OS menubar
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.macos.useScreenMenuBar", "true" );

		RuntimeEnv.getRuntimeEnv();
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
		messageFrame = WindowBuilder.make(messageWindow)
				.withTitle("Message Box")
				.resizeable(false)
				.withLocationUnderneath(stackFrame)
				.withMenuBar(HyperCardMenuBar.instance)
				.notInitiallyVisible()
				.build();
	}

	public static RuntimeEnv getRuntimeEnv() {
		if (_instance == null) {
			_instance = new RuntimeEnv();
		}

		return _instance;
	}

	public void executeHandler(PartSpecifier me, Script script, String handler, boolean onNewThread) {
		executeStatementList(me, script.getHandler(handler), onNewThread);
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

	public Value executeUserFunction(PartSpecifier me, UserFunction function, ArgumentList arguments, boolean onNewThread) {
		FunctionExecutionTask functionTask = new FunctionExecutionTask(me, function, arguments);
		
		try {
			if (SwingUtilities.isEventDispatchThread() || onNewThread)
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

	public void setTheMouse(boolean isDown) {
		this.mouseIsDown = isDown;
	}

	public Value getTheMouse() {
		return mouseIsDown ? new Value("down") : new Value("up");
	}

	public boolean isMessageBoxVisible () {
		return messageFrame.isVisible();
	}

	public void setMessageBoxVisible (boolean visible) {
		SwingUtilities.invokeLater(() -> messageFrame.setVisible(visible));
	}

	public void setMsgBoxText(Object theMsg) {
		SwingUtilities.invokeLater(() -> messageWindow.setMsgBoxText(theMsg.toString()));
	}

	public String getMsgBoxText() {
		return messageWindow.getMsgBoxText();
	}

	public void dialogSyntaxError(Exception e) {
		e.printStackTrace();
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(stackWindow.getWindowPanel(), e.getMessage()));
	}

	@Override
	public void onCurrentCardChanged(CardPart newCard) {
		SwingUtilities.invokeLater(() -> stackWindow.setDisplayedCard(newCard));
	}
}
