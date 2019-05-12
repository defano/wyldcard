package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.enums.PageFlag;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.PageEntryRecord;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class PageBlock extends Block {

    private int listId;
    private int checksum;
    private PageEntryRecord[] pageEntries = new PageEntryRecord[0];

    public PageBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(root, blockType, blockSize, blockId, blockData);
    }

    public PageEntryRecord[] getPageEntries() {
        return pageEntries;
    }

    public int getListId() {
        return listId;
    }

    public int getChecksum() {
        return checksum;
    }

    public boolean isChecksumValid() {
        int checksum = 0x00;
        for (PageEntryRecord pe : getPageEntries()) {
            checksum = rightRotate(checksum + pe.getCardId(), 3);
        }
        return checksum == this.checksum;
    }

    @Override
    public void unpack() throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        if (getStack().getBlocks(BlockType.LIST).size() != 1) {
            throw new ImportException(this, "Unable to cross-reference LIST block from PAGE; stack is corrupt.");
        }

        ListBlock listBlock = getListBlock();
        Short pageEntryCount = listBlock.getPageEntryCountForPage(getBlockId());
        short pageEntrySize = listBlock.getPageEntrySize();

        if (pageEntryCount == null) {
            throw new ImportException(this, "Unable to find page entry in list index for block id " + getBlockId() + "; stack is corrupted.");
        }

        try {
            listId = sis.readInt();
            checksum = sis.readInt();

            pageEntries = new PageEntryRecord[pageEntryCount];
            for (int idx = 0; idx < pageEntryCount; idx++) {
                int cardId = sis.readInt();
                PageFlag[] flags = PageFlag.fromBitmask(sis.readByte());
                byte[] searchHashData = sis.readBytes(pageEntrySize - 5);

                pageEntries[idx] = new PageEntryRecord(cardId, flags, searchHashData);
            }

        } catch (IOException e) {
            throw new ImportException(this, "Malformed page block; stack is corrupt.");
        }
    }

    private ListBlock getListBlock() throws ImportException {
        List<Block> listBlocks = getStack().getBlocks(BlockType.LIST);

        if (listBlocks.isEmpty()) {
            throw new ImportException(this, "Encountered page block before list block; stack is corrupted.");
        }

        if (listBlocks.size() > 1) {
            throw new ImportException(this, "Found multiple list blocks when only one was expected; stack is corrupted.");
        }

        return (ListBlock) listBlocks.get(0);
    }

}
