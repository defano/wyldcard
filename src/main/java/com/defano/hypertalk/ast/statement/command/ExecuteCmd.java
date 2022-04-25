package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.LiteralExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import java.io.*;
import java.util.Locale;

public class ExecuteCmd extends Command {
  private final Expression command;
  private final Expression stdin;

  public ExecuteCmd(ParserRuleContext context, Expression command) {
    super(context, "execute");

    this.command = command;
    this.stdin   = new LiteralExp(context, "");
  }

  public ExecuteCmd(ParserRuleContext context, Expression command, Expression stdin) {
    super(context, "execute");

    this.command = command;
    this.stdin   = stdin;
  }
    
  public void onExecute(ExecutionContext context) throws HtException {
    String command = this.command.evaluate(context).toString().trim(); // trailing spaces may cause problems
    if (command.equals("")) {
      context.setIt(new Value(""));
      context.setResult(new Value(""));
      return;
    }

    try {
      boolean isWindows = false;

      String OSName = System.getProperty("os.name");
      if (OSName != null) {
        OSName = OSName.toLowerCase(Locale.ENGLISH);
        isWindows = OSName.contains("windows");
      }

      String actualCmd = (isWindows ? "cmd /c " : "") + command;
      Process process = Runtime.getRuntime().exec(actualCmd);

      if (this.stdin != null) {
        String stdin = this.stdin.evaluate(context).toString();
        try {
          OutputStream stdinStream = process.getOutputStream();
          stdinStream.write(stdin.getBytes());
          stdinStream.close();
        } catch (Exception signal) {
          System.err.println("\"execute\" could write input to stdin, reason:");
          signal.printStackTrace();
        }
      }

      InputStream stdoutStream = process.getInputStream();
      BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutStream));
      StringBuilder stdout = new StringBuilder();
      String stdoutLine;
      while ((stdoutLine = stdoutReader.readLine()) != null) {
        stdout.append(stdoutLine);
        stdout.append("\n");
      }
      stdoutReader.close();

      InputStream stderrStream = process.getErrorStream();
      BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrStream));
      StringBuilder stderr = new StringBuilder();
      String stderrLine;
      while ((stderrLine = stderrReader.readLine()) != null) {
        stdout.append(stderrLine);
        stdout.append("\n");
      }
      stderrReader.close();

      int exitCode = process.waitFor();
      if (exitCode == 0) {
        context.setIt(new Value(stdout.toString()));
        context.setResult(new Value(""));
      } else {
        context.setIt(new Value(stderr.toString()));
        context.setResult(new Value(exitCode + "\n" + stderr.toString()));
      }
    } catch (Exception signal) {
      context.setIt(new Value(""));
      context.setResult(new Value(signal.getMessage()));
      System.err.println("\"execute\" command failed with:");
      signal.printStackTrace();
    }
  }
}
