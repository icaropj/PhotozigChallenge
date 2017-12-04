package icaro.com.br.photozigchallenge.model;

import java.io.Serializable;

/**
 * Created by icaro on 02/12/2017.
 */

public class Txt implements Serializable{

    private String txt;

    private Float time;

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }
}
