package com.example.productpresentation.tv

import android.Manifest.permission.READ_EXTERNAL_STORAGE
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
import com.example.productpresentation.database.ConfigurationDBService
import com.example.productpresentation.databinding.TvMainActivityBinding
import com.example.productpresentation.tv.TvSettings.MediaTypeSettings.uri
import com.example.productpresentation.uriList
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.util.*


//default activity for android tv devices
class TvActivity: AppCompatActivity() {

    private var shuffling = false
    private lateinit var binding: TvMainActivityBinding
    private lateinit var player: ExoPlayer
    private var lastInputTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TvMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestStoragePermission()
        showViews()
        playMedia()
        requestTextInputFocus()
        ConfigurationDBService(this).getConfiguration()


        //hide text view to write admin code into
        binding.tvActivityAdminCodeInput.showSoftInputOnFocus = false
        binding.tvActivityAdminCodeInput.requestFocus()
        //disable soft input keyboard
        binding.tvActivityAdminCodeInput.inputType = InputType.TYPE_NULL

        binding.tvActivityAdminCodeInput.addTextChangedListener {
            lastInputTime = Calendar.getInstance().timeInMillis
            if(it.toString() == admin.accessCode){
                binding.tvActivityAdminCodeInput.setText("")
                stopVideo()
                val intent = Intent(this, TvSettings::class.java)
                startActivity(intent)
            }
            else{
                val handler = Handler(mainLooper)
                val runnable = object: Runnable{
                    override fun run() {
                        if(Calendar.getInstance().timeInMillis - lastInputTime > 10000){
                            binding.tvActivityAdminCodeInput.setText("")
                        }
                        else{
                            handler.postDelayed(this, 500)
                        }
                    }
                }
                handler.post(runnable)
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
        if(admin.mediaType == MediaType.Photos){
            binding.tvActivityImageView.isGone = false
        }
        else if(admin.mediaType == MediaType.Video){
            binding.tvActivityVideoPlayer.isGone = false
        }
        else if(admin.mediaType == MediaType.WebPage){
            binding.tvActivityWebView.isGone = false
        }

    }

    private fun setImage(uri: Uri) {
        binding.tvActivityImageView.setImageURI(uri)
    }

    private fun startPhotoSlideshow(){
        var index = 0
        shuffling = true
        val handler = Handler(mainLooper)
        val runnableCode = object: Runnable {
            override fun run(){
                try {
                    if (index < uriList.size) {
                        setImage(uriList[index])
                        index++
                    } else {
                        index = 0
                        setImage(uriList[index])
                    }
                } catch (exception: UninitializedPropertyAccessException) {
                    shuffling = false
                    println(exception.message)
                }
                if(shuffling){
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnableCode)
    }

    private fun playMedia(){
        if(admin.mediaType == MediaType.Photos){
            if(uriList.isNotEmpty()){
                startPhotoSlideshow()
            }
        }
        else if(admin.mediaType == MediaType.Video){
            playVideo(uri)
        }
        else if(admin.mediaType == MediaType.WebPage){
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

    private fun requestStoragePermission(){
        requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopVideo()
        finishAffinity()
    }
}
