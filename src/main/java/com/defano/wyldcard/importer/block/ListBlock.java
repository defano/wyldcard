package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.type.PageBlockIndex;

import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ListBlock extends Block {

    private int pageCount;          // number of PAGE blocks
    private int pageSize;           // size of a PAGE block; usually 2048 bytes
    private int pageEntryTotal;     // total number of entries in all PAGE blocks; should equal number of cards
    private short pageEntrySize;    // length of an entry in a PAGE block
    private int pageEntryTotal2;    // total number of entries in all PAGE blocks; should equal number of cards
    private PageBlockIndex[] pageIndices;

    public ListBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId) {
        super(root, blockType, blockSize, blockId);
    }

    @Override
    public void deserialize(byte[] data, ImportResult results) throws ImportException {

        StackInputStream sis = new StackInputStream(data);

        try {
            this.pageCount = sis.readInt();
            this.pageSize = sis.readInt();
            this.pageEntryTotal = sis.readInt();
            this.pageEntrySize = sis.readShort();

            sis.readShort(5); // Unknown fields; skip

            this.pageEntryTotal2 = sis.readInt();

            sis.readInt(); // Unknown field; skip

            pageIndices = new PageBlockIndex[pageCount];
            for (int idx = 0; idx < pageCount; idx++) {
                int id = sis.readInt();
                short entryCount = sis.readShort();
                pageIndices[idx] = new PageBlockIndex(id, entryCount);
            }

        } catch (IOException e) {
            results.throwError(this, "Malformed list block; stack is corrupt.", e);
        }
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

    public PageBlockIndex[] getPageIndices() {
        return pageIndices;
    }

    public Short getPageEntryCountForPageId(int pageId) {
        return Arrays.stream(pageIndices)
                .filter(i -> i.getPageId() == pageId)
                .map(PageBlockIndex::getPageEntryCount)
                .findFirst()
                .orElse(null);
    }
}
