package com.example.demoottmobile.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.usecase.GetCategoriesUseCase
import com.example.demoottmobile.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<MediaCategory>>> = _categoriesState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            try {
                val categories = getCategoriesUseCase()
                _categoriesState.value = UiState.Success(categories)
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
