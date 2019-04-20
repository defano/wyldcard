package com.defano.hypertalk.ast.preemptions;

import com.defano.hypertalk.exception.HtNoSuchPropertyException;

/**
 * Represents an interruption to the normal flow of script execution (for example, arising from 'next', 'pass', or
 * 'exit to hypercard' statement).
 *
 * A Preemption is modeled as an Exception in Java, but does not imply that an unexpected or erroneous condition has
 * been encountered either in Java or within a HyperTalk script. These situations are referred to as preemptions to
 * distinguish them from real errors, which use Java's exception naming conventions (i.e.,
 * {@link HtNoSuchPropertyException} or {@link com.defano.hypertalk.exception.HtException}).
 */
public class Preemption extends Exception {
}
