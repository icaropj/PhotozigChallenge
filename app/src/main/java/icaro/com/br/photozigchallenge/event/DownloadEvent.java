package icaro.com.br.photozigchallenge.event;

import java.io.File;

/**
 * Created by icaro on 02/12/2017.
 */

public class DownloadEvent {

    private File file;

    public DownloadEvent(File file){
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
