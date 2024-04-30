package com.rubenmackin.firestorage.ui.xml.upload

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.rubenmackin.firestorage.R
import com.rubenmackin.firestorage.databinding.ActivityUploadXmlBinding
import com.rubenmackin.firestorage.databinding.DialogImageSelectorBinding
import com.rubenmackin.firestorage.ui.xml.list.ListXmlActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@AndroidEntryPoint
class UploadXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent = Intent(context, UploadXmlActivity::class.java)
    }

    private lateinit var binding: ActivityUploadXmlBinding
    private val uploadXmlViewModel: UploadXmlViewModel by viewModels()

    private lateinit var uri: Uri

    //LAUCHERS
    private var intentGalleryLaucher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadXmlViewModel.uploadAndGetImage(uri) { downloadUri ->
                    showNewImage(downloadUri)
                }
            }

        }

    private fun showNewImage(downloadUri: Uri) {
        Glide.with(this).load(downloadUri).into(binding.ivImage)
    }

    private var intentCameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it && uri.path?.isNotEmpty() == true) {
                uploadXmlViewModel.uploadAndGetImage(uri) { downloadUri ->
                    showNewImage(downloadUri)
                    clearText()
                }
            } // comprobar la URI
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadXmlBinding.inflate(layoutInflater)
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
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uploadXmlViewModel.isLoading.collect {
                    binding.pbImage.isVisible = it
                    if (it) {
                        binding.ivPlaceHolder.isVisible = false
                        binding.ivImage.setImageDrawable(null)
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.fabImage.setOnClickListener {
            showImageDialog()
        }
        binding.btnNavigateToList.setOnClickListener {
            startActivity(ListXmlActivity.create(this))
        }
    }

    private fun showImageDialog() {
        val dialogBinding = DialogImageSelectorBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.btnTakePhoto.setOnClickListener {
            takePhoto()
            alertDialog.dismiss()
        }

        dialogBinding.btnGalery.setOnClickListener {
            getImageFromGalery()
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun getImageFromGalery() {
        intentGalleryLaucher.launch("image/*")
    }

    private fun takePhoto() {
        generateUri()
        intentCameraLauncher.launch(uri)
    }

    private fun generateUri() {
        uri = FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.rubenmackin.firestorage.provider",
            createFile()
        )
    }

    private fun clearText() {
        binding.etTitle.setText("")
        binding.etTitle.clearFocus()
    }

    @SuppressLint("SimpleDateFormat")
    private fun createFile(): File {
        val userTitle = binding.etTitle.text.toString()
        val name: String = userTitle.ifEmpty {
            SimpleDateFormat("yyyyMMdd_hhmmss").format(Date()) + "image"
        }

        return File.createTempFile(name, ".jpg", externalCacheDir)
    }
}