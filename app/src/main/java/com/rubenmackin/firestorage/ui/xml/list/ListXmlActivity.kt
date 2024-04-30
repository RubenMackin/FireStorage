package com.rubenmackin.firestorage.ui.xml.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.rubenmackin.firestorage.R
import com.rubenmackin.firestorage.databinding.ActivityListXmlBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) = Intent(context, ListXmlActivity::class.java)
    }

    private val listXmlViewModel: ListXmlViewModel by viewModels()
    private lateinit var binding: ActivityListXmlBinding
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        listXmlViewModel.getAllImages()
    }

    private fun initUI() {
        initUIState()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        galleryAdapter = GalleryAdapter()
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(this@ListXmlActivity, 2)
            adapter = galleryAdapter
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listXmlViewModel.uiState.collect { uiState ->
                    Log.i("aaris", uiState.images.toString())
                    galleryAdapter.updateList(uiState.images)
                    binding.pbGallery.isVisible = uiState.isLoading
                }
            }
        }
    }
}