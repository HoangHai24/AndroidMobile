# Hướng Dẫn Học MVVM + Clean Architecture (Android - Kotlin)

> Tài liệu này dành cho lập trình viên Android mới bắt đầu muốn hiểu sâu về kiến trúc MVVM kết hợp Clean Architecture. Mỗi khái niệm đều có giải thích chi tiết kèm ví dụ code thực tế từ project OTT này.

---

## Mục lục

1. [Tại sao cần kiến trúc?](#1-tại-sao-cần-kiến-trúc)
2. [MVVM là gì?](#2-mvvm-là-gì)
3. [Clean Architecture là gì?](#3-clean-architecture-là-gì)
4. [Kết hợp MVVM + Clean Architecture](#4-kết-hợp-mvvm--clean-architecture)
5. [Lớp Domain (Domain Layer)](#5-lớp-domain-domain-layer)
6. [Lớp Data (Data Layer)](#6-lớp-data-data-layer)
7. [Lớp Presentation (Presentation Layer)](#7-lớp-presentation-presentation-layer)
8. [Luồng dữ liệu (Data Flow)](#8-luồng-dữ-liệu-data-flow)
9. [Dependency Injection với Hilt](#9-dependency-injection-với-hilt)
10. [StateFlow & UiState](#10-stateflow--uistate)
11. [Repository Pattern](#11-repository-pattern)
12. [Use Case Pattern](#12-use-case-pattern)
13. [Ví dụ thực tế: Màn hình Home](#13-ví-dụ-thực-tế-màn-hình-home)
14. [Các lỗi thường gặp khi học](#14-các-lỗi-thường-gặp-khi-học)
15. [Tóm tắt & Checklist](#15-tóm-tắt--checklist)

---

## 1. Tại sao cần kiến trúc?

### Vấn đề khi không có kiến trúc

Hãy tưởng tượng bạn viết toàn bộ logic vào trong một `Activity`:

```kotlin
// ❌ CÁCH SAI - "God Activity" - làm tất cả mọi thứ trong 1 class
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gọi API trực tiếp trong Activity
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .build()
        val api = retrofit.create(MovieApi::class.java)

        // Xử lý dữ liệu ngay trong Activity
        lifecycleScope.launch {
            val movies = api.getMovies()
            val filtered = movies.filter { it.rating > 7.0 }
            // Cập nhật UI
            recyclerView.adapter = MovieAdapter(filtered)
        }

        // Logic business ngay trong click listener
        btnFavorite.setOnClickListener {
            val db = Room.databaseBuilder(...).build()
            lifecycleScope.launch {
                db.movieDao().insertFavorite(currentMovie)
            }
        }
    }
}
```

**Hậu quả:**
| Vấn đề | Giải thích |
|--------|-----------|
| Khó test | Không thể test logic vì nó gắn chặt với Android framework |
| Khó maintain | Một file có hàng nghìn dòng code |
| Không thể tái sử dụng | Logic API gắn chặt với UI |
| Khó làm nhóm | 2 người không thể cùng sửa 1 file |
| Memory leak | Activity giữ reference đến các object nặng |

### Giải pháp: Tách biệt trách nhiệm (Separation of Concerns)

Mỗi class chỉ nên làm **một việc duy nhất**:
- **Fragment/Activity**: Chỉ hiển thị UI và nhận input từ người dùng
- **ViewModel**: Chỉ quản lý trạng thái và logic UI
- **UseCase**: Chứa một nghiệp vụ cụ thể
- **Repository**: Quản lý nguồn dữ liệu
- **DataSource**: Thực hiện việc lấy dữ liệu (mạng, database...)

---

## 2. MVVM là gì?

**MVVM** = **M**odel - **V**iew - **V**iew**M**odel

### Sơ đồ MVVM

```
┌─────────────────────────────────────────────────────┐
│                      VIEW                           │
│         (Fragment / Activity / XML Layout)          │
│                                                     │
│  - Hiển thị dữ liệu lên màn hình                   │
│  - Nhận sự kiện từ người dùng (click, scroll...)    │
│  - KHÔNG chứa business logic                        │
└──────────────────────┬──────────────────────────────┘
                       │ observe (lắng nghe)
                       │ events (gửi sự kiện)
                       ▼
┌─────────────────────────────────────────────────────┐
│                   VIEWMODEL                         │
│                                                     │
│  - Chứa UI State (trạng thái giao diện)             │
│  - Xử lý logic liên quan đến UI                     │
│  - Gọi UseCase để lấy dữ liệu                       │
│  - Sống lâu hơn Fragment (survive rotation)         │
└──────────────────────┬──────────────────────────────┘
                       │ gọi
                       ▼
┌─────────────────────────────────────────────────────┐
│                     MODEL                           │
│         (UseCase + Repository + DataSource)         │
│                                                     │
│  - Chứa dữ liệu và business logic                   │
│  - Hoàn toàn độc lập với Android framework          │
│  - Có thể test bằng Unit Test thuần túy             │
└─────────────────────────────────────────────────────┘
```

### Nguyên tắc quan trọng nhất của MVVM

> **ViewModel KHÔNG được biết View tồn tại.**

ViewModel không được giữ reference đến `Activity`, `Fragment`, `Context` hay bất kỳ Android View nào. Thay vào đó, View chủ động **quan sát (observe)** dữ liệu từ ViewModel.

```kotlin
// ✅ ĐÚNG: ViewModel không biết Fragment là gì
class HomeViewModel @HiltViewModel constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<MediaCategory>>> = _uiState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val categories = getCategoriesUseCase()
                _uiState.value = UiState.Success(categories)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}
```

```kotlin
// ✅ ĐÚNG: Fragment quan sát ViewModel, không làm ngược lại
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Fragment lắng nghe ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showData(state.data)
                        is UiState.Error   -> showError(state.message)
                    }
                }
            }
        }
    }
}
```

---

## 3. Clean Architecture là gì?

Clean Architecture (do Robert C. Martin - "Uncle Bob" đề xuất) chia ứng dụng thành các vòng tròn đồng tâm:

```
         ┌──────────────────────────────────────────┐
         │           FRAMEWORKS & DRIVERS           │
         │    (Android, Room, Retrofit, Hilt...)    │
         │   ┌──────────────────────────────────┐   │
         │   │       INTERFACE ADAPTERS         │   │
         │   │   (ViewModel, Repository Impl,   │   │
         │   │    Adapter, Fragment, Activity)  │   │
         │   │   ┌──────────────────────────┐   │   │
         │   │   │     APPLICATION          │   │   │
         │   │   │     BUSINESS RULES       │   │   │
         │   │   │      (Use Cases)         │   │   │
         │   │   │   ┌──────────────────┐   │   │   │
         │   │   │   │   ENTERPRISE     │   │   │   │
         │   │   │   │  BUSINESS RULES  │   │   │   │
         │   │   │   │  (Entities/      │   │   │   │
         │   │   │   │   Models)        │   │   │   │
         │   │   │   └──────────────────┘   │   │   │
         │   │   └──────────────────────────┘   │   │
         │   └──────────────────────────────────┘   │
         └──────────────────────────────────────────┘
```

### Nguyên tắc Dependency Rule

> **Mũi tên phụ thuộc chỉ được đi từ ngoài vào trong.**

- Lớp ngoài **biết** lớp trong
- Lớp trong **KHÔNG được biết** lớp ngoài

```
Presentation → Domain ← Data
```

Cụ thể:
- `ViewModel` có thể gọi `UseCase` (Presentation → Domain) ✅
- `UseCase` có thể gọi `Repository interface` (Domain sử dụng interface) ✅  
- `RepositoryImpl` implement `Repository interface` (Data → Domain) ✅
- `UseCase` **KHÔNG ĐƯỢC** gọi `RepositoryImpl` trực tiếp ❌
- `UseCase` **KHÔNG ĐƯỢC** import class nào của Android framework ❌

---

## 4. Kết hợp MVVM + Clean Architecture

Trong Android, 2 kiến trúc này thường được dùng cùng nhau:

```
app/
├── domain/                    ← Lớp Domain (tinh khiết, không Android)
│   ├── model/                 ← Các entity/model
│   │   ├── MediaItem.kt
│   │   └── MediaCategory.kt
│   ├── repository/            ← Interface của repository
│   │   └── MediaRepository.kt
│   └── usecase/               ← Use cases
│       ├── GetCategoriesUseCase.kt
│       ├── GetChannelsUseCase.kt
│       └── GetListingItemsUseCase.kt
│
├── data/                      ← Lớp Data (implement domain)
│   ├── source/
│   │   └── MockDataSource.kt  ← Nguồn dữ liệu
│   └── repository/
│       └── MediaRepositoryImpl.kt  ← Implement Repository interface
│
├── presentation/              ← Lớp Presentation (MVVM)
│   ├── home/
│   │   ├── HomeFragment.kt    ← View
│   │   ├── HomeViewModel.kt   ← ViewModel
│   │   └── adapter/
│   ├── channel/
│   ├── listing/
│   └── player/
│
└── di/                        ← Dependency Injection
    └── RepositoryModule.kt
```

---

## 5. Lớp Domain (Domain Layer)

### Đặc điểm
- **Không có** import từ `androidx`, `android.*` hay bất kỳ framework nào
- Chứa **business logic** thuần túy
- Có thể copy sang project khác mà không cần thay đổi

### 5.1 Model (Entity)

Model là các class đơn giản đại diện cho dữ liệu trong domain:

```kotlin
// domain/model/MediaItem.kt
data class MediaItem(
    val id: String,          // ID duy nhất
    val title: String,       // Tên hiển thị
    val thumbnailUrl: String, // URL ảnh thumbnail
    val streamUrl: String,   // URL stream video
    val type: MediaType      // Loại media
)

enum class MediaType {
    MOVIE,    // Phim lẻ
    SERIES,   // Phim series
    CHANNEL   // Kênh TV trực tiếp
}
```

```kotlin
// domain/model/MediaCategory.kt
data class MediaCategory(
    val id: String,
    val title: String,
    val items: List<MediaItem>  // Danh sách media trong category
)
```

**Tại sao dùng `data class`?**
- Tự động tạo `equals()`, `hashCode()`, `toString()`, `copy()`
- Immutable (bất biến) theo mặc định → an toàn hơn
- Dễ so sánh và debug

### 5.2 Repository Interface

Interface định nghĩa **hợp đồng** - những gì lớp Data phải làm:

```kotlin
// domain/repository/MediaRepository.kt
interface MediaRepository {

    // Lấy tất cả categories
    suspend fun getCategories(): List<MediaCategory>

    // Lấy danh sách kênh
    suspend fun getChannels(): List<MediaItem>

    // Lấy danh sách items theo category
    suspend fun getListingItems(categoryId: String): List<MediaItem>
}
```

**Tại sao dùng `interface` thay vì class trực tiếp?**

```
Domain layer biết interface MediaRepository
           ↑
Data layer implements interface này

→ Domain KHÔNG PHỤ THUỘC vào Data layer
→ Có thể swap implementation bất kỳ lúc nào
→ Dễ mock khi viết Unit Test
```

**Ví dụ swap implementation:**
```kotlin
// Production: dùng API thật
class NetworkMediaRepository : MediaRepository { ... }

// Testing: dùng fake data
class FakeMediaRepository : MediaRepository {
    override suspend fun getCategories() = fakeCategories
}

// Dev: dùng mock data local
class MockMediaRepository : MediaRepository { ... }
```

### 5.3 Use Case

Use Case là class chứa **một nghiệp vụ cụ thể**:

```kotlin
// domain/usecase/GetCategoriesUseCase.kt
class GetCategoriesUseCase @Inject constructor(
    private val repository: MediaRepository  // Inject interface, không phải impl
) {
    // Operator invoke cho phép gọi như function: getCategoriesUseCase()
    suspend operator fun invoke(): List<MediaCategory> {
        return repository.getCategories()
    }
}
```

```kotlin
// domain/usecase/GetListingItemsUseCase.kt
class GetListingItemsUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(categoryId: String): List<MediaItem> {
        return repository.getListingItems(categoryId)
    }
}
```

**Khi nào cần Use Case, khi nào gọi Repository trực tiếp?**

| Tình huống | Nên dùng |
|-----------|---------|
| Logic đơn giản, 1 repository | Repository trực tiếp trong ViewModel |
| Logic phức tạp, kết hợp nhiều repository | Use Case |
| Logic được dùng lại ở nhiều ViewModel | Use Case |
| Cần transform dữ liệu phức tạp | Use Case |

---

## 6. Lớp Data (Data Layer)

### 6.1 Data Source

Data Source là nơi thực sự lấy dữ liệu:

```kotlin
// data/source/MockDataSource.kt
class MockDataSource {

    private val HLS_URL = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    fun getCategories(): List<MediaCategory> {
        return listOf(
            MediaCategory(
                id = "trending",
                title = "Đang thịnh hành",
                items = generateItems("trending", 8)
            ),
            MediaCategory(
                id = "movies",
                title = "Phim lẻ",
                items = generateItems("movies", 10)
            ),
            // ... các category khác
        )
    }

    private fun generateItems(prefix: String, count: Int): List<MediaItem> {
        return (1..count).map { index ->
            MediaItem(
                id = "${prefix}_$index",
                title = "Nội dung $prefix #$index",
                thumbnailUrl = "https://picsum.photos/seed/${prefix}$index/300/200",
                streamUrl = HLS_URL,
                type = MediaType.MOVIE
            )
        }
    }
}
```

Trong thực tế, bạn sẽ có nhiều loại DataSource:

```kotlin
// DataSource từ API (Retrofit)
class RemoteDataSource @Inject constructor(
    private val api: MovieApi  // Retrofit API interface
) {
    suspend fun getMovies(): List<MovieDto> = api.getMovies()
}

// DataSource từ Database (Room)
class LocalDataSource @Inject constructor(
    private val dao: MovieDao  // Room DAO
) {
    suspend fun getMovies(): List<MovieEntity> = dao.getAllMovies()
    suspend fun saveMovies(movies: List<MovieEntity>) = dao.insertAll(movies)
}
```

### 6.2 Repository Implementation

`RepositoryImpl` implement interface từ domain và phối hợp các DataSource:

```kotlin
// data/repository/MediaRepositoryImpl.kt
@Singleton  // Chỉ tạo 1 instance duy nhất
class MediaRepositoryImpl @Inject constructor(
    private val dataSource: MockDataSource
) : MediaRepository {  // Implement interface từ Domain layer

    override suspend fun getCategories(): List<MediaCategory> {
        // Giả lập call API (có delay)
        return withContext(Dispatchers.IO) {
            delay(500) // Giả lập network delay
            dataSource.getCategories()
        }
    }

    override suspend fun getChannels(): List<MediaItem> {
        return withContext(Dispatchers.IO) {
            delay(400)
            dataSource.getChannels()
        }
    }

    override suspend fun getListingItems(categoryId: String): List<MediaItem> {
        return withContext(Dispatchers.IO) {
            delay(300)
            dataSource.getCategories()
                .find { it.id == categoryId }
                ?.items
                ?: emptyList()
        }
    }
}
```

**Pattern offline-first thực tế:**
```kotlin
// Trong app thực tế: lấy từ local trước, sync từ remote
override suspend fun getCategories(): List<MediaCategory> {
    return withContext(Dispatchers.IO) {
        // 1. Lấy từ local database ngay lập tức
        val localData = localDataSource.getCategories()

        // 2. Bắt đầu sync từ remote trong background
        launch {
            try {
                val remoteData = remoteDataSource.getCategories()
                localDataSource.saveCategories(remoteData) // cập nhật local
            } catch (e: Exception) {
                // Lỗi mạng → dùng local data, không crash app
            }
        }

        // 3. Trả về local data ngay lập tức (UI không phải chờ mạng)
        localData
    }
}
```

---

## 7. Lớp Presentation (Presentation Layer)

### 7.1 ViewModel

ViewModel là cầu nối giữa View và Domain:

```kotlin
// presentation/home/HomeViewModel.kt
@HiltViewModel  // Báo cho Hilt biết class này cần inject
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase  // Inject use case
) : ViewModel() {

    // MutableStateFlow: private, chỉ ViewModel được thay đổi
    private val _uiState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)

    // StateFlow: public, Fragment chỉ được đọc, không được ghi
    val uiState: StateFlow<UiState<List<MediaCategory>>> = _uiState.asStateFlow()

    init {
        // Tự động load data khi ViewModel được tạo
        loadCategories()
    }

    fun loadCategories() {
        // viewModelScope: tự động cancel khi ViewModel bị destroy
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val categories = getCategoriesUseCase()  // Gọi use case
                _uiState.value = UiState.Success(categories)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Có lỗi xảy ra")
            }
        }
    }
}
```

**Vòng đời của ViewModel:**
```
Activity/Fragment tạo → ViewModel tạo
         ↓
Xoay màn hình → Fragment destroy → Fragment recreate
                      ↓
              ViewModel VẪN SỐNG (không bị destroy khi xoay màn hình!)
                      ↓
Fragment mới connect lại với ViewModel cũ → dữ liệu không bị mất
         ↓
Người dùng back/finish Activity → ViewModel bị destroy → onCleared() gọi
```

### 7.2 Fragment (View)

```kotlin
// presentation/home/HomeFragment.kt
@AndroidEntryPoint  // Bắt buộc nếu Fragment cần inject hoặc dùng HiltViewModel
class HomeFragment : Fragment(R.layout.fragment_home) {

    // Hilt tự tạo ViewModel và inject dependencies vào nó
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {
        // repeatOnLifecycle: tự động collect khi STARTED, stop khi STOPPED
        // Tránh leak và không xử lý update khi Fragment ở background
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.recyclerView.isVisible = false
                        }
                        is UiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = true
                            categoryAdapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }

    // Quan trọng: null binding khi view bị destroy để tránh memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## 8. Luồng dữ liệu (Data Flow)

### Luồng từ khi user mở màn hình đến khi thấy dữ liệu:

```
Người dùng mở màn hình Home
        │
        ▼
HomeFragment.onViewCreated()
        │
        ├─ Gọi: setupRecyclerView()
        ├─ Gọi: observeViewModel()  ← bắt đầu collect StateFlow
        │
        ▼
HomeViewModel.init { loadCategories() }  ← tự động chạy
        │
        ├─ _uiState.value = UiState.Loading
        │           │
        │           ▼
        │   HomeFragment nhận UiState.Loading
        │   → Hiển thị ProgressBar
        │
        ├─ Gọi: getCategoriesUseCase()
        │
        ▼
GetCategoriesUseCase.invoke()
        │
        ▼
MediaRepository.getCategories()  ← gọi interface
        │
        ▼ (Hilt inject MediaRepositoryImpl)
MediaRepositoryImpl.getCategories()
        │
        ├─ withContext(Dispatchers.IO)  ← chuyển sang background thread
        ├─ delay(500ms)  ← giả lập network
        ├─ dataSource.getCategories()  ← lấy data
        │
        ▼ (trả về List<MediaCategory>)
GetCategoriesUseCase nhận List<MediaCategory>
        │
        ▼
HomeViewModel nhận List<MediaCategory>
        │
        ├─ _uiState.value = UiState.Success(categories)
        │           │
        │           ▼
        │   HomeFragment nhận UiState.Success
        │   → Ẩn ProgressBar
        │   → Hiển thị RecyclerView với dữ liệu
        │
        ▼
Người dùng thấy danh sách phim ✅
```

### Luồng khi có lỗi:

```
MediaRepositoryImpl.getCategories() throw Exception("No internet")
        │
        ▼
GetCategoriesUseCase throw Exception  (bubble up)
        │
        ▼
HomeViewModel: catch (e: Exception)
        │
        ├─ _uiState.value = UiState.Error("No internet")
        │           │
        │           ▼
        │   HomeFragment nhận UiState.Error
        │   → Hiển thị thông báo lỗi
        │   → Nút retry (?) → viewModel.loadCategories()
```

---

## 9. Dependency Injection với Hilt

### DI là gì?

**Dependency Injection (DI)** = Không tự tạo object, để người khác "tiêm" vào cho bạn.

```kotlin
// ❌ KHÔNG dùng DI - tự tạo object
class HomeViewModel : ViewModel() {
    // Tự tạo tất cả dependencies - khó test, khó thay đổi
    private val dataSource = MockDataSource()
    private val repository = MediaRepositoryImpl(dataSource)
    private val useCase = GetCategoriesUseCase(repository)
}

// ✅ DÙNG DI - nhận object từ bên ngoài
class HomeViewModel @Inject constructor(
    private val useCase: GetCategoriesUseCase  // Hilt tự tạo và inject
) : ViewModel()
```

### Hilt hoạt động như thế nào?

**Bước 1:** Đánh dấu Application class
```kotlin
@HiltAndroidApp  // Bắt buộc - tạo Hilt component
class OttApplication : Application()
```

**Bước 2:** Tạo Module để dạy Hilt cách tạo object
```kotlin
@Module
@InstallIn(SingletonComponent::class)  // Scope: sống cùng Application
object RepositoryModule {

    // @Binds: Khi ai cần MediaRepository, hãy dùng MediaRepositoryImpl
    @Binds
    @Singleton  // Chỉ tạo 1 instance
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository
}
```

**Bước 3:** Đánh dấu class cần inject
```kotlin
@Singleton
class MediaRepositoryImpl @Inject constructor(  // @Inject: Hilt biết cách tạo class này
    private val dataSource: MockDataSource
) : MediaRepository
```

**Bước 4:** Đánh dấu Fragment/Activity
```kotlin
@AndroidEntryPoint  // Báo Fragment này cần inject
class HomeFragment : Fragment()
```

**Bước 5:** Dùng ViewModel với Hilt
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GetCategoriesUseCase
) : ViewModel()

// Trong Fragment:
private val viewModel: HomeViewModel by viewModels()  // Hilt tự inject
```

### Hilt tự quản lý vòng đời:

```
@Singleton  → Sống cùng Application (toàn bộ vòng đời app)
@ViewModelScoped → Sống cùng ViewModel
@ActivityScoped  → Sống cùng Activity
@FragmentScoped  → Sống cùng Fragment
```

---

## 10. StateFlow & UiState

### UiState - Đại diện cho mọi trạng thái

```kotlin
// common/UiState.kt
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()          // Đang tải
    data class Success<T>(val data: T) : UiState<T>()  // Thành công
    data class Error(val message: String) : UiState<Nothing>()  // Lỗi
}
```

**`sealed class` là gì?**  
Là class có tập hợp con cố định, đảm bảo `when` expression phải xử lý tất cả các trường hợp:

```kotlin
// Compiler sẽ báo lỗi nếu thiếu một trường hợp
when (state) {
    is UiState.Loading -> // bắt buộc
    is UiState.Success -> // bắt buộc
    is UiState.Error   -> // bắt buộc
    // Nếu thiếu một cái → compile error!
}
```

### StateFlow vs LiveData

| | `StateFlow` | `LiveData` |
|--|------------|-----------|
| Thư viện | Kotlin Coroutines | AndroidX Lifecycle |
| Yêu cầu Android | Không ❌ | Có (cần Context) ✅ |
| Initial value | Bắt buộc có | Không bắt buộc |
| Collect trong test | Dễ hơn | Khó hơn |
| Hot/Cold | Hot (luôn phát) | Hot |
| Khuyến nghị (2024+) | ✅ | Vẫn ổn nhưng ít được dùng |

```kotlin
// StateFlow: collect đúng cách
viewLifecycleOwner.lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {  // Tự động start/stop
        viewModel.uiState.collect { state ->
            // Xử lý state
        }
    }
}

// Sai: collect trực tiếp không dùng repeatOnLifecycle
// lifecycleScope.launch {
//     viewModel.uiState.collect { ... }  // ← Sẽ tiếp tục collect kể cả khi app ở background!
// }
```

### MutableStateFlow vs StateFlow

```kotlin
class HomeViewModel : ViewModel() {

    // MutableStateFlow: private, ViewModel tự thay đổi
    private val _uiState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)

    // StateFlow: public (read-only), Fragment chỉ có thể đọc
    val uiState: StateFlow<UiState<List<MediaCategory>>> = _uiState.asStateFlow()
    //                                                         ↑
    //                               .asStateFlow() chuyển Mutable → read-only
}
```

---

## 11. Repository Pattern

### Vì sao cần Repository?

Repository là lớp trừu tượng hóa nguồn dữ liệu. View và ViewModel không cần biết dữ liệu đến từ đâu (mạng, database, cache...):

```
ViewModel ─────→ Repository Interface
                       ↑
         ┌─────────────┼──────────────┐
         │             │              │
    NetworkRepo    LocalRepo     CacheRepo
    (Retrofit)    (Room DB)    (Memory)
```

### Offline-First Repository

```kotlin
class OfflineFirstMovieRepository @Inject constructor(
    private val localDataSource: LocalMovieDataSource,   // Room
    private val remoteDataSource: RemoteMovieDataSource  // Retrofit
) : MovieRepository {

    override suspend fun getMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            // Bước 1: Trả ngay local data (UX tốt hơn - không chờ mạng)
            val localMovies = localDataSource.getMovies()

            // Bước 2: Sync từ remote trong background
            try {
                val remoteMovies = remoteDataSource.getMovies()

                // Bước 3: Cập nhật local database
                localDataSource.clearAndInsert(remoteMovies)

                // Bước 4: Trả remote data (mới nhất)
                remoteMovies

            } catch (e: IOException) {
                // Mạng lỗi → trả local data, không crash
                localMovies
            }
        }
    }
}
```

---

## 12. Use Case Pattern

### Khi nào use case có nhiều logic hơn?

```kotlin
// Use case phức tạp: kết hợp nhiều nguồn dữ liệu
class GetRecommendedMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository,
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(): List<Movie> {
        // 1. Lấy thông tin user
        val userPreferences = userRepository.getUserPreferences()

        // 2. Lấy lịch sử xem
        val watchHistory = historyRepository.getWatchHistory()

        // 3. Lấy tất cả phim
        val allMovies = movieRepository.getAllMovies()

        // 4. Logic business: lọc và sắp xếp theo sở thích
        return allMovies
            .filter { movie ->
                movie.genres.any { it in userPreferences.favoriteGenres }
            }
            .filterNot { movie ->
                watchHistory.any { it.movieId == movie.id }  // Bỏ phim đã xem
            }
            .sortedByDescending { it.rating }
            .take(20)  // Lấy tối đa 20 phim
    }
}
```

### Use case với Flow (reactive)

```kotlin
// Trả về Flow thay vì suspend function để reactive với thay đổi real-time
class ObserveMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(roomId: String): Flow<List<Message>> {
        return chatRepository.observeMessages(roomId)
            .map { messages -> messages.sortedBy { it.timestamp } }
            .distinctUntilChanged()  // Chỉ emit khi thực sự thay đổi
    }
}

// Trong ViewModel:
val messages = observeMessagesUseCase(roomId)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

---

## 13. Ví dụ thực tế: Màn hình Home

### Tổng quan luồng màn hình Home trong project

```
HomeFragment.kt ──observe──→ HomeViewModel.kt
                                    │
                                    └──call──→ GetCategoriesUseCase.kt
                                                        │
                                                        └──call──→ MediaRepository (interface)
                                                                          │
                                                                 (Hilt inject)
                                                                          │
                                                                          ▼
                                                               MediaRepositoryImpl.kt
                                                                          │
                                                                          └──→ MockDataSource.kt
```

### Code đầy đủ kết nối tất cả các lớp

**1. Model (Domain)**
```kotlin
data class MediaCategory(val id: String, val title: String, val items: List<MediaItem>)
data class MediaItem(val id: String, val title: String, val thumbnailUrl: String, val streamUrl: String)
```

**2. Repository Interface (Domain)**
```kotlin
interface MediaRepository {
    suspend fun getCategories(): List<MediaCategory>
}
```

**3. Use Case (Domain)**
```kotlin
class GetCategoriesUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke() = repository.getCategories()
}
```

**4. Repository Implementation (Data)**
```kotlin
@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val dataSource: MockDataSource
) : MediaRepository {
    override suspend fun getCategories() = withContext(Dispatchers.IO) {
        delay(500)
        dataSource.getCategories()
    }
}
```

**5. DI Module**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaRepository
}
```

**6. ViewModel (Presentation)**
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { loadCategories() }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getCategoriesUseCase() }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Lỗi") }
        }
    }
}
```

**7. Fragment (Presentation)**
```kotlin
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showShimmer()
                        is UiState.Success -> {
                            hideShimmer()
                            adapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            hideShimmer()
                            showErrorSnackbar(state.message)
                        }
                    }
                }
            }
        }
    }
}
```

---

## 14. Các lỗi thường gặp khi học

### ❌ Lỗi 1: Tạo object trực tiếp trong ViewModel thay vì inject

```kotlin
// ❌ SAI
class HomeViewModel : ViewModel() {
    private val repository = MediaRepositoryImpl(MockDataSource())  // Tự tạo
}

// ✅ ĐÚNG
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase  // Inject
) : ViewModel()
```

### ❌ Lỗi 2: Giữ reference tới Context/Activity trong ViewModel

```kotlin
// ❌ SAI - Memory leak!
class HomeViewModel(
    private val context: Context  // Activity/Fragment context → leak!
) : ViewModel()

// ✅ ĐÚNG - Dùng ApplicationContext nếu thực sự cần
class HomeViewModel(
    @ApplicationContext private val context: Context  // Application context - an toàn
) : ViewModel()

// ✅ TỐT NHẤT - Không dùng context trong ViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GetCategoriesUseCase
) : ViewModel()
```

### ❌ Lỗi 3: Gọi API trên Main Thread

```kotlin
// ❌ SAI - sẽ crash với NetworkOnMainThreadException
override suspend fun getCategories(): List<MediaCategory> {
    return dataSource.getCategories()  // Nếu gọi Retrofit trực tiếp → crash!
}

// ✅ ĐÚNG - chuyển sang IO thread
override suspend fun getCategories(): List<MediaCategory> {
    return withContext(Dispatchers.IO) {
        dataSource.getCategories()
    }
}
```

### ❌ Lỗi 4: Collect Flow không dùng repeatOnLifecycle

```kotlin
// ❌ SAI - tiếp tục collect khi app ở background → waste resources, có thể crash
override fun onViewCreated(...) {
    lifecycleScope.launch {
        viewModel.uiState.collect { ... }  // Không stop khi Fragment ở background
    }
}

// ✅ ĐÚNG
override fun onViewCreated(...) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {  // Tự stop/start theo lifecycle
            viewModel.uiState.collect { ... }
        }
    }
}
```

### ❌ Lỗi 5: Quên null binding trong onDestroyView

```kotlin
// ❌ SAI - Memory leak!
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    // Không null binding → Fragment giữ view reference sau khi view bị destroy
}

// ✅ ĐÚNG
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Giải phóng reference
    }
}
```

### ❌ Lỗi 6: Domain layer import Android class

```kotlin
// ❌ SAI - UseCase import Android class → vi phạm Clean Architecture
import android.content.Context  // ← KHÔNG ĐƯỢC trong Domain layer!

class GetCategoriesUseCase(private val context: Context) { ... }

// ✅ ĐÚNG - UseCase hoàn toàn thuần Kotlin
class GetCategoriesUseCase @Inject constructor(
    private val repository: MediaRepository  // Chỉ dùng interface từ Domain
) { ... }
```

---

## 15. Tóm tắt & Checklist

### Sơ đồ tổng quan cuối cùng

```
┌──────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                           │
│                                                                  │
│  Fragment/Activity          ViewModel                            │
│  (View)          ──────→   (UiState, viewModelScope)            │
│  - XML Layout    observe    - StateFlow<UiState<T>>             │
│  - ViewBinding             - Gọi UseCase                        │
│  - User events             - Không biết View                    │
└─────────────────────────────────┬────────────────────────────────┘
                                  │ inject & call
┌─────────────────────────────────▼────────────────────────────────┐
│                       DOMAIN LAYER                               │
│                   (Thuần Kotlin, no Android)                     │
│                                                                  │
│  UseCase                    Repository (interface)               │
│  - 1 nghiệp vụ              - Hợp đồng với Data layer           │
│  - operator invoke          - suspend fun                        │
│  - Gọi Repository           - Không biết implementation         │
└─────────────────────────────────┬────────────────────────────────┘
                                  │ implement
┌─────────────────────────────────▼────────────────────────────────┐
│                        DATA LAYER                                │
│                                                                  │
│  RepositoryImpl             DataSource                           │
│  - Implement interface      - Remote: Retrofit API              │
│  - Phối hợp DataSources     - Local: Room Database              │
│  - withContext(IO)          - Cache: Memory/SharedPref           │
└──────────────────────────────────────────────────────────────────┘
```

### ✅ Checklist khi viết code

**Domain Layer:**
- [ ] Model class dùng `data class`
- [ ] Repository là `interface`, không phải `class`
- [ ] UseCase có `operator fun invoke()`
- [ ] Không có import nào từ `android.*` hoặc `androidx.*`

**Data Layer:**
- [ ] RepositoryImpl có `@Singleton` annotation
- [ ] Các suspend fun dùng `withContext(Dispatchers.IO)`
- [ ] RepositoryImpl implement interface từ Domain

**Presentation Layer:**
- [ ] ViewModel có `@HiltViewModel`
- [ ] ViewModel không giữ Context/Fragment reference
- [ ] StateFlow private là `MutableStateFlow`, public là `StateFlow`
- [ ] Fragment dùng `repeatOnLifecycle(STARTED)` để collect
- [ ] Fragment null binding trong `onDestroyView()`
- [ ] Fragment có `@AndroidEntryPoint`

**DI:**
- [ ] Application có `@HiltAndroidApp`
- [ ] Module dùng `@Binds` để bind interface → implementation
- [ ] Scope phù hợp (`@Singleton` cho Repository)

---

## Tài liệu tham khảo

| Tài liệu | Link |
|----------|------|
| Android Architecture Guide | https://developer.android.com/topic/architecture |
| MVVM Architecture | https://developer.android.com/topic/libraries/architecture/viewmodel |
| Hilt DI | https://developer.android.com/training/dependency-injection/hilt-android |
| Kotlin Flow | https://developer.android.com/kotlin/flow |
| Clean Architecture (Uncle Bob) | https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html |
| Now in Android (Sample App) | https://github.com/android/nowinandroid |

---

*Tài liệu này được viết dựa trên project OTT Demo - `com.example.demoottmobile`*
