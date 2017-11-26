package com.defano.hypercard.runtime.serializer;

import com.google.gson.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

class DocumentSerializer implements JsonSerializer<StyledDocument>, JsonDeserializer<StyledDocument> {
    public StyledDocument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        byte[] rtf = Base64.getDecoder().decode(json.getAsString());
        return convertRtfToDocument(rtf);
    }

    public JsonElement serialize(StyledDocument src, Type typeOfSrc, JsonSerializationContext context) {
        byte[] rtf = convertDocumentToRtf(src);
        return new JsonPrimitive(Base64.getEncoder().encodeToString(rtf));
    }

    private StyledDocument convertRtfToDocument(byte[] rtf) {
        StyledDocument doc = new DefaultStyledDocument();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(rtf);

            new RTFEditorKit().read(bais, doc, 0);
            bais.close();

            // RTFEditorKit appears to (erroneously) append a newline when we deserialize; get rid of that.
            doc.remove(doc.getLength() - 1, 1);

            return doc;

        } catch (Exception e) {
            doc = new DefaultStyledDocument();
        }

        return doc;
    }

    private byte[] convertDocumentToRtf(StyledDocument doc) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            new RTFEditorKit().write(baos, doc, 0, doc.getLength());
            baos.close();

            return baos.toByteArray();

        } catch (IOException | BadLocationException e) {
            throw new RuntimeException("An error occurred while saving field contents.", e);
        }
    }

}
