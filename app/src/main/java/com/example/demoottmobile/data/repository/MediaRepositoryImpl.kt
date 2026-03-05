package com.example.demoottmobile.data.repository

import com.example.demoottmobile.data.source.MockDataSource
import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mockDataSource: MockDataSource
) : MediaRepository {

    override suspend fun getCategories(): List<MediaCategory> = withContext(Dispatchers.IO) {
        delay(500) // Simulate network delay
        mockDataSource.getCategories()
    }

    override suspend fun getChannels(): List<MediaItem> = withContext(Dispatchers.IO) {
        delay(400)
        mockDataSource.getChannels()
    }

    override suspend fun getListingItems(categoryId: String): List<MediaItem> = withContext(Dispatchers.IO) {
        delay(400)
        mockDataSource.getListingItems(categoryId)
    }
}
