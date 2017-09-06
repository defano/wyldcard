package com.defano.hypercard.context;

import com.defano.hypertalk.exception.HtSemanticException;

import java.io.*;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class FileTable {

    private final static FileTable instance = new FileTable();
    private final Set<FileHandle> openFiles = new HashSet<>();

    private FileTable() {}

    public static FileTable getInstance() {
        return instance;
    }

    public FileHandle open(String filename) throws HtSemanticException {
        if (getOpenFileHandle(filename) != null) {
            throw new HtSemanticException("File " + filename + " is already open.");
        }

        FileHandle handle = new FileHandle(filename);
        openFiles.add(handle);
        return handle;
    }

    public void close(String filename) throws HtSemanticException {
        FileHandle handle = getOpenFileHandle(filename);
        if (handle != null) {
            handle.close();
            openFiles.remove(handle);
        } else {
            throw new HtSemanticException("File " + filename + " cannot be closed because it is not open.");
        }
    }

    public FileHandle getOpenFileHandle(String filename) {
        for (FileHandle thisHandle : openFiles) {
            if (thisHandle.isIdentifiedBy(filename)) {
                return thisHandle;
            }
        }

        return null;
    }

    public class FileHandle {
        private final File file;
        private final FileWriter writer;
        private int cursor = 0;

        private FileHandle (String filename) throws HtSemanticException {
            file = new File(filename);
            try {
                writer = new FileWriter(file);
            } catch (IOException e) {
                throw new HtSemanticException("Can't open file " + filename, e);
            }
        }

        private void close() throws HtSemanticException {
            try {
                this.writer.close();
            } catch (IOException e) {
                throw new HtSemanticException("Can't close file " + file.getName());
            }
        }

        public String readAll() throws HtSemanticException {
            Scanner scanner = null;
            try {
                scanner = new Scanner(file).useDelimiter("\\Z");
                return scanner.next();
            } catch (FileNotFoundException e) {
                throw new HtSemanticException("An error occurred reading file " + file.getName());
            } catch (NoSuchElementException e) {
                return "";
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }

        public String readFor(int count) throws HtSemanticException {
            String contents = readAll();

            if (contents.length() < cursor + count) {
                throw new HtSemanticException("Cannot read " + count + " characters from " + file.getName() + " because the file isn't that long.");
            }

            String substring = contents.substring(cursor, cursor + count);
            cursor += count;
            return substring;
        }

        public String readAt(int at) throws HtSemanticException {
            String contents = readAll();
            if (at > contents.length()) {
                throw new HtSemanticException("Cannot read file " + file.getName() + " at " + at + " because the file isn't that long.");
            }

            cursor = contents.length();
            return contents.substring(at);
        }

        public String readAt(int at, int count) throws HtSemanticException {
            String contents = readAt(at);
            if (count > contents.length()) {
                throw new HtSemanticException("Cannot read " + count + " characters at " + at + " because the file isn't that long.");
            }

            cursor = at + count;
            return contents.substring(0, count);
        }

        public String readUntil(int at, String until) throws HtSemanticException {
            String contents = readAt(at);

            int index = contents.indexOf(until);
            if (index < 0) {
                index = contents.length();
            }

            cursor = at + index;
            return contents.substring(0, index);
        }

        public void write(String text) throws HtSemanticException {
            write(text, cursor);
        }

        public void write(String text, int at) throws HtSemanticException {
            try {
                writer.write(text, at, text.length());
            } catch (IOException e) {
                throw new HtSemanticException("An error occurred writing to " + file.getName());
            }
        }

        private boolean isIdentifiedBy(String filename) {
            return new File(filename).getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath());
        }
    }

}
