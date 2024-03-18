package pdl.backend;

import java.awt.PageAttributes.MediaType;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;

  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }

  public String getSize(final String name){
    try {
      String url = "./images/"+name;
      File f = new File(url);
      BufferedImage bimg = ImageIO.read(f);
      int width = bimg.getWidth();
      int height = bimg.getHeight();
      String res = String.valueOf(width)+"*"+String.valueOf(height);
      return res;
    }catch(Exception e){
      System.out.println("Error on getSize()");
    }
    return null;
  }

  public String getMediaType(final String name){
    if (name.endsWith(".png")) {
      return "MediaType.IMAGE_PNG"; 
    } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
      return "MediaType.IMAGE_JPEG";
    }
    return "null";
  }
}