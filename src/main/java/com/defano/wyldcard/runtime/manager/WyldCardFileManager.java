package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.Set;

/**
 * A singleton representing the state of files opened for reading and writing by script.
 */
@Singleton
public class WyldCardFileManager implements FileManager {

    private final Set<FileHandle> openFiles = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public FileHandle open(String filename) {
        FileHandle handle = getFileHandle(filename);

        if (handle != null) {
            handle.reset();
            return handle;
        }

        handle = new FileHandle(filename);
        openFiles.add(handle);
        return handle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(String filename) throws HtException {
        FileHandle handle = getFileHandle(filename);
        if (handle != null) {
            handle.close();
            openFiles.remove(handle);
        } else {
            throw new HtSemanticException("File " + filename + " cannot be closed because it is not open.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileHandle getFileHandle(String filename) {
        for (FileHandle thisHandle : openFiles) {
            if (thisHandle.isIdentifiedBy(filename)) {
                return thisHandle;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeAll() {
        for (FileHandle thisOpenFile : openFiles) {
            try {
                thisOpenFile.close();
            } catch (HtException e) {
                // Nothing to do
            }
        }
        openFiles.clear();
    }

}
