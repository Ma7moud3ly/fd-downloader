package com.ma7moud3ly.fd.services;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;
import com.ma7moud3ly.fd.activities.HomeActivity;

import androidx.annotation.Nullable;


public class FloatingWidgetService extends Service {


    private WindowManager mWindowManager;
    private View mOverlayView;
    private ImageView close, restore, toggle;
    private View container;
    private SimpleExoPlayer videoPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);
        mOverlayView = LayoutInflater.from(this).inflate(R.layout.widget_floating, null);


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mOverlayView, params);

        container = mOverlayView.findViewById(R.id.floating_layout);
        restore = mOverlayView.findViewById(R.id.floating_restore);
        close = mOverlayView.findViewById(R.id.floating_close);
        toggle = mOverlayView.findViewById(R.id.floating_toggle);

        close.setOnClickListener(v -> this.stopSelf());
        restore.setOnClickListener(v -> startHomeActivity());

        container.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float Xdiff = Math.round(event.getRawX() - initialTouchX);
                        float Ydiff = Math.round(event.getRawY() - initialTouchY);
                        params.x = initialX + (int) Xdiff;
                        params.y = initialY + (int) Ydiff;
                        mWindowManager.updateViewLayout(mOverlayView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("video") && intent.hasExtra("position")) {
            String video = intent.getStringExtra("video");
            Long position = intent.getLongExtra("position", 0);
            initVideo(video, position);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initVideo(String video, Long position) {
        videoPlayer = new SimpleExoPlayer.Builder(this).build();
        toggle.setOnClickListener(v -> {
            if (videoPlayer.isPlaying()) {
                videoPlayer.pause();
                toggle.setImageResource(R.drawable.play);
            } else {
                videoPlayer.play();
                toggle.setImageResource(R.drawable.pause);
            }
        });
        try {
            MediaItem mediaItem = MediaItem.fromUri(video);
            videoPlayer.setMediaItem(mediaItem);
            videoPlayer.prepare();
            videoPlayer.seekTo(position);
            videoPlayer.play();
        } catch (Exception e) {
            App.toast(getString(R.string.video_failed));
            e.printStackTrace();
        }
    }

    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("position", videoPlayer.getCurrentPosition());
        homeIntent.putExtra("video", videoPlayer.getCurrentMediaItem().mediaId);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayer.stop();
        videoPlayer.release();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
    }

}
