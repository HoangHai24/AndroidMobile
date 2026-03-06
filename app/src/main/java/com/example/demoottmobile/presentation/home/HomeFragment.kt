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

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "private var _binding: FragmentHomeBinding? = null"
//   - "var" = biến có thể ghi, nullable (có dấu "?")
//   - Java: private FragmentHomeBinding _binding = null;
//
// "private val binding get() = _binding!!"
//   - "get()" → custom getter: mọi lần đọc "binding" là gọi getter này
//   - "!!" → khẳng định không null (nếu null sẽ throw NullPointerException)
//   - Java: không có khái niệm này, phải tự kiểm tra null
//
// "private val viewModel: HomeViewModel by viewModels()"
//   - Kotlin delegate: "by viewModels()" → Hilt tự tạo và inject ViewModel
//   - Java: viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
//
// "private val mainCategoryAdapter by lazy { CategoryAdapter(...) }"
//   - "by lazy" → chỉ khởi tạo lần đầu tiên khi được truy cập.
//   - Java không có, phải khởi tạo thủ công trong onCreate() hoặc tự viết lazy init.
//
// "onTitleClick = ::navigateToListing"
//   - "::" → truyền tham chiếu tới hàm mà không gọi ngay.
//   - Java tương đương: this::navigateToListing hoặc lambda
//
// "viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(...) { ... } }"
//   - Lắng nghe StateFlow một cách an toàn với vòng đời Fragment.
//   - Tự dừng khi Fragment vào background, tiếp tục khi quay lại foreground.
//   - Java: viewModel.getCategoriesState().observe(getViewLifecycleOwner(), state -> { ... });
//
// Java tương đương tổng thể:
//
//   @AndroidEntryPoint
//   public class HomeFragment extends Fragment {
//       private FragmentHomeBinding binding;
//       private HomeViewModel viewModel;
//       private CategoryAdapter mainCategoryAdapter;
//       private CategoryAdapter drawerCategoryAdapter;
//
//       @Override
//       public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                Bundle savedInstanceState) {
//           binding = FragmentHomeBinding.inflate(inflater, container, false);
//           return binding.getRoot();
//       }
//
//       @Override
//       public void onViewCreated(View view, Bundle savedInstanceState) {
//           viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
//           setupRecyclerViews();
//           setupListeners();
//           observeState();
//       }
//
//       @Override
//       public void onDestroyView() {
//           super.onDestroyView();
//           binding = null; // chứng ngừa memory leak
//       }
//   }
// ═══════════════════════════════════════════════════════

@AndroidEntryPoint
class HomeFragment : Fragment() {

    // "?" = nullable, có thể là null. Java: private FragmentHomeBinding _binding = null;
    private var _binding: FragmentHomeBinding? = null
    // "!!" = khẳng định không null (safe vì chỉ truy cập trong onViewCreated-onDestroyView)
    private val binding get() = _binding!!

    // "by viewModels()" = Kotlin delegate, Hilt tự tạo ViewModel
    // Java: viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    private val viewModel: HomeViewModel by viewModels()

    // "by lazy" = khởi tạo lười biếng - chỉ tạo khi lần đầu dùng
    // Java: phải tự khởi tạo trong onViewCreated()
    private val mainCategoryAdapter by lazy {
        CategoryAdapter(
            // "::navigateToListing" = truyền tham chiếu hàm
            // Java: category -> navigateToListing(category)
            onTitleClick = ::navigateToListing,
            onItemClick = ::navigateToPlayer
        )
    }

    private val drawerCategoryAdapter by lazy {
        CategoryAdapter(
            onTitleClick = { category ->
                // Lambda body: đóng drawer rồi navigate
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navigateToListing(category)
            },
            onItemClick = { item ->
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                navigateToPlayer(item)
            }
        )
    }

    // "override fun onCreateView": @Override public View onCreateView(...) trong Java
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate layout + bind views
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root // binding.root = binding.getRoot() trong Java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerViews() {
        // ".apply { ... }" → gọi nhiều hàm trên cùng 1 object
        // Java: binding.rvCategories.setAdapter(...); binding.rvCategories.setLayoutManager(...);
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
        // ".setOnClickListener { ... }" = .setOnClickListener(v -> { ... }) trong Java
        binding.btnMenu.setOnClickListener {
            // "isDrawerOpen(...)" kiểm tra drawer đang mở hay đếng
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun observeState() {
        // "viewLifecycleOwner.lifecycleScope.launch { ... }" → tạo coroutine an toàn với lifecycle
        // Java: viewModel.getCategoriesState().observe(getViewLifecycleOwner(), state -> { ... });
        viewLifecycleOwner.lifecycleScope.launch {
            // "repeatOnLifecycle(STARTED)" → tự dừng khi Fragment background, tiếp khi foreground
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { state ->
                    // "when" = switch trong Java nhưng mạnh hơn
                    when (state) {
                        is UiState.Loading -> {
                            // "?.let { ... }" → chỉ chạy nếu activity không null
                            // Java: if (getActivity() != null) { GlobalLoading.show(getActivity()); }
                            activity?.let { GlobalLoading.show(it) }
                        }
                        is UiState.Success -> {
                            activity?.let { GlobalLoading.hide(it) }
                            // "state.data" → lấy data từ UiState.Success
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

    // Hàm navigate nhận MediaCategory, tạo action (Safe Args) và gọi navigate
    // Java: private void navigateToListing(MediaCategory category) { ... }
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

    // Bắt buộc null binding để tránh memory leak
    // Java: binding = null;
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
