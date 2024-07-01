package com.fileserver.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fileserver.model.AppFile;
import com.fileserver.model.StoreFileValidator;

/**
 * File Service
 */
@Service
public class FileService {

    /**
     * Get the list of files in a folder
     * 
     * @param storeFileValidator the store file validator
     * @return the list of files in a folder
     */
    public List<AppFile> folder(StoreFileValidator storeFileValidator) {
        try {
            File foundf = storeFileValidator.validateDirectory();
            if (foundf == null) {
                return null;
            }
            List<AppFile> ret = new ArrayList<>();
            for (File f : foundf.listFiles()) {
                ret.add(new AppFile(f));
            }
            return ret;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Result of the download file operation
     */
    public class DownloadFileResult {
        public final FileSystemResource file;
        public final String filename;

        public DownloadFileResult(FileSystemResource file, String filename) {
            this.file = file;
            this.filename = filename;
        }
    }

    /**
     * Download a file
     * 
     * @param storeFileValidator the store file validator
     * @return the result of the download file operation
     */
    public DownloadFileResult downloadFile(StoreFileValidator storeFileValidator) {
        try {
            File foundf = storeFileValidator.validateFile();
            if (foundf == null) {
                return null;
            }
            return new DownloadFileResult(new FileSystemResource(foundf), foundf.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Result of the upload file operation
     */
    public class UploadFileResult {
        public final int size;

        public UploadFileResult(int size) {
            this.size = size;
        }
    }

    /**
     * Upload a file
     * 
     * @param file               the file to upload
     * @param storeFileValidator the store file validator
     * @return the result of the upload file operation
     */
    public UploadFileResult uploadFile(MultipartFile file, StoreFileValidator storeFileValidator) {
        File file_to_save = storeFileValidator.validateNewFile(file.getOriginalFilename());
        if (file_to_save == null) {
            return null;
        }
        try (FileOutputStream out = new FileOutputStream(file_to_save); InputStream file_in = file.getInputStream()) {
            return new UploadFileResult(IOUtils.copy(file_in, out));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}