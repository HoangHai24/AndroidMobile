package com.example.demoottmobile.domain.repository

import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem

interface MediaRepository {
    suspend fun getCategories(): List<MediaCategory>
    suspend fun getChannels(): List<MediaItem>
    suspend fun getListingItems(categoryId: String): List<MediaItem>
}
