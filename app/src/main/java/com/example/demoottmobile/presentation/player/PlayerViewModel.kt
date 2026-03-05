package com.example.demoottmobile.presentation.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    var player: ExoPlayer? = null
        private set

    private var currentUrl: String? = null

    fun preparePlayer(streamUrl: String) {
        if (currentUrl == streamUrl && player != null) return
        releasePlayer()
        currentUrl = streamUrl
        player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun pausePlayer() {
        player?.pause()
    }

    fun resumePlayer() {
        player?.play()
    }

    fun releasePlayer() {
        player?.release()
        player = null
        currentUrl = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
