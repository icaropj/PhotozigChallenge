package icaro.com.br.photozigchallenge.base;

import icaro.com.br.photozigchallenge.model.Asset;
import icaro.com.br.photozigchallenge.model.AssetsWrapper;
import icaro.com.br.photozigchallenge.util.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by icaro on 02/12/2017.
 */

public interface BaseService {

    @GET("assets.json")
    public Call<AssetsWrapper> assets();

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);

}
