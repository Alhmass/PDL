package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;


public class Convolution {

  public static void meanFilter(GrayU8 input, GrayU8 output, int size) {
      int n = size/2; int nb = size * size;
      for(int y = n; y < input.height-n; y++) {
        for(int x = n; x < input.width-n; x++) {
          int sum = 0;
          for(int u = -n; u <= n; u++) {
            for(int v = -n; v <= n; v++) {
              sum += input.get(x + u, y + v);
            }
          }
          output.set(x, y, sum/nb);
        }
      }
  }

  public static void convolution(GrayU8 input, GrayS16 output, int[][] kernel) {
      int n_w = kernel.length/2;
      int n_h = kernel[0].length/2;
      for(int y = n_h; y < input.height-n_h; y++) {
        for(int x = n_w; x < input.width-n_w; x++) {
          int r = 0;
          for(int u = -n_w; u <= n_w; u++) {
            for(int v = -n_h; v <= n_h; v++) {
              r += input.get(x + u, y + v) * kernel[u + n_w][v + n_h];
            }
          }
          output.set(x, y, r);
        }
      }
  }

  public static void gradientImage(GrayU8 input, GrayU8 output, int[][] kernelX, int[][] kernelY){
      GrayS16 dx = new GrayS16(input.width, input.height);
      GrayS16 dy = new GrayS16(input.width, input.height);
      convolution(input, dx, kernelX);
      convolution(input, dy, kernelY);
      for(int y = 0; y < input.height; y++) {
        for(int x = 0; x < input.width; x++) {
          double m = Math.sqrt((dx.get(x, y) * dx.get(x, y)) + (dy.get(x, y) * dy.get(x, y)));
          if(m > 255) {
            output.set(x, y, 255);
          } else {
            output.set(x, y, (int)m);
          }
        }
      }
  }

  public static void gradientImageSobel(GrayU8 input, GrayU8 output){
    int[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    int[][] kernelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    gradientImage(input, output, kernelX, kernelY);
  }

  public static void gradientImagePrewitt(GrayU8 input, GrayU8 output){
    int[][] kernelX = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};
    int[][] kernelY = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
    gradientImage(input, output, kernelX, kernelY);
  }

  
  public static void main(final String[] args) {
    // load image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    GrayU8 output = input.createSameShape();

    // processing (select a line at a time)
    // meanFilter(input, output, 19);
    // gradientImageSobel(input, output);
    // gradientImagePrewitt(input, output);
    int[][] kX = {{-1, 0, 1}};
    int[][] kY = {{-1}, {0}, {1}};
    gradientImage(input, output, kX, kY);
    
    // save output image
    final String outputPath = args[1];
    UtilImageIO.saveImage(output, outputPath);
    System.out.println("Image saved in: " + outputPath);
  }

}
