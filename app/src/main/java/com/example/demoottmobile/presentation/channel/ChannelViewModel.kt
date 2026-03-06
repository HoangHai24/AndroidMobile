package com.example.demoottmobile.presentation.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.usecase.GetChannelsUseCase
import com.example.demoottmobile.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// Giống HomeViewModel - xem giải thích đầy đủ ở HomeViewModel.kt
// ═══════════════════════════════════════════════════════

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase
) : ViewModel() {

    // Private MutableStateFlow: chỉ ViewModel được ghi
    // Java tương đương: private MutableLiveData<UiState<List<MediaItem>>> _channelsState;
    private val _channelsState = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)

    // Public StateFlow read-only: Fragment chỉ đọc được
    // Java: public LiveData<UiState<List<MediaItem>>> getChannelsState() { return _channelsState; }
    val channelsState: StateFlow<UiState<List<MediaItem>>> = _channelsState.asStateFlow()

    // "init" = cód trong constructor của Java - chạy ngay khi ViewModel được tạo
    init {
        loadChannels()
    }

    // Java: public void loadChannels() { ... dùng executor/thread ... }
    fun loadChannels() {
        viewModelScope.launch {
            _channelsState.value = UiState.Loading
            try {
                val channels = getChannelsUseCase() // gọi operator invoke
                _channelsState.value = UiState.Success(channels)
            } catch (e: Exception) {
                // "?:" = Elvis operator: nếu null thì dùng giá trị bên phải
                _channelsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
