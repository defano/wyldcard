package com.defano.wyldcard.importer;

public interface ConversionProgressObserver {
    void onConversionProgressUpdate(int cardsImported, int totalCards, String message);

}
