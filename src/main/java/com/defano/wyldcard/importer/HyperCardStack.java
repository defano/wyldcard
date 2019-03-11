package com.defano.wyldcard.importer;

import com.defano.wyldcard.importer.block.Block;
import com.defano.wyldcard.importer.result.Results;
import com.defano.wyldcard.importer.type.BlockType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HyperCardStack {

    private final List<Block> blockList = new ArrayList<>();
    private final Results results = new Results();

    public static HyperCardStack fromFile(File f) throws FileNotFoundException {
        return new HyperCardStack().deserialize(new StackInputStream(f));
    }

    private HyperCardStack deserialize(StackInputStream fis) {
        try {

            BlockType blockType = null;

            do {
                int blockSize = fis.readInt();
                int blockTypeId = fis.readInt();
                int blockId = fis.readInt();
                int padding = fis.readInt();
                byte[] data = fis.readBytes(blockSize - 16);

                blockType = BlockType.fromBlockId(blockTypeId);

                Block block = blockType.getBlockInstance(blockId, blockSize);
                if (block != null) {
                    block.deserialize(data, results);
                }

                blockList.add(block);

            } while (blockType != BlockType.TAIL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public List<Block> getBlocks() {
        return blockList;
    }

    public List<Block> getBlocks(BlockType type) {
        return blockList.stream()
                .filter(b -> b.blockType == type)
                .collect(Collectors.toList());
    }

    public Block getBlock(int blockId) {
        return blockList.stream()
                .filter(b -> b.blockId == blockId)
                .findFirst().orElse(null);
    }

}
