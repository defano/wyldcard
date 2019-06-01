/*
 * TestGrammar
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.ScriptCompiler;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class TestGrammar {

    @SuppressWarnings("deprecation")
    @Test
    public void testGrammar() throws IOException, HtException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("examples/Script.txt");
        String testScript = IOUtils.toString(in);

        long start = System.currentTimeMillis();
        ScriptCompiler.blockingCompile(CompilationUnit.SCRIPT, testScript);
        long end = System.currentTimeMillis();

        System.out.println("Cold-compiled test script in " + (end - start) + "ms.");

        start = System.currentTimeMillis();
        ScriptCompiler.blockingCompile(CompilationUnit.SCRIPT, testScript);
        end = System.currentTimeMillis();

        System.out.println("Warm-compiled test script in " + (end - start) + "ms.");

        start = System.currentTimeMillis();
        ScriptCompiler.blockingCompile(CompilationUnit.SCRIPT, testScript);
        end = System.currentTimeMillis();

        System.out.println("Hot-compiled test script in " + (end - start) + "ms.");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSLLParsePerformance() throws HtException, IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("examples/Simple.txt");
        String testScript = IOUtils.toString(in);

        long start = System.currentTimeMillis();
        ScriptCompiler.blockingCompile(CompilationUnit.SCRIPT, testScript);
        long end = System.currentTimeMillis();

        System.out.println("Cold-compiled test script in " + (end - start) + "ms.");

        int total = 0;
        int count = 20;
        for (int x = 0; x < count; x++) {
            start = System.currentTimeMillis();
            ScriptCompiler.blockingCompile(CompilationUnit.SCRIPT, testScript);
            end = System.currentTimeMillis();

            total += (end - start);
        }

        System.out.println("Warm-compiled script mean: " + ((double)total / (double)count) + "ms");
    }

}
