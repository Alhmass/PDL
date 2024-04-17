package pdl.backend;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Filtres {

    public static Image saveImage(Planar<GrayU8> imageOutput, String name) {
        // Check if the original file is a gif and change the extension to png
        if (name.endsWith(".gif")){
            name = name.replace(".gif", ".png");
        }

        // Save the image
        UtilImageIO.saveImage(imageOutput, "./images/" + name);

        // Open the file associated with the image and take the bytes to create the new image in the Dao
        File file = new File("./images/" + name);
        byte[] byteArray = new byte[(int) file.length()];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            inputStream.read(byteArray);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return new Image(name, byteArray);
    }

    public static Image Brightness(Image img, int intensity) {
        String imagePath = "./images/" + img.getName();

        BufferedImage output = UtilImageIO.loadImage(imagePath);
        Planar<GrayU8> imageOutput = ConvertBufferedImage.convertFromPlanar(output, null, true, GrayU8.class);

        for (int y = 0; y < imageOutput.height; y++) {
            for (int x = 0; x < imageOutput.width; x++) {
                for (int band = 0; band < imageOutput.getNumBands(); band++) {
                    int value = imageOutput.getBand(band).get(x, y);
                    if (value + intensity > 255) {
                        value = 255;
                    } else {
                        value += intensity;
                    }
                    imageOutput.getBand(band).set(x, y, value);
                }
            }
        }

        String outputPath = "brightness_" + img.getName();
        return saveImage(imageOutput, outputPath);
    }

    public static Image GrayLevel(Image img) {
        String imagePath = "./images/" + img.getName();

        BufferedImage output = UtilImageIO.loadImage(imagePath);
        Planar<GrayU8> imageOutput = ConvertBufferedImage.convertFromPlanar(output, null, true, GrayU8.class);

        for (int y = 0; y < imageOutput.height; y++) {
            for (int x = 0; x < imageOutput.width; x++) {
                int colorR = imageOutput.getBand(0).get(x, y);
                int colorG = imageOutput.getBand(1).get(x, y);
                int colorB = imageOutput.getBand(2).get(x, y);

                int color = (int) (0.3 * colorR + 0.59 * colorG + 0.11 * colorB);

                if (color >= 255)
                    color = 255;
                else if (color <= 0)
                    color = 0;

                for (int i = 0; i < imageOutput.getNumBands(); ++i) {
                    imageOutput.getBand(i).set(x, y, color);
                }
            }
        }

        String outputPath = "gray_" + img.getName();
        return saveImage(imageOutput, outputPath);
    }

    public static Image Mean(Image img, int size) {
        String imagePath = "./images/" + img.getName();

        BufferedImage output = UtilImageIO.loadImage(imagePath);
        Planar<GrayU8> imageOutput = ConvertBufferedImage.convertFromPlanar(output, null, true, GrayU8.class);

        int n = size / 2;
        int nb = size * size;
        for (int y = n; y < imageOutput.height - n; y++) {
            for (int x = n; x < imageOutput.width - n; x++) {
                for (int band = 0; band < imageOutput.getNumBands(); band++) {
                    int sum = 0;
                    for (int u = -n; u <= n; u++) {
                        for (int v = -n; v <= n; v++) {
                            sum += imageOutput.getBand(band).get(x + u, y + v);
                        }
                    }
                    int new_pix = sum / nb;
                    if (new_pix > 255)
                        new_pix = 255;
                    imageOutput.getBand(band).set(x, y, new_pix);
                }
            }
        }

        String outputPath = "mean_" + img.getName();
        return saveImage(imageOutput, outputPath);
    }

    public static Image Coloration(Image img, int R, int G, int B) {
        String imagePath = "./images/" + img.getName();

        BufferedImage output = UtilImageIO.loadImage(imagePath);
        Planar<GrayU8> imageOutput = ConvertBufferedImage.convertFromPlanar(output, null, true, GrayU8.class);

        for (int y = 0; y < imageOutput.height; y++) {
            for (int x = 0; x < imageOutput.width; x++) {
                int colorR = imageOutput.getBand(0).get(x, y);
                int colorG = imageOutput.getBand(1).get(x, y);
                int colorB = imageOutput.getBand(2).get(x, y);

                int newR = colorR + R;
                int newG = colorG + G;
                int newB = colorB + B;

                if (newR < 0)
                    imageOutput.getBand(0).set(x, y, 0);
                if (newR > 255)
                    imageOutput.getBand(0).set(x, y, 255);
                else
                    imageOutput.getBand(0).set(x, y, newR);

                if (newG < 0)
                    imageOutput.getBand(1).set(x, y, 0);
                if (newG > 255)
                    imageOutput.getBand(1).set(x, y, 255);
                else
                    imageOutput.getBand(1).set(x, y, newG);

                if (newB < 0)
                    imageOutput.getBand(2).set(x, y, 0);
                if (newB > 255)
                    imageOutput.getBand(2).set(x, y, 255);
                else
                    imageOutput.getBand(2).set(x, y, newB);
            }
        }

        String outputPath = "coloration_" + img.getName();
        return saveImage(imageOutput, outputPath);
    }
    
    public static boolean checkIfExist(String filter, String name){
        System.out.println(filter+"_"+name);

        String newImg = filter+"_"+name; //Create the file name
        File file = new File("./images/"+newImg); //Open the file
        
        return file.exists(); //Delete the file
    }
}
