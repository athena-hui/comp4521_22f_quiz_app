package com.example.comp4521_quiz_app

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class BackgroundMusicService: Service() {
    private lateinit var mediaPlayer : MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.isLooping = true; // Set looping
        mediaPlayer.setVolume(50F, 50F);
    }

    override fun onStart(intent: Intent?, startId: Int) {
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop();
    }
}