package com.example.demoottmobile.presentation.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "@ApplicationContext private val context: Context"
//   - Inject Application Context (không có Activity) → an toàn, không leak.
//   - Dùng để tạo ExoPlayer (cần Context nhưng không cần Activity).
//
// "var player: ExoPlayer? = null\n    private set"
//   - "var" = biến có thể thay đổi (khác "val" là read-only)
//   - "ExoPlayer?" → Nullable: player có thể là null
//   - "private set" → getter public, setter private.
//   Java tương đương:
//     private ExoPlayer player = null;
//     public ExoPlayer getPlayer() { return player; } // public getter
//     // không có public setter
//
// "player = ExoPlayer.Builder(context).build().apply { ... }"
//   - ".apply { ... }" → gọi nhiều hàm trên cùng 1 object ("this" bên trong).
//   Java tương đương:
//     ExoPlayer p = new ExoPlayer.Builder(context).build();
//     p.setMediaItem(mediaItem);
//     p.prepare();
//     p.setPlayWhenReady(true);
//     p.setRepeatMode(Player.REPEAT_MODE_OFF);
//     player = p;
//
// "override fun onCleared()"
//   - Gọi khi ViewModel bị destroy. Giải phóng ExoPlayer để tránh leak bộ nhớ.
//   Java: @Override protected void onCleared() { ... }
//
// "player?.release()" - Safe call operator:
//   - "?." → chỉ gọi .release() nếu player không null
//   Java: if (player != null) { player.release(); }
// ═══════════════════════════════════════════════════════

@HiltViewModel
class PlayerViewModel @Inject constructor(
    // "@ApplicationContext" → inject Application context (an toàn, không leak)
    @ApplicationContext private val context: Context
) : ViewModel() {

    // "var" = có thể thay đổi (khác "val" là read-only)
    // "ExoPlayer?" → kiểu nullable (có thể null). Java: ExoPlayer player = null;
    // "private set" → bên ngoài chỉ đọc, bên trong mới được ghi
    var player: ExoPlayer? = null
        private set

    // Lưu URL đang phát để tránh khởi tạo lại nếu URL không thay đổi
    private var currentUrl: String? = null

    fun preparePlayer(streamUrl: String) {
        // "&&" = and trong Java
        // "player != null" → Kotlin: player != null (giống Java)
        if (currentUrl == streamUrl && player != null) return
        releasePlayer() // Giải phóng player cũ trước khi tạo mới
        currentUrl = streamUrl

        // ".apply { ... }" → gọi nhiều hàm trên player vừa tạo
        // "this" bên trong apply là ExoPlayer
        // Java: ExoPlayer p = ...; p.setMediaItem(...); p.prepare(); ...; player = p;
        player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)  // = player.setMediaItem(mediaItem);
            prepare()               // = player.prepare();
            playWhenReady = true    // = player.setPlayWhenReady(true);
            repeatMode = Player.REPEAT_MODE_OFF  // = player.setRepeatMode(...);
        }
    }

    // "player?.pause()" → nếu player không null thì gọi .pause()
    // Java: if (player != null) player.pause();
    fun pausePlayer() {
        player?.pause()
    }

    fun resumePlayer() {
        player?.play()
    }

    fun releasePlayer() {
        player?.release() // Giải phóng tài nguyên (bộ nhớ, decoder, ...)
        player = null
        currentUrl = null
    }

    // "override fun onCleared()" → @Override protected void onCleared() trong Java
    // Gọi khi ViewModel bị destroy (user back khỏi màn hình player)
    override fun onCleared() {
        super.onCleared()
        releasePlayer() // Bắt buộc phải giải phóng ExoPlayer để tránh memory leak
    }
}
