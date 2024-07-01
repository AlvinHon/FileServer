package com.fileserver.app;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.fileserver.app.FileService.DownloadFileResult;
import com.fileserver.app.FileService.UploadFileResult;
import com.fileserver.model.AppFile;
import com.fileserver.model.IRootFolder;
import com.fileserver.model.StoreFileValidator;

/**
 * File Server Application
 */
@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class FileServerController implements IRootFolder {

	public final static String ROOTFOLDER = "root";
	public final static String INDEXPATH = "index.html";
	private String rootPath;
	private final FileService fileService;

	public FileServerController(FileService fileService) {
		// create root folder if not exist
		File root = new File(ROOTFOLDER);
		if (!root.exists()) {
			root.mkdir();
		}
		this.rootPath = root.getAbsolutePath();
		this.fileService = fileService;
	}

	/**
	 * Home page
	 */
	@RequestMapping("/")
	public RedirectView home() {
		return new RedirectView(INDEXPATH);
	}

	/**
	 * Get the name of the current directory of the server application
	 * 
	 * @return the name of the current directory of the server application
	 */
	@RequestMapping("/curdir")
	@ResponseBody
	public String curDir() {
		return Paths.get("").toAbsolutePath().toString();
	}

	/**
	 * List all files in the target folder
	 * 
	 * @param path the path of the folder
	 * @return List of AppFile, null if the folder does not exist
	 */
	@RequestMapping("/goto")
	@ResponseBody
	public List<AppFile> folder(@RequestParam("path") String path) {
		StoreFileValidator storeFileValidator = new StoreFileValidator(Path.of(ROOTFOLDER, path), this);
		return this.fileService.folder(storeFileValidator);
	}

	/**
	 * Download a file
	 * 
	 * @param response the http response
	 * @param path     the path of the file
	 * @return FileSystemResource, null if the file does not exist.
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public FileSystemResource downloadFile(HttpServletResponse response, @RequestParam("file") String path) {
		StoreFileValidator storeFileValidator = new StoreFileValidator(Path.of(ROOTFOLDER, path), this);
		DownloadFileResult result = this.fileService.downloadFile(storeFileValidator);

		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\";", result.filename));
		return result.file;
	}

	/**
	 * Upload a file
	 * 
	 * @param response the http response
	 * @param file     the file to upload
	 * @param path     the path of the folder to store the file
	 * @return the number of bytes stored, 0 if the file is not stored
	 */
	@PostMapping("/upload")
	@ResponseBody
	public int uploadFile(HttpServletResponse response, @RequestParam("file") MultipartFile file,
			@RequestParam("path") String path) {
		StoreFileValidator storeFileValidator = new StoreFileValidator(Path.of(ROOTFOLDER, path), this);
		UploadFileResult result = this.fileService.uploadFile(file, storeFileValidator);

		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return 0;
		}

		return result.size;
	}

	/**
	 * Check if the file is under the root folder
	 */
	@Override
	public boolean isUnderRootFolder(File path) {
		try {
			return path.getCanonicalPath().startsWith(this.rootPath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		// Start the application
		SpringApplication.run(FileServerController.class, args);
	}

}