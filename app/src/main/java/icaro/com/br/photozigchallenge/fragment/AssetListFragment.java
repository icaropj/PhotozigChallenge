package icaro.com.br.photozigchallenge.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import icaro.com.br.photozigchallenge.R;
import icaro.com.br.photozigchallenge.adapter.AssetAdapter;
import icaro.com.br.photozigchallenge.base.BaseFragment;
import icaro.com.br.photozigchallenge.listener.AssetListener;
import icaro.com.br.photozigchallenge.model.Asset;
import icaro.com.br.photozigchallenge.model.AssetsWrapper;
import icaro.com.br.photozigchallenge.retrofit.RetrofitSingleton;
import icaro.com.br.photozigchallenge.service.DownloadService;
import icaro.com.br.photozigchallenge.util.Constants;
import icaro.com.br.photozigchallenge.util.FileUtils;
import icaro.com.br.photozigchallenge.util.JsonUtils;
import icaro.com.br.photozigchallenge.util.MessageUtils;
import icaro.com.br.photozigchallenge.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetListFragment extends BaseFragment implements AssetListener {

    private Context mContext;
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.fragment_asset_list_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_asset_list_swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private AssetAdapter mAdapter;
    private List<Asset> mAssets = new ArrayList<>();
    private AssetsWrapper mAssetsWrapper;

    public AssetListFragment() {
    }

    public static AssetListFragment newInstance() {
        AssetListFragment fragment = new AssetListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_list, container, false);
        ButterKnife.bind(this, view);

        setRecyclerView();

        mContext = getContext();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAssets();
            }
        });

        return view;
    }

    private void setRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(NetworkUtils.isOnline(mContext)){
            fetchAssets();
        }else{
            MessageUtils.shortToast(mContext, getString(R.string.no_connection));
        }
    }

    private void fetchAssets() {
        Call<AssetsWrapper> assetsCall = RetrofitSingleton.getInstance().getBaseService().assets();
        assetsCall.enqueue(new Callback<AssetsWrapper>() {
            @Override
            public void onResponse(Call<AssetsWrapper> call, Response<AssetsWrapper> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                if(response.isSuccessful()){
                    mAssetsWrapper = response.body();
                    mAssets = response.body().getObjects();
                    mAdapter = new AssetAdapter(mAssets, mContext, AssetListFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    MessageUtils.shortToast(mContext, getString(R.string.response_error));
                }
            }

            @Override
            public void onFailure(Call<AssetsWrapper> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                t.printStackTrace();
                MessageUtils.shortToast(mContext, getString(R.string.error_fetch));
            }
        });
    }

    @Override
    public void onAssetDetailClick(View view, int position) {
        String json = JsonUtils.toJson(mAssetsWrapper);
        AssetDetailFragment assetDetailFragment = AssetDetailFragment.newInstance(json, position);
        mListener.replaceFragment(assetDetailFragment);
    }

    @Override
    public void onAssetDownloadClick(View view, int position) {
        if (checkPermission()) {
            Asset asset = mAssets.get(position);
            String downloadsDir = Environment.DIRECTORY_DOWNLOADS;
            String audioPath = Environment.getExternalStoragePublicDirectory(downloadsDir).toString() + "/" + asset.getSg();
            String videoPath = Environment.getExternalStoragePublicDirectory(downloadsDir).toString() + "/" + asset.getBg();
            if(FileUtils.fileExist(videoPath) && FileUtils.fileExist(audioPath)){
                MessageUtils.shortToast(mContext, getString(R.string.already_downloaded));
            }else{
                if(!FileUtils.fileExist(videoPath)){
                    startDownload(asset.getBg());
                }
                if(!FileUtils.fileExist(audioPath)){
                    startDownload(asset.getSg());
                }
            }
        } else {
            requestPermission();
        }
    }

    private void startDownload(String fileName){
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constants.FILE_NAME, fileName);
        intent.putExtra(Constants.ASSETS_PATH_EXTRA, mAssetsWrapper.getAssetsLocation() + "/" + fileName);
        mContext.startService(intent);
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions((Activity) mContext,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void replaceFragment(Fragment frag);
    }
}
