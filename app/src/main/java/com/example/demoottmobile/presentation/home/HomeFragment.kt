package com.example.demoottmobile.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoottmobile.databinding.FragmentHomeBinding
import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.model.MediaItem
import com.example.demoottmobile.presentation.common.UiState
import com.example.demoottmobile.presentation.common.loading.GlobalLoading
import com.example.demoottmobile.presentation.common.toast.GlobalToast
import com.example.demoottmobile.presentation.home.adapter.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val mainCategoryAdapter by lazy {
        CategoryAdapter(
            onTitleClick = ::navigateToListing,
            onItemClick = ::navigateToPlayer
        )
    }

    private val drawerCategoryAdapter by lazy {
        CategoryAdapter(
            onTitleClick = { category ->
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navigateToListing(category)
            },
            onItemClick = { item ->
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navigateToPlayer(item)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerViews() {
        binding.rvCategories.apply {
            adapter = mainCategoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
        binding.rvDrawerCategories.apply {
            adapter = drawerCategoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            activity?.let { GlobalLoading.show(it) }
                        }
                        is UiState.Success -> {
                            activity?.let { GlobalLoading.hide(it) }
                            mainCategoryAdapter.submitList(state.data)
                            drawerCategoryAdapter.submitList(state.data)
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

    private fun navigateToListing(category: MediaCategory) {
        val action = HomeFragmentDirections.actionHomeFragmentToListingGridFragment(
            categoryId = category.id,
            categoryTitle = category.title
        )
        findNavController().navigate(action)
    }

    private fun navigateToPlayer(item: MediaItem) {
        val action = HomeFragmentDirections.actionHomeFragmentToPlayerFragment(
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
