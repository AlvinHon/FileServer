package com.fileserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fileserver.app.FileServerController;
import com.fileserver.app.FileService;
import com.fileserver.model.AppFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = FileServerController.class)
@AutoConfigureMockMvc
public class FileServerControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileService fileService;

	@Test
	public void testHomePage() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection());
		mockMvc.perform(get("/index.html"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
	}

	@Test
	public void testCurrentDirectory() throws Exception {
		mockMvc.perform(get("/curdir"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGotoEmptyFolder() throws Exception {
		when(fileService.folder(any())).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/goto?path=emptyfolder"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().string("[]"));
	}

	@Test
	public void testGotoFolder() throws Exception {
		when(fileService.folder(any())).thenReturn(new ArrayList<>() {
			{
				add(new AppFile(new File("test.txt")));
			}
		});
		mockMvc.perform(get("/goto?path=folder"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content()
						.string("[{\"fileName\":\"test.txt\",\"isFolder\":false,\"length\":0,\"lastModified\":0}]"));
	}

	@Test
	public void testDownloadFile() throws Exception {
		var tmp_file = File.createTempFile("file", "");
		tmp_file.deleteOnExit();
		when(fileService.downloadFile(any()))
				.thenReturn(fileService.new DownloadFileResult(new FileSystemResource(tmp_file), "file"));
		mockMvc.perform(get("/download?file=file"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM_VALUE))
				.andExpect(content().bytes(new byte[] {}));
	}

	@Test
	public void testUploadFile() throws Exception {
		final int save_filesize = 1;
		when(fileService.uploadFile(any(), any()))
				.thenReturn(fileService.new UploadFileResult(save_filesize));

		mockMvc.perform(multipart("/upload?path=folder").file("file", new byte[] {}))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().string(save_filesize + ""));
	}
}