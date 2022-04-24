package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.Locale;

public class OSNameFunc extends Expression {
  public OSNameFunc(ParserRuleContext context) {
    super(context);
  }

  @Override
  protected Value onEvaluate(ExecutionContext context) {
    String OSName = System.getProperty("os.name");
    if (OSName == null) {
      return new Value("");
    }

    OSName = OSName.toLowerCase(Locale.ENGLISH);
    if (OSName.contains("windows")) {
      return new Value("windows");
    } else if (
      OSName.contains("linux")   || OSName.contains("mpe/ix") ||
      OSName.contains("freebsd") || OSName.contains("irix")   ||
      OSName.contains("unix")
    ) {
      return new Value("unix");
    } else if (OSName.contains("mac os")) {
      return new Value("macos");
    } else if (
      OSName.contains("sun os")  || OSName.contains("sunos") ||
      OSName.contains("solaris") || OSName.contains("hp-ux") ||
      OSName.contains("aix")
    ) {
      return new Value("posix");
    } else {
      return new Value("");
    }
  }
}
