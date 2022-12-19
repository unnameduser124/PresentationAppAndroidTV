package com.example.productpresentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.productpresentation.database.ConfigurationDBService
import com.example.productpresentation.databinding.FirstConfigurationLayoutBinding
import com.example.productpresentation.tv.TvSettings

class FirstConfig: AppCompatActivity() {

    private lateinit var binding: FirstConfigurationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FirstConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestStoragePermission()

        binding.firstConfCodeInput.requestFocus()

        binding.confirmFirstConfigButton.setOnClickListener {
            val code = binding.firstConfCodeInput.text.toString()
            val password = binding.firstConfPasswordInput.text.toString()
            if(code!=""){
                admin.apply{
                    accessCode = code
                    this.password = password
                }

                ConfigurationDBService(this).updateConfiguration(admin)
                val intent = Intent(this, TvSettings::class.java)
                intent.putExtra("REQUIRE_PASSWORD", false)
                startActivity(intent)
                firstStart = false
            }
            else{
                Toast.makeText(this, "Code can't be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun requestStoragePermission(){
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

}