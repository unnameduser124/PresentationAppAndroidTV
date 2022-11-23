package com.example.productpresentation.tv

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import com.example.productpresentation.admin
import com.example.productpresentation.databinding.AdminPasswordPopupBinding
import com.example.productpresentation.databinding.ConfirmPopupBinding
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
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = true
                binding.tvSettingsWebUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = false
                binding.passwordChangeInputLayout.isGone = true
                binding.confirmPasswordChangeButton.isGone = true
                currentMediaType = MediaType.Photos
            }
            if(i==binding.videoOption.id){
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = false
                binding.tvSettingsWebUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = true
                binding.passwordChangeInputLayout.isGone = true
                binding.confirmPasswordChangeButton.isGone = true
                currentMediaType = MediaType.Video
            }
            if(i==binding.webPageOption.id){
                binding.tvSettingsWebUrlEditTextLayout.isGone = false
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = true
                binding.passwordChangeInputLayout.isGone = true
                binding.confirmPasswordChangeButton.isGone = true
                currentMediaType = MediaType.WebPage
            }
            if(i==binding.adminSettings.id){
                binding.tvSettingsWebUrlEditTextLayout.isGone = true
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = true
                binding.passwordChangeInputLayout.isGone = false
                binding.confirmPasswordChangeButton.isGone = false
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

        binding.confirmPasswordChangeButton.setOnClickListener {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            binding.root.post{
                confirmPasswordChangePopup()
            }
        }

        binding.root.post{
            passwordPopup()
        }
    }
    enum class MediaType{
        Photos,
        Video,
        WebPage,
        NoSelection
    }

    private fun setupViewsOnStart(){
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

    private fun getPopupWindow(popupBinding: ConstraintLayout): PopupWindow {
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        val focusable = true

        val popupWindow = PopupWindow(popupBinding, width, height, focusable)
        popupWindow.contentView = popupBinding
        return popupWindow
    }

    private fun passwordPopup(){
        val popupBinding = AdminPasswordPopupBinding.inflate(layoutInflater)

        val popupWindow = getPopupWindow(popupBinding.root)

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)


        popupWindow.setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                if (motionEvent.x < 0 || motionEvent.x > LinearLayout.LayoutParams.WRAP_CONTENT) return true
                return motionEvent.y < 0 || motionEvent.y > LinearLayout.LayoutParams.WRAP_CONTENT
            }
        })
        popupBinding.passwordConfirmButton.setOnClickListener { 
            if(popupBinding.adminPanelPasswordInput.text.toString() == admin.password){
                popupWindow.dismiss()
            }
            else{
                Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmPasswordChangePopup(){
        val popupBinding = ConfirmPopupBinding.inflate(layoutInflater)

        val popupWindow = getPopupWindow(popupBinding.root)

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        popupBinding.confirmButton.setOnClickListener {
            admin.password = binding.passwordChangeInput.text.toString()
            popupWindow.dismiss()
        }
        popupBinding.cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun onExit(){
        webPageLink = binding.tvSettingsWebUrlEditText.text.toString()
        uri = Uri.parse(binding.tvSettingsVideoFromUrlEditText.text.toString())

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