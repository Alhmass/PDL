package pdl.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;
  private SQLController sqlController;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
    // check if the folder "images" exist
    File dirImage = new File("./images");
    if (!dirImage.exists() || !dirImage.isDirectory()) {
      throw new RuntimeException("The folder images does not exist");
    }

    // Create the filter ".png" and ".jpg"
    String[] extensions = { ".jpg", ".png", ".jpeg" };
    ImageFilter filter = new ImageFilter(extensions, 3);

    // Store all the file name who ended with ".png" or ".jpg"
    String[] files = dirImage.list(filter);

    // Debug Message if the folder "images" doesn't contain ".png" or ".jpg" files
    if (files.length == 0) {
      System.out.println("Image Directory empty");
    }

    // Store all the images in the ImageDao of ImageController
    for (int i = 0; i < files.length; ++i) {
      File file = new File("./images/" + files[i]);
      byte[] byteArray = new byte[(int) file.length()];
      try (FileInputStream inputStream = new FileInputStream(file)) {
        inputStream.read(byteArray);
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
      Image newImg = new Image(files[i], byteArray);
      this.imageDao.create(newImg);
    }
    this.sqlController = new SQLController(this.imageDao);
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    var img = this.imageDao.retrieve(id);
    if (img.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    else {
      return ResponseEntity
          .ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(img.get().getData());
    }
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    if (id == 0)
      return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    var img = this.imageDao.retrieve(id);
    if (img.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    else {
      this.imageDao.delete(img.get());
      return new ResponseEntity<>(HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) throws IOException {
    if (file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
      Image nimg = new Image(file.getOriginalFilename(), file.getBytes());
      this.imageDao.create(nimg);
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    for (Image item : this.imageDao.retrieveAll()) {
      ObjectNode node = mapper.createObjectNode();
      node.put("name", item.getName());
      node.put("id", item.getId());
      nodes.add(node);
    }
    return nodes;
  }

}
