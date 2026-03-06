package com.example.demoottmobile.presentation.listing

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.demoottmobile.R
import com.example.demoottmobile.databinding.FragmentListingGridBinding
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.presentation.channel.GridSpacingItemDecoration
import com.example.demoottmobile.presentation.common.UiState
import com.example.demoottmobile.presentation.common.loading.GlobalLoading
import com.example.demoottmobile.presentation.common.toast.GlobalToast
import com.example.demoottmobile.presentation.listing.adapter.ListingGridAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "private val args: ListingGridFragmentArgs by navArgs()"
//   - Safe Args: tự sinh class "ListingGridFragmentArgs" từ nav_graph.xml
//   - "by navArgs()" = delegate lấy args từ Navigation Component.
//   - Java: ListingGridFragmentArgs args = ListingGridFragmentArgs.fromBundle(getArguments());
//
// "args.categoryId" → lấy giá trị categoryId được truyền từ Fragment trước.
// "args.categoryTitle" → lấy title category.
//
// "args.categoryTitle.ifEmpty { getString(...) }"
//   - Nếu categoryTitle rỗng thì dùng string mặc định từ resources.
//   Java: categoryTitle.isEmpty() ? getString(...) : categoryTitle
// ═══════════════════════════════════════════════════════

@AndroidEntryPoint
class ListingGridFragment : Fragment() {

    private var _binding: FragmentListingGridBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListingGridViewModel by viewModels()

    // "by navArgs()" = lấy Safe Args được truyền từ HomeFragment/ChannelFragment
    // Java: ListingGridFragmentArgs.fromBundle(getArguments())
    private val args: ListingGridFragmentArgs by navArgs()

    private val listingAdapter by lazy {
        ListingGridAdapter { item -> navigateToPlayer(item) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeState()
        // Truyền categoryId từ args vào ViewModel để load data
        viewModel.loadItems(args.categoryId)
    }

    private fun setupToolbar() {
        // "args.categoryTitle.ifEmpty { ... }" → nếu rỗng thì dùng string mặc định
        // Java: args.getCategoryTitle().isEmpty() ? getString(R.string.....) : args.getCategoryTitle()
        binding.tvTitle.text = args.categoryTitle.ifEmpty { getString(R.string.listing_grid_title) }
        // "{ findNavController().navigateUp() }" = lambda không tham số
        // Java: v -> findNavController().navigateUp()
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        val spanCount = 2  // Lưới 2 cột
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        binding.rvListing.apply {
            adapter = listingAdapter
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
            setHasFixedSize(false)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listingState.collect { state ->
                    when (state) {
                        is UiState.Loading -> activity?.let { GlobalLoading.show(it) }
                        is UiState.Success -> {
                            activity?.let { GlobalLoading.hide(it) }
                            listingAdapter.submitList(state.data)
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
        val action = ListingGridFragmentDirections.actionListingGridFragmentToPlayerFragment(
            streamUrl = item.streamUrl,
            itemTitle = item.title
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
