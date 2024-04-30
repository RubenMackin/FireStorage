package com.rubenmackin.firestorage.ui.compose.upload

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubenmackin.firestorage.data.StoregeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UploadComposeViewModel @Inject constructor(private val storegeService: StoregeService): ViewModel() {

    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading:StateFlow<Boolean> = _isLoading

    fun uploadBasicImage(uri: Uri) {
        storegeService.uploadBasicImage(uri)
    }

    fun uploadAndGetImage(uri: Uri, onSuccessDownload: (Uri) -> Unit) {

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    storegeService.uploadAndDownloadImage(uri)
                }
                onSuccessDownload(result)
            } catch (e: Exception) {
                Log.i("error", e.message.orEmpty())
            }
            _isLoading.value = false
        }
    }
}