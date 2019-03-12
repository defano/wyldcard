package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.type.PageEntry;

import java.io.IOException;
import java.util.List;

public class PageBlock extends Block {

    private int listId; // ID number of the LIST block
    private List<PageEntry> pageEntryList;

    public PageBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId) {
        super(root, blockType, blockSize, blockId);
    }

    @Override
    public void deserialize(byte[] data, ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(data);

        if (super.stack == null || super.stack.getBlocks(BlockType.LIST).size() != 1) {
            report.error(this, "Unable to cross-reference LIST block from PAGE; stack is corrupt.");
        }

        ListBlock listBlock = ((ListBlock) stack.getBlock(BlockType.LIST).orElse(null));

        Short pageEntryCount = listBlock.getPageEntryCountForPageId(blockId);
        short pageEntrySize = listBlock.getPageEntrySize();

        try {
            sis.readInt();  // Unknown field; skip

        } catch (IOException e) {
            report.error(this, "Malformed page block; stack is corrupt.");
        }
    }
}
