package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.PartContentRecord;
import com.defano.wyldcard.stackreader.record.PartRecord;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Represents data elements that are common to CARD and BKGD blocks.
 */
@SuppressWarnings("unused")
public abstract class CardLayerBlock extends Block {

    private short nextPartId;
    private int partListSize;
    private short partContentCount; // number of part contents
    private int partContentSize;
    private PartRecord[] parts;
    private PartContentRecord[] contents;
    private String name; // the name of the card
    private String script; // the card script

    @SuppressWarnings("WeakerAccess")
    public CardLayerBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    /**
     * Gets the number of parts (buttons and fields) appearing on this card or background.
     *
     * @return The card part count
     */
    public abstract short getPartCount();

    public abstract PartContentRecord getPartContents(int partId);

    /**
     * Get the number of corresponding BMAP block containing this card's image. Use {@link #getImage()} to retrieve
     * the image directly.
     *
     * @return This image id of this card.
     */
    public abstract int getBitmapId();

    /**
     * Gets the image (painted graphics) of this card or background.
     *
     * @return The layer's bitmap image.
     */
    public BufferedImage getImage() {
        return getStack().getImage(getBitmapId());
    }

    /**
     * Gets the number of {@link PartContentRecord} records describing the formatting of the contents of this card or
     * background.
     *
     * @return The number of content records.
     */
    public short getPartContentCount() {
        return partContentCount;
    }

    /**
     * Get an array of parts (buttons and fields) that appear on this card or background. See {@link PartRecord} for
     * details. The number of records should equal {@link #getPartCount()}.
     *
     * @return The parts appearing on this layer.
     */
    public PartRecord[] getParts() {
        return parts;
    }

    /**
     * Gets an array of {@link PartContentRecord} describing the contents of a button or field appearing on this layer.
     * A {@link PartContentRecord} describes the text formatting of each part's contents.
     * <p>
     * Length of the array should equal {@link #getPartContentCount()}.
     *
     * @return The array of part content records.
     */
    public PartContentRecord[] getContents() {
        return contents;
    }

    /**
     * Gets the name of this part, as it appears in HyperCard.
     *
     * @return The part name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the text of the script associated with this part.
     *
     * @return The script text.
     */
    public String getScript() {
        return script;
    }

    /**
     * Gets the next ID that should be used when creating a new button or field on this layer.
     *
     * @return The next part id
     */
    public short getNextPartId() {
        return nextPartId;
    }

    /**
     * The size, in bytes, of the part list.
     *
     * @return The size of the part list.
     */
    public int getPartListSize() {
        return partListSize;
    }

    /**
     * The total size, in bytes, of the part content list.
     *
     * @return The size of the part content list, in bytes.
     */
    public int getPartContentSize() {
        return partContentSize;
    }

    /**
     * Unpacks the data from the given StackInputStream into properties provided by this class. The input stream
     * should not be closed by this method.
     *
     * @param sis The StackInputStream to read
     * @throws ImportException Thrown if an error occurs unpacking data from the input stream.
     */
    public void unpack(StackInputStream sis) throws ImportException {

        int partCount = getPartCount();

        try {
            nextPartId = sis.readShort();
            partListSize = sis.readInt();
            partContentCount = sis.readShort();
            partContentSize = sis.readInt();

            // Deserialize buttons and fields
            parts = new PartRecord[partCount];
            for (int partIdx = 0; partIdx < partCount; partIdx++) {
                short entrySize = sis.readShort();
                byte[] entryData = sis.readBytes(entrySize - 2);

                parts[partIdx] = PartRecord.deserialize(this, entrySize, entryData);
            }

            // Deserialize text formatting
            contents = new PartContentRecord[partContentCount];
            for (int partContentsIdx = 0; partContentsIdx < partContentCount; partContentsIdx++) {
                short partId = sis.readShort();
                short length = sis.readShort();
                byte[] partContentsData = sis.readBytes(length);

                contents[partContentsIdx] = PartContentRecord.deserialize(this, partId, partContentsData);

                if (length % 2 != 0) {
                    sis.readByte();
                }
            }

            name = sis.readString();
            script = sis.readString();

        } catch (IOException e) {
            throw new ImportException(this, "Malformed CARD or BGND block.", e);
        }
    }
}
