package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.Locale;

public class OSVersionFunc extends Expression {
  public OSVersionFunc(ParserRuleContext context) {
    super(context);
  }

  @Override
  protected Value onEvaluate(ExecutionContext context) {
    String OSVersion = System.getProperty("os.version");
    if (OSVersion == null) {
      return new Value("");
    } else {
      return new Value(OSVersion.toLowerCase(Locale.ENGLISH));
    }
  }
}
