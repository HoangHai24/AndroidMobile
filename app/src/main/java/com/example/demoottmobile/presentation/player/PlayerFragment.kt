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

private const val DEFAULT_STREAM_URL =
    "https://cdn-demo-sigma-livestreaming.sigma.video/data/vod/sigma-vod/" +
    "168b85fe-3184-41e6-a85b-f491c302a92e/hls-BM/master.m3u8"

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModels()
    private val args: PlayerFragmentArgs by navArgs()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_BUFFERING -> {
                    binding.playerProgress.visibility = View.VISIBLE
                }
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
        binding.tvPlayerTitle.visibility = if (title.isNotEmpty()) View.VISIBLE else View.GONE
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun initPlayer() {
        val streamUrl = args.streamUrl.ifEmpty { DEFAULT_STREAM_URL }
        viewModel.preparePlayer(streamUrl)
        binding.playerView.player = viewModel.player
        viewModel.player?.addListener(playerListener)
    }

    override fun onStart() {
        super.onStart()
        viewModel.resumePlayer()
    }

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        binding.playerView.player = null
        viewModel.player?.removeListener(playerListener)
        _binding = null
        super.onDestroyView()
    }
}
