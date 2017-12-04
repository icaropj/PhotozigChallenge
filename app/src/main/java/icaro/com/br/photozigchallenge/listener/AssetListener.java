package icaro.com.br.photozigchallenge.listener;

import android.view.View;

/**
 * Created by icaro on 02/12/2017.
 */

public interface AssetListener {
    void onAssetDetailClick(View view, int position);

    void onAssetDownloadClick(View view, int position);
}
