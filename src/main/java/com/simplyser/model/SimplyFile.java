package com.simplyser.model;

import java.io.File;

public class SimplyFile{
    private File file;

    private String fileName;
    private boolean isFolder;
    private boolean canWrite;
    private boolean canRead;
    private boolean canExecute;
    private long length;
    private long lastModified;
    
    public SimplyFile(File f) {
        this.file = f;
        setIsFolder(f.isDirectory());
        setCanWrite(f.canWrite());
        setCanRead(f.canRead());
        setCanExecute(f.canExecute());
        setLength(f.length());
        setLastModified(f.lastModified());
        setFileName(file.getName());
    }

    public File rawFile() { return file; }

    public String getFileName() { return fileName; }
    public void setFileName(String fn) { fileName = fn; }
    public boolean getIsFolder() { return isFolder; }
    public void setIsFolder(boolean isf) { isFolder = isf; }
    public boolean getCanWrite() { return canWrite; }
    public void setCanWrite(boolean cw) { canWrite = cw; }
    public boolean getCanRead() { return canRead; }
    public void setCanRead(boolean cr) { canRead = cr; }
    public boolean getCanExecute() { return canExecute; }
    public void setCanExecute(boolean ce) { canExecute = ce; }
    public long getLength() { return length; }
    public void setLength(long l) { length = l; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lm) { lastModified = lm; }
} 