package com.example.productpresentation

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.productpresentation.databinding.ActivityMainBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun hideSystemBars() {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(window.decorView) ?: return
            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            hideSystemBars()
        }

        var player = ExoPlayer.Builder(this).build()
        binding.videoPlayer.player = player
        binding.videoPlayer.keepScreenOn = true

        val videoSource = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")
        val mediaItem = MediaItem.fromUri(videoSource)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        player.addListener(object: Player.Listener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState == Player.STATE_ENDED){
                    val videoSource = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")
                    val mediaItem = MediaItem.fromUri(videoSource)
                    player.setMediaItem(mediaItem)
                    println("ENDED")
                    player.prepare()
                    player.play()
                }
            }
        })


    }
}