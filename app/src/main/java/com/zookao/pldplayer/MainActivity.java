package com.zookao.pldplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        VideoView videoView =  findViewById(R.id.video_view);

//        String path = "http://hc.yinyuetai.com/uploads/videos/common/2B40015FD4683805AAD2D7D35A80F606.mp4?sc=364e86c8a7f42de3&br=783&rd=Android";
        String path = "http://appcdn.quxueedu.com/video/H_n8X8nPFqX3L3Jotrg_1517370260018.mp4";
        videoView.setVideoPath(path);

    }

    public void onStopCustomVideoView(View v) {
        finish();
    }
}
