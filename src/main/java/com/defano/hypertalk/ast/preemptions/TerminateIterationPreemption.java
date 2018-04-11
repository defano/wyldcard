package com.defano.hypertalk.ast.preemptions;

/**
 * Represents an interruption in the flow of execution in the current block (StatementList). For example, 'next repeat'
 * interrupts the current StatementList returning control to the top of the loop but does not exit the current handler
 * or function.
 */
public class TerminateIterationPreemption extends Preemption {
}
