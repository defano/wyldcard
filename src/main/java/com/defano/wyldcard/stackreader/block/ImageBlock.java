/*
 * This code is derived from WOBAFormat.java, a part of Kreative
 * Software's PowerPaint application. The original source code can
 * be found on GitHub, here: https://github.com/kreativekorp/powerpaint/blob/master/main/java/PowerPaint/src/com/kreative/paint/format/WOBAFormat.java
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 *
 * @author Rebecca G. Bettencourt, Kreative Software
 * @author Matt DeFano
 */

package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.decoder.WOBAImageDecoder;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("unused")
public class ImageBlock extends Block implements WOBAImageDecoder {

    private short bitmapTop;         // top of the card rectangle
    private short bitmapLeft;        // left of the card rectangle
    private short bitmapBottom;      // bottom of the card rectangle
    private short bitmapRight;       // right of the card rectangle
    private short maskBoundTop;      // top of the mask bounding rectangle
    private short maskBoundLeft;     // left of the mask bounding rectangle
    private short maskBoundBottom;   // bottom of the mask bounding rectangle
    private short maskBoundRight;    // right of the mask bounding rectangle
    private short imageBoundTop;     // top of the image bounding rectangle
    private short imageBoundLeft;    // left of the image bounding rectangle
    private short imageBoundBottom;  // bottom of the image bounding rectangle
    private short imageBoundRight;   // right of the image bounding rectangle
    private int maskSize;            // size of the mask data
    private int imageSize;           // size of the image data
    private byte[] maskData;         // the WOBA-compressed mask data
    private byte[] imageData;        // the WOBA-compressed image data

    private Rectangle boundRect;     // the image bounding rectangle
    private Rectangle maskRect;      // the image mask rectangle
    private Rectangle imageRect;     // the image rectangle
    private BufferedImage image;     // the decoded image

    public ImageBlock(HyperCardStack root, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(root, blockType, blockSize, blockId, blockData);
    }

    public short getBitmapTop() {
        return bitmapTop;
    }

    public short getBitmapLeft() {
        return bitmapLeft;
    }

    public short getBitmapBottom() {
        return bitmapBottom;
    }

    public short getBitmapRight() {
        return bitmapRight;
    }

    public short getMaskBoundTop() {
        return maskBoundTop;
    }

    public short getMaskBoundLeft() {
        return maskBoundLeft;
    }

    public short getMaskBoundBottom() {
        return maskBoundBottom;
    }

    public short getMaskBoundRight() {
        return maskBoundRight;
    }

    public short getImageBoundTop() {
        return imageBoundTop;
    }

    public short getImageBoundLeft() {
        return imageBoundLeft;
    }

    public short getImageBoundBottom() {
        return imageBoundBottom;
    }

    public short getImageBoundRight() {
        return imageBoundRight;
    }

    public int getMaskSize() {
        return maskSize;
    }

    public int getImageSize() {
        return imageSize;
    }

    public byte[] getMaskData() {
        return maskData;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public Rectangle getBoundRect() {
        return boundRect;
    }

    public Rectangle getMaskRect() {
        return maskRect;
    }

    public Rectangle getImageRect() {
        return imageRect;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void unpack() throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            sis.readShort(4);   // Unknown field; skip

            bitmapTop = sis.readShort();
            bitmapLeft = sis.readShort();
            bitmapBottom = sis.readShort();
            bitmapRight = sis.readShort();
            boundRect = new Rectangle(bitmapLeft, bitmapTop, bitmapRight - bitmapLeft, bitmapBottom - bitmapTop);

            maskBoundTop = sis.readShort();
            maskBoundLeft = sis.readShort();
            maskBoundBottom = sis.readShort();
            maskBoundRight = sis.readShort();
            maskRect = new Rectangle(maskBoundLeft, maskBoundTop, maskBoundRight - maskBoundLeft, maskBoundBottom - maskBoundTop);

            imageBoundTop = sis.readShort();
            imageBoundLeft = sis.readShort();
            imageBoundBottom = sis.readShort();
            imageBoundRight = sis.readShort();
            imageRect = new Rectangle(imageBoundLeft, imageBoundTop, imageBoundRight - imageBoundLeft, imageBoundBottom - imageBoundTop);

            sis.readInt(2); // Unknown field; skip

            maskSize = sis.readInt();
            imageSize = sis.readInt();
            maskData = sis.readBytes(maskSize);
            imageData = sis.readBytes(imageSize);

            image = decodeImage(boundRect, maskRect, imageRect, imageSize, imageData, maskSize, maskData);

            if (image == null) {
                image = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
            }

        } catch (IOException e) {
            throw new ImportException(this, "Malformed image block; stack is corrupt.");
        }
    }
}
