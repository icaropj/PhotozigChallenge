package icaro.com.br.photozigchallenge.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icaro.com.br.photozigchallenge.R;
import icaro.com.br.photozigchallenge.listener.AssetListener;
import icaro.com.br.photozigchallenge.model.Asset;
import icaro.com.br.photozigchallenge.service.DownloadService;
import icaro.com.br.photozigchallenge.util.Constants;

/**
 * Created by icaro on 02/12/2017.
 */

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.MyViewHolder>{

    private List<Asset> mAssets;
    private Context mContext;
    private AssetListener mListener;

    public AssetAdapter(List<Asset> mAssets, Context mContext, AssetListener mListener) {
        this.mAssets = mAssets;
        this.mContext = mContext;
        this.mListener = mListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assets, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Asset asset = mAssets.get(position);

        Picasso.with(mContext)
                .load(Constants.getBaseUrl() + "assets/" + asset.getIm())
                .into(holder.ivImage);

        holder.tvName.setText(asset.getName());
    }

    @Override
    public int getItemCount() {
        return mAssets != null ? mAssets.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_assets_imageview)
        ImageView ivImage;

        @BindView(R.id.item_assets_name)
        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

//            itemView.setOnClickListener(this);
        }

        @OnClick({R.id.item_assets_download, R.id.item_assets_detail})
        public void onClick(View v) {
            if(mListener == null) return;
            if (v.getId()== R.id.item_assets_detail){
                mListener.onAssetDetailClick(v, getAdapterPosition());
            }else {
                mListener.onAssetDownloadClick(v, getAdapterPosition());

            }
        }
    }
}
