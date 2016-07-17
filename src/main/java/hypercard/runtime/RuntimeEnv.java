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
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RuntimeEnv implements Serializable {
	private static final long serialVersionUID = 3092430577877088297L;

	private static RuntimeEnv _instance;
	private StackWindow mainWind;
	private boolean supressMessages = false;
	private boolean mouseIsDown;

	private ExecutorService scriptExecutor = Executors.newSingleThreadExecutor();

	public static void main(String argv[]) {
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

		// Create the main window, center it on the screen and display it
		mainWind = new StackWindow();
		WindowBuilder.make(mainWind)
				.withTitle("HyperCard")
				.resizeable(false)
				.quitOnClose()
				.withModel(new CardPart())
				.withLocationRelativeTo(null)
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

	public Component getCardPanel() {
		return mainWind.getWindowPanel();
	}

	public CardPart getCard () {
		return mainWind.getCurrentCard();
	}

	public Point getTheMouseLoc() {
		CardPart theCard = mainWind.getCurrentCard();
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

	public void setMsgBoxText(Object theMsg) {
		mainWind.setMsgBoxText(theMsg.toString());
	}

	public String getMsgBoxText() {
		return mainWind.getMsgBoxText();
	}

	public void sendMessage(PartSpecifier ps, String message)
			throws PartException, HtSemanticException {
		if (!supressMessages)
			GlobalContext.getContext().get(ps).sendMessage(message);
	}

	public void dialogSyntaxError(Exception e) {
		JOptionPane.showMessageDialog(mainWind.getWindowPanel(),
				"Syntax error: " + e.getMessage());
	}
}
