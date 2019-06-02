package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A reference to a file that is logically opened for reading and writing by scripts.
 */
public class FileHandle {

    private final File file;            // The file associated with this handle
    private StringBuilder contents;     // The in-memory buffer of file contents
    private FileWriter writer;          // The writer used to mutate file contents
    private int cursor = 0;             // The index at which we're reading/writing the file contents

    FileHandle(String filename) {
        file = new File(filename);
    }

    /**
     * Resets the cursor (position at which file is being read or written) to zero.
     */
    void reset() {
        this.cursor = 0;
    }

    /**
     * Flushes the buffer and closes the file associated with this handle. Has no effect if the file is not open.
     *
     * @throws HtSemanticException If an error occurs closing the file or writing its buffer to disk
     */
    void close() throws HtException {
        if (writer != null) {
            try {
                writer.write(contents.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new HtSemanticException("An error occurred writing to file " + file.getName());
            }
        }
    }

    /**
     * Reads and returns the entire contents of the file.
     *
     * @param updateCursor When true, the file cursor will be updates to point to the end of the file.
     * @return The entire contents of the file
     * @throws HtSemanticException Thrown if the file does not exist or cannot be read.
     */
    public String readAll(boolean updateCursor) throws HtSemanticException {

        if (contents != null) {
            return contents.toString();
        }

        contents = new StringBuilder(getFileContents());

        if (updateCursor) {
            cursor = contents.length();
        }

        return contents.toString();
    }

    /**
     * Reads a given number of characters from the file starting from the cursor position.
     *
     * @param count        The number of characters to read.
     * @param updateCursor When true, the cursor will be incremented by count.
     * @return The characters read from the file.
     * @throws HtSemanticException Thrown if the file does not exist or cannot be read.
     */
    public String readFor(int count, boolean updateCursor) throws HtSemanticException {
        String allContents = readAll(false);

        if (allContents.length() < cursor + count) {
            count = allContents.length() - cursor;
        }

        String substring = allContents.substring(cursor, cursor + count);
        if (updateCursor) {
            cursor += count;
        }
        return substring;
    }

    /**
     * Reads the remainder of the file starting a given position or offset.
     *
     * @param at           The position (offset) at which to start reading.
     * @param updateCursor When true, the cursor will be updated to point to the end of the file.
     * @return The characters read from the file.
     * @throws HtSemanticException Thrown if the file does not exist or cannot be read.
     */
    public String readAt(int at, boolean updateCursor) throws HtSemanticException {
        String allContents = readAll(false);

        if (allContents.length() < at) {
            at = allContents.length();
        }

        if (updateCursor) {
            cursor = allContents.length();
        }

        return allContents.substring(at);
    }

    /**
     * Reads a given number of characters starting at a given position in the file.
     *
     * @param at           The position (offset) at which to start reading.
     * @param count        The number of characters to be read.
     * @param updateCursor When true, the cursor will be updated to point at the last character read.
     * @return The characters read from the file.
     * @throws HtSemanticException Thrown if the file does not exist or cannot be read.
     */
    public String readAt(int at, int count, boolean updateCursor) throws HtSemanticException {
        String contentsAt = readAt(at, false);

        if (contentsAt.length() < count) {
            count = contentsAt.length() - cursor;
        }

        if (updateCursor) {
            cursor = at + count;
        }

        return contentsAt.substring(0, count);
    }

    /**
     * Reads the file until the given string is reached or the end of the file.
     *
     * @param until        The string pattern to find (case insensitive)
     * @param updateCursor When true, the cursor will be updated to point at the last character read.
     * @return The characters read from the file
     * @throws HtSemanticException Thrown if the file does not exist or cannot be read.
     */
    public String readUntil(String until, boolean updateCursor) throws HtSemanticException {
        String contentsAt = readAt(cursor, false);

        int index = contentsAt.toLowerCase().indexOf(until.toLowerCase());
        if (index < 0) {
            index = contentsAt.length();
        } else {
            index += until.length();        // Include pattern in read data
        }

        if (updateCursor) {
            cursor += index;
        }

        return contentsAt.substring(0, index);
    }

    /**
     * Writes a string of characters to the end of the file.
     *
     * @param text         The text to append to the file.
     * @param updateCursor When true, the cursor will be moved to the end of the file.
     * @throws HtSemanticException Thrown if the file cannot be written.
     */
    public void writeAtTail(String text, boolean updateCursor) throws HtSemanticException {
        openForWriting(false);
        contents.append(text);

        if (updateCursor) {
            cursor = contents.length();
        }
    }

    /**
     * Inserts a string of characters to the file beginning at the current cursor location.
     *
     * @param text         The text to write to the file.
     * @param updateCursor When true, the cursor will be updated to point to the last character written to the file.
     * @throws HtSemanticException Thrown if the file cannot be written.
     */
    public void write(String text, boolean updateCursor) throws HtSemanticException {
        openForWriting(true);
        contents.insert(cursor, text);

        if (updateCursor) {
            cursor += text.length();
        }
    }

    /**
     * Inserts a string of characters to the file starting at a given position.
     *
     * @param text         The text to be written to the file.
     * @param at           The position at which the text should be written.
     * @param updateCursor When true, the cursor is updated to point at the last character written.
     * @throws HtSemanticException Thrown if the file cannot be written.
     */
    public void writeAt(String text, int at, boolean updateCursor) throws HtSemanticException {
        openForWriting(false);

        if (at < 0) {
            at = contents.length() - Math.abs(at);
        }

        if (at < 0) {
            at = 0;
        }

        if (at > contents.length()) {
            at = contents.length();
        }

        contents.insert(at, text);

        if (updateCursor) {
            cursor = at + text.length();
        }
    }

    /**
     * Determines if the given file name or file path refers to the file represented by this FileHandle.
     *
     * @param filename The name (optionally including a path) to a file
     * @return True of the filename refers to this file; false otherwise
     */
    public boolean isIdentifiedBy(String filename) {
        return new File(filename).getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath());
    }

    /**
     * Reads and returns the entire contents of this file.
     *
     * @return The contents of this file
     * @throws HtSemanticException Thrown if the file does not exist.
     */
    private String getFileContents() throws HtSemanticException {
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\Z");
            return scanner.next();
        } catch (FileNotFoundException e) {
            throw new HtSemanticException("The file " + file.getAbsolutePath() + " does not exist.");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Returns the entire contents of this file or the empty string if the file does not exist or cannot be read.
     *
     * @return The entire contents of this file or the empty string
     */
    private String getFileContentsOrEmpty() {
        try {
            return getFileContents();
        } catch (HtSemanticException e) {
            return "";
        }
    }

    /**
     * Attempts to open this file (on the file system) for writing. Has no effect if the file is already open for
     * writing.
     *
     * @param overwriteContents When true, the contents of the file will be overwritten provided the file exists and
     *                          it has not already been loaded into memory.
     * @throws HtSemanticException Thrown if an error occurs opening the file for writing.
     */
    private void openForWriting(boolean overwriteContents) throws HtSemanticException {

        // File already open for writing
        if (writer != null) {
            return;
        }

        // Open for writing overwrites previous contents
        if (contents == null) {
            if (overwriteContents) {
                contents = new StringBuilder();
            } else {
                contents = new StringBuilder(getFileContentsOrEmpty());
            }
        }

        // Open file for writing, or die
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new HtSemanticException("An error occurred writing to file " + file.getAbsolutePath() + ".");
        }
    }
}
