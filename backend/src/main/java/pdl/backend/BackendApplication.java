package pdl.backend;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class BackendApplication {
	public static void main(String[] args) {
		File dirImage = new File("./images");
		if (!dirImage.exists() || !dirImage.isDirectory()) {
			throw new RuntimeException("The folder images does not exist");
		}

		String[] extensions = {".jpg", ".png"};
		ImageFilter filter = new ImageFilter(extensions, 2);

		String[] files = dirImage.list(filter);

		if (files == null){
			System.out.println("Image Directory empty");
		}

		//TO-DO : Traiter les images dans files dans la BDD

		SpringApplication.run(BackendApplication.class, args);
	}

}
