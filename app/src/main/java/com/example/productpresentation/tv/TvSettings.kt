package com.example.productpresentation.tv

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.example.productpresentation.*
import com.example.productpresentation.database.ConfigurationDBService
import com.example.productpresentation.databinding.AdminPanelLockPopupBinding
import com.example.productpresentation.databinding.AdminPasswordPopupBinding
import com.example.productpresentation.databinding.ConfirmPopupBinding
import com.example.productpresentation.databinding.TvSettingsLayoutBinding
import java.util.*


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
    private var locked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TvSettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(configurationLocked){
            binding.root.post{
                lockedOutPopup()
            }
        }
        else{
            binding.root.post{
                passwordPopup()
            }
        }

        binding.sourceRadioGroup.setOnCheckedChangeListener { _, i ->
            if(i==binding.photoOption.id){
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show()
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = true
                binding.tvSettingsWebUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = false
                binding.passwordChangeInputLayout.isGone = true
                binding.confirmPasswordChangeButton.isGone = true
                binding.photoDisplayTimeInputLayout.isGone = false
                binding.configurationCodeInputLayout.isGone = true
                binding.confirmConfigurationCodeChangeButton.isGone = true
                admin.mediaType = MediaType.Photos
            }
            if(i==binding.videoOption.id){
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = false
                binding.tvSettingsWebUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = true
                binding.passwordChangeInputLayout.isGone = true
                binding.confirmPasswordChangeButton.isGone = true
                binding.photoDisplayTimeInputLayout.isGone = true
                binding.configurationCodeInputLayout.isGone = true
                binding.confirmConfigurationCodeChangeButton.isGone = true
                admin.mediaType = MediaType.Video
            }
            if(i==binding.webPageOption.id){
                binding.tvSettingsWebUrlEditTextLayout.isGone = false
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = true
                binding.passwordChangeInputLayout.isGone = true
                binding.confirmPasswordChangeButton.isGone = true
                binding.photoDisplayTimeInputLayout.isGone = true
                binding.configurationCodeInputLayout.isGone = true
                binding.confirmConfigurationCodeChangeButton.isGone = true
                admin.mediaType = MediaType.WebPage
            }
            if(i==binding.adminSettings.id){
                binding.tvSettingsWebUrlEditTextLayout.isGone = true
                binding.tvSettingsVideoFromUrlEditTextLayout.isGone = true
                binding.pickPhotosButton.isGone = true
                binding.passwordChangeInputLayout.isGone = false
                binding.confirmPasswordChangeButton.isGone = false
                binding.photoDisplayTimeInputLayout.isGone = true
                binding.configurationCodeInputLayout.isGone = false
                binding.confirmConfigurationCodeChangeButton.isGone = false
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

        binding.confirmConfigurationCodeChangeButton.setOnClickListener{
            val newCode = binding.configurationCodeInput.text.toString()
            if(newCode!=""){
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)

                binding.root.post{
                    confirmCodeChangePopup()
                }
            }
        }

    }

    private fun setupViewsOnStart(){
        when (admin.mediaType) {
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
                locked = false
                popupWindow.dismiss()
            }
            else{
                wrongPasswordCounter++
                Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show()
                if(wrongPasswordCounter>2){
                    popupWindow.dismiss()
                    lockTimeStart = Calendar.getInstance().timeInMillis
                    configurationLocked = true
                    binding.root.post{
                        lockedOutPopup()
                    }
                }
            }
        }
        popupWindow.setOnDismissListener {
            if(locked){
                val intent = Intent(this, TvActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }
    }

    private fun confirmCodeChangePopup() {
        val popupBinding = ConfirmPopupBinding.inflate(layoutInflater)

        val popupWindow = getPopupWindow(popupBinding.root)

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        popupWindow.setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                if (motionEvent.x < 0 || motionEvent.x > LinearLayout.LayoutParams.WRAP_CONTENT) return true
                return motionEvent.y < 0 || motionEvent.y > LinearLayout.LayoutParams.WRAP_CONTENT
            }
        })

        popupBinding.confirmButton.setOnClickListener {
            admin.accessCode = binding.configurationCodeInput.text.toString()
            ConfigurationDBService(it.context).updateConfiguration(admin)
            popupWindow.dismiss()
        }
        popupBinding.cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }
    private fun lockedOutPopup(){
        val popupBinding = AdminPanelLockPopupBinding.inflate(layoutInflater)

        val popupWindow = getPopupWindow(popupBinding.root)
        popupWindow.isFocusable = false

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        popupBinding.lockedOutTime.text = "${formatTime(getTimeLeft())}"

        val handler = Handler(mainLooper)
        val runnable = object: Runnable{
            override fun run() {
                popupBinding.lockedOutTime.text = "${formatTime(getTimeLeft())}"
                if(getTimeLeft()<=0){
                    configurationLocked=false
                    popupWindow.dismiss()
                    wrongPasswordCounter = 0
                    binding.root.post{
                        passwordPopup()
                    }
                }
                if(configurationLocked){
                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.post(runnable)


    }

    private fun confirmPasswordChangePopup(){
        val popupBinding = ConfirmPopupBinding.inflate(layoutInflater)

        val popupWindow = getPopupWindow(popupBinding.root)

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        popupBinding.confirmButton.setOnClickListener {
            admin.password = binding.passwordChangeInput.text.toString()
            ConfigurationDBService(it.context).updateConfiguration(admin)
            popupWindow.dismiss()
        }
        popupBinding.cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun getTimeLeft(): Long{
        return (lockTimeStart+600000)-Calendar.getInstance().timeInMillis
    }

    private fun formatTime(timeInMillis: Long): String{
        val seconds = timeInMillis/1000
        val minutes = seconds/60
        val leftSeconds = seconds%60
        return "$minutes min $leftSeconds s"
    }

    private fun onExit(){
        webPageLink = binding.tvSettingsWebUrlEditText.text.toString()
        uri = Uri.parse(binding.tvSettingsVideoFromUrlEditText.text.toString())
        ConfigurationDBService(this).updateConfiguration(admin)
    }

    companion object MediaTypeSettings{
        var photoUris = mutableListOf<Uri>()
        var webPageLink = ""
        lateinit var uri: Uri
        var photoDisplayTimeSeconds = 30
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