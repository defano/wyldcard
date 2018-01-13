package com.defano.hypercard.runtime.interpreter;

import com.defano.hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.tree.ParseTree;

public enum CompilationUnit {
    SCRIPT,
    SCRIPTLET;

    public ParseTree getParseTree(HyperTalkParser parser) {
        switch (this) {
            case SCRIPT:
                return parser.script();
            case SCRIPTLET:
                return parser.scriptlet();
        }

        throw new IllegalStateException("Bug! Unimplemented compilation unit.");
    }

}
