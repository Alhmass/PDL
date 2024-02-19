package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;


public class GrayLevelProcessing {

	public static void threshold(GrayU8 input, int t) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl < t) {
					gl = 0;
				} else {
					gl = 255;
				}
				input.set(x, y, gl);
			}
		}
	}

	public static void changeBrightness (GrayU8 input, int delta) {
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int gl = input.get(x, y);
				if(gl + delta > 255) {
					input.set(x, y, 255);
				} else {
					input.set(x, y, gl + delta);
				}
			}
		}
	}

	public static void dynamic (GrayU8 input) {
		int min = 255, max = 0;
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int gl = input.get(x, y);
				if(gl < min) {
					min = gl;
				}
				if(gl > max) {
					max = gl;
				}
			}
		}
		int[] lut = new int[256];
		for(int i = 0; i < 255; i++) {
			lut[i] = (255*(i-min))/(max-min);
		}
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int gl = input.get(x, y);
				input.set(x, y, lut[gl]);
			}
		}
	}

	public static void egalisation (GrayU8 input) {
		int[] hist = new int[256];
		for(int i = 0; i < 256; i++) hist[i] = 0;
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int gl = input.get(x, y);
				hist[gl]++;
			}
		}
		int sum = 0;
		int nb = input.height * input.width;
		for(int i = 0; i < 256; i++) {
			sum += hist[i];
			hist[i] = (sum * 255) / nb;
		}
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int gl = input.get(x, y);
				input.set(x, y, hist[gl]);
			}
		}
	}

    public static void main( String[] args ) {

    	// load image
		if (args.length < 2) {
			System.out.println("missing input or output image filename");
			System.exit(-1);
		}
		final String inputPath = args[0];
		GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
		if(input == null) {
			System.err.println("Cannot read input file '" + inputPath);
			System.exit(-1);
		}

		// processing (select a line at a time)
		
        // threshold(input, 128);
		// changeBrightness(input, 120);
		// dynamic(input);
		egalisation(input);
		
		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(input, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}