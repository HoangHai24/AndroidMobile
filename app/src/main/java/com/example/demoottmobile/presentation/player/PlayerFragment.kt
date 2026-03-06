package com.example.demoottmobile.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.Player
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demoottmobile.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

// Hằng số cấp file dùng khi không truyền stream URL
// Java: private static final String DEFAULT_STREAM_URL = "...";
private const val DEFAULT_STREAM_URL =
    "https://cdn-demo-sigma-livestreaming.sigma.video/data/vod/sigma-vod/" +
    "168b85fe-3184-41e6-a85b-f491c302a92e/hls-BM/master.m3u8"

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "private val playerListener = object : Player.Listener { ... }"
//   - Tạo anonymous class implement interface Player.Listener.
//   - Java tương đương:
//       private final Player.Listener playerListener = new Player.Listener() {
//           @Override
//           public void onPlaybackStateChanged(int state) { ... }
//       };
//
// "when (state) { Player.STATE_BUFFERING -> ... }"
//   - "when" = switch-case trong Java nhưng mạnh hơn nhiều.
//   - Java: switch(state) { case Player.STATE_BUFFERING: ...; break; }
//
// "args.streamUrl.ifEmpty { DEFAULT_STREAM_URL }"
//   - Nếu URL truyền vào rỗng thì dùng URL mặc định.
//   - Java: args.getStreamUrl().isEmpty() ? DEFAULT_STREAM_URL : args.getStreamUrl()
//
// "args.itemTitle.isNotEmpty()"
//   - Kiểm tra string không rỗng. Java: !args.getItemTitle().isEmpty()
//
// Override callbacks vòng đời Fragment:
//   onStart  → resume player (Fragment hiển thị)
//   onStop   → pause player (Fragment ẩn, vd navigate sang màn hình khác)
//   onDestroyView → giải phóng player view (giảm mạnh tay tài nguyên)
// ═══════════════════════════════════════════════════════

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModels()
    private val args: PlayerFragmentArgs by navArgs()

    // "object : Player.Listener { ... }" → anonymous class trong Java
    // Java: new Player.Listener() { @Override public void onPlaybackStateChanged(int state) { ... } }
    private val playerListener = object : Player.Listener {
        // Override hàm từ interface Player.Listener
        // Java: @Override public void onPlaybackStateChanged(int state) { ... }
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                // Khi đang buffer: hiển thị progress bar
                Player.STATE_BUFFERING -> {
                    binding.playerProgress.visibility = View.VISIBLE
                }
                // Khi sẵn sàng/kết thúc/idle: ẩn progress bar
                Player.STATE_READY, Player.STATE_ENDED, Player.STATE_IDLE -> {
                    binding.playerProgress.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        initPlayer()
    }

    private fun setupUI() {
        val title = args.itemTitle
        binding.tvPlayerTitle.text = title
        // "if ... View.VISIBLE else View.GONE" → if-else trong java nhưng viết 1 dòng
        // Java: binding.tvPlayerTitle.setVisibility(title.isNotEmpty() ? View.VISIBLE : View.GONE);
        binding.tvPlayerTitle.visibility = if (title.isNotEmpty()) View.VISIBLE else View.GONE
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun initPlayer() {
        // "args.streamUrl.ifEmpty { DEFAULT_STREAM_URL }" → nếu rỗng dùng URL mặc định
        // Java: String url = args.getStreamUrl().isEmpty() ? DEFAULT_STREAM_URL : args.getStreamUrl();
        val streamUrl = args.streamUrl.ifEmpty { DEFAULT_STREAM_URL }
        viewModel.preparePlayer(streamUrl)
        // Gắn ExoPlayer vào PlayerView để hiển thị
        binding.playerView.player = viewModel.player
        // "?.addListener(...)" → chỉ gọi nếu player không null
        viewModel.player?.addListener(playerListener)
    }

    // onStart = Fragment hiển thị lại sau khi bị ẩn
    // Java: @Override public void onStart() { super.onStart(); viewModel.resumePlayer(); }
    override fun onStart() {
        super.onStart()
        viewModel.resumePlayer()
    }

    // onStop = Fragment bị ẩn (navigate sang màn khác)
    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer()
    }

    // Giải phóng player khỏi PlayerView và tách listener trước khi view bị destroy
    override fun onDestroyView() {
        binding.playerView.player = null         // Gỡ player khỏi View
        viewModel.player?.removeListener(playerListener) // Tách listener
        _binding = null
        super.onDestroyView()
    }
}
