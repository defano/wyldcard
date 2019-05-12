package com.defano.wyldcard.stackreader.misc;

import com.defano.wyldcard.stackreader.block.Block;

import java.util.Comparator;

public class ImportOrderComparator implements Comparator<Block> {

    @Override
    public int compare(Block o1, Block o2) {
        return Integer.compare(o1.getBlockType().getImportOrder(), o2.getBlockType().getImportOrder());
    }
}
