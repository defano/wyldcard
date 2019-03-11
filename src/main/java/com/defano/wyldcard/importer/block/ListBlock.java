package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.result.MalformedBlockIssue;
import com.defano.wyldcard.importer.result.Results;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.importer.type.PageEntry;

import java.io.IOException;

public class ListBlock extends Block {

    private int pageCount;          // number of PAGE blocks
    private int pageSize;           // size of a PAGE block; usually 2048 bytes
    private int pageEntryTotal;     // total number of entries in all PAGE blocks; should equal number of cards
    private short pageEntrySize;    // length of an entry in a PAGE block
    private int pageEntryTotal2;    // total number of entries in all PAGE blocks; should equal number of cards
    private PageEntry[] pages;

    @Override
    public Block deserialize(byte[] data, Results results) {

        StackInputStream sis = new StackInputStream(data);

        try {
            this.pageCount = sis.readInt();
            this.pageSize = sis.readInt();
            this.pageEntryTotal = sis.readInt();
            this.pageEntrySize = sis.readShort();

            sis.readShort(5); // Unknown field; skip

            this.pageEntryTotal2 = sis.readInt();

            sis.readInt(); // Unknown field; skip

            pages = new PageEntry[pageEntryTotal];
            for (int idx = 0; idx < pageEntryTotal; idx++) {
                int id = sis.readInt();
                short entryCount = sis.readShort();

                pages[idx] = new PageEntry(id, entryCount);
            }

        } catch (IOException e) {
            results.error(new MalformedBlockIssue(BlockType.LIST, blockId));
        }

        return null;
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

    public PageEntry[] getPages() {
        return pages;
    }
}
