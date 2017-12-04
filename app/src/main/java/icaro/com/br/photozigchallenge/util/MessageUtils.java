package icaro.com.br.photozigchallenge.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by icaro on 02/12/2017.
 */

public class MessageUtils {
    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void snackbar(CoordinatorLayout coordinatorLayout, String message, String action, View.OnClickListener onClickListener) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(action, onClickListener).show();
    }
}
