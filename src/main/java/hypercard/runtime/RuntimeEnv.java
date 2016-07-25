/**
 * RuntimeEnv.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package hypercard.runtime;

import hypercard.gui.menu.HyperCardMenuBar;
import hypercard.gui.window.MessageWindow;
import hypercard.gui.window.StackWindow;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModel;
import hypercard.parts.model.StackModelObserver;
import hypertalk.ast.common.Value;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.StatementList;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RuntimeEnv implements StackModelObserver {

	private static RuntimeEnv _instance;

	private StackWindow stackWindow;
	private MessageWindow messageWindow;
	private JFrame messageFrame;
	private StackModel stack;
	private boolean supressMessages = false;
	private boolean mouseIsDown;

	private ExecutorService scriptExecutor = Executors.newSingleThreadExecutor();

	public static void main(String argv[]) {
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

		HyperCardMenuBar menuBar = new HyperCardMenuBar();

		stack = StackModel.newStack("Untitled");
		stack.addObserver(this);

		// Create the main window, center it on the screen and display it
		stackWindow = new StackWindow();
		JFrame stackFrame = WindowBuilder.make(stackWindow)
				.withTitle("HyperCard")
				.resizeable(false)
				.quitOnClose()
				.withMenuBar(menuBar)
				.withModel(stack.getCurrentCard())
				.build();

		messageWindow = new MessageWindow();
		messageFrame = WindowBuilder.make(messageWindow)
				.withTitle("Message Box")
				.resizeable(false)
				.withLocationUnderneath(stackFrame)
				.withMenuBar(menuBar)
				.notInitiallyVisible()
				.build();
	}

	public static RuntimeEnv getRuntimeEnv() {
		if (_instance == null) {
			_instance = new RuntimeEnv();
		}

		return _instance;
	}

	public void executeStatementList(StatementList handler) {
		HandlerExecutionTask handlerTask = new HandlerExecutionTask(handler);
		
		if (SwingUtilities.isEventDispatchThread())
			scriptExecutor.submit(handlerTask);
		else
			handlerTask.run();
	}

	public Value executeUserFunction(UserFunction function, ArgumentList arguments) {
		FunctionExecutionTask functionTask = new FunctionExecutionTask(function, arguments);
		
		try {
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
		this.stack = model;
		this.stack.addObserver(this);
		this.stackWindow.setDisplayedCard(stack.getCurrentCard());
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
		messageFrame.setVisible(visible);
	}

	public void setMsgBoxText(Object theMsg) {
		messageWindow.setMsgBoxText(theMsg.toString());
	}

	public String getMsgBoxText() {
		return messageWindow.getMsgBoxText();
	}

	public void dialogSyntaxError(Exception e) {
		JOptionPane.showMessageDialog(stackWindow.getWindowPanel(),
				"Syntax error: " + e.getMessage());
	}

	@Override
	public void onCurrentCardChanged(CardPart newCard) {
		stackWindow.setDisplayedCard(newCard);
	}
}
