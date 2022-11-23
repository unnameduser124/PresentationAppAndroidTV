package com.example.productpresentation.tv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import com.example.productpresentation.CustomChromeWebViewClient
import com.example.productpresentation.CustomWebViewClient
import com.example.productpresentation.admin
import com.example.productpresentation.databinding.TvMainActivityBinding
import com.example.productpresentation.tv.TvSettings.MediaTypeSettings.uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


//default activity for android tv devices
class TvActivity: AppCompatActivity() {

    private lateinit var binding: TvMainActivityBinding
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TvMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showViews()
        playMedia()
        requestTextInputFocus()

        //hide text view to write admin code into
        binding.tvActivityAdminCodeInput.showSoftInputOnFocus = false
        binding.tvActivityAdminCodeInput.requestFocus()
        //disable soft input keyboard
        binding.tvActivityAdminCodeInput.inputType = InputType.TYPE_NULL

        binding.tvActivityAdminCodeInput.addTextChangedListener {
            if(it.toString() == admin.accessCode){
                binding.tvActivityAdminCodeInput.setText("")
                stopVideo()
                val intent = Intent(this, TvSettings::class.java)
                startActivity(intent)
            }
        }
    }
    //fun makePreferred(c: Context) {
    //    val p: PackageManager = c.packageManager
    //    val cN = ComponentName(c, TvActivity::class.java)
    //    p.setComponentEnabledSetting(
    //        cN,
    //        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
    //        PackageManager.DONT_KILL_APP
    //    )
    //    val selector = Intent(Intent.ACTION_MAIN)
    //    selector.addCategory(Intent.CATEGORY_HOME)
    //    c.startActivity(selector)
    //    //p.setComponentEnabledSetting(
    //    //    cN,
    //    //    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    //    //    PackageManager.DONT_KILL_APP
    //    //)
    //}
    private fun playVideo(videoSource: Uri) {
        if(TvSettings.uriInitialized()){
            player = ExoPlayer.Builder(this).build()
            binding.tvActivityVideoPlayer.player = player
            binding.tvActivityVideoPlayer.keepScreenOn = true

            val mediaItem = MediaItem.fromUri(videoSource)
            player.setMediaItem(mediaItem)
            player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            player.prepare()
            player.play()
        }
    }

    private fun stopVideo() {
        try {
            player.stop()
        } catch (exception: UninitializedPropertyAccessException) {
            println(exception.message)
        }
    }

    //register remote key presses, maybe enable admin access after certain remote key press combination?
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                Toast.makeText(this, "dpadup", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                Toast.makeText(this, "dpaddown", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                Toast.makeText(this, "dpadright", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                Toast.makeText(this, "dpadleft", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                //for some reason doesn't register center button click
                Toast.makeText(this, "dpadcenter", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun showViews(){
        if(TvSettings.currentMediaType == TvSettings.MediaType.Photos){
            println(TvSettings.currentMediaType)
            binding.tvActivityImageView.isGone = false
        }
        else if(TvSettings.currentMediaType == TvSettings.MediaType.Video){
            println(TvSettings.currentMediaType)
            binding.tvActivityVideoPlayer.isGone = false
        }
        else if(TvSettings.currentMediaType == TvSettings.MediaType.WebPage){
            println(TvSettings.currentMediaType)
            binding.tvActivityWebView.isGone = false
        }

    }

    private fun playMedia(){
        if(TvSettings.currentMediaType == TvSettings.MediaType.Photos){
            TODO("Don't know what will be the photo source")
        }
        else if(TvSettings.currentMediaType == TvSettings.MediaType.Video){
            playVideo(uri)
        }
        else if(TvSettings.currentMediaType == TvSettings.MediaType.WebPage){
            val webViewClient = CustomWebViewClient(TvSettings.webPageLink)
            binding.tvActivityWebView.webViewClient  = webViewClient
            val webChromeClient =  CustomChromeWebViewClient(TvSettings.webPageLink)
            binding.tvActivityWebView.webChromeClient = webChromeClient
            binding.tvActivityWebView.loadUrl(TvSettings.webPageLink)
        }
    }

    private fun requestTextInputFocus(){
        val handler = Handler(mainLooper)

        val runnable = object: Runnable{
            override fun run() {
                binding.tvActivityAdminCodeInput.requestFocus()
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        stopVideo()
        finishAffinity()
    }
}
