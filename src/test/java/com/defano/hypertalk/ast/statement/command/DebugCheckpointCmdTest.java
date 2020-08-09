package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebugCheckpointCmdTest extends GuiceTest<DebugCheckpointCmd> {

    @BeforeEach
    void setUp() {
        initialize(new DebugCheckpointCmd(mockParserRuleContext));
    }

    @Test
    void testThatStatementIsPermBreakpoint() {
        assertTrue(uut.isPermanentBreakpoint());
    }
}