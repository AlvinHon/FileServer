package com.fileserver.model;

import java.io.File;
import java.nio.file.Path;

/**
 * StoreFileValidator is a class to validate the file path.
 */
public class StoreFileValidator {
    private Path path;
    private IRootFolder rootFolder;

    public StoreFileValidator(Path path, IRootFolder rootFolder) {
        this.path = path;
        this.rootFolder = rootFolder;
    }

    /**
     * Validate the file path as the path to a directory
     * 
     * @return the file if it is valid, otherwise null
     */
    public File validateDirectory() {
        File file = this.path.toFile();
        if (!file.exists() || !file.isDirectory() || !rootFolder.isUnderRootFolder(file)) {
            return null;
        }
        return file;
    }

    /**
     * Validate the file path as the path to a file
     * 
     * @return the file if it is valid, otherwise null
     */
    public File validateFile() {
        File file = this.path.toFile();
        if (!file.exists() || !file.isFile() || !rootFolder.isUnderRootFolder(file)) {
            return null;
        }
        return file;
    }

    /**
     * Validate the file path as the path to a directory and create a new file in it
     * 
     * @param name the name of the file
     * @return the file if it is valid, otherwise null
     */
    public File validateNewFile(String name) {
        File file = this.validateDirectory();
        if (file == null) {
            return null;
        }
        File new_file = file.toPath().resolve(name).toFile();
        // sanity check for the input file name
        if (!rootFolder.isUnderRootFolder(new_file)) {
            return null;
        }
        return new_file;
    }
}
