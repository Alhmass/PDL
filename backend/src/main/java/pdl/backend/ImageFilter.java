package pdl.backend;

import java.io.File;
import java.io.FilenameFilter;

public class ImageFilter implements FilenameFilter{
    String[] extension;
    int size;


    public ImageFilter(String[] extension, int size){
        this.extension = extension;
        this.size = size;
    }

    public boolean accept(File dir, String name){
        for (int i = 0; i < this.size; ++i){
            if (name.endsWith(this.extension[i])) return true;
        }
        return false;
    }
}
