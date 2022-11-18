package com.example.productpresentation

import android.Manifest
import android.app.UiModeManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.productpresentation.databinding.ActivityMainBinding
import com.example.productpresentation.tv.TvActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkStoragePermission()

        //detect whether device is an android tv and if it is redirect to TvActivity
        val uiModeManager = getSystemService(UI_MODE_SERVICE)
        if ((uiModeManager as UiModeManager).currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            val intent = Intent(this, TvActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        binding.webViewButton.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }

        binding.exoPlayerButton.setOnClickListener {
            val intent = Intent(this, ExoPlayerActivity::class.java)
            startActivity(intent)
        }
    }

    //ask for storage permission needed to get access to media
    private fun checkStoragePermission(){
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if(!isGranted){
                    Toast.makeText(this, "permission needed", Toast.LENGTH_SHORT).show()
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) -> {
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}