package com.fileserver.app;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
//import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.fileserver.model.RawFile;

@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class FileServerApplication {

	public final static String ROOTFOLDER = "root";

	@RequestMapping("/")
	RedirectView home() {
		return new RedirectView("index.html");
	}

	@RequestMapping("/curdir")
	@ResponseBody
	String curDir() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		return s;
	}

	@RequestMapping("/goto")
	@ResponseBody
	List<RawFile> folder(@RequestParam("path") String p) {
		try {
			File foundf = new File(ROOTFOLDER + File.separator + p);
			if (!foundf.exists() || (!foundf.isFile() && !foundf.isDirectory())) {
				return null;
			}
			if (foundf.isDirectory()) {
				return getAllFiles(ROOTFOLDER + File.separator + p);
			}

			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	FileSystemResource downloadfile(HttpServletResponse response, @RequestParam("file") String p) {
		try {
			File foundf = new File(ROOTFOLDER + File.separator + p);
			if (!foundf.exists() || !foundf.isFile() || !CheckRoot(foundf)) {
				return null;
			}
			if (!foundf.isFile()) {
				return null;
			}
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\";", foundf.getName()));
			return new FileSystemResource(foundf);
		} catch (Exception ex) {

		}
		return null;
	}

	@PostMapping("/upload")
	@ResponseBody
	public String handleFileUpload(HttpServletResponse response, @RequestParam("file") MultipartFile file,
			@RequestParam("path") String path) {
		File wfile = new File(ROOTFOLDER + File.separator + path + File.separator + file.getOriginalFilename());
		if (!CheckRoot(wfile)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(wfile);
			IOUtils.copy(file.getInputStream(), out);
			return "You successfully uploaded " + file.getOriginalFilename() + " (" + file.getSize() + ") to /root/"
					+ path + "!";
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return null;
		} finally {
			try {
				file.getInputStream().close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	private boolean CheckRoot(File f) {
		try {
			// ensure requesting path is inside ROOT folder
			String checkPath = new File(ROOTFOLDER).getAbsolutePath();
			return f.getCanonicalPath().startsWith(checkPath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private List<RawFile> getAllFiles(String path) {
		List<RawFile> ret = new ArrayList<>();

		File folder = new File(path);
		if (!CheckRoot(folder)) {
			return ret;
		}

		if (folder.isDirectory()) {
			for (File f : folder.listFiles()) {
				try {
					ret.add(new RawFile(f));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (folder.isFile()) {
			ret.add(new RawFile(folder));
		}

		return ret;
	}

	public static void main(String[] args) {
		SpringApplication.run(FileServerApplication.class, args);
	}
}