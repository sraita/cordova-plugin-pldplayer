package com.zookao.pldplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

public class VideoView extends FrameLayout implements View.OnClickListener{


    private static final String TAG = "VideoView";
    private static final String VIDEO_PATH_480 = "http://hc.yinyuetai.com/uploads/videos/common/2B40015FD4683805AAD2D7D35A80F606.mp4?sc=364e86c8a7f42de3&br=783&rd=Android";
    private static final String VIDEO_PATH_1080 = "http://appcdn.quxueedu.com/video/H_n8X8nPFqX3L3Jotrg_1517370260018.mp4";

    private static final int DELAY = 500;
    private static final int HIDE_DELAY = 5000;

    private double PLAY_SPEED = 1.0;
    private int currentTime = 0;

    private PLVideoTextureView mTextureView;
    private ImageView mPlay;
    private Button mToolPlay;
    private TextView mCurrentTime;
    private TextView mDuration;
    private SeekBar mSeekBar;
    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private ProgressBar mProgressBar;
    private LinearLayout mQualityLayout;
    private Button mChangeQuality;
    private Button mChangeSpeed;





    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_video, this);
        mTextureView = findViewById(R.id.pl_video_texture_view);
        mCurrentTime = findViewById(R.id.current);
        mDuration = findViewById(R.id.duration);
        mTopLayout = findViewById(R.id.top_layout);
        mBottomLayout = findViewById(R.id.bottom_layout);
        mProgressBar = findViewById(R.id.progress_bar);
        mSeekBar = findViewById(R.id.seek_bar);
        mChangeQuality = findViewById(R.id.change_quality);
        mQualityLayout = findViewById(R.id.quality_layout);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_FIT_PARENT);
        mTextureView.setOnPreparedListener(mOnPreparedListener);
        mTextureView.setOnCompletionListener(mOnCompletionListener);
        mPlay = findViewById(R.id.play);
        mPlay.setOnClickListener(this);
        mToolPlay = findViewById(R.id.play_status);
        mToolPlay.setOnClickListener(this);
        mChangeQuality.setOnClickListener(this);


        RadioGroup mQualityGroup = findViewById(R.id.quality_group);

        mQualityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentTime = (int) mTextureView.getCurrentPosition();
                changeVideoQuality(checkedId);
            }
        });

        // 更新播放速度
        mChangeSpeed = findViewById(R.id.speed);
        mChangeSpeed.setOnClickListener(this);





        mTextureView.start();
    }



    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {

            mProgressBar.setVisibility(View.GONE);

            long duration = mTextureView.getDuration();
            mDuration.setText(TimeUtils.stringForTime(duration));
            mSeekBar.setMax((int) duration);
            postDelayed(mTicker, DELAY);

            mPlay.setVisibility(View.VISIBLE);
            mTopLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            postDelayed(mHider, HIDE_DELAY);
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            mPlay.setImageResource(R.drawable.play_selector);
            mCurrentTime.setText("00:00");
            removeCallbacks(mTicker);
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mTextureView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeCallbacks(mHider);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            postDelayed(mHider, HIDE_DELAY);
        }
    };

    public void setVideoPath(String path) {
        mTextureView.setVideoPath(path);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
            case R.id.play_status:
                togglePlay();
                break;
            case R.id.change_quality:
                toggleQualityChange();
                break;
            case R.id.speed:
                changeVideoSpeed();
                break;
        }
    }


    /**
     * 显示视频质量切换框
     */
    private void toggleQualityChange() {
        if (mQualityLayout.getVisibility() == View.VISIBLE) {
            mQualityLayout.setVisibility(View.GONE);
        } else {
            mQualityLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新视频质量
     */
    private void changeVideoQuality(int id) {
        switch (id) {
            case R.id.quality_1080p:
                mTextureView.setVideoPath(VIDEO_PATH_1080);
                mChangeQuality.setText("1080p");
                break;
            case R.id.quality_720p:
                mChangeQuality.setText("720p");
                break;
            case R.id.quality_480p:
                mTextureView.setVideoPath(VIDEO_PATH_480);
                mChangeQuality.setText("清晰");
                break;
            case R.id.quality_360p:
                mChangeQuality.setText("流畅");
                break;
        }

        mQualityLayout.setVisibility(View.GONE);
    }

    /**
     * 切换视频播放速率
     */
    private void changeVideoSpeed() {
        if (PLAY_SPEED < 1.5) {
            PLAY_SPEED += 0.25;
        } else {
            PLAY_SPEED = 0.75;
        }

        switch (String.valueOf(PLAY_SPEED)) {
            case "0.75":
                mTextureView.setPlaySpeed(0x00030004);
                break;
            case "1.0":
                mTextureView.setPlaySpeed(0x00010001);
                break;
            case "1.25":
                mTextureView.setPlaySpeed(0x00050004);
                break;
            case "1.5":
                mTextureView.setPlaySpeed(0x00030002);
                break;
        }

        Log.i("playSpeed", "playSpeed is : "+PLAY_SPEED);

        mChangeSpeed.setText(PLAY_SPEED+"x");
    }

    private void togglePlay() {
        if (mTextureView.isPlaying()) {
            mTextureView.pause();
            mPlay.setImageResource(R.drawable.play_selector);
            mToolPlay.setBackgroundResource(R.drawable.play_status);
            removeCallbacks(mTicker);
            removeCallbacks(mHider);

        } else {
            mTextureView.start();
            mPlay.setVisibility(View.GONE);
            mTopLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            mPlay.setImageResource(R.drawable.pause_selector);
            mToolPlay.setBackgroundResource(R.drawable.pause);

            postDelayed(mTicker, DELAY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mProgressBar.getVisibility() == View.GONE) {
                    mPlay.setVisibility(VISIBLE);
                    mTopLayout.setVisibility(VISIBLE);
                    mBottomLayout.setVisibility(VISIBLE);
                    postDelayed(mHider, HIDE_DELAY);
                }

                if (mQualityLayout.getVisibility() == View.VISIBLE) {
                    mQualityLayout.setVisibility(View.GONE);
                }
                break;
        }
        return super.onTouchEvent(event);
    }




    private Runnable mTicker = new Runnable() {

        @Override
        public void run() {
            mCurrentTime.setText(TimeUtils.stringForTime(mTextureView.getCurrentPosition()));
            mDuration.setText(TimeUtils.stringForTime(mTextureView.getDuration()));
            mSeekBar.setProgress((int) mTextureView.getCurrentPosition());
            postDelayed(mTicker, DELAY);
        }
    };

    private Runnable mHider = new Runnable() {
        @Override
        public void run() {
            mPlay.setVisibility(View.GONE);
            mTopLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTextureView.stopPlayback();
        removeCallbacks(mTicker);
        removeCallbacks(mHider);
    }


}
