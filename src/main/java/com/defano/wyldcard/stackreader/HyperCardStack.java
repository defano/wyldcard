package com.defano.wyldcard.stackreader;

import com.defano.wyldcard.stackreader.block.Block;
import com.defano.wyldcard.stackreader.block.BlockType;
import com.defano.wyldcard.stackreader.block.ImageBlock;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.ImportOrderComparator;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public class HyperCardStack {

    private final List<Block> blocks = new ArrayList<>();

    public static HyperCardStack fromFile(File f, ImportResult result) throws FileNotFoundException, ImportException {
        return fromInputStream(new FileInputStream(f), result);
    }

    public static HyperCardStack fromInputStream(InputStream sis, ImportResult result) throws ImportException {
        HyperCardStack stack = new HyperCardStack();
        stack.deserialize(new StackInputStream(sis), result == null ? new ImportResult() : result);
        return stack;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<Block> getBlocks(BlockType type) {
        return blocks.stream()
                .filter(b -> b.getBlockType() == type)
                .collect(Collectors.toList());
    }

    public Block getBlock(int blockId) {
        return blocks.stream()
                .filter(b -> b.getBlockId() == blockId)
                .findFirst()
                .orElse(null);
    }

    public BufferedImage getImage(int bitmapId) {
        return getBlocks(BlockType.BMAP).stream()
                .filter(b -> b.getBlockId() == bitmapId)
                .map(b -> ((ImageBlock)b).getImage())
                .findFirst()
                .orElse(null);
    }

    private void deserialize(StackInputStream fis, ImportResult report) throws ImportException {
        BlockType blockType;

        try {

            // First pass, read in raw block data
            do {
                int blockSize = fis.readInt();
                int blockTypeId = fis.readInt();
                int blockId = fis.readInt();
                fis.readInt();  // Padding; ignored
                byte[] blockData = fis.readBytes(blockSize - 16);

                blockType = BlockType.fromBlockId(blockTypeId);

                if (blockType == null) {
                    report.throwError(null, "Encountered block with unknown enums: " + blockTypeId + ".");
                } else {
                    Block block = blockType.instantiateBlock(this, blockId, blockSize, blockData);
                    if (block != null) {
                        blocks.add(block);
                    }
                }

            } while (blockType != BlockType.TAIL);

            // Second pass: unpack block data
            ArrayList<Block> importOrder = new ArrayList<>(blocks);
            importOrder.sort(new ImportOrderComparator());

            for (Block thisBlock : importOrder) {
                thisBlock.unpack(report);
            }

        } catch (IOException e) {
            report.throwError(null, "Malformed block array; stack structure is corrupt.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(blocks.stream().map(Block::getBlockType).toArray(), ToStringStyle.SIMPLE_STYLE);
    }
}
