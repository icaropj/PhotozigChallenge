package icaro.com.br.photozigchallenge.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import icaro.com.br.photozigchallenge.R;
import icaro.com.br.photozigchallenge.base.BaseActivity;
import icaro.com.br.photozigchallenge.fragment.AssetDetailFragment;
import icaro.com.br.photozigchallenge.fragment.AssetListFragment;

public class MainActivity extends BaseActivity
        implements AssetListFragment.OnFragmentInteractionListener,
        AssetDetailFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            initToolbar();
            this.replaceFragment(AssetListFragment.newInstance());
        }
    }

    @Override
    public void replaceFragment(Fragment frag) {
        super.replaceFragment(frag);
    }
}
