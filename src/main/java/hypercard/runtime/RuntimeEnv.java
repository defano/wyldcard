/**
 * RuntimeEnv.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypercard.gui.menu.HyperCardMenuBar;
import hypercard.gui.window.MessageWindow;
import hypercard.gui.window.StackWindow;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.CardPart;
import hypercard.parts.PartException;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.StatementList;
import hypertalk.exception.HtSemanticException;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

public class RuntimeEnv {

	private static RuntimeEnv _instance;

	private HyperCardMenuBar menuBar;
	private StackWindow stackWindow;
	private MessageWindow messageWindow;
	private JFrame messageFrame;
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

		menuBar = new HyperCardMenuBar();

		// Create the main window, center it on the screen and display it
		stackWindow = new StackWindow();
		JFrame stackFrame = WindowBuilder.make(stackWindow)
				.withTitle("HyperCard")
				.resizeable(false)
				.quitOnClose()
				.withMenuBar(menuBar)
				.withModel(CardPart.newCard())
				.build();

		messageWindow = new MessageWindow();
		messageFrame = WindowBuilder.make(messageWindow)
				.withTitle("Message Box")
				.resizeable(false)
				.withLocationUnderneath(stackFrame)
				.withMenuBar(menuBar)
				.build();
	}

	public static RuntimeEnv getRuntimeEnv() {
		if (_instance == null) {
			_instance = new RuntimeEnv();
		}

		return _instance;
	}

	/**
	 * 
	 * @param handler
	 */
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

	public StackWindow getStackWindow () {
		return stackWindow;
	}

	public Component getCardPanel() {
		return stackWindow.getWindowPanel();
	}

	public CardPart getCard () {
		return stackWindow.getCurrentCard();
	}

	public Point getTheMouseLoc() {
		CardPart theCard = stackWindow.getCurrentCard();
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

	public void sendMessage(PartSpecifier ps, String message)
			throws PartException, HtSemanticException {
		if (!supressMessages)
			GlobalContext.getContext().get(ps).sendMessage(message);
	}

	public void dialogSyntaxError(Exception e) {
		JOptionPane.showMessageDialog(stackWindow.getWindowPanel(),
				"Syntax error: " + e.getMessage());
	}
}
