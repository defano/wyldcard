package com.defano.hypercard.runtime;

import com.defano.hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.tree.ParseTree;

public enum CompilationUnit {
    SCRIPT,
    SCRIPTLET,
    EFFECT_EXPRESSION,
    MENU_EXPRESSION,
    MENUITEM_EXPRESSION;

    public ParseTree getParseTree(HyperTalkParser parser) {
        switch (this) {
            case SCRIPT:
                return parser.script();
            case SCRIPTLET:
                return parser.scriptlet();
            case EFFECT_EXPRESSION:
                return parser.effectExpression();
            case MENU_EXPRESSION:
                return parser.menuExpression();
            case MENUITEM_EXPRESSION:
                return parser.menuItemExpression();
        }

        throw new IllegalStateException("Bug! Unimplemented compilation unit.");
    }

}
