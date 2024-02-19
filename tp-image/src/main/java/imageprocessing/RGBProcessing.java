package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;
import boofcv.alg.color.ColorHsv;

public class RGBProcessing {

	public static void changeBrightness (Planar<GrayU8> input, int delta) {
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				for(int band = 0; band < 3; band++) {
					int value = input.getBand(band).get(x, y);
					if(value + delta > 255) {
						value = 255;
					} else {
						value += delta;
					}
					input.getBand(band).set(x, y, value);
				}
			}
		}
	}

	public static void RGB_to_Gray (Planar<GrayU8> input) {
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				double value = 0.3 * r + 0.59 * g + 0.11 * b;
				for(int band = 0; band < 3; band++) {
					input.getBand(band).set(x, y, (int)value);
				}
			}
		}
	}

	public static void meanFilter(Planar<GrayU8> input, Planar<GrayU8> output, int size) {
		int n = size/2; int nb = size * size;
		for(int y = n; y < input.height-n; y++) {
			for(int x = n; x < input.width-n; x++) {
				for(int band = 0; band < 3; band++) {
					int sum = 0;
					for(int u = -n; u <= n; u++) {
						for(int v = -n; v <= n; v++) {
							sum += input.getBand(band).get(x + u, y + v);
						}
					}
					output.getBand(band).set(x, y, sum/nb);
				}
			}
		}
	}

	public static void colorHSV(Planar<GrayU8> input, int hue) {
		double newHue = hue * Math.PI / 180;
		double[] tmp = new double[3];
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				ColorHsv.rgbToHsv(r, g, b, tmp);
				ColorHsv.hsvToRgb(newHue, tmp[1], tmp[2], tmp);
				for(int i = 0; i < 3; i++) {
					input.getBand(i).set(x, y, (int)tmp[i]);
				}
			}
		}
	}

	public static Planar<GrayU8> genHisto(Planar<GrayU8> input) {
		double[] hsv = new double[3];
		int[] count = new int[360]; 
		for(int i = 0; i < 360; i++) count[i] = 0;
		int max = 0;
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				ColorHsv.rgbToHsv(r, g, b, hsv);
				double hue = hsv[0] * 180 / Math.PI;
				count[(int)hue] += 1;
				if(count[(int)hue] > max) {
					max = count[(int)hue];
				}
			}
		}
		Planar<GrayU8> output = input.createNew(360, 200);
		int value;
		for(int y = 0; y < output.height; y++) {
			for(int x = 0; x < output.width; x++) {
				if((count[x]*200/max) < output.height-y) {
					value = 0;
				} else {
					value = 255;
				}
				for(int band = 0; band < 3; band++) {
					output.getBand(band).set(x, y, value);
				}
			}
		}
		return output;
	}

	public static Planar<GrayU8> genHistoHueSat(Planar<GrayU8> input) {
		double[] hsv = new double[3];
		int[][] count = new int[360][100];
		for(int i = 0; i < 360; i++) {
			for(int j = 0; j < 100; j++) {
				count[i][j] = 0;
			}
		}
		int max = 0;
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				ColorHsv.rgbToHsv(r, g, b, hsv);
				double hue = hsv[0] * 180 / Math.PI;
				double s = hsv[1] * 100;
				if((int)s == 100) s = 99;
				count[(int)hue][(int)s] += 1;
				if(count[(int)hue][(int)s] > max) {
					max = count[(int)hue][(int)s];
				}
			}
		}
		Planar<GrayU8> output = input.createNew(360, 100);
		int value;
		for(int y = 0; y < output.height; y++) {
			for(int x = 0; x < output.width; x++) {
				value = count[x][y];
				if(value > 255) value = 255;
				for(int band = 0; band < 3; band++) {
					output.getBand(band).set(x, y, value);
				}
			}
		}
		return output;
	}

    public static void main( String[] args ) {

    	// load image
		if (args.length < 2) {
			System.out.println("missing input or output image filename");
			System.exit(-1);
		}
		final String inputPath = args[0];
		//GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
		BufferedImage input = UtilImageIO.loadImage(inputPath);
		Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
		if(input == null) {
			System.err.println("Cannot read input file '" + inputPath);
			System.exit(-1);
		}
		//output for meanFilter
		// Planar<GrayU8> output = image.createSameShape();

		// processing (select a line at a time)
	
		// changeBrightness(image, 120);
		// meanFilter(image, output, 19);
		// RGB_to_Gray(image);
		// colorHSV(image, 270);
		// Planar<GrayU8> output = genHisto(image);
		Planar<GrayU8> output = genHistoHueSat(image);
		
		// save output image
		final String outputPath = args[1];
		//select image for changeBrightness, RGB_to_Gray, colorHSV
		// UtilImageIO.saveImage(image, outputPath);
		//select output for meanFilter, genHisto, genHistoHueSat
		UtilImageIO.saveImage(output, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}