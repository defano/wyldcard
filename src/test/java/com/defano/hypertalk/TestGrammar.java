/*
 * TestGrammar
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk;

import com.defano.hypercard.runtime.interpreter.Interpreter;
import com.defano.hypertalk.exception.HtException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class TestGrammar {

    @Test
    public void testGrammar() throws IOException, HtException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("examples/Script.txt");
        String testScript = IOUtils.toString(in);

        long start = System.currentTimeMillis();
        Interpreter.compileScript(testScript);
        long end = System.currentTimeMillis();

        System.out.println("Cold-compiled test script in " + (end - start) + "ms.");

        start = System.currentTimeMillis();
        Interpreter.compileScript(testScript);
        end = System.currentTimeMillis();

        System.out.println("Warm-compiled test script in " + (end - start) + "ms.");

        start = System.currentTimeMillis();
        Interpreter.compileScript(testScript);
        end = System.currentTimeMillis();

        System.out.println("Hot-compiled test script in " + (end - start) + "ms.");

    }

}
