package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.PageBlockIndexRecord;
import com.defano.wyldcard.stackreader.record.PageEntryRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ListBlock extends Block {

    private int pageCount;
    private int pageSize;
    private int pageEntryTotal;
    private short pageEntrySize;
    private int cardsInStackCount;
    private PageBlockIndexRecord[] pageIndices;
    private short hashIntegerCount;
    private short searchHashValueCount;
    private int checksum;

    public ListBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(root, blockType, blockSize, blockId, blockData);
    }

    /**
     * Number of PAGE blocks referenced in the LIST.
     *
     * @return The number of PAGE blocks.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * The size, in bytes, of a PAGE block (typically 2048 bytes)
     *
     * @return The size of the a PAGE block.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * The total number of entries in all PAGE blocks, should equal the number of cards in the stack.
     *
     * @return The number of entries in all PAGE blocks.
     */
    public int getPageEntryTotal() {
        return pageEntryTotal;
    }

    /**
     * The size of a card entry, in bytes.
     *
     * @return The size of a card entry.
     */
    public short getPageEntrySize() {
        return pageEntrySize;
    }

    /**
     * Gets the total number of cards in the stack.
     * @return Number of cards in the stack.
     */
    public int getCardsInStackCount() {
        return cardsInStackCount;
    }

    public short getHashIntegerCount() {
        return hashIntegerCount;
    }

    public short getSearchHashValueCount() {
        return searchHashValueCount;
    }

    public int getChecksum() {
        return checksum;
    }

    public boolean isChecksumValid() {
        int checksum = 0x00;
        for (PageBlockIndexRecord pi : getPageIndices()) {
            checksum = rightRotate(pi.getPageId() + checksum, 3) + pi.getPageEntryCount();
        }
        return checksum == this.checksum;
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
    public void unpack() throws ImportException {

        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            this.pageCount = sis.readInt();
            this.pageSize = sis.readInt();
            this.pageEntryTotal = sis.readInt();
            this.pageEntrySize = sis.readShort();
            sis.readShort(); // Unknown field
            this.hashIntegerCount = sis.readShort();
            this.searchHashValueCount = sis.readShort();
            this.checksum = sis.readInt();
            this.cardsInStackCount = sis.readInt();
            sis.readInt(); // Unknown field

            pageIndices = new PageBlockIndexRecord[pageCount];
            for (int idx = 0; idx < pageCount; idx++) {
                int cardId = sis.readInt();
                short entryCount = sis.readShort();
                pageIndices[idx] = new PageBlockIndexRecord(cardId, entryCount);
            }

        } catch (IOException e) {
            throw new ImportException(this, "Malformed list block; stack is corrupt.", e);
        }
    }
}
