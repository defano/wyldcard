package com.defano.wyldcard.importer;

public interface ConversionProgressObserver {

    /**
     * Invoked to indicate that progress has been made in converting a HyperCard stack to WyldCard.
     *
     * @param cardsImported The number of cards in the stack that have been converted.
     * @param totalCards    The total number of cards in the stack.
     * @param message       A message indicating the current operation.
     */
    void onConversionProgressUpdate(int cardsImported, int totalCards, String message);
}
