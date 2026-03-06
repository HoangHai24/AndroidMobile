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

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG (giống HomeViewModel - xem HomeViewModel.kt)
// Điểm khác: loadItems nhận tham số categoryId thay vì không tham số
// và không tự gọi trong init (Fragment gọi thủ công sau khi có tham số nav)
// ═══════════════════════════════════════════════════════

@HiltViewModel
class ListingGridViewModel @Inject constructor(
    private val getListingItemsUseCase: GetListingItemsUseCase
) : ViewModel() {

    // Trạng thái UI: Loading / Success / Error
    private val _listingState = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val listingState: StateFlow<UiState<List<MediaItem>>> = _listingState.asStateFlow()

    // Không có "init" vì cần categoryId từ Fragment trước khi load.
    // Fragment sẽ gọi "viewModel.loadItems(categoryId)" sau khi nhận args từ navigation.
    // Java: public void loadItems(String categoryId) { ... }
    fun loadItems(categoryId: String) {
        viewModelScope.launch {
            _listingState.value = UiState.Loading
            try {
                // Gọi use case với tham số categoryId
                val items = getListingItemsUseCase(categoryId)
                _listingState.value = UiState.Success(items)
            } catch (e: Exception) {
                _listingState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
