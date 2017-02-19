/*
 * TestGrammar
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.exception.HtException;

import org.junit.Test;

import static junit.framework.Assert.fail;

public class TestGrammar {

    private String validStatements[] = new String[]{
            "",
            "3",
            "3.0",
            "3.",
            ".3",
            "hello",
            "answer \"hello\" with \"choice 1\"",
            "answer \"hello\" with \"choice 1\" or \"choice 2\"",
            "answer \"hello\" with \"choice 1\" or \"choice 2\" or \"choice 2\"",
            "ask \"hello\"",
            "ask \"hello\" with \"answer\""
    };

    private String validScripts[] = new String[] {
            "",
            "-- comment",
            "\n\n\n",
            "on mouseUp\nend mouseUp",
    };

    @SuppressWarnings("deprecation")
    @Test
    public void testValidStatements () {
        for (String thisStatement : validStatements) {
            try {
                Interpreter.compile(thisStatement);
            } catch (HtException e) {
                fail("Compile error in [" + thisStatement + "]: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testValidScripts () {
        for (String thisScript : validScripts) {
            try {
                Interpreter.compile(thisScript);
            } catch (HtException e) {
                fail("Compile error in [" + thisScript + "]: " + e.getMessage());
            }
        }
    }

}
