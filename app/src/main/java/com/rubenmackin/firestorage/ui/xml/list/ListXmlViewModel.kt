package com.rubenmackin.firestorage.ui.xml.list

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
class ListXmlViewModel @Inject constructor(private val storageService: StoregeService) :
    ViewModel() {


    private var _uiState = MutableStateFlow(ListUIState(false, emptyList()))
    val uiState: StateFlow<ListUIState> = _uiState


    fun getAllImages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = withContext(Dispatchers.IO) {
                storageService.getAllImages().map { it.toString() }
            }
            _uiState.value = _uiState.value.copy(isLoading = false, images = result)
        }
    }
}

data class ListUIState(
    val isLoading: Boolean,
    val images: List<String>
)