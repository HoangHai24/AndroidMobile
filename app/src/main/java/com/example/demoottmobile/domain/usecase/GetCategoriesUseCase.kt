package com.example.demoottmobile.domain.usecase

import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.repository.MediaRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<MediaCategory> = repository.getCategories()
}
