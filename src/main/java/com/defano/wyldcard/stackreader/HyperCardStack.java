package com.defano.wyldcard.stackreader;

import com.defano.wyldcard.stackreader.block.*;
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

    /**
     * Gets a list of all the block structures contained in this stack, in the order they appear in the file.
     *
     * @return The list of all block records.
     */
    public List<Block> getBlocks() {
        return blocks;
    }

    /**
     * Gets a list of all blocks of a given type that appear in the stack, in the order they appeared in the stack file.
     *
     * @param type The type of block to return
     * @return The list of matching blocks.
     */
    public List<Block> getBlocks(BlockType type) {
        return blocks.stream()
                .filter(b -> b.getBlockType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Gets the first block matching the requested class.
     *
     * @param klass The class of the de-serialized block type. Use {@link BlockType#blockClass()} as a valid argument.
     * @param <T>   Any subclass of {@link Block} representing the type of the block to return.
     * @return The first block of the requested type; throws IllegalArgumentException if no such block is found.
     */
    @SuppressWarnings("unchecked")
    public <T extends Block> T getBlock(Class<T> klass) {
        return blocks.stream()
                .filter(b -> b.getClass() == klass)
                .map(b -> (T) b)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No block matching type " + klass));
    }

    /**
     * Gets a list of blocks matching the requested type, in the order they appear in the stack file.
     *
     * @param klass The class of the de-serialized block type. Use {@link BlockType#blockClass()} as a valid argument.
     * @param <T>   Any subclass of {@link Block} representing the type of the block to return.
     * @return A list of blocks of the given type; an empty list of no blocks of the given type are found in the file.
     */
    @SuppressWarnings("unchecked")
    public <T extends Block> List<T> getBlocks(Class<T> klass) {
        return blocks.stream()
                .filter(b -> b.getClass() == klass)
                .map(b -> (T) b)
                .collect(Collectors.toList());
    }

    /**
     * Gets the block of the requested type and id, throwing IllegalArgumentException if no such block is found in the
     * stack. Block structures often make reference to other blocks by id; use this method to find a block referenced
     * by another block.
     *
     * @param klass The class of the de-serialized block type. Use {@link BlockType#blockClass()} as a valid argument.
     * @param blockId The id of the block to retrieve.
     * @param <T>   Any subclass of {@link Block} representing the type of the block to return.
     * @return The block matching the given type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Block> T getBlock(Class<T> klass, int blockId) {
        return blocks.stream()
                .filter(b -> b.getBlockId() == blockId)
                .filter(b -> b.getClass() == klass)
                .map(b -> (T) b)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No block matching type " + klass + " and id " + blockId));
    }

    /**
     * Retrieves a list of {@link CardBlock} objects in the order that cards logically appear in the stack (that is,
     * the order that a HyperCard user would encounter them), not the physical order that they appear in the stack
     * file structure.
     *
     * @return A list of all the stack's cards, in the order they appear to the user in the stack.
     */
    public List<CardBlock> getCardBlocks() {
        return getBlock(ListBlock.class).getCards();
    }

    public BufferedImage getImage(int bitmapId) {
        return blocks.stream()
                .filter(b -> b.getBlockId() == bitmapId)
                .filter(b -> b.getClass() == ImageBlock.class)
                .map(b -> ((ImageBlock) b).getImage())
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
