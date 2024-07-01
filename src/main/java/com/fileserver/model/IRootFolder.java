package com.fileserver.model;

import java.io.File;

/**
 * Implements this interface for determining if a file is under the root folder
 * defined by the server application.
 */
public interface IRootFolder {
    boolean isUnderRootFolder(File path);
}
