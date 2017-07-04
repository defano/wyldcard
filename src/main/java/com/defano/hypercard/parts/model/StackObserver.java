/*
 * StackModelObserver
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.parts.CardPart;
import com.defano.hypercard.parts.StackPart;

public interface StackObserver {
    void onStackOpened(StackPart newStack);
    void onCardClosed(CardPart oldCard);
    void onCardOpened(CardPart newCard);
}
