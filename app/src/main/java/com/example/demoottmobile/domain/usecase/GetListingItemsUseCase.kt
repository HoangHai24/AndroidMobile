package com.example.demoottmobile.domain.usecase

import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.repository.MediaRepository
import javax.inject.Inject

class GetListingItemsUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(categoryId: String): List<MediaItem> =
        repository.getListingItems(categoryId)
}
