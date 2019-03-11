package com.defano.wyldcard.importer;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.importer.type.BlockType;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HyperCardImporter {

    public static void main(String[] argv) throws FileNotFoundException {
        HyperCardStack.fromFile(new File("/Users/matt/Home"));
    }

    public void importStack(ExecutionContext context) {
        File stackFile = findFile(context);
        if (stackFile != null) {
            importStack(context, stackFile);
        }
    }

    public void importStack(ExecutionContext context, File stackFile) {
        try {

            try {
                StackInputStream fis = new StackInputStream(stackFile);

                int blockSize = fis.readInt();
                int blockType = fis.readInt();
                int blockId = fis.readInt();
                int ignored = fis.readInt();

                byte[] data = new byte[blockSize - 16];
                fis.read(data);

                BlockType type = BlockType.fromBlockId(blockType);
//                Block thisBlock = type.readBlock(blockSize, blockId, data);

                System.err.println(type);
//                System.err.println(((StackBlock) thisBlock).getStackScript());

//                char blockType1 = (char) fis.readByte();
//                char blockType2 = (char) fis.readByte();
//                char blockType3 = (char) fis.readByte();
//                char blockType4 = (char) fis.readByte();

                System.err.println("Size: " + blockSize + " type: " + type);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public File findFile(ExecutionContext context) {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), "Import Stack", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(StackModel.FILE_EXTENSION));
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            return fd.getFiles()[0];
        }

        return null;
    }

}
