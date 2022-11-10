package com.example.productpresentation

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.productpresentation.databinding.ExoPlayerActivityLayoutBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.IOException

class ExoPlayerActivity: AppCompatActivity() {

    private lateinit var binding: ExoPlayerActivityLayoutBinding
    private  var selectedVideoPath: String? = null
    private val SELECT_VIDEO = 1
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExoPlayerActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideStatusBar()

        try{
            playVideo(Uri.parse(intent.getStringExtra("URI")))
        }
        catch (exception: NullPointerException){
            println("NO PATH")
        }

        binding.filePickerButton.setOnClickListener {
            stopVideo()
            val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, SELECT_VIDEO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            selectedVideoPath = getPath(data?.data)
            try {
                if (selectedVideoPath == null) {
                    finish()
                } else {
                    println(selectedVideoPath)
                    val intent = Intent(this, ExoPlayerActivity::class.java)
                    intent.putExtra("URI", selectedVideoPath)
                    startActivity(intent)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else null
    }

    private fun playVideo(videoSource: Uri){
        player = ExoPlayer.Builder(this).build()
        binding.videoPlayer.player = player
        binding.videoPlayer.keepScreenOn = true

        val mediaItem = MediaItem.fromUri(videoSource)
        player.setMediaItem(mediaItem)
        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        player.prepare()
        player.play()

        //player.addListener(object: Player.Listener{
        //    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        //        if(playbackState == Player.STATE_ENDED){
        //            player.setMediaItem(mediaItem)
        //            println("ENDED")
        //            player.prepare()
        //            player.play()
        //        }
        //    }
        //})
    }
     private fun stopVideo(){
         try{
             player.stop()
         }
         catch(exception: UninitializedPropertyAccessException){
             println(exception.message)
         }
     }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun hideStatusBar(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            hideSystemBars()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        stopVideo()
        finish()
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVideo()
    }
}