package icaro.com.br.photozigchallenge.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by icaro on 02/12/2017.
 */

public class MessageUtils {
    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
