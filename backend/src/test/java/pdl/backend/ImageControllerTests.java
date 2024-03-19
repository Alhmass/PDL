package pdl.backend;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
	@Test
	@Order(1)
	public void getImageListShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(get("/images")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	@Order(2)
	public void getImageShouldReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/images/1")).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	@Order(3)
	public void getImageShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(get("/images/0")).andExpect(status().isOk());
	}

	@Test
	@Order(4)
	public void deleteImagesShouldReturnMethodNotAllowed() throws Exception {
	this.mockMvc.perform(delete("/images")).andDo(print()).andExpect(status().isMethodNotAllowed());
	}

	@Test
	@Order(5)
	public void deleteImageShouldReturnNotFound() throws Exception {
		this.mockMvc.perform(delete("/images/1")).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	@Order(6)
	public void deleteImageShouldReturnSuccess() throws Exception {
		this.mockMvc.perform(delete("/images/0")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	@Order(7)
	public void createImageShouldReturnSuccess() throws Exception {
		final ClassPathResource imgFile = new ClassPathResource("test.jpg");
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", imgFile.getInputStream());
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(multipartFile)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@Order(8)
	public void createImageShouldReturnUnsupportedMediaType() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "text/plain", "Test".getBytes());
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(multipartFile)).andDo(print())
				.andExpect(status().isUnsupportedMediaType());
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

	@Test
	@Order(11)
	public void checkJsonFormat() throws Exception {
		String result = this.mockMvc.perform(get("/images")).andReturn().getResponse().getContentAsString();
		String[] images = result.split("},");
		for (int i = 0; i < images.length; i++) {
			String[] image = images[i].split(",");
			if (!image[0].contains("\"id\":\"") || !image[1].contains("\"name\":\"") || !image[2].contains("\"MediaType\":\"") || !image[3].contains("\"size\":\"")) {
				fail("Erreur: Le format du json n'est pas correct");
			}
		}
	}
}