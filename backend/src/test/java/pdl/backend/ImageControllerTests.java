package pdl.backend;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	public static void reset() {
  	// reset Image class static counter
  	ReflectionTestUtils.setField(Image.class, "count", Long.valueOf(0));
	}
	/*@Test
	@Order(1)
	public void getImageListShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(get("/images")).andDo(print()).andExpect(status().isOk()).andExpect(content().json("[{'name':'logo.jpg','id': 0}]"));
	}*/

	@Test
	@Order(2)
	public void getImageShouldReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/images/99")).andExpect(status().isNotFound());
	}

	@Test
	@Order(3)
	public void getImageShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(get("/images/0")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.IMAGE_JPEG));
	}

	@Test
	@Order(4)
	public void deleteImagesShouldReturnMethodNotAllowed() throws Exception {
		this.mockMvc.perform(delete("/images/0")).andExpect(status().isMethodNotAllowed());
	}

	/*@Test
	@Order(5)
	public void deleteImageShouldReturnNotFound() throws Exception {
		this.mockMvc.perform(delete("/images/1")).andExpect(status().isNotFound());
	}*/

	/*@Test
	@Order(6)
	public void deleteImageShouldReturnSuccess() throws Exception {
		MockMultipartFile testfile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());
		this.mockMvc.perform(multipart("/images").file(testfile)).andExpect(status().isOk());
		this.mockMvc.perform(delete("/images/1")).andExpect(status().isOk());
	}*/

	@Test
	@Order(7)
	public void createImageShouldReturnSuccess() throws Exception {
		MockMultipartFile testfile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());
		this.mockMvc.perform(multipart("/images").file(testfile)).andExpect(status().isOk());
	}

	@Test
	@Order(8)
	public void createImageShouldReturnUnsupportedMediaType() throws Exception {
		MockMultipartFile testfile = new MockMultipartFile("file", "test_unsupported.html", MediaType.TEXT_HTML_VALUE, "<p>test</p>".getBytes());
		this.mockMvc.perform(multipart("/images").file(testfile)).andExpect(status().isUnsupportedMediaType());
	}

	@Test
	@Order(9)
	public boolean checkIfFolderImageExist() throws Exception {
		String env = "./src/test/java/pdl/backend/test/images";
		File file = new File(env);
		if (file.exists()) 
				fail("Error: Le dossier image ne devrait pas exister");
		return true;
	}

	@Test
	@Order(10)
	public boolean checkImageExtension() throws Exception {
		String env = "./src/test/java/pdl/backend/images";
		File file = new File(env);
		if (file.exists()){
			String[] extensions = { ".jpg", ".png", ".jpeg"};
    		ImageFilter filter = new ImageFilter(extensions, 3);

			String[] files = file.list(filter);
			for (int i = 0; i < files.length; ++i) {
				if (!files[i].endsWith(".jpg") && !files[i].endsWith(".png") && !files[i].endsWith(".jpeg")) {
					fail("Erreur: Type d'image incorrect, les images devraient être en .jpg, .png ou .jpeg");
				}
			}
		} else {
			fail("Erreur: Le dossier image devrait exister");
		}
		return true;
	}
}
