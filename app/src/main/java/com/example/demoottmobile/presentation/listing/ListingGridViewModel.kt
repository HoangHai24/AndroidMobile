package com.example.demoottmobile.presentation.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.usecase.GetListingItemsUseCase
import com.example.demoottmobile.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingGridViewModel @Inject constructor(
    private val getListingItemsUseCase: GetListingItemsUseCase
) : ViewModel() {

    private val _listingState = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val listingState: StateFlow<UiState<List<MediaItem>>> = _listingState.asStateFlow()

    fun loadItems(categoryId: String) {
        viewModelScope.launch {
            _listingState.value = UiState.Loading
            try {
                val items = getListingItemsUseCase(categoryId)
                _listingState.value = UiState.Success(items)
            } catch (e: Exception) {
                _listingState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
