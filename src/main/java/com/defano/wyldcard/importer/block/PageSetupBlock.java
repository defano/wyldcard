package com.defano.wyldcard.importer.block;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.enums.PrintMethod;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.ImportResult;
import com.defano.wyldcard.importer.misc.StackInputStream;

import java.io.IOException;

@SuppressWarnings("unused")
public class PageSetupBlock extends Block {

    private short printManagerVersion;
    private short verticalDpi;
    private short horizontalDpi;
    private short printablePageTop;
    private short printablePageLeft;
    private short printablePageBottom;
    private short printablePageRight;
    private short paperTop;
    private short paperLeft;
    private short paperBottom;
    private short paperRight;
    private short printerDeviceNumber;
    private short pageV;
    private short pageH;
    private byte port;
    private byte feedType;
    private short verticalResolution;
    private short horizontalResolution;
    private short pageTop;
    private short pageLeft;
    private short pageBottom;
    private short pageRight;
    private short firstPage;
    private short lastPage;
    private short numberOfCopies;
    private PrintMethod printMethod;
    private int spoolName;
    private short spoolVolume;
    private byte spoolVersion;

    public PageSetupBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public short getPrintManagerVersion() {
        return printManagerVersion;
    }

    public short getVerticalDpi() {
        return verticalDpi;
    }

    public short getHorizontalDpi() {
        return horizontalDpi;
    }

    public short getPrintablePageTop() {
        return printablePageTop;
    }

    public short getPrintablePageLeft() {
        return printablePageLeft;
    }

    public short getPrintablePageBottom() {
        return printablePageBottom;
    }

    public short getPrintablePageRight() {
        return printablePageRight;
    }

    public short getPaperTop() {
        return paperTop;
    }

    public short getPaperLeft() {
        return paperLeft;
    }

    public short getPaperBottom() {
        return paperBottom;
    }

    public short getPaperRight() {
        return paperRight;
    }

    public short getPrinterDeviceNumber() {
        return printerDeviceNumber;
    }

    public short getPageV() {
        return pageV;
    }

    public short getPageH() {
        return pageH;
    }

    public byte getPort() {
        return port;
    }

    public byte getFeedType() {
        return feedType;
    }

    public short getVerticalResolution() {
        return verticalResolution;
    }

    public short getHorizontalResolution() {
        return horizontalResolution;
    }

    public short getPageTop() {
        return pageTop;
    }

    public short getPageLeft() {
        return pageLeft;
    }

    public short getPageBottom() {
        return pageBottom;
    }

    public short getPageRight() {
        return pageRight;
    }

    public short getFirstPage() {
        return firstPage;
    }

    public short getLastPage() {
        return lastPage;
    }

    public short getNumberOfCopies() {
        return numberOfCopies;
    }

    public PrintMethod getPrintMethod() {
        return printMethod;
    }

    public int getSpoolName() {
        return spoolName;
    }

    public short getSpoolVolume() {
        return spoolVolume;
    }

    public byte getSpoolVersion() {
        return spoolVersion;
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            printManagerVersion = sis.readShort();
            sis.readShort();
            verticalDpi = sis.readShort();
            horizontalDpi = sis.readShort();
            printablePageTop = sis.readShort();
            printablePageLeft = sis.readShort();
            printablePageBottom = sis.readShort();
            printablePageRight = sis.readShort();
            paperTop = sis.readShort();
            paperLeft = sis.readShort();
            paperBottom = sis.readShort();
            paperRight = sis.readShort();
            printerDeviceNumber = sis.readShort();
            pageV = sis.readShort();
            pageH = sis.readShort();
            port = sis.readByte();
            feedType = sis.readByte();
            sis.skipBytes(2);
            verticalResolution = sis.readShort();
            horizontalResolution = sis.readShort();
            pageTop = sis.readShort();
            pageLeft = sis.readShort();
            pageBottom = sis.readShort();
            pageRight = sis.readShort();
            sis.skipBytes(16);
            firstPage = sis.readShort();
            lastPage = sis.readShort();
            numberOfCopies = sis.readShort();
            printMethod = PrintMethod.fromMethodByte(sis.readByte());
            sis.skipBytes(1);
            spoolName = sis.readInt();
            spoolVolume = sis.readShort();
            spoolVersion = sis.readByte();
        } catch (IOException e) {
            report.throwError(this, "Malformed PRST report.");
        }
    }
}
