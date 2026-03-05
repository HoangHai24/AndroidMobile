package com.example.demoottmobile.domain.model

data class MediaItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val streamUrl: String,
    val type: MediaType = MediaType.VOD
)

enum class MediaType {
    VOD, LIVE, CHANNEL
}
