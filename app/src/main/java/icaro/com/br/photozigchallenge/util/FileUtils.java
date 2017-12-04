package icaro.com.br.photozigchallenge.util;

import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by icaro on 02/12/2017.
 */

public class FileUtils {
    public static boolean fileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getMimeType(File file) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
