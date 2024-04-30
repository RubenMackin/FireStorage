package com.rubenmackin.firestorage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.textclassifier.TextClassificationContext
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rubenmackin.firestorage.databinding.ActivityMainBinding
import com.rubenmackin.firestorage.databinding.ActivityUploadXmlBinding
import com.rubenmackin.firestorage.ui.compose.upload.UploadComposeActivity
import com.rubenmackin.firestorage.ui.xml.upload.UploadXmlActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.btnNavigateToCompose.setOnClickListener {
            startActivity(UploadComposeActivity.create(this))
        }
        binding.btnNavigateToXml.setOnClickListener {
            startActivity(UploadXmlActivity.create(this))
        }
    }
}