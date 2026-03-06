package com.example.demoottmobile.presentation.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.demoottmobile.databinding.FragmentChannelBinding
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.presentation.channel.adapter.ChannelGridAdapter
import com.example.demoottmobile.presentation.common.UiState
import com.example.demoottmobile.presentation.common.loading.GlobalLoading
import com.example.demoottmobile.presentation.common.toast.GlobalToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG (giống HomeFragment - xem HomeFragment.kt)
// Điểm khác: dùng GridLayoutManager (lưới 4 cột) thay vì LinearLayoutManager
// và thêm GridSpacingItemDecoration để tạo khoảng cách đều giữa các item.
// ═══════════════════════════════════════════════════════

@AndroidEntryPoint
class ChannelFragment : Fragment() {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChannelViewModel by viewModels()

    // Lambda ngắn gọn: { item -> navigateToPlayer(item) }
    // Java: item -> navigateToPlayer(item)
    private val channelAdapter by lazy {
        ChannelGridAdapter { item -> navigateToPlayer(item) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
    }

    private fun setupRecyclerView() {
        // 4 columns, spacing 8dp
        val spanCount = 4
        // "resources.getDimensionPixelSize(...)" → lấy giá trị dp đổi ra pixel
        val spacing = resources.getDimensionPixelSize(com.example.demoottmobile.R.dimen.grid_spacing)
        binding.rvChannels.apply {
            adapter = channelAdapter
            // GridLayoutManager: hiển thị theo lưới, spanCount = số cột
            // Java: new GridLayoutManager(requireContext(), spanCount)
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            // ItemDecoration: thêm khoảng cách giữa các item trong lưới
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
            setHasFixedSize(true)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> activity?.let { GlobalLoading.show(it) }
                        is UiState.Success -> {
                            activity?.let { GlobalLoading.hide(it) }
                            channelAdapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            activity?.let { GlobalLoading.hide(it) }
                            GlobalToast.showError(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToPlayer(item: MediaItem) {
        val action = ChannelFragmentDirections.actionChannelFragmentToPlayerFragment(
            streamUrl = item.streamUrl,
            itemTitle = item.title
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // null binding tránh memory leak
    }
}
