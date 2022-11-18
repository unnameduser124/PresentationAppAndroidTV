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
import androidx.core.view.isGone
import com.example.productpresentation.databinding.ExoPlayerActivityLayoutBinding
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.IOException

class ExoPlayerActivity: AppCompatActivity() {

    private lateinit var binding: ExoPlayerActivityLayoutBinding
    private  var selectedVideoPath: String? = null
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExoPlayerActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //default video link (for testing convenience)
        binding.urlTextInput.setText("http://media.developer.dolby.com/Atmos/MP4/Universe_Fury2.mp4")

        binding.videoPlayer.isGone = true

        hideStatusBar()

        //try to play video from memory path passed through intent, catch error if link is null (may be unnecessary)
        try{
            binding.videoPlayer.isGone = false
            playVideo(Uri.parse(intent.getStringExtra("URI")))
            binding.filePickerButton.isGone = true
            binding.urlTextInput.isGone = true
            binding.urlTextInputLayout.isGone = true
            binding.playFromUrlButton.isGone = true
        }
        catch (exception: NullPointerException){
            println("NO PATH")
        }

        binding.playFromUrlButton.setOnClickListener{
            val link = binding.urlTextInput.text.toString()
            try{
                binding.videoPlayer.isGone = false
                playVideo(Uri.parse(link))
                binding.filePickerButton.isGone = true
                binding.urlTextInput.isGone = true
                binding.urlTextInputLayout.isGone = true
                binding.playFromUrlButton.isGone = true
            }
            catch(exception: ExoPlaybackException){
                println(exception.message)
            }
        }
        binding.filePickerButton.setOnClickListener {
            stopVideo()
            val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, SELECT_VIDEO)
        }
    }

    //function receiving intent from file picker
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

    //get video path from picker intent
    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else null
    }

    //play video from Uri and put it on repeat
    private fun playVideo(videoSource: Uri){
        player = ExoPlayer.Builder(this).build()
        binding.videoPlayer.player = player
        binding.videoPlayer.keepScreenOn = true

        val mediaItem = MediaItem.fromUri(videoSource)
        player.setMediaItem(mediaItem)
        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        player.prepare()
        player.play()
    }

     private fun stopVideo(){
         try{
             player.stop()
         }
         catch(exception: UninitializedPropertyAccessException){
             println(exception.message)
         }
     }

    //hide status bar, available on swipe
    private fun hideStatusBar(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        window.decorView.setOnSystemUiVisibilityChangeListener {
            hideSystemBars()
        }
    }
    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
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

    companion object{
        const val SELECT_VIDEO = 1
    }
}