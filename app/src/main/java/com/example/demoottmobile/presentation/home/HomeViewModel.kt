package com.example.demoottmobile.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoottmobile.domain.model.MediaCategory
import com.example.demoottmobile.domain.usecase.GetCategoriesUseCase
import com.example.demoottmobile.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "@HiltViewModel" → Cho Hilt biết class này là ViewModel cần inject.
// "class HomeViewModel @Inject constructor(...) : ViewModel()"
//   - ": ViewModel()" = extends ViewModel trong Java
//   - "@Inject constructor" = Hilt tự tạo và inject các dependency
//
// "MutableStateFlow<UiState<List<MediaCategory>>>"
//   - StateFlow = luồng data có thể emit nhiều giá trị theo thời gian.
//   - Giống LiveData<T> trong Java/Android nhưng mạnh hơn, phù hợp Kotlin.
//   - "Mutable" = có thể thay đổi giá trị từ trong ViewModel.
//   Java tương đương: MutableLiveData<UiState<List<MediaCategory>>>
//
// "val categoriesState: StateFlow<...> = _categoriesState.asStateFlow()"
//   - Public read-only view: Fragment chỉ có thể đọc, không được ghi.
//   - Java tương đương: LiveData<T> getState() { return _state; }
//
// "init { loadCategories() }"
//   - Khối code chạy ngay khi object được tạo (như constructor của Java).
//   Java tương đương:
//     public HomeViewModel(GetCategoriesUseCase useCase) {
//         this.getCategoriesUseCase = useCase;
//         loadCategories(); // gọi trong constructor
//     }
//
// "viewModelScope.launch { ... }"
//   - Tạo một coroutine (tác vụ bất đồng bộ) gắn với vòng đời của ViewModel.
//   - Tự động hủy khi ViewModel bị destroy → không leak.
//   Java tương đương (dùng ExecutorService thủ công):
//     executor.execute(() -> { /* background work */ });
//
// "_categoriesState.value = UiState.Loading"
//   - Cập nhật giá trị của StateFlow → Fragment sẽ nhận được tự động.
//   Java: _categoriesState.setValue(new UiState.Loading());
//
// Java tương đương tổng thể:
//
//   @HiltViewModel
//   public class HomeViewModel extends ViewModel {
//       private final MutableLiveData<UiState<List<MediaCategory>>> _categoriesState;
//       private final GetCategoriesUseCase getCategoriesUseCase;
//
//       @Inject
//       public HomeViewModel(GetCategoriesUseCase getCategoriesUseCase) {
//           this.getCategoriesUseCase = getCategoriesUseCase;
//           _categoriesState = new MutableLiveData<>(new UiState.Loading());
//           loadCategories();
//       }
//
//       public LiveData<UiState<List<MediaCategory>>> getCategoriesState() {
//           return _categoriesState;
//       }
//
//       public void loadCategories() {
//           _categoriesState.setValue(new UiState.Loading());
//           executor.execute(() -> {
//               try {
//                   List<MediaCategory> cats = getCategoriesUseCase.execute();
//                   _categoriesState.postValue(new UiState.Success<>(cats));
//               } catch (Exception e) {
//                   _categoriesState.postValue(new UiState.Error(e.getMessage()));
//               }
//           });
//       }
//   }
// ═══════════════════════════════════════════════════════

// "@HiltViewModel" → cho Hilt biết inject vào ViewModel này
@HiltViewModel
// ": ViewModel()" = extends ViewModel (Java)
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // "private val _categoriesState" → chỉ ViewModel được ghi
    // "MutableStateFlow" → MutableLiveData trong Java
    // "(UiState.Loading)" → giá trị khởi tạo ban đầu là Loading
    private val _categoriesState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)

    // "val" (không có Mutable) → Fragment chỉ đọc được, không ghi được
    // ".asStateFlow()" → chuyển Mutable → read-only
    // Java: public LiveData<UiState<...>> getCategoriesState() { return _categoriesState; }
    val categoriesState: StateFlow<UiState<List<MediaCategory>>> = _categoriesState.asStateFlow()

    // "init" → Khối code chạy ngay khi ViewModel được tạo
    // Java: viết trong constructor
    init {
        loadCategories()
    }

    fun loadCategories() {
        // "viewModelScope.launch" → Tạo coroutine, tự hủy khi ViewModel destroy
        // Java: ExecutorService hoặc Thread thủ công
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            try {
                // gọi use case như gọi hàm bình thường ("operator invoke")
                val categories = getCategoriesUseCase()
                _categoriesState.value = UiState.Success(categories)
            } catch (e: Exception) {
                // "e.message" = e.getMessage() trong Java
                // "?: "Unknown error"" → nếu null thì dùng "Unknown error" (Elvis operator)
                _categoriesState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
