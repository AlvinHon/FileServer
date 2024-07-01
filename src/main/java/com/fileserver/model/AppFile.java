package com.fileserver.model;

import java.io.File;

/**
 * AppFile
 * 
 * This class is a model class for the file wrapper on File. It is the returned
 * type to the web client as a JSON object.
 */
public class AppFile {
    private File file;

    private String fileName;
    private boolean isFolder;
    private long length;
    private long lastModified;

    public AppFile(File f) {
        this.file = f;
        setIsFolder(f.isDirectory());
        setLength(f.length());
        setLastModified(f.lastModified());
        setFileName(file.getName());
    }

    public File rawFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fn) {
        fileName = fn;
    }

    public boolean getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isf) {
        isFolder = isf;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long l) {
        length = l;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lm) {
        lastModified = lm;
    }
}