package icaro.com.br.photozigchallenge.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by icaro on 02/12/2017.
 */

public class AssetsWrapper implements Serializable{

    private String assetsLocation;

    private List<Asset> objects;

    public String getAssetsLocation() {
        return assetsLocation;
    }

    public void setAssetsLocation(String assetsLocation) {
        this.assetsLocation = assetsLocation;
    }

    public List<Asset> getObjects() {
        return objects;
    }

    public void setObjects(List<Asset> objects) {
        this.objects = objects;
    }
}
