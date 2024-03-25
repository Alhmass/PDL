package pdl.backend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

@Repository
public class SQLController implements InitializingBean {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private ImageDao imageDao;

	public SQLController(ImageDao imageDao) {
		this.imageDao = imageDao;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.jdbcTemplate.execute("DROP TABLE IF EXISTS images");
		this.jdbcTemplate.execute(
				"CREATE TABLE IF NOT EXISTS images (id bigserial PRIMARY KEY, name char(255), histogram2D vector(360), histogram3D vector(1728), tags varchar(10000))");

		for (Image img : this.imageDao.retrieveAll()) {
			addImage(img, "");
		}			
	}

	public void addImage(Image img, String tags) {
		Planar<GrayU8> input = null;
		int[] h2D = new int[360];
		int[] h3D = new int[1728];
		try {
			InputStream ImageStream = new ByteArrayInputStream(img.getData());
			BufferedImage Image = ImageIO.read(ImageStream);
			input = ConvertBufferedImage.convertFromPlanar(Image, null, true, GrayU8.class);
			System.out.println("Succeed to load " + img.getName());
			try {
				h2D = genHistoHueSat(input);
				h3D = genHistoRGB(input);
			} catch (Exception e) {
				System.out.println("Failed to generate histogram : " + img.getName());
			}
			if(tags.equals("")) {
				try {
					FileReader file_tag = new FileReader("./images/tag/" + img.getName() + ".txt");
					int c;
					while((c = file_tag.read()) != -1) {
						tags += (char)c;
					}
					file_tag.close();
				} catch (Exception e) {
					System.out.println("Tags not found for : " + img.getName());
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to load : " + img.getName());
		}
		this.jdbcTemplate.update("INSERT INTO images (id, name, histogram2D, histogram3D, tags) VALUES (?, ?, ?, ?, ?)",
				img.getId(),
				img.getName(), h2D, h3D, tags);
	}

	public void deleteImage(long id) {
		this.jdbcTemplate.update("DELETE FROM images WHERE id=?", id);
	}

	public int getNbImages() {
		return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM images", Integer.class);
	}

	public String[] getTags(long id) {
		String request = "SELECT tags FROM images WHERE id=" + id;
		String tag = this.jdbcTemplate.queryForObject(request, String.class);
		return tag.split("@");
	}

	public List<Image> searchTags(String tag) {
		String request = "SELECT id FROM images WHERE tags LIKE '%" + tag + "%'";
		List<Map<String, Object>> res = this.jdbcTemplate.queryForList(request);
		List<Image> img_list = new ArrayList<Image>();
		for(Map<String, Object> row : res) {
			img_list.add(imageDao.retrieve((long)row.get("id")).get());
		}
		return img_list;
	}

	public List<Object> getSimilarTags(long id, String tag, int size) {
		String query = "SELECT id FROM images WHERE id != " + id + " AND tags LIKE '%" + tag + "%' LIMIT " + size;
		List<Map<String, Object>> res = this.jdbcTemplate.queryForList(query);
		Image[] img_list = new Image[res.size()];
		double[] dist_list = new double[res.size()];
		int count = 0;
		for(Map<String, Object> row : res) {
			img_list[count] = imageDao.retrieve((long)row.get("id")).get();
			dist_list[count] = 1;
			count++;
		}
		return Arrays.asList(img_list, dist_list);
	}

	public List<Object> getSimilarImages2D(long id, int size){
		String query = "SELECT id, histogram2D <-> (SELECT histogram2D FROM images WHERE id = " + id + ") AS distance FROM images WHERE id != " + id + " ORDER BY distance LIMIT " + size;
		List<Map<String, Object>> res = this.jdbcTemplate.queryForList(query);
		Image[] img_list = new Image[res.size()];
		double[] dist_list = new double[res.size()];
		int count = 0;
		for(Map<String, Object> row : res) {
			img_list[count] = imageDao.retrieve((long)row.get("id")).get();
			dist_list[count] = (double)row.get("distance");
			count++;
		}
		return Arrays.asList(img_list, dist_list);
	}

	public List<Object> getSimilarImages3D(long id, int size){
		String query = "SELECT id, histogram3D <-> (SELECT histogram3D FROM images WHERE id = " + id + ") AS distance FROM images WHERE id != " + id + " ORDER BY distance LIMIT " + size;
		List<Map<String, Object>> res = this.jdbcTemplate.queryForList(query);
		Image[] img_list = new Image[res.size()];
		double[] dist_list = new double[res.size()];
		int count = 0;
		for (Map<String, Object> row : res) {
			img_list[count] = imageDao.retrieve((long)row.get("id")).get();
			dist_list[count] = (double)row.get("distance");
			count++;
		}
		return Arrays.asList(img_list, dist_list);
	}

	private static int[] genHistoHueSat(Planar<GrayU8> input) {
		double[] hsv = new double[3];
		int[][] count = new int[36][10];
		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 10; j++) {
				count[i][j] = 0;
			}
		}
		for (int y = 0; y < input.height; y++) {
			for (int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				ColorHsv.rgbToHsv(r, g, b, hsv);
				double hue = hsv[0] * 180 / Math.PI;
				double s = hsv[1] * 100;
				if ((int) s == 100)
					s = 99;
				count[(int) (hue / 10)][(int) (s / 10)] += 1;
			}
		}
		int c = 0;
		int[] res = new int[360];
		for (int x = 0; x < 36; x++) {
			for (int y = 0; y < 10; y++) {
				res[c] = count[x][y];
				c++;
			}
		}
		return res;
	}

	private static int[] genHistoRGB(Planar<GrayU8> input) {
		int[][][] count = new int[12][12][12];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				for (int k = 0; k < 12; k++) {
					count[i][j][k] = 0;
				}
			}
		}
		for (int y = 0; y < input.height; y++) {
			for (int x = 0; x < input.width; x++) {
				int r = input.getBand(0).get(x, y);
				int g = input.getBand(1).get(x, y);
				int b = input.getBand(2).get(x, y);
				count[(int) (r / 22)][(int) (g / 22)][(int) (b / 22)] += 1;
			}
		}
		int c = 0;
		int[] res = new int[1728];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				for (int k = 0; k < 12; k++) {
					res[c] = count[i][j][k];
					c++;
				}
			}
		}
		return res;
	}
}