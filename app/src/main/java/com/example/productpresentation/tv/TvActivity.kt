package com.example.productpresentation.tv

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.productpresentation.databinding.TvMainActivityBinding


class TvActivity: AppCompatActivity() {

    private lateinit var binding: TvMainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TvMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvUrlTextInput.showSoftInputOnFocus = false
        binding.tvUrlTextInput.requestFocus()

        binding.tvUrlTextInput.inputType = InputType.TYPE_NULL

        binding.tvUrlTextInput.addTextChangedListener {
            if(it.toString() == "code"){
                val intent = Intent(this, TvSettings::class.java)
                startActivity(intent)
            }
        }
    }

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
                Toast.makeText(this, "dpadcenter", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                return false
            }
        }
    }
}
