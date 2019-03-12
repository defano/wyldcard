package com.defano.wyldcard.importer;

import com.defano.wyldcard.importer.block.Block;
import com.defano.wyldcard.importer.result.ImportResult;
import com.defano.wyldcard.importer.type.BlockType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class HyperCardStack {

    private final List<Block> blockList = new ArrayList<>();

    public static void main(String[] argv) throws FileNotFoundException, ImportException {
        HyperCardStack.fromFile(new File("/Users/matt/Home"), new ImportResult());
//        HyperCardStack.fromFile(new File("/Users/matt/Addresses"));
//        fromFile(new File("/Users/matt/Practice"), new ImportResult());
    }

    public static HyperCardStack fromFile(File f, ImportResult result) throws FileNotFoundException, ImportException {
        return fromInputStream(new FileInputStream(f), result);
    }

    public static HyperCardStack fromInputStream(InputStream sis, ImportResult result) throws ImportException {
        HyperCardStack stack = new HyperCardStack();
        stack.deserialize(new StackInputStream(sis), result == null ? new ImportResult() : result);
        return stack;
    }

    public List<Block> getBlocks() {
        return blockList;
    }

    public List<Block> getBlocks(BlockType type) {
        return blockList.stream()
                .filter(b -> b.getBlockType() == type)
                .collect(Collectors.toList());
    }

    public Optional<Block> getBlock(BlockType type) {
        return blockList.stream()
                .filter(b -> b.getBlockType() == type)
                .findFirst();
    }

    public Optional<Block> getBlock(int blockId) {
        return blockList.stream()
                .filter(b -> b.getBlockId() == blockId)
                .findFirst();
    }

    private void deserialize(StackInputStream fis, ImportResult report) throws ImportException {
        BlockType blockType;

        try {
            do {
                int blockSize = fis.readInt();
                int blockTypeId = fis.readInt();
                int blockId = fis.readInt();
                fis.readInt();  // Padding; ignored
                byte[] data = fis.readBytes(blockSize - 16);

                blockType = BlockType.fromBlockId(blockTypeId);

                Block block = blockType.instantiateBlock(this, blockId, blockSize);
                if (block != null) {
                    block.deserialize(data, report);
                    blockList.add(block);
                }

            } while (blockType != BlockType.TAIL);

        } catch (IOException e) {
            report.error(null, "Malformed block array; stack structure is corrupt.", e);
        }
    }
}
