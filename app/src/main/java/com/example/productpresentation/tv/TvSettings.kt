package com.example.productpresentation.tv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.productpresentation.databinding.TvSettingsLayoutBinding

//settings activity for android tv devices
class TvSettings: AppCompatActivity() {

    private lateinit var binding: TvSettingsLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TvSettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sourceRadioGroup.setOnCheckedChangeListener { _, i ->
            if(i==binding.photoOption.id){
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show()
            }
            if(i==binding.videoOption.id){
                binding.tvSettingsFromUrlEditText.isGone = false
                binding.tvSettingsWebUrlEditText.isGone = true
            }
            if(i==binding.webPageOption.id){
                binding.tvSettingsWebUrlEditText.isGone = false
                binding.tvSettingsFromUrlEditText.isGone = true
            }
        }
    }
}