package icaro.com.br.photozigchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by icaro on 02/12/2017.
 */

public class Asset implements Serializable{

    private String name;

    private String bg;

    private String im;

    private String sg;

    private List<Txt> txts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getSg() {
        return sg;
    }

    public void setSg(String sg) {
        this.sg = sg;
    }

    public List<Txt> getTxts() {
        return txts;
    }

    public void setTxts(List<Txt> txts) {
        this.txts = txts;
    }

}
