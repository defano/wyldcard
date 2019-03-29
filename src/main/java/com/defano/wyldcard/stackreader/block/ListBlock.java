package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.record.PageBlockIndexRecord;
import com.defano.wyldcard.stackreader.record.PageEntryRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ListBlock extends Block {

    private int pageCount;          // number of PAGE blocks
    private int pageSize;           // size of a PAGE block; usually 2048 bytes
    private int pageEntryTotal;     // total number of entries in all PAGE blocks; should equal number of cards
    private short pageEntrySize;    // length of an entry in a PAGE block
    private int pageEntryTotal2;    // total number of entries in all PAGE blocks; should equal number of cards
    private PageBlockIndexRecord[] pageIndices;

    public ListBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(root, blockType, blockSize, blockId, blockData);
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageEntryTotal() {
        return pageEntryTotal;
    }

    public short getPageEntrySize() {
        return pageEntrySize;
    }

    public int getPageEntryTotal2() {
        return pageEntryTotal2;
    }

    public PageBlockIndexRecord[] getPageIndices() {
        return pageIndices;
    }

    public List<PageBlock> getPages() {
        return Arrays.stream(pageIndices)
                .map(i -> getStack().getBlock(PageBlock.class, i.getPageId()))
                .collect(Collectors.toList());
    }

    public List<CardBlock> getCards() {
        ArrayList<CardBlock> cardBlocks = new ArrayList<>();

        for (PageBlock pageBlock : getPages()) {
            for (PageEntryRecord pageEntry : pageBlock.getPageEntries()) {
                cardBlocks.add(getStack().getBlock(CardBlock.class, pageEntry.getCardId()));
            }
        }

        return cardBlocks;
    }

    public Short getPageEntryCountForPage(int pageId) {
        return Arrays.stream(pageIndices)
                .filter(i -> i.getPageId() == pageId)
                .map(PageBlockIndexRecord::getPageEntryCount)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void unpack(ImportResult results) throws ImportException {

        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            this.pageCount = sis.readInt();
            this.pageSize = sis.readInt();
            this.pageEntryTotal = sis.readInt();
            this.pageEntrySize = sis.readShort();

            sis.readShort(5); // Unknown fields; skip

            this.pageEntryTotal2 = sis.readInt();

            sis.readInt(); // Unknown field; skip

            pageIndices = new PageBlockIndexRecord[pageCount];
            for (int idx = 0; idx < pageCount; idx++) {
                int cardId = sis.readInt();
                short entryCount = sis.readShort();
                pageIndices[idx] = new PageBlockIndexRecord(cardId, entryCount);
            }

        } catch (IOException e) {
            results.throwError(this, "Malformed list block; stack is corrupt.", e);
        }
    }
}