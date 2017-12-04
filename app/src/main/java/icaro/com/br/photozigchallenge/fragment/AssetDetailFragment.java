package icaro.com.br.photozigchallenge.fragment;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icaro.com.br.photozigchallenge.R;
import icaro.com.br.photozigchallenge.event.DownloadEvent;
import icaro.com.br.photozigchallenge.event.EventBusSingleton;
import icaro.com.br.photozigchallenge.model.Asset;
import icaro.com.br.photozigchallenge.model.AssetsWrapper;
import icaro.com.br.photozigchallenge.model.Txt;
import icaro.com.br.photozigchallenge.service.DownloadService;
import icaro.com.br.photozigchallenge.util.Constants;
import icaro.com.br.photozigchallenge.util.FileUtils;
import icaro.com.br.photozigchallenge.util.JsonUtils;

public class AssetDetailFragment extends Fragment implements ExoPlayer.EventListener  {

    private static final String ASSET_PARAM = "asset";
    private static final String ASSET_POSITION = "assetPosition";

    private OnFragmentInteractionListener mListener;

    private SimpleExoPlayer mExoAudio;

    private ProgressDialog mProgressDialog;

    @BindView(R.id.fragment_asset_detail_exoplayer)
    SimpleExoPlayerView mExoPlayerView;

    @BindView(R.id.fragment_asset_detail_videoview)
    VideoView mVideoView;

    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaSessionCompat mMediaSession;

    private Asset mAsset;
    private Context mContext;

    private EventBus bus;

    private String audioPath, videoPath;
    private int mPosition;
    private List<Asset> mAssets;
    private AssetsWrapper mWrapper;

    @BindView(R.id.fragment_asset_detail_txt)
    TextView tvTxt;

    public AssetDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param assetsWrapperJson Parameter 1.
     * @return A new instance of fragment AssetDetailFragment.
     */
    public static AssetDetailFragment newInstance(String assetsWrapperJson, int position) {
        AssetDetailFragment fragment = new AssetDetailFragment();
        Bundle args = new Bundle();
        args.putString(ASSET_PARAM, assetsWrapperJson);
        args.putInt(ASSET_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String assetsWrapperJson = getArguments().getString(ASSET_PARAM);
            mWrapper = JsonUtils.fromJson(assetsWrapperJson);
            this.mAssets = mWrapper.getObjects();
            this.mPosition = getArguments().getInt(ASSET_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_detail, container, false);
        ButterKnife.bind(this, view);

        mContext = getContext();

        this.mAsset = mAssets.get(mPosition);

        bus = EventBusSingleton.getInstance().getBus();

        if(!bus.isRegistered(this)){
            bus.register(this);
        }

        initMediaSession();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Carregando...");
        mProgressDialog.setCancelable(false);
        initMedia();

        return view;
    }

    @OnClick(R.id.exo_rew)
    public void prevClick(View view){
        if(mPosition > 0){
            resetSession();
            mPosition--;
            this.mAsset = mAssets.get(mPosition);
            initMediaSession();
            initMedia();
        }else{
            mExoAudio.seekTo(0);
        }
    }

    @OnClick(R.id.exo_ffwd)
    public void nextClick(View view){
        if(mPosition < mAssets.size()){
            resetSession();
            mPosition++;
            this.mAsset = mAssets.get(mPosition);
            initMediaSession();
            initMedia();
        }
    }

    private void initMedia() {
        String downloadsDir = Environment.DIRECTORY_DOWNLOADS;
        audioPath = Environment.getExternalStoragePublicDirectory(downloadsDir).toString() + "/" + mAsset.getSg();
        videoPath = Environment.getExternalStoragePublicDirectory(downloadsDir).toString() + "/" + mAsset.getBg();
        if(FileUtils.fileExist(videoPath)){
            setVideoView();
            if(FileUtils.fileExist(this.audioPath)) hideProgressDialog();
        }else{
            startDownload(mAsset.getBg());
        }
        if(FileUtils.fileExist(audioPath)){
            initExo();
            if(FileUtils.fileExist(this.videoPath)) hideProgressDialog();
        }else{
            startDownload(mAsset.getSg());
        }
    }

    private void startDownload(String fileName){
        mProgressDialog.show();
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Constants.FILE_NAME, fileName);
        intent.putExtra(Constants.ASSETS_PATH_EXTRA, mWrapper.getAssetsLocation() + "/" + fileName);
        mContext.startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadFinished(DownloadEvent event){
        String mimeType = FileUtils.getMimeType(event.getFile());
        if(mimeType.contains("audio")) {
            this.audioPath = event.getFile().getAbsolutePath();
            initExo();
            if(FileUtils.fileExist(this.videoPath)) hideProgressDialog();
        }
        if(mimeType.contains("video")) {
            this.videoPath = event.getFile().getAbsolutePath();
            setVideoView();
            if(FileUtils.fileExist(this.audioPath)) hideProgressDialog();
        }
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private void resetSession() {
        mVideoView.stopPlayback();
        mExoAudio.stop();
        mExoAudio = null;
    }

    private void initMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, "MEDIA_TAG");

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setActive(true);
    }

    private void initExo() {
        if (mExoAudio == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoAudio = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoAudio);

            mExoAudio.addListener(this);

            Uri audioUri = Uri.parse(audioPath);
            String userAgent = Util.getUserAgent(mContext, getString(R.string.app_name));
            MediaSource mediaSourceAudio = new ExtractorMediaSource(audioUri, new DefaultDataSourceFactory(
                    mContext, userAgent), new DefaultExtractorsFactory(), null, null);

            mExoAudio.prepare(mediaSourceAudio);

            initTxtTimer();
            mExoAudio.setPlayWhenReady(true);
        }
    }

    private void initTxtTimer() {
        List<Txt> txts = mAsset.getTxts();
        if(txts != null){
            for (Txt txt : txts){
                createTxtTimer(txt);
            }
        }
    }

    private void createTxtTimer(Txt txt) {
        Float time = txt.getTime();
        final String txtMessage = txt.getTxt();

        long timeLong = (long) (time * 1000);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.obj = txtMessage;
                txtHandler.sendMessage(message);
            }
        }, timeLong);
    }

    Handler txtHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String text = (String) msg.obj;
            tvTxt.setText(text);
            txtPostHandler.postDelayed(txtPostRun, 2000);
        }
    };

    Handler txtPostHandler = new Handler();
    Runnable txtPostRun = new Runnable() {
        @Override
        public void run() {
            tvTxt.setText("");
        }
    };

    public void setVideoView(){
        mVideoView.setKeepScreenOn(true);
        mVideoView.setVideoPath(videoPath);
        mVideoView.start();
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    private void releaseExoAudio() {
        if(mExoAudio != null) {
            mExoAudio.stop();
            mExoAudio.release();
            mExoAudio = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseExoAudio();
        mMediaSession.setActive(false);
        bus.unregister(this);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoAudio.getCurrentPosition(), 1f);
            mVideoView.start();
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoAudio.getCurrentPosition(), 1f);
            mVideoView.pause();
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
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
