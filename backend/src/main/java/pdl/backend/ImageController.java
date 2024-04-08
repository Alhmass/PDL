package pdl.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    if (img.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    if (img.get().getName().endsWith(".png")) {
      MediaType ImgType = MediaType.IMAGE_PNG;
    } else {
      MediaType ImgType = MediaType.IMAGE_JPEG;
    }
    return ResponseEntity
        .ok()
        .contentType(ImgType)
        .body(img.get().getData());
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    var img = this.imageDao.retrieve(id);
    if (img.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    else {
      File toDelete = new File("./images/" + img.get().getName());
      File toDeleteTag = new File("./images/tag/" + img.get().getName() + ".txt");
      if (toDelete.exists())
        toDelete.delete();
      if (toDeleteTag.exists())
        toDeleteTag.delete();
      this.sqlController.deleteImage(img.get().getId());
      this.imageDao.delete(img.get());
      return new ResponseEntity<>(HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/images/{id}/tags", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public List<String> getImageTags(@PathVariable("id") long id) {
    List<String> list = new ArrayList<>();
    for (String item : this.sqlController.getTags(id)) {
      list.add(item);
    }
    return list;
  }

  @RequestMapping(value = "/images/{id}/similar", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ResponseEntity<?> similar(@PathVariable("id") long id, @RequestParam("number") String n,
      @RequestParam("descriptor") String descr, @RequestParam("tags") Optional<String> tag) {
    if (this.imageDao.retrieve(id).isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String containsOnlyNB = "\\d+";
    if (!Pattern.matches(containsOnlyNB, n)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    int nb = Integer.parseInt(n);
    List<Object> res;
    if (descr.equals("histogram2D")) {
      res = this.sqlController.getSimilarImages2D(id, nb);
    } else if (descr.equals("histogram3D")) {
      res = this.sqlController.getSimilarImages3D(id, nb);
    } else if (descr.equals("tags")) {
      if (tag.isPresent()) {
        res = this.sqlController.getSimilarTags(id, tag.get(), nb);
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
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
      node.put("type", res_img[img].getMediaType(res_img[img].getName()));
      node.put("size", res_img[img].getSize(res_img[img].getName()));
      nodes.add(node);
    }
    return ResponseEntity
        .ok()
        .body(nodes);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      @RequestParam("tags") Optional<String> tag,
      RedirectAttributes redirectAttributes) throws IOException {
    if (file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)
        || file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
      Image nimg = new Image(file.getOriginalFilename(), file.getBytes());
      this.imageDao.create(nimg);
      if (tag.isPresent()) {
        this.sqlController.addImage(nimg, tag.get());
      } else {
        this.sqlController.addImage(nimg, "");
      }
      try {
        FileOutputStream nFile = new FileOutputStream("./images/" + file.getOriginalFilename());
        nFile.write(file.getBytes());
        nFile.flush();
        nFile.close();
      } catch (IOException e) {
        throw new RuntimeException("Cannot create file in images folder!");
      }
      if (tag.isPresent() && !tag.get().equals("")) {
        try {
          FileWriter tagFile = new FileWriter("./images/tag/" + file.getOriginalFilename() + ".txt");
          tagFile.append(tag.get());
          tagFile.close();
        } catch (Exception e) {
          System.out.println("Failed to create tag file for : " + file.getOriginalFilename());
        }
      }
      return new ResponseEntity<>(HttpStatus.CREATED);
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
      node.put("type", item.getMediaType(item.getName()));
      node.put("size", item.getSize(item.getName()));
      nodes.add(node);
    }
    return nodes;
  }

  @RequestMapping(value = "/images/search", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ResponseEntity<?> searchTags(@RequestParam("tags") String tag) {
    List<Image> res = this.sqlController.searchTags(tag);
    ArrayNode nodes = mapper.createArrayNode();
    for (Image img : res) {
      ObjectNode node = mapper.createObjectNode();
      node.put("id", img.getId());
      node.put("name", img.getName());
      node.put("type", img.getMediaType(img.getName()));
      node.put("size", img.getSize(img.getName()));
      nodes.add(node);
    }
    return ResponseEntity
        .ok()
        .body(nodes);
  }

  @RequestMapping(value = "/images/{id}/filter", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ResponseEntity<?> imageFilter(@PathVariable("id") long id, @RequestParam("filter") String filter,
      @RequestParam("number") Optional<String> number) {
    if (this.imageDao.retrieve(id).isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // Declaration of the image to return, set to the original image by default
    Image input_image = this.imageDao.retrieve(id).get();
    Image output_image;

    // Apply the filter "Gray Level" to the image, who convert the image to a gray
    // level image
    if (filter.equals("GrayLevel")) {
      output_image = Filtres.GrayLevel(input_image);
    } else {
      int n;

      // Check if the parameter number is present and convert it to an integer
      if (number.isPresent()) {
        n = Integer.parseInt(number.get());
      } else {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
      }

      // Apply the "brightness" filter to the image, who change the brightness of the
      // image
      if (filter.equals("brightness"))
        output_image = Filtres.Brightness(input_image, n);

      // Apply the "mean" filter to the image, who apply a blur effect to the image
      else if (filter.equals("mean"))
        output_image = Filtres.Mean(input_image, n);

      // Apply an "coloration" filter to the image, who change the color of each pixel
      // depend of the RGB value given in the parameter number
      else if (filter.equals("colors")) {
        String nb = number.get();
        int r = Integer.parseInt(nb.substring(0, 3));
        int g = Integer.parseInt(nb.substring(3, 6));
        int b = Integer.parseInt(nb.substring(6, 9));
        output_image = Filtres.Coloration(input_image, r, g, b);
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    imageDao.create(output_image);

    ArrayNode nodes = mapper.createArrayNode();
      ObjectNode node = mapper.createObjectNode();
      node.put("id", output_image.getId());
      node.put("name", output_image.getName());
      nodes.add(node);

    // Return the response
    return ResponseEntity
        .ok()
        .body(nodes);
  }
}