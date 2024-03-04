package pdl.backend;

import org.springframework.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class SQLController implements InitializingBean{
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.jdbcTemplate.execute("DROP TABLE IF EXISTS images");
		this.jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS images (id bigserial PRIMARY KEY, name char(255), histogram2D vector(360), histogram3D vector(1728))");
	}

	public void addImage(int id, String name) {
		BufferedImage input = UtilImageIO.loadImage(name);
		Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
		if(input == null) {
			System.err.println("Cannot read input file '" + name);
			System.exit(-1);
		}
		int[] h2D = genHistoHueSat(image);
		int[] h3D = genHistoRGB(image);
		this.jdbcTemplate.update("INSERT INTO image (id, name, histogram2D, histogram3D) VALUES (?, ?, ?, ?)", id, name, h2D, h3D);
	}

	public int getNbImages() {
		return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM images", Integer.class);
	}




	private static int[] genHistoHueSat(Planar<GrayU8> input) {
		double[] hsv = new double[3];
		int[][] count = new int[36][10];
		for(int i = 0; i < 360; i++) {
			for(int j = 0; j < 100; j++) {
				count[i][j] = 0;
			}
		}
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				ColorHsv.rgbToHsv(r, g, b, hsv);
				double hue = hsv[0] * 180 / Math.PI;
				double s = hsv[1] * 100;
				if((int)s == 100) s = 99;
				count[(int)(hue/10)][(int)(s/10)] += 1;
			}
		}
		int c = 0;
		int[] res = new int[360];
		for(int x = 0; x < 36; x++) {
			for(int y = 0; y < 10; y++) {
				res[c] = count[x][y];
				c++;
			}
		}
		return res;
	}

	private static int[] genHistoRGB(Planar<GrayU8> input) {
		int[][][] count = new int[12][12][12];
		for(int i = 0; i < 12; i++) {
			for(int j = 0; j < 12; j++) {
				for(int k = 0; k < 12; k++) {
					count[i][j][k] = 0;
				}
			}
		}
		for(int y = 0; y < input.height; y++) {
			for(int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				count[(int)(r/22)][(int)(g/22)][(int)(b/22)] += 1;
			}
		}
		int c = 0;
		int[] res = new int[1728];
		for(int i = 0; i < 12; i++) {
			for(int j = 0; j < 12; j++) {
				for(int k = 0; k < 12; k++) {
					res[c] = count[i][j][k];
					c++;
				}
			}
		}
		return res;
	}
}



/*

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


  @Override
  public void afterPropertiesSet() throws Exception {
    // Drop table
    jdbcTemplate.execute("DROP TABLE IF EXISTS employee");

    // Create table
    this.jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS employee (id bigserial PRIMARY KEY, name character varying(255))");

    // Insert rows
    jdbcTemplate.update("INSERT INTO employee (name) VALUES (?), (?), (?)", (Object[]) names);
  }

  public int getNbEmployees() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employee", Integer.class);
  }

  public String getEmployeeName(long id) {
    return jdbcTemplate.queryForObject("SELECT name FROM employee WHERE id = ?", String.class, id);
  }

}
 */