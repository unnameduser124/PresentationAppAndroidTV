package com.example.productpresentation.tv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import com.example.productpresentation.databinding.TvSettingsLayoutBinding

//settings activity for android tv devices
class TvSettings: AppCompatActivity() {

    private val pickPhotos =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                uris.forEach{ uri ->
                    this.contentResolver.takePersistableUriPermission(uri, flag)
                }
            } else {
                println("NO FILES SELECTED")
            }
        }
    private lateinit var binding: TvSettingsLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TvSettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.sourceRadioGroup.setOnCheckedChangeListener { _, i ->
            if(i==binding.photoOption.id){
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show()
                binding.tvSettingsVideoFromUrlEditText.isGone = true
                binding.tvSettingsWebUrlEditText.isGone = true
                binding.pickPhotosButton.isGone = false
                currentMediaType = MediaType.Photos
            }
            if(i==binding.videoOption.id){
                binding.tvSettingsVideoFromUrlEditText.isGone = false
                binding.tvSettingsWebUrlEditText.isGone = true
                binding.pickPhotosButton.isGone = true
                currentMediaType = MediaType.Video
            }
            if(i==binding.webPageOption.id){
                binding.tvSettingsWebUrlEditText.isGone = false
                binding.tvSettingsVideoFromUrlEditText.isGone = true
                binding.pickPhotosButton.isGone = true
                currentMediaType = MediaType.WebPage
            }
        }

        setupViewsOnStart()


        binding.pickPhotosButton.setOnClickListener{
            pickPhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        binding.tvSettingsWebUrlEditText.doOnTextChanged { text, start, before, count ->
            webPageLink = text.toString()
        }

        binding.tvSettingsVideoFromUrlEditText.doOnTextChanged { text, start, before, count ->
            uri = Uri.parse(text.toString())
        }
    }
    enum class MediaType{
        Photos,
        Video,
        WebPage,
        NoSelection
    }

    fun setupViewsOnStart(){
        when (currentMediaType) {
            MediaType.Video -> {
                binding.videoOption.isChecked = true
                binding.videoOption.callOnClick()
            }
            MediaType.WebPage -> {
                binding.tvSettingsWebUrlEditText.setText(webPageLink)
                binding.webPageOption.isChecked = true
                binding.webPageOption.callOnClick()
            }
            else -> {
                binding.photoOption.isChecked = true
                binding.photoOption.callOnClick()
            }
        }
    }
    private fun onExit(){
        webPageLink = binding.tvSettingsWebUrlEditText.toString()
        uri = Uri.parse(binding.tvSettingsVideoFromUrlEditText.toString())

    }

    companion object MediaTypeSettings{
        var currentMediaType = MediaType.NoSelection
        var photoUris = mutableListOf<Uri>()
        var webPageLink = ""
        lateinit var uri: Uri
        fun uriInitialized(): Boolean {
            return this::uri.isInitialized
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onExit()
        val intent = Intent(this, TvActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}