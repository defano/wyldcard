package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.type.PageEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PageBlock extends Block {

    private final List<PageEntry> pageEntries = new ArrayList<>();

    public PageBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId) {
        super(root, blockType, blockSize, blockId);
    }

    public List<PageEntry> getPageEntries() {
        return pageEntries;
    }

    @Override
    public void deserialize(byte[] data, ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(data);

        if (getStack() == null || getStack().getBlocks(BlockType.LIST).size() != 1) {
            report.throwError(this, "Unable to cross-reference LIST block from PAGE; stack is corrupt.");
        }

        ListBlock listBlock = getListBlock(report);
        Short pageEntryCount = listBlock.getPageEntryCountForPage(blockId);
        short pageEntrySize = listBlock.getPageEntrySize();

        if (pageEntryCount == null) {
            report.throwError(this, "Unable to find page entry in list index for block id " + blockId + "; stack is corrupted.");
        }

        try {
            sis.readInt();  // Unknown field; skip

            //noinspection ConstantConditions
            for (int idx = 0; idx < pageEntryCount; idx++) {
                int cardId = sis.readInt();
                byte[] pageData = sis.readBytes(pageEntrySize - 4);

                pageEntries.add(new PageEntry(cardId, ((pageData[0] & 0x08) != 0)));
            }

        } catch (IOException e) {
            report.throwError(this, "Malformed page block; stack is corrupt.");
        }
    }

    private ListBlock getListBlock(ImportResult report) throws ImportException {
        List<Block> listBlocks = getStack().getBlocks(BlockType.LIST);

        if (listBlocks.isEmpty()) {
            report.throwError(this, "Encountered page block before list block; stack is corrupted.");
        }

        if (listBlocks.size() > 1) {
            report.throwError(this, "Found multiple list blocks when only one was expected; stack is corrupted.");
        }

        return (ListBlock) listBlocks.get(0);
    }

}
