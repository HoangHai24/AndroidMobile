# Các Mô Hình Kiến Trúc Android: MVC, MVP, MVI, MVVM

> Tài liệu này so sánh toàn diện các mô hình kiến trúc phổ biến trong Android, với flow minh hoạ, code ví dụ thực tế, ưu/nhược điểm và lý do tại sao MVVM được lựa chọn cho project này.

---

## Mục lục

1. [Tổng quan các mô hình](#1-tổng-quan-các-mô-hình)
2. [MVC - Model View Controller](#2-mvc---model-view-controller)
3. [MVP - Model View Presenter](#3-mvp---model-view-presenter)
4. [MVI - Model View Intent](#4-mvi---model-view-intent)
5. [MVVM - Model View ViewModel](#5-mvvm---model-view-viewmodel)
6. [So sánh tổng hợp](#6-so-sánh-tổng-hợp)
7. [Tại sao chọn MVVM?](#7-tại-sao-chọn-mvvm)
8. [MVVM vs MVI — Phân tích chuyên sâu](#8-mvvm-vs-mvi--phân-tích-chuyên-sâu)

---

## 1. Tổng quan các mô hình

Tất cả các mô hình kiến trúc đều giải quyết cùng một vấn đề: **tách biệt UI khỏi logic**. Sự khác biệt nằm ở *ai chịu trách nhiệm gì* và *dữ liệu chảy theo hướng nào*.

```
                    Vấn đề cốt lõi cần giải quyết
                    ───────────────────────────────
                    "Khi người dùng bấm nút, ai xử lý?
                     Dữ liệu trả về thể hiện lên UI thế nào?
                     Ai giữ trạng thái (state) của màn hình?"

            ┌──────────┬──────────┬──────────┬──────────┐
            │   MVC    │   MVP    │   MVI    │   MVVM   │
            │ (1970s)  │ (1990s)  │ (2015+)  │ (2012+)  │
            └──────────┴──────────┴──────────┴──────────┘
                 │           │          │           │
               God         Two       Uni-       Observer
             Activity    -way      directional   Pattern
                        Contract    Flow
```

---

## 2. MVC - Model View Controller

### Khái niệm

**MVC** = **M**odel - **V**iew - **C**ontroller

| Thành phần | Trách nhiệm |
|-----------|------------|
| **Model** | Dữ liệu và business logic |
| **View** | Hiển thị UI (XML layout) |
| **Controller** | Nhận input, điều phối Model và View |

Trong Android, **Activity/Fragment đóng vai trò Controller** — đây là nguồn gốc của mọi vấn đề.

### Flow dữ liệu

```
Người dùng
    │
    │ tương tác (click, scroll...)
    ▼
┌─────────────────────────────┐
│         CONTROLLER          │
│     (Activity/Fragment)     │◄──────────────────┐
│                             │                   │
│  - Nhận user input          │                   │
│  - Gọi Model                │                   │
│  - Cập nhật View trực tiếp  │                   │
└──────────┬──────────────────┘                   │
           │                                      │
    gọi    │                              notify  │
           ▼                                      │
┌─────────────────────┐              ┌────────────────────┐
│        MODEL        │─────────────►│        VIEW        │
│                     │   update     │                    │
│  - Data class       │              │  - XML Layout      │
│  - API call         │              │  - RecyclerView    │
│  - Database query   │              │  - TextView...     │
└─────────────────────┘              └────────────────────┘

⚠️  VẤN ĐỀ: Controller biết cả Model lẫn View
             → Controller phình to vô kiểm soát
```

### Code ví dụ

```kotlin
// ❌ MVC trong Android - Activity là Controller
class HomeActivity : AppCompatActivity() {

    // View components - Activity giữ reference trực tiếp
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CategoryAdapter

    // Model - tạo trực tiếp trong Controller
    private val repository = MediaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Controller tự kéo View references
        recyclerView = findViewById(R.id.rv_categories)
        progressBar = findViewById(R.id.progress_bar)
        adapter = CategoryAdapter()
        recyclerView.adapter = adapter

        // Controller gọi Model
        loadCategories()

        // Controller xử lý click
        findViewById<Button>(R.id.btn_refresh).setOnClickListener {
            loadCategories()
        }
    }

    // Controller điều phối: gọi Model, rồi tự cập nhật View
    private fun loadCategories() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Gọi Model
                val categories = repository.getCategories()

                // Cập nhật View trực tiếp từ Controller
                progressBar.visibility = View.GONE
                adapter.submitList(categories)

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Khi xoay màn hình → Activity bị destroy → loadCategories() bị gọi lại
    // → Gọi API lại từ đầu → UX tệ
}
```

### Ưu điểm

- ✅ Đơn giản, dễ hiểu với người mới
- ✅ Ít boilerplate code
- ✅ Phù hợp cho app nhỏ, prototype nhanh

### Nhược điểm

- ❌ **God Activity**: Activity vừa là Controller vừa quản lý View → file hàng nghìn dòng
- ❌ **Không thể test**: Logic gắn với Android framework, không unit test được
- ❌ **Xoay màn hình mất data**: Không có cơ chế giữ state khi configuration change
- ❌ **View và Controller liên kết chặt**: Khó tái sử dụng code
- ❌ **Khó làm nhóm**: 2 người khó cùng sửa 1 Activity

---

## 3. MVP - Model View Presenter

### Khái niệm

**MVP** = **M**odel - **V**iew - **P**resenter

MVP xuất hiện để giải quyết vấn đề của MVC: tách Controller ra khỏi Activity bằng cách thêm lớp **Presenter**.

| Thành phần | Trách nhiệm |
|-----------|------------|
| **Model** | Dữ liệu, business logic, data access |
| **View** | Chỉ hiển thị UI, định nghĩa Contract interface |
| **Presenter** | Nhận sự kiện từ View, điều phối Model, gọi lại View qua interface |

**Điểm khác biệt then chốt:** Presenter và View giao tiếp qua **Contract interface** → có thể test bằng mock.

### Flow dữ liệu

```
Người dùng
    │
    │ tương tác
    ▼
┌────────────────────────────┐
│           VIEW             │
│   (Activity/Fragment)      │
│                            │
│  Implements ViewContract   │
│  - showLoading()           │
│  - showCategories(list)    │◄──────────────────────┐
│  - showError(msg)          │                       │
└──────────────┬─────────────┘                       │
               │                                     │
  gọi qua      │ presenterRef.loadCategories()       │ gọi qua
  interface    │                                     │ interface
               ▼                                     │
┌────────────────────────────┐                       │
│         PRESENTER          │                       │
│                            │                       │
│  Giữ reference tới View    │                       │
│  - loadCategories()        │          ViewContract │
│  - onItemClicked(item)     │──────────────────────►│
│  - onRefresh()             │   viewRef?.showData() │
└──────────────┬─────────────┘                       │
               │                                     │
               │ gọi                                 │
               ▼                                     │
┌────────────────────────────┐                       │
│           MODEL            │                       │
│                            │                       │
│  MediaRepository           │                       │
│  - getCategories()         │                       │
│  - getChannels()           │                       │
└────────────────────────────┘                       │
                                                     │
⚠️  VẤN ĐỀ: Presenter giữ reference tới View        │
             → Memory leak nếu View bị destroy       │
             → Phải manually null view reference     │
```

### Code ví dụ

```kotlin
// BƯỚC 1: Định nghĩa Contract (giao tiếp 2 chiều giữa View và Presenter)
interface HomeContract {

    // Những gì View phải làm (Presenter gọi vào đây)
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showCategories(categories: List<MediaCategory>)
        fun showError(message: String)
    }

    // Những gì Presenter phải làm (View gọi vào đây)
    interface Presenter {
        fun loadCategories()
        fun onItemClicked(item: MediaItem)
        fun onDestroy()  // Để null view reference, tránh leak
    }
}
```

```kotlin
// BƯỚC 2: Presenter - hoàn toàn tách khỏi Android framework
class HomePresenter(
    private var view: HomeContract.View?,  // Giữ reference tới View qua interface
    private val repository: MediaRepository
) : HomeContract.Presenter {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun loadCategories() {
        view?.showLoading()

        coroutineScope.launch {
            try {
                val categories = withContext(Dispatchers.IO) {
                    repository.getCategories()
                }
                view?.hideLoading()
                view?.showCategories(categories)  // Gọi lại View qua interface

            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError(e.message ?: "Lỗi không xác định")
            }
        }
    }

    override fun onItemClicked(item: MediaItem) {
        // Logic xử lý click - hoàn toàn có thể Unit Test
        if (item.streamUrl.isNotEmpty()) {
            view?.navigateToPlayer(item)  // Nếu View có hàm này
        }
    }

    override fun onDestroy() {
        // Phải gọi khi Fragment/Activity bị destroy để tránh memory leak
        view = null
        coroutineScope.cancel()
    }
}
```

```kotlin
// BƯỚC 3: Fragment implement View interface
class HomeFragment : Fragment(), HomeContract.View {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Presenter được inject hoặc tạo thủ công
    private lateinit var presenter: HomeContract.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tạo Presenter, truyền chính Fragment này vào làm View
        presenter = HomePresenter(
            view = this,  // Fragment tự đóng vai View
            repository = MediaRepositoryImpl(MockDataSource())
        )

        binding.btnRefresh.setOnClickListener {
            presenter.loadCategories()
        }

        presenter.loadCategories()
    }

    // Implement các hàm từ HomeContract.View
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showCategories(categories: List<MediaCategory>) {
        adapter.submitList(categories)
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()  // ⚠️ Phải gọi thủ công - dễ quên!
        _binding = null
    }
}
```

```kotlin
// BƯỚC 4: Unit Test Presenter dễ dàng với mock
class HomePresenterTest {

    // Mock View để kiểm tra Presenter có gọi đúng hàm không
    private val mockView = mock<HomeContract.View>()
    private val mockRepository = mock<MediaRepository>()
    private lateinit var presenter: HomePresenter

    @Before
    fun setup() {
        presenter = HomePresenter(mockView, mockRepository)
    }

    @Test
    fun `loadCategories success - should show categories`() = runTest {
        val fakeData = listOf(MediaCategory("1", "Trending", emptyList()))
        whenever(mockRepository.getCategories()).thenReturn(fakeData)

        presenter.loadCategories()

        // Kiểm tra Presenter có gọi đúng hàm trên View không
        verify(mockView).showLoading()
        verify(mockView).hideLoading()
        verify(mockView).showCategories(fakeData)
    }

    @Test
    fun `loadCategories error - should show error`() = runTest {
        whenever(mockRepository.getCategories()).thenThrow(RuntimeException("Network error"))

        presenter.loadCategories()

        verify(mockView).showError("Network error")
    }
}
```

### Ưu điểm

- ✅ **Tách biệt rõ ràng**: Presenter không biết View là gì, chỉ biết interface
- ✅ **Dễ Unit Test**: Mock View interface, test Presenter độc lập
- ✅ **Tốt hơn MVC nhiều**: Activity/Fragment gọn hơn, chỉ implement interface

### Nhược điểm

- ❌ **Memory leak tiềm ẩn**: Presenter giữ reference View → phải `onDestroy()` thủ công
- ❌ **Xoay màn hình mất state**: Presenter bị tạo lại cùng Fragment
- ❌ **Boilerplate**: Mỗi màn hình phải tạo Contract interface mới
- ❌ **Two-way binding phức tạp**: View và Presenter gọi nhau qua nhiều interface
- ❌ **Không survive configuration change**: Không có cơ chế built-in như ViewModel

---

## 4. MVI - Model View Intent

### Khái niệm

**MVI** = **M**odel - **V**iew - **I**ntent

MVI là mô hình mới nhất, lấy cảm hứng từ Elm architecture và Redux (web). Đặc trưng: **luồng dữ liệu một chiều** (unidirectional data flow).

| Thành phần | Trách nhiệm |
|-----------|------------|
| **Intent** | Ý định của người dùng (không phải Android Intent!) — vd: `LoadCategories`, `RefreshClicked` |
| **Model** | **Toàn bộ state** của màn hình tại một thời điểm (immutable) |
| **View** | Nhận state, render UI, emit Intent |

**Điểm khác biệt then chốt:**
- Chỉ **1 State object** duy nhất đại diện toàn bộ màn hình
- State là **immutable** — không bao giờ sửa trực tiếp, chỉ tạo bản mới
- Luồng **một chiều**: Intent → ViewModel/Reducer → State → View → Intent → ...

### Flow dữ liệu

```
                    ┌─────────────────────────────────────┐
                    │                                     │
                    │        LUỒNG MỘT CHIỀU              │
                    │                                     │
    ┌───────────────▼──────────────┐                     │
    │             VIEW             │                     │
    │                              │                     │
    │  Render dựa trên State       │                     │
    │  - isLoading: Boolean        │                     │
    │  - categories: List<...>     │                     │
    │  - error: String?            │                     │
    └──────────────────────────────┘                     │
              │                                          │
              │ emit Intent (ý định người dùng)          │
              │                                          │
              ▼                                          │
    ┌──────────────────────────────┐                     │
    │           INTENT             │                     │
    │  (sealed class)              │                     │
    │                              │                     │
    │  - LoadCategoriesIntent      │                     │
    │  - RefreshIntent             │                     │
    │  - ItemClickedIntent(item)   │                     │
    └──────────────────────────────┘                     │
              │                                          │
              │ process                                  │
              ▼                                          │
    ┌──────────────────────────────┐                     │
    │      VIEWMODEL/REDUCER       │                     │
    │                              │                     │
    │  processIntent(intent) {     │                     │
    │    oldState + action         │                     │
    │    → newState (immutable)    │                     │
    │  }                           │                     │
    └──────────────────────────────┘                     │
              │                                          │
              │ emit new State                           │
              │                                          │
              └─────────────────────────────────────────►│
                                  (State)                │
                    ─────────────────────────────────────┘

✅ Luồng hoàn toàn có thể predict (đoán được) và trace (truy vết)
```

### Code ví dụ

```kotlin
// BƯỚC 1: Định nghĩa Intent (ý định người dùng)
sealed class HomeIntent {
    object LoadCategories : HomeIntent()
    object RefreshCategories : HomeIntent()
    data class ItemClicked(val item: MediaItem) : HomeIntent()
    data class CategoryTitleClicked(val category: MediaCategory) : HomeIntent()
}
```

```kotlin
// BƯỚC 2: Định nghĩa State (toàn bộ trạng thái màn hình - immutable)
data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<MediaCategory> = emptyList(),
    val error: String? = null,
    val navigateTo: NavigationEvent? = null  // one-time event
)

// One-time navigation event (consume 1 lần rồi null)
sealed class NavigationEvent {
    data class ToPlayer(val item: MediaItem) : NavigationEvent()
    data class ToListing(val category: MediaCategory) : NavigationEvent()
}
```

```kotlin
// BƯỚC 3: ViewModel xử lý Intent và cập nhật State
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // Chỉ 1 StateFlow duy nhất cho toàn màn hình
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    // Channel để nhận Intent từ View
    private val intentChannel = Channel<HomeIntent>(Channel.UNLIMITED)

    init {
        // Xử lý tất cả Intent trong 1 vòng lặp
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                processIntent(intent)
            }
        }
        // Tự động load lần đầu
        sendIntent(HomeIntent.LoadCategories)
    }

    // View gọi hàm này để emit Intent
    fun sendIntent(intent: HomeIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    // Reducer: Intent + State cũ → State mới (không mutate trực tiếp!)
    private suspend fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadCategories,
            is HomeIntent.RefreshCategories -> loadCategories()

            is HomeIntent.ItemClicked -> {
                // Tạo state mới với navigation event
                _state.update { currentState ->
                    currentState.copy(
                        navigateTo = NavigationEvent.ToPlayer(intent.item)
                    )
                }
            }

            is HomeIntent.CategoryTitleClicked -> {
                _state.update { currentState ->
                    currentState.copy(
                        navigateTo = NavigationEvent.ToListing(intent.category)
                    )
                }
            }
        }
    }

    private suspend fun loadCategories() {
        // Tạo state mới (không sửa state cũ trực tiếp)
        _state.update { it.copy(isLoading = true, error = null) }

        try {
            val categories = getCategoriesUseCase()
            _state.update { it.copy(isLoading = false, categories = categories) }
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    // Sau khi navigate, consume event
    fun onNavigationHandled() {
        _state.update { it.copy(navigateTo = null) }
    }
}
```

```kotlin
// BƯỚC 4: Fragment chỉ render State và emit Intent
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Emit Intent khi người dùng bấm refresh
        binding.btnRefresh.setOnClickListener {
            viewModel.sendIntent(HomeIntent.RefreshCategories)
        }

        // Adapter emit Intent khi item được click
        val adapter = CategoryAdapter(
            onTitleClick = { category ->
                viewModel.sendIntent(HomeIntent.CategoryTitleClicked(category))
            },
            onItemClick = { item ->
                viewModel.sendIntent(HomeIntent.ItemClicked(item))
            }
        )

        // Observe State - View chỉ render, không có logic
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    // Hàm render thuần túy: cùng state → cùng UI
    private fun renderState(state: HomeUiState) {
        binding.progressBar.isVisible = state.isLoading
        binding.rvCategories.isVisible = !state.isLoading && state.error == null

        if (state.categories.isNotEmpty()) {
            adapter.submitList(state.categories)
        }

        state.error?.let { errorMsg ->
            Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG).show()
        }

        // Xử lý navigation event (one-time)
        state.navigateTo?.let { event ->
            when (event) {
                is NavigationEvent.ToPlayer -> navigateToPlayer(event.item)
                is NavigationEvent.ToListing -> navigateToListing(event.category)
            }
            viewModel.onNavigationHandled()  // Consume event
        }
    }
}
```

### Ưu điểm

- ✅ **Predictable**: Cùng State + cùng Intent → luôn ra cùng kết quả
- ✅ **Dễ debug**: Log toàn bộ Intent và State thay đổi → trace được mọi thứ
- ✅ **Không race condition**: Luồng một chiều, không ai tranh giành ghi State
- ✅ **Dễ test**: Test pure function `processIntent(state, intent) → newState`
- ✅ **Time-travel debugging**: Có thể replay lại chuỗi Intent để tái hiện bug

### Nhược điểm

- ❌ **Boilerplate rất nhiều**: Intent, State, Reducer, Effect... mỗi màn hình cần cả bộ
- ❌ **Over-engineering** cho màn hình đơn giản
- ❌ **Khó học**: Tư duy khác hoàn toàn, cần thời gian làm quen
- ❌ **Performance**: Tạo State object mới mỗi lần update → GC pressure nếu nhiều field
- ❌ **Ecosystem Android chưa native**: Cần thêm thư viện (Orbit, MVI Kotlin...) hoặc tự implement

---

## 5. MVVM - Model View ViewModel

### Khái niệm

**MVVM** = **M**odel - **V**iew - **V**iew**M**odel

MVVM là sự kết hợp của **Observer Pattern** với kiến trúc phân lớp. Google chính thức khuyến nghị từ 2017 và xây dựng toàn bộ Jetpack xung quanh nó.

| Thành phần | Trách nhiệm |
|-----------|------------|
| **Model** | Dữ liệu + business logic (UseCase, Repository, DataSource) |
| **View** | UI render, observe ViewModel, gửi events |
| **ViewModel** | Giữ và xử lý UI state, gọi UseCase, **không biết View** |

**Điểm khác biệt then chốt:**
- ViewModel **không giữ reference** tới View
- View **chủ động quan sát** (observe) ViewModel qua StateFlow/LiveData
- ViewModel **tự động survive** configuration change (xoay màn hình)

### Flow dữ liệu

```
Người dùng
    │
    │ tương tác
    ▼
┌──────────────────────────────────────────────────┐
│                     VIEW                         │
│              (Fragment/Activity)                 │
│                                                  │
│  - Observe StateFlow từ ViewModel                │
│  - Render UI dựa trên State                      │
│  - Gọi hàm ViewModel khi có event               │
└──────────┬───────────────────────────────────────┘
           │                          ▲
  gọi hàm │ viewModel.loadData()     │ observe (StateFlow)
           │                          │
           ▼                          │
┌──────────────────────────────────────────────────┐
│                   VIEWMODEL                      │
│                                                  │
│  private _state: MutableStateFlow (ghi)          │
│  public  state: StateFlow (đọc)                  │◄────────────┐
│                                                  │             │
│  - Không biết Fragment/Activity là gì            │             │
│  - Survive xoay màn hình                         │             │
│  - Tự hủy coroutine khi cleared                  │             │
└──────────┬───────────────────────────────────────┘             │
           │                                                     │
  gọi      │ useCase.invoke()                                    │ emit
           ▼                                                     │
┌──────────────────────────────────────────────────┐             │
│                 DOMAIN LAYER                     │             │
│          (UseCase + Repository Interface)        │─────────────┘
│                                                  │
│  - Business logic thuần Kotlin                   │
│  - Không phụ thuộc Android framework             │
└──────────┬───────────────────────────────────────┘
           │
           │ implement
           ▼
┌──────────────────────────────────────────────────┐
│                  DATA LAYER                      │
│           (RepositoryImpl + DataSource)          │
│                                                  │
│  - Gọi API / Database / Cache                    │
│  - withContext(Dispatchers.IO)                   │
└──────────────────────────────────────────────────┘

✅ ViewModel không bao giờ gọi ngược lại View
✅ View luôn là người chủ động observe
```

### Vòng đời ViewModel - Điểm mạnh nhất

```
Activity/Fragment tạo
        │
        ▼
ViewModel tạo (lần đầu)
        │
        ▼
[... người dùng xoay màn hình ...]
        │
        ▼
Fragment bị DESTROY (onDestroyView → onDestroy)
        │
        ├─► ViewModel KHÔNG bị destroy (vẫn sống!)
        │
        ▼
Fragment mới được TẠO LẠI
        │
        ▼
Fragment kết nối lại với ViewModel cũ
        │
        ▼
StateFlow vẫn có data cũ → Fragment nhận ngay, không cần gọi API lại!
        │
        ▼
[... người dùng bấm Back ...]
        │
        ▼
Activity/Fragment finish → ViewModel.onCleared() → tự hủy
```

### Code ví dụ đầy đủ

```kotlin
// ────────────────────────────────────────────────────
// DOMAIN LAYER
// ────────────────────────────────────────────────────

// Model
data class MediaCategory(
    val id: String,
    val title: String,
    val items: List<MediaItem>
)

// Repository Interface (contract)
interface MediaRepository {
    suspend fun getCategories(): List<MediaCategory>
}

// UseCase
class GetCategoriesUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<MediaCategory> =
        repository.getCategories()
}
```

```kotlin
// ────────────────────────────────────────────────────
// DATA LAYER
// ────────────────────────────────────────────────────

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val dataSource: MockDataSource
) : MediaRepository {

    override suspend fun getCategories(): List<MediaCategory> {
        return withContext(Dispatchers.IO) {
            delay(500) // Giả lập network
            dataSource.getCategories()
        }
    }
}
```

```kotlin
// ────────────────────────────────────────────────────
// PRESENTATION LAYER - UiState (shared)
// ────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

```kotlin
// ────────────────────────────────────────────────────
// PRESENTATION LAYER - ViewModel
// ────────────────────────────────────────────────────

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // Private MutableStateFlow: chỉ ViewModel ghi
    private val _categoriesState =
        MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)

    // Public StateFlow: View chỉ đọc, không ghi được
    val categoriesState: StateFlow<UiState<List<MediaCategory>>> =
        _categoriesState.asStateFlow()

    init {
        loadCategories()  // Tự load khi ViewModel được tạo
    }

    fun loadCategories() {
        viewModelScope.launch {  // Tự cancel khi ViewModel bị destroy
            _categoriesState.value = UiState.Loading
            try {
                val result = getCategoriesUseCase()
                _categoriesState.value = UiState.Success(result)
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: "Có lỗi xảy ra")
            }
        }
    }
}
```

```kotlin
// ────────────────────────────────────────────────────
// PRESENTATION LAYER - Fragment (View)
// ────────────────────────────────────────────────────

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Hilt tạo và inject ViewModel, tự quản lý lifecycle
    private val viewModel: HomeViewModel by viewModels()

    private val adapter by lazy {
        CategoryAdapter(
            onTitleClick = ::navigateToListing,
            onItemClick = ::navigateToPlayer
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvCategories.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle tự stop collect khi Fragment vào background
            // và restart khi Fragment quay về foreground
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.rvCategories.isVisible = false
                        }
                        is UiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.rvCategories.isVisible = true
                            adapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            Snackbar.make(
                                binding.root, state.message, Snackbar.LENGTH_LONG
                            ).setAction("Thử lại") {
                                viewModel.loadCategories()
                            }.show()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToListing(category: MediaCategory) {
        val action = HomeFragmentDirections
            .actionHomeFragmentToListingGridFragment(category.id, category.title)
        findNavController().navigate(action)
    }

    private fun navigateToPlayer(item: MediaItem) {
        val action = HomeFragmentDirections
            .actionHomeFragmentToPlayerFragment(item.streamUrl, item.title)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Tránh memory leak
    }
}
```

```kotlin
// ────────────────────────────────────────────────────
// UNIT TEST - ViewModel dễ test hơn MVP
// ────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // Dispatcher test - kiểm soát coroutine trong test
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val mockUseCase = mock<GetCategoriesUseCase>()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(mockUseCase)
    }

    @Test
    fun `initial state is Loading`() {
        // ViewModel emit Loading ngay khi tạo
        assertThat(viewModel.categoriesState.value).isInstanceOf(UiState.Loading::class.java)
    }

    @Test
    fun `loadCategories success - emits Success state`() = runTest {
        val fakeData = listOf(MediaCategory("1", "Trending", emptyList()))
        whenever(mockUseCase()).thenReturn(fakeData)

        // Không cần mock View - test thẳng ViewModel
        viewModel.loadCategories()
        advanceUntilIdle()

        val state = viewModel.categoriesState.value
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        assertThat((state as UiState.Success).data).isEqualTo(fakeData)
    }

    @Test
    fun `loadCategories failure - emits Error state`() = runTest {
        whenever(mockUseCase()).thenThrow(RuntimeException("No internet"))

        viewModel.loadCategories()
        advanceUntilIdle()

        val state = viewModel.categoriesState.value
        assertThat(state).isInstanceOf(UiState.Error::class.java)
        assertThat((state as UiState.Error).message).isEqualTo("No internet")
    }
}
```

### Ưu điểm

- ✅ **Survive configuration change**: ViewModel tự sống qua xoay màn hình — không gọi API lại
- ✅ **Không memory leak**: ViewModel không giữ View reference
- ✅ **Google officially supported**: Toàn bộ Jetpack (Room, Navigation, Hilt...) tích hợp sẵn
- ✅ **Dễ test**: Test ViewModel không cần mock Android framework
- ✅ **Cân bằng**: Ít boilerplate hơn MVI, tách biệt tốt hơn MVP
- ✅ **Coroutine integration**: `viewModelScope` tự hủy coroutine khi ViewModel bị destroy

### Nhược điểm

- ❌ **State phân tán**: Nhiều `StateFlow` riêng lẻ có thể khó quản lý khi màn hình phức tạp
- ❌ **One-time event khó xử lý**: Navigation, Toast, Dialog không phải "state" — cần workaround
- ❌ **Hai chiều không rõ ràng**: View vừa observe, vừa gọi hàm ViewModel → đôi khi khó trace
- ❌ **Học curve với Coroutine/Flow**: Cần hiểu `StateFlow`, `repeatOnLifecycle`, `lifecycleScope`

---

## 6. So sánh tổng hợp

### Bảng so sánh chi tiết

| Tiêu chí | MVC | MVP | MVI | MVVM |
|---------|-----|-----|-----|------|
| **Độ phức tạp** | Thấp | Trung bình | Cao | Trung bình |
| **Boilerplate** | Ít | Nhiều | Rất nhiều | Vừa phải |
| **Testability** | Khó | Tốt (mock interface) | Rất tốt | Tốt |
| **Survive rotation** | ❌ Không | ❌ Không | ✅ Có (ViewModel) | ✅ Có |
| **Memory leak** | Có thể | Có thể (View ref) | Khó | Hiếm |
| **Data flow** | 2 chiều | 2 chiều | 1 chiều | Observer |
| **State management** | Không rõ | Trong Presenter | Immutable State | StateFlow |
| **Google support** | ❌ | ❌ | Một phần | ✅ Chính thức |
| **Jetpack tích hợp** | Kém | Kém | Tự làm | Hoàn toàn |
| **Phù hợp với** | Prototype | App nhỏ-trung | App phức tạp | Mọi loại app |
| **Team size** | 1 người | 2-3 người | 3+ người | Mọi team |

### Khi nào dùng gì?

```
Dự án nhỏ/prototype, cần nhanh
        → MVC

Muốn tách logic, team nhỏ, cần testable
        → MVP

App phức tạp, nhiều state phụ thuộc nhau, cần debug tốt
        → MVI

Hầu hết các trường hợp, Google ecosystem, Jetpack
        → MVVM ← Lựa chọn này ✅
```

---

## 7. Tại sao chọn MVVM?

### Lý do kỹ thuật

**1. Google chính thức khuyến nghị và đầu tư**

```
Android Jetpack được thiết kế xung quanh MVVM:
  - ViewModel → survives configuration change
  - LiveData/StateFlow → observable state
  - Room → observe database với Flow
  - Navigation → tích hợp với ViewModel
  - Hilt → inject thẳng vào ViewModel
  - DataBinding/ViewBinding → bind với ViewModel
```

**2. Giải quyết vấn đề cốt lõi của Android**

```
Vấn đề lớn nhất Android:
  Configuration Change (xoay màn hình, đổi language...)
  → Activity/Fragment bị destroy và recreate
  → Mất hết state, phải load lại API

MVVM giải quyết: ViewModel tồn tại độc lập với lifecycle của View
  → Xoay màn hình? View mới connect lại với ViewModel cũ
  → Data vẫn còn, không cần gọi API lại
  → UX tốt hơn đáng kể
```

**3. Không memory leak by design**

```
MVP: Presenter giữ View reference → fragment rotate → leak
     ↓
     Phải manually onDestroy(), dễ quên

MVVM: ViewModel KHÔNG biết View tồn tại
      View observe ViewModel (không phải ngược lại)
      → Khi View bị destroy, ViewModel không giữ gì cả
      → Không leak
```

**4. Coroutine integration**

```kotlin
// viewModelScope: scope gắn với ViewModel lifecycle
// Tự hủy TẤT CẢ coroutine khi ViewModel.onCleared() được gọi
// → Không cần manually cancel, không bao giờ leak coroutine

viewModelScope.launch {
    val data = repository.getCategories()  // Nếu ViewModel bị clear, tự cancel
    _state.value = UiState.Success(data)
}
```

**5. Cân bằng giữa đơn giản và đủ mạnh**

```
MVC: Quá đơn giản → không scale được
MVI: Quá phức tạp → overkill cho màn hình đơn giản
MVP: Ổn nhưng nhiều boilerplate và không survive rotation

MVVM: Điểm cân bằng tốt nhất
  - Đủ tách biệt để test
  - Đủ đơn giản để không overkill
  - Survive rotation built-in
  - Ít boilerplate hơn MVP
```

### So sánh trực tiếp: MVC vs MVP vs MVVM cho màn hình Home

```
Màn hình Home OTT: Load categories từ API, hiển thị lên RecyclerView

MVC (Activity):
  - Activity: 300+ dòng
  - Không test được
  - Xoay màn hình → load lại từ đầu
  - Dễ viết nhất

MVP (Fragment + Presenter):
  - HomeContract.kt: ~30 dòng
  - HomePresenter.kt: ~80 dòng  → test được
  - HomeFragment.kt: ~100 dòng
  - Xoay màn hình → Presenter bị recreate, data mất
  - Phải gọi presenter.onDestroy()

MVVM (Fragment + ViewModel):
  - HomeViewModel.kt: ~40 dòng  → test được
  - HomeFragment.kt: ~80 dòng
  - Xoay màn hình → ViewModel sống, data giữ nguyên ✅
  - Hilt tự quản lý lifecycle ✅
  - Tích hợp tốt với Navigation, Room, DataBinding ✅
```

### Kết luận

MVVM được chọn cho project OTT này vì:

1. **Phù hợp với Jetpack** — Hilt, Navigation Component, ExoPlayer MediaController đều thiết kế cho MVVM
2. **Survive rotation** — Video player đặc biệt cần giữ state khi xoay màn hình (ExoPlayer position, buffering state)
3. **Clean + đủ đơn giản** — Kết hợp với Clean Architecture (UseCase, Repository) mà không over-engineer
4. **Team productivity** — Mỗi người làm 1 layer, ít conflict merge
5. **Dễ mở rộng** — Từ mock data → real API chỉ cần thay RepositoryImpl, không đụng vào ViewModel hay Fragment

---

---

## 8. MVVM vs MVI — Phân tích chuyên sâu

> MVVM và MVI có nhiều điểm tương đồng đến mức dễ nhầm lẫn. Phần này phân tích rõ **điểm giống**, **điểm khác** và khi nào nên chọn cái nào — dựa trên codebase thực tế của project.

---

### 8.1. Điểm GIỐNG nhau

| Khía cạnh | MVVM | MVI |
|---|---|---|
| **Tách biệt UI & logic** | ViewModel tách logic khỏi View | Intent/Model tách logic khỏi View |
| **Luồng data phản ứng** | StateFlow / LiveData | StateFlow / Observable |
| **Không thao tác UI trực tiếp** | ViewModel không giữ reference View | ViewModel/Store không giữ reference View |
| **Quan sát state** | Fragment observe StateFlow | Fragment observe StateFlow |
| **Kết hợp Clean Architecture** | Dùng UseCase | Dùng UseCase |
| **Testability** | ViewModel test độc lập | ViewModel/Store test độc lập |

Cả hai đều **không phải kiến trúc hoàn chỉnh** — chúng chỉ định nghĩa cách giao tiếp UI ↔ ViewModel, còn tầng Data/Domain vẫn dùng Repository + UseCase như nhau.

---

### 8.2. Điểm KHÁC nhau — Cốt lõi

#### Hướng luồng dữ liệu

```
MVVM — gần như một chiều nhưng View có thể gọi hàm tự do:

  View ──► ViewModel  (gọi hàm bất kỳ: loadData(), refresh(), onItemClick()...)
  View ◄── ViewModel  (observe StateFlow)

  ⚠ Vấn đề: View có thể gọi nhiều hàm theo thứ tự bất kỳ
    → Khó đoán trạng thái hiện tại (ít predictable hơn)

──────────────────────────────────────────────────────

MVI — một chiều nghiêm ngặt (strict unidirectional):

  View ──► Intent ──► ViewModel (Reducer) ──► State ──► View
      └────────────────────────────────────────────────────┘
                      (vòng lặp khép kín)

  ✅ View CHỈ gửi Intent, KHÔNG gọi hàm trực tiếp
    → Trạng thái luôn đoán được (fully predictable)
```

---

#### So sánh cách View "ra lệnh" cho ViewModel

```kotlin
// ── MVVM (project hiện tại) ──────────────────────────────────
// View gọi hàm trực tiếp, tên hàm tự do → linh hoạt nhưng phân tán

viewModel.loadChannels()          // Fragment gọi khi vào màn hình
viewModel.onItemClicked(item)     // Fragment gọi khi user bấm item
viewModel.refresh()               // Fragment gọi khi pull-to-refresh
viewModel.clearError()            // Fragment gọi khi dismiss error

// Có thể gọi theo thứ tự bất kỳ → dễ gây bug nếu team lớn


// ── MVI ──────────────────────────────────────────────────────
// View chỉ gửi 1 loại message: Intent (sealed class)
// Tất cả hành động được liệt kê tường minh → dễ audit

sealed class ChannelIntent {
    object LoadChannels              : ChannelIntent()
    data class ItemClicked(
        val item: MediaItem
    )                                : ChannelIntent()
    object Refresh                   : ChannelIntent()
    object ClearError                : ChannelIntent()
}

// Fragment:
viewModel.handleIntent(ChannelIntent.LoadChannels)
viewModel.handleIntent(ChannelIntent.ItemClicked(item))
// → 1 entry point duy nhất, biết ngay mọi thứ View có thể làm
```

---

#### So sánh State Management

```kotlin
// ── MVVM (project hiện tại) ──────────────────────────────────
// Nhiều StateFlow riêng lẻ, mỗi cái quản lý 1 phần

class ChannelViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Nếu cần thêm field → thêm StateFlow mới
    private val _isRefreshing = MutableStateFlow(false)
    private val _selectedFilter = MutableStateFlow("All")
}
// ⚠ Nguy cơ: 3 state riêng → có thể không đồng bộ với nhau
//   (vd: isRefreshing = true nhưng uiState đã Success)


// ── MVI ──────────────────────────────────────────────────────
// 1 State object duy nhất — gộp tất cả vào trong

data class ChannelState(
    val isLoading: Boolean          = false,
    val channels: List<MediaItem>   = emptyList(),
    val error: String?              = null,
    val isRefreshing: Boolean       = false,
    val selectedFilter: String      = "All"
)

class ChannelViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChannelState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: ChannelIntent) {
        when (intent) {
            is ChannelIntent.LoadChannels ->
                _state.update { it.copy(isLoading = true, error = null) }
            is ChannelIntent.ItemClicked  -> { /* navigate */ }
            is ChannelIntent.Refresh      ->
                _state.update { it.copy(isRefreshing = true) }
            is ChannelIntent.ClearError   ->
                _state.update { it.copy(error = null) }
        }
    }
}
// ✅ Luôn nhất quán: không thể xảy ra trạng thái mâu thuẫn
// ✅ Dễ debug: chỉ cần in _state.value là thấy toàn bộ màn hình
```

---

#### So sánh xử lý Side Effects

Side effect = hành động một lần không thuộc về State (navigate, show toast, play sound...).

```kotlin
// ── MVVM — không có quy ước thống nhất ───────────────────────
// Mỗi developer có cách xử lý riêng:

// Cách 1: SharedFlow
private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
val navigationEvent = _navigationEvent.asSharedFlow()

// Cách 2: Gọi trực tiếp từ Fragment
private var pendingNavigation: MediaItem? = null

// Cách 3: Channel
private val _events = Channel<UiEvent>()

// → Không nhất quán, khó onboard người mới


// ── MVI — Effect được định nghĩa rõ ràng ─────────────────────
sealed class ChannelEffect {
    data class NavigateToPlayer(val url: String) : ChannelEffect()
    data class ShowToast(val message: String)    : ChannelEffect()
    object HideKeyboard                          : ChannelEffect()
}

// ViewModel emit effect riêng biệt:
private val _effect = Channel<ChannelEffect>(Channel.BUFFERED)
val effect = _effect.receiveAsFlow()

// Fragment:
viewLifecycleOwner.lifecycleScope.launch {
    viewModel.effect.collect { effect ->
        when (effect) {
            is ChannelEffect.NavigateToPlayer -> navigateTo(effect.url)
            is ChannelEffect.ShowToast        -> Toast.show(effect.message)
            is ChannelEffect.HideKeyboard     -> hideKeyboard()
        }
    }
}
// → State stream (liên tục) tách biệt với Effect stream (một lần)
```

---

### 8.3. Sơ đồ so sánh tổng thể

```
┌─────────────────────────────────────────────────────────────┐
│                          MVVM                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌──────────┐  gọi hàm bất kỳ  ┌─────────────────────┐   │
│   │          │ ────────────────► │                     │   │
│   │ Fragment │                   │     ViewModel       │   │
│   │          │ ◄──────────────── │  (nhiều StateFlow)  │   │
│   └──────────┘  observe từng     └──────────┬──────────┘   │
│                 StateFlow riêng             │               │
│                                         UseCase            │
│                                             │               │
│                                        Repository          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                           MVI                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌──────────┐    Intent        ┌─────────────────────┐    │
│   │          │ ────────────────► │                     │    │
│   │ Fragment │                  │  ViewModel/Reducer  │    │
│   │          │ ◄──────────────── │   (1 State object)  │    │
│   └────┬─────┘  observe 1 State └──────────┬──────────┘    │
│        │        + Effect stream            │                │
│        │                               UseCase             │
│        └──── Effect (navigate, toast) ──────┘              │
│                                        Repository          │
└─────────────────────────────────────────────────────────────┘
```

---

### 8.4. Chuyển đổi project từ MVVM sang MVI

Project hiện tại đã có **nền tảng tốt** (sealed class `UiState`, `StateFlow`). Để chuyển sang MVI thuần, cần 3 bước:

**Bước 1 — Tạo sealed class Intent cho mỗi màn hình:**
```kotlin
// Tạo file mới: ChannelIntent.kt
sealed class ChannelIntent {
    object LoadChannels              : ChannelIntent()
    data class ItemClicked(val item: MediaItem) : ChannelIntent()
}
```

**Bước 2 — Gộp state thành 1 data class:**
```kotlin
// Thay vì nhiều StateFlow → 1 State
data class ChannelState(
    val isLoading: Boolean        = false,
    val channels: List<MediaItem> = emptyList(),
    val error: String?            = null
)
```

**Bước 3 — ViewModel xử lý Intent qua 1 entry point:**
```kotlin
fun handleIntent(intent: ChannelIntent) {
    viewModelScope.launch {
        when (intent) {
            ChannelIntent.LoadChannels -> loadChannels()
            is ChannelIntent.ItemClicked -> openPlayer(intent.item)
        }
    }
}
```

**Fragment gửi Intent thay vì gọi hàm:**
```kotlin
// Trước (MVVM):
viewModel.loadChannels()

// Sau (MVI):
viewModel.handleIntent(ChannelIntent.LoadChannels)
```

---

### 8.5. Khi nào nên dùng cái nào?

| Tình huống | Nên dùng | Lý do |
|---|---|---|
| App nhỏ, 1-3 developer | **MVVM** | Ít boilerplate, nhanh |
| Deadline gọn, prototype | **MVVM** | Linh hoạt, dễ thay đổi |
| State màn hình phức tạp (5+ fields) | **MVI** | 1 State object rõ ràng hơn |
| Nhiều developer cùng làm 1 màn hình | **MVI** | Quy ước thống nhất |
| Cần debug / replay lại state | **MVI** | In `state.value` là xong |
| Quen với React/Redux/Flux | **MVI** | Tư duy hoàn toàn giống nhau |
| App lớn, cần audit tất cả hành động | **MVI** | sealed class Intent = tài liệu sống |

---

### 8.6. Tóm tắt

```
                MVVM                        MVI
           ─────────────────          ─────────────────
  View    │ Gọi hàm tự do   │        │ Gửi Intent      │
  State   │ Nhiều StateFlow  │        │ 1 State object  │
  Effect  │ Không quy ước   │        │ Effect stream   │
  Debug   │ Khó hơn         │        │ Dễ hơn          │
  Code    │ Ít hơn          │        │ Nhiều hơn       │
  Phù hợp│ Project nhỏ-vừa│        │ Project lớn     │
           ─────────────────          ─────────────────
```

> **Kết luận**: Project OTT này dùng **MVVM**, nhưng đã có tư duy gần MVI (`sealed class UiState`, `StateFlow`). Hai kiến trúc không đối lập — MVI giống như **MVVM có kỷ luật hơn**: thêm quy ước về Intent và gộp State, phần còn lại hoàn toàn giống nhau.

---

*Tài liệu này là phần mở rộng của `MVVM_CleanArchitecture_Guide.md` — tập trung vào so sánh kiến trúc.*  
*Project: `com.example.demoottmobile`*
