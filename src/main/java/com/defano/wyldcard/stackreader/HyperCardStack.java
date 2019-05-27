package com.defano.wyldcard.stackreader;

import com.defano.wyldcard.stackreader.block.*;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.misc.UnsupportedVersionException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a HyperCard stack file.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class HyperCardStack {

    private static final Logger LOG = LoggerFactory.getLogger(HyperCardStack.class);
    private final List<Block> blocks = new ArrayList<>();

    public static HyperCardStack fromFile(File f) throws FileNotFoundException, ImportException {
        return fromInputStream(new FileInputStream(f));
    }

    public static HyperCardStack fromInputStream(InputStream sis) throws ImportException {
        HyperCardStack stack = new HyperCardStack();
        stack.unpack(new StackInputStream(sis));
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
     * Gets the first block in the file matching the requested class.
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

    public <T extends Block> boolean hasBlock(Class<T> klass) {
        return blocks.stream().anyMatch(b -> b.getClass() == klass);
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
     * @param klass   The class of the de-serialized block type. Use {@link BlockType#blockClass()} as a valid argument.
     * @param blockId The id of the block to retrieve.
     * @param <T>     Any subclass of {@link Block} representing the type of the block to return.
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
        if (hasBlock(ListBlock.class)) {
            return getBlock(ListBlock.class).getCards();
        } else {
            return getBlocks(CardBlock.class);
        }
    }

    public BufferedImage getImage(int bitmapId) {
        return blocks.stream()
                .filter(b -> b.getBlockId() == bitmapId)
                .filter(b -> b.getClass() == ImageBlock.class)
                .map(b -> ((ImageBlock) b).getImage())
                .findFirst()
                .orElse(null);
    }

    private void unpack(StackInputStream fis) throws ImportException {
        BlockType blockType = null;

        try {

            do {
                int blockSize = fis.readInt() & 0xffff;     // 4-byte block size, but never > 64KB
                int blockTypeId = fis.readInt();            // 4-byte block identifier (i.e, 'STAK')
                int blockId = fis.readInt();                // 4-byte block id number
                fis.readInt();                              // 4-byte padding, ignored

                LOG.debug("Unpacking {} bytes of block {}, id={}", blockSize, BlockType.fromBlockId(blockTypeId), blockId);

                byte[] blockData = fis.readBytes(blockSize - 16);
                blockType = BlockType.fromBlockId(blockTypeId);
                Block block = blockType.instantiate(this, blockId, blockSize, blockData);

                blocks.add(block);

                if (block instanceof StackBlock && block.getMajorVersion(((StackBlock) block).getModifyVersion()) < 2) {
                    throw new UnsupportedVersionException("Cannot import stacks from HyperCard 1.x. Please use the \"Convert Stack...\" command in HyperCard 2.x to update this stack.");
                }

            } while (blockType != BlockType.TAIL);

        } catch (IOException e) {
            throw new ImportException("Malformed block of type " + blockType + "; stack structure is corrupt.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(blocks.stream().map(Block::getBlockType).toArray(), ToStringStyle.SIMPLE_STYLE);
    }
}
