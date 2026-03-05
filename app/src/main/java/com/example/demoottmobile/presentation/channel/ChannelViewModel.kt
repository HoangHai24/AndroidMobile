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

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase
) : ViewModel() {

    private val _channelsState = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val channelsState: StateFlow<UiState<List<MediaItem>>> = _channelsState.asStateFlow()

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _channelsState.value = UiState.Loading
            try {
                val channels = getChannelsUseCase()
                _channelsState.value = UiState.Success(channels)
            } catch (e: Exception) {
                _channelsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
