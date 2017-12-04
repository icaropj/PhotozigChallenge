package icaro.com.br.photozigchallenge.util;

import com.google.gson.Gson;

import java.util.List;

import icaro.com.br.photozigchallenge.model.Asset;
import icaro.com.br.photozigchallenge.model.AssetsWrapper;

/**
 * Created by icaro on 02/12/2017.
 */

public class JsonUtils {

    public static String toJson(Object obj){
        return new Gson().toJson(obj);
    }

    public static AssetsWrapper fromJson(String json){
        return new Gson().fromJson(json, AssetsWrapper.class);
    }

}
