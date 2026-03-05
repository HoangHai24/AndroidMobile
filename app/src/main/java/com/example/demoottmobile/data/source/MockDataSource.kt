package com.example.demoottmobile.data.source

import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.domain.model.MediaType
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_STREAM_URL =
    "https://cdn-demo-sigma-livestreaming.sigma.video/data/vod/sigma-vod/" +
    "168b85fe-3184-41e6-a85b-f491c302a92e/hls-BM/master.m3u8"

private val PLACEHOLDER_THUMBNAILS = listOf(
    "https://picsum.photos/seed/ott1/320/180",
    "https://picsum.photos/seed/ott2/320/180",
    "https://picsum.photos/seed/ott3/320/180",
    "https://picsum.photos/seed/ott4/320/180",
    "https://picsum.photos/seed/ott5/320/180",
    "https://picsum.photos/seed/ott6/320/180",
    "https://picsum.photos/seed/ott7/320/180",
    "https://picsum.photos/seed/ott8/320/180",
    "https://picsum.photos/seed/ott9/320/180",
    "https://picsum.photos/seed/ott10/320/180",
    "https://picsum.photos/seed/ott11/320/180",
    "https://picsum.photos/seed/ott12/320/180",
    "https://picsum.photos/seed/ott13/320/180",
    "https://picsum.photos/seed/ott14/320/180",
    "https://picsum.photos/seed/ott15/320/180",
    "https://picsum.photos/seed/ott16/320/180"
)

@Singleton
class MockDataSource @Inject constructor() {

    fun getCategories(): List<MediaCategory> = listOf(
        MediaCategory(
            id = "cat_trending",
            title = "Trending Now",
            items = generateItems("trending", 16)
        ),
        MediaCategory(
            id = "cat_movies",
            title = "Movies",
            items = generateItems("movies", 14)
        ),
        MediaCategory(
            id = "cat_series",
            title = "TV Series",
            items = generateItems("series", 16)
        ),
        MediaCategory(
            id = "cat_sports",
            title = "Sports",
            items = generateItems("sports", 12)
        ),
        MediaCategory(
            id = "cat_kids",
            title = "Kids & Family",
            items = generateItems("kids", 10)
        ),
        MediaCategory(
            id = "cat_news",
            title = "News",
            items = generateItems("news", 8)
        )
    )

    fun getChannels(): List<MediaItem> = (1..24).map { i ->
        MediaItem(
            id = "ch_$i",
            title = "Channel $i",
            thumbnailUrl = PLACEHOLDER_THUMBNAILS[(i - 1) % PLACEHOLDER_THUMBNAILS.size],
            streamUrl = DEFAULT_STREAM_URL,
            type = MediaType.CHANNEL
        )
    }

    fun getListingItems(categoryId: String): List<MediaItem> =
        generateItems(categoryId, 20)

    private fun generateItems(prefix: String, count: Int): List<MediaItem> =
        (1..count).map { i ->
            MediaItem(
                id = "${prefix}_$i",
                title = "${prefix.replaceFirstChar { it.uppercase() }} Item $i",
                thumbnailUrl = PLACEHOLDER_THUMBNAILS[(i - 1) % PLACEHOLDER_THUMBNAILS.size],
                streamUrl = DEFAULT_STREAM_URL,
                type = MediaType.VOD
            )
        }
}
