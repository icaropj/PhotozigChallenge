package icaro.com.br.photozigchallenge.base;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import icaro.com.br.photozigchallenge.R;
import icaro.com.br.photozigchallenge.fragment.AssetListFragment;

public class BaseActivity extends AppCompatActivity {

    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initToolbar(){
        if(mToolbar != null){
            setSupportActionBar(mToolbar);
        }
    }

    protected void replaceFragment(Fragment frag){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, frag, TAG_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);

        if (fragment instanceof AssetListFragment) {
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
