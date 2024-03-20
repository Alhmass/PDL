package pdl.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

  private static final MediaType ImgType = null;

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  private SQLController sqlController;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
    // check if the folder "images" exist
    File dirImage = new File("./images");
    if (!dirImage.exists() || !dirImage.isDirectory()) {
      throw new RuntimeException("The folder images does not exist");
    }

    // Create the filter ".png", ".jpg" and ".jpeg"
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
    if (img.get().getName().endsWith(".png")) {
      MediaType ImgType = MediaType.IMAGE_PNG;
    } else {
      MediaType ImgType = MediaType.IMAGE_JPEG;
    }
    if (img.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    else {
      return ResponseEntity
          .ok()
          .contentType(ImgType)
          .body(img.get().getData());
    }
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    var img = this.imageDao.retrieve(id);
    if (img.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    else {
      File toDelete = new File("./images/" + img.get().getName());
      if (toDelete.exists())
        toDelete.delete();
      this.sqlController.deleteImage(img.get().getId());
      this.imageDao.delete(img.get());
      return new ResponseEntity<>(HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/images/{id}/similar", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ResponseEntity<?> similar(@PathVariable("id") long id, @RequestParam("number") String n,
      @RequestParam("descriptor") String histogram) {
    if (this.imageDao.retrieve(id).isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String containsOnlyNB = "\\d+";
    if (!Pattern.matches(containsOnlyNB, n)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    int nb = Integer.parseInt(n);
    List<Object> res;
    if (histogram.equals("histogram2D")) {
      res = this.sqlController.getSimilarImages2D(id, nb);
    } else if (histogram.equals("histogram3D")) {
      res = this.sqlController.getSimilarImages3D(id, nb);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    Image[] res_img = (Image[]) res.get(0);
    double[] res_dist = (double[]) res.get(1);
    ArrayNode nodes = mapper.createArrayNode();
    for (int img = 0; img < res_img.length; img++) {
      ObjectNode node = mapper.createObjectNode();
      node.put("id", res_img[img].getId());
      node.put("name", res_img[img].getName());
      node.put("similar_score", res_dist[img]);
      node.put("MediaType", res_img[img].getMediaType(res_img[img].getName()));
      node.put("size", res_img[img].getSize(res_img[img].getName()));
      nodes.add(node);
    }
    return ResponseEntity
        .ok()
        .body(nodes);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) throws IOException {
    if (file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)
        || file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
      Image nimg = new Image(file.getOriginalFilename(), file.getBytes());
      this.imageDao.create(nimg);
      this.sqlController.addImage(nimg);
      try {
        FileOutputStream nFile = new FileOutputStream("./images/" + file.getOriginalFilename());
        nFile.write(file.getBytes());
        nFile.flush();
        nFile.close();
      } catch (IOException e) {
        throw new RuntimeException("Cannot create file in images folder!");
      }
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    for (Image item : this.imageDao.retrieveAll()) {
      ObjectNode node = mapper.createObjectNode();
      node.put("name", item.getName());
      node.put("id", item.getId());
      node.put("MediaType", item.getMediaType(item.getName()));
      node.put("size", item.getSize(item.getName()));
      nodes.add(node);
    }
    return nodes;
  }
}
