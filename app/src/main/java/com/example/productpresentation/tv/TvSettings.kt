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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.productpresentation.*
import com.example.productpresentation.database.ConfigurationDBService
import com.example.productpresentation.database.UriDBService
import com.example.productpresentation.databinding.AdminPanelLockPopupBinding
import com.example.productpresentation.databinding.AdminPasswordPopupBinding
import com.example.productpresentation.databinding.ConfirmPopupBinding
import com.example.productpresentation.databinding.TvSettingsLayoutBinding
import com.example.productpresentation.fileExplorer.FileExplorerActivity
import com.example.productpresentation.fileExplorer.VideoFileExplorerActivity
import java.io.File
import java.util.*


//settings activity for android tv devices
class TvSettings : AppCompatActivity() {

    private val pickPhotos =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                uris.forEach { uri ->
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

        binding.fillScreenSwitch.isChecked = admin.fillScreen
        binding.fillScreenSwitchVideo.isChecked = admin.fillScreen

        if (configurationLocked) {
            binding.root.post {
                lockedOutPopup()
            }
        } else {
            val requirePassword = intent.getBooleanExtra("REQUIRE_PASSWORD", true)
            if (requirePassword) {
                binding.root.post {
                    passwordPopup()
                }
            }
        }

        binding.sourceRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                binding.photoOption.id -> {
                    admin.mediaType = MediaType.Photos
                    ConfigurationDBService(this).updateConfiguration(admin)
                    if(uriList.isNotEmpty()){
                        if(!isImage(uriList.first().path)){
                            uriList.clear()
                        }
                    }
                    photoOptionViewsVisibility()
                }
                binding.videoOption.id -> {
                    admin.mediaType = MediaType.Video
                    ConfigurationDBService(this).updateConfiguration(admin)
                    if(uriList.isNotEmpty()){
                        if(!isVideo(uriList.first().path)){
                            uriList.clear()
                        }
                    }
                    videoOptionViewsVisibility()
                }
                binding.webPageOption.id -> {
                    admin.mediaType = MediaType.WebPage
                    ConfigurationDBService(this).updateConfiguration(admin)
                    if(uriList.isNotEmpty()){
                        if(isVideo(uriList.first().path) || isImage(uriList.first().path)){
                            uriList.clear()
                        }
                    }
                    webPageOptionViewsVisibility()
                }
                binding.adminSettings.id -> {
                    adminConfigViewsVisibility()
                }
            }
        }

        setupViewsOnStart()

        binding.pickPhotosButton.setOnClickListener {
            val intent = Intent(this, FileExplorerActivity::class.java)
            intent.putExtra("MEDIA_TYPE", "PHOTO")
            startActivity(intent)
        }

        binding.pickVideoButton.setOnClickListener {
            val intent = Intent(this, VideoFileExplorerActivity::class.java)
            intent.putExtra("MEDIA_TYPE", "PHOTO")
            startActivity(intent)
        }

        binding.tvSettingsWebUrlEditText.doOnTextChanged { text, start, before, count ->
            uriList.clear()
            val dbService = UriDBService(this)
            dbService.clearTable()
            val path = text.toString()
            dbService.addWebPageUri(path)
            uriList = dbService.getAllUris().toMutableList()
        }

        binding.tvSettingsVideoFromUrlEditText.doOnTextChanged { text, start, before, count ->
            if (isVideo(text.toString())) {
                uriList.clear()
                val dbService = UriDBService(this)
                dbService.clearTable()
                dbService.addUri(Uri.parse(text.toString()))
                uriList = dbService.getAllUris().toMutableList()
            }
        }

        binding.confirmPasswordChangeButton.setOnClickListener {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            binding.root.post {
                confirmPasswordChangePopup()
            }
        }

        binding.confirmConfigurationCodeChangeButton.setOnClickListener {
            val newCode = binding.configurationCodeInput.text.toString()
            if (newCode != "") {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)

                binding.root.post {
                    confirmCodeChangePopup()
                }
            }
        }

        binding.photoDisplayTimeInput.doOnTextChanged { text, start, before, count ->
            if (text.toString() != "") {
                admin.photoDisplayTime = text.toString().toInt()
            }
        }

        binding.fillScreenSwitch.setOnCheckedChangeListener { _, isChecked ->
            admin.fillScreen = isChecked
            binding.fillScreenSwitchVideo.isChecked = admin.fillScreen
        }
        binding.fillScreenSwitchVideo.setOnCheckedChangeListener { _, isChecked ->
            admin.fillScreen = isChecked
            binding.fillScreenSwitch.isChecked = admin.fillScreen
        }

        binding.photoDisplayTimeInput.setText(admin.photoDisplayTime.toString())
    }

    private fun videoOptionViewsVisibility() {
        binding.configElementsGroup.isGone = true
        binding.webElementsGroup.isGone = true
        binding.photoElementsGroup.isGone = true
        binding.videoElementsGroup.isGone = false
        if (uriList.isNotEmpty()) {
            binding.tvSettingsVideoFromUrlEditText.setText(uriList.first().path)
        } else {
            binding.tvSettingsVideoFromUrlEditText.setText("")
        }
        admin.mediaType = MediaType.Video
    }

    private fun webPageOptionViewsVisibility() {
        binding.videoElementsGroup.isGone = true
        binding.configElementsGroup.isGone = true
        binding.photoElementsGroup.isGone = true
        binding.webElementsGroup.isGone = false
        if (uriList.isNotEmpty()) {
            binding.tvSettingsWebUrlEditText.setText(uriList.first().path)
        } else {
            binding.photosPickedFolderTextView.text = ""
        }
        admin.mediaType = MediaType.WebPage
    }

    private fun adminConfigViewsVisibility() {
        binding.videoElementsGroup.isGone = true
        binding.webElementsGroup.isGone = true
        binding.photoElementsGroup.isGone = true
        binding.configElementsGroup.isGone = false
    }

    private fun photoOptionViewsVisibility() {
        binding.videoElementsGroup.isGone = true
        binding.configElementsGroup.isGone = true
        binding.webElementsGroup.isGone = true
        binding.photoElementsGroup.isGone = false

        if (uriList.isNotEmpty()) {
            println(getUriLocation(uriList.first().path))
            binding.photosPickedFolderTextView.text = getUriLocation(uriList.first().path)
        } else {
            binding.photosPickedFolderTextView.isGone = true
        }
        admin.mediaType = MediaType.Photos
    }

    private fun getUriLocation(path: String): String {
        for (i in path.length - 1 downTo 0) {
            if (path[i] == '/') {
                return path.substring(0, i)
            }
        }
        return System.getenv("EXTERNAL_STORAGE") as String;
    }

    private fun setupViewsOnStart() {
        when (admin.mediaType) {
            MediaType.Video -> {
                binding.videoOption.isChecked = true
                videoOptionViewsVisibility()
                binding.videoOption.callOnClick()
            }
            MediaType.WebPage -> {
                binding.tvSettingsWebUrlEditText.setText(webPageLink)
                binding.webPageOption.isChecked = true
                webPageOptionViewsVisibility()
                binding.webPageOption.callOnClick()
            }
            else -> {
                binding.photoOption.isChecked = true
                photoOptionViewsVisibility()
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

    private fun passwordPopup() {
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
            if (popupBinding.adminPanelPasswordInput.text.toString() == admin.password) {
                locked = false
                popupWindow.dismiss()
            } else {
                wrongPasswordCounter++
                Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show()
                if (wrongPasswordCounter > 2) {
                    popupWindow.dismiss()
                    lockTimeStart = Calendar.getInstance().timeInMillis
                    configurationLocked = true
                    binding.root.post {
                        lockedOutPopup()
                    }
                }
            }
        }
        popupWindow.setOnDismissListener {
            if (locked) {
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

    private fun lockedOutPopup() {
        val popupBinding = AdminPanelLockPopupBinding.inflate(layoutInflater)

        val popupWindow = getPopupWindow(popupBinding.root)
        popupWindow.isFocusable = false

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        popupBinding.lockedOutTime.text = formatTime(getTimeLeft())

        val handler = Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                popupBinding.lockedOutTime.text = formatTime(getTimeLeft())
                if (getTimeLeft() <= 0) {
                    configurationLocked = false
                    popupWindow.dismiss()
                    wrongPasswordCounter = 0
                    binding.root.post {
                        passwordPopup()
                    }
                }
                if (configurationLocked) {
                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.post(runnable)


    }

    private fun confirmPasswordChangePopup() {
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

    private fun getTimeLeft(): Long {
        return (lockTimeStart + 600000) - Calendar.getInstance().timeInMillis
    }

    private fun formatTime(timeInMillis: Long): String {
        val seconds = timeInMillis / 1000
        val minutes = seconds / 60
        val leftSeconds = seconds % 60
        return "$minutes min $leftSeconds s"
    }

    private fun isVideo(file: String): Boolean {
        if (file.endsWith(".mp4")) {
            return true
        } else if (file.endsWith(".mkv")) {
            return true
        } else if (file.endsWith(".webm")) {
            return true
        }
        return false
    }

    private fun isImage(file: String): Boolean {
        if(file.endsWith(".jpg")){
            return true
        }
        else if(file.endsWith(".jpeg")){
            return true
        }
        else if(file.endsWith(".png")){
            return true
        }
        else if(file.endsWith(".webp")){
            return true
        }
        return false
    }

    private fun onExit() {
        webPageLink = binding.tvSettingsWebUrlEditText.text.toString()
        ConfigurationDBService(this).updateConfiguration(admin)
    }

    companion object MediaTypeSettings {
        var webPageLink = ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onExit()
        val intent = Intent(this, TvActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}