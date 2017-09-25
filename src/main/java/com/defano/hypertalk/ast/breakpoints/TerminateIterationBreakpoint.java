package com.defano.hypertalk.ast.breakpoints;

/**
 * Represents an interruption in the flow of execution in the current block (StatementList). For example, 'next repeat'
 * interrupts the current StatementList returning control to the top of the loop but does not exit the current handler
 * or function.
 */
public class TerminateIterationBreakpoint extends Breakpoint {
}
