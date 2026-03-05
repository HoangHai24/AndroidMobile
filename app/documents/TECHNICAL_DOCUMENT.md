# Tài Liệu Kỹ Thuật — OTT Mobile Android

> Dành cho người mới bắt đầu lập trình Android  
> Ngôn ngữ: Kotlin | UI: XML + Fragment | Kiến trúc: MVVM + Clean Architecture

---

## Mục Lục

1. [Tổng Quan Dự Án](#1-tổng-quan-dự-án)
2. [Cấu Trúc Thư Mục](#2-cấu-trúc-thư-mục)
3. [Kiến Trúc Tổng Thể](#3-kiến-trúc-tổng-thể)
4. [Các Thư Viện Sử Dụng](#4-các-thư-viện-sử-dụng)
5. [Lớp Domain — Trái Tim Của Ứng Dụng](#5-lớp-domain--trái-tim-của-ứng-dụng)
6. [Lớp Data — Nguồn Dữ Liệu](#6-lớp-data--nguồn-dữ-liệu)
7. [Lớp Presentation — Giao Diện Người Dùng](#7-lớp-presentation--giao-diện-người-dùng)
8. [Dependency Injection (Hilt)](#8-dependency-injection-hilt)
9. [Luồng Điều Hướng (Navigation)](#9-luồng-điều-hướng-navigation)
10. [Luồng Dữ Liệu Từ A → Z](#10-luồng-dữ-liệu-từ-a--z)
11. [Màn Hình Home — Phân Tích Chi Tiết](#11-màn-hình-home--phân-tích-chi-tiết)
12. [Màn Hình Channel](#12-màn-hình-channel)
13. [Màn Hình ListingGrid](#13-màn-hình-listinggrid)
14. [Màn Hình Player](#14-màn-hình-player)
15. [Các Component Dùng Chung (Global)](#15-các-component-dùng-chung-global)
16. [Vòng Đời Fragment Và Quản Lý Bộ Nhớ](#16-vòng-đời-fragment-và-quản-lý-bộ-nhớ)
17. [Sơ Đồ Kiến Trúc Tổng Quát](#17-sơ-đồ-kiến-trúc-tổng-quát)

---

## 1. Tổng Quan Dự Án

Đây là ứng dụng **OTT (Over-The-Top)** — tương tự Netflix hay YouTube — cho phép người dùng xem danh sách nội dung (phim, kênh truyền hình, series) và phát video trực tuyến.

### Tính năng chính:
| Tính năng | Mô tả |
|---|---|
| Trang Home | Hiển thị danh sách nội dung theo danh mục, cuộn ngang |
| Drawer (ngăn kéo) | Menu bên trái mở ra khi nhấn nút hamburger |
| Trang Channel | Lưới 4 cột hiển thị các kênh truyền hình |
| Trang ListingGrid | Lưới 2 cột khi nhấn vào tiêu đề danh mục |
| Trang Player | Phát video HLS với đầy đủ thanh điều khiển |
| Bottom Tab | 2 tab dưới cùng: Home và Channel |

---

## 2. Cấu Trúc Thư Mục

```
app/src/main/
│
├── java/com/example/demoottmobile/
│   │
│   ├── domain/                        ← Lớp DOMAIN (logic nghiệp vụ, không phụ thuộc Android)
│   │   ├── model/
│   │   │   ├── MediaItem.kt           ← Đối tượng dữ liệu: phim/kênh/video
│   │   │   └── MediaCategory.kt       ← Đối tượng danh mục (Trending, Movies, ...)
│   │   ├── repository/
│   │   │   └── MediaRepository.kt     ← Giao diện (interface) định nghĩa các hàm lấy dữ liệu
│   │   └── usecase/
│   │       ├── GetCategoriesUseCase.kt  ← UC lấy danh sách danh mục
│   │       ├── GetChannelsUseCase.kt    ← UC lấy danh sách kênh
│   │       └── GetListingItemsUseCase.kt← UC lấy danh sách items theo danh mục
│   │
│   ├── data/                          ← Lớp DATA (cung cấp dữ liệu thực tế)
│   │   ├── source/
│   │   │   └── MockDataSource.kt      ← Dữ liệu giả lập (mock): 6 danh mục, 24 kênh
│   │   └── repository/
│   │       └── MediaRepositoryImpl.kt ← Cài đặt (implement) MediaRepository
│   │
│   ├── di/                            ← Dependency Injection
│   │   └── RepositoryModule.kt        ← Khai báo cách tạo repository
│   │
│   ├── presentation/                  ← Lớp PRESENTATION (UI)
│   │   ├── MainActivity.kt            ← Activity duy nhất, chứa toàn bộ Fragment
│   │   ├── common/
│   │   │   ├── UiState.kt             ← Trạng thái UI: Loading / Success / Error
│   │   │   ├── dialog/GlobalDialog.kt ← Dialog dùng toàn cục
│   │   │   ├── toast/GlobalToast.kt   ← Toast dùng toàn cục
│   │   │   └── loading/GlobalLoading.kt← Loading overlay toàn cục
│   │   ├── home/
│   │   │   ├── HomeFragment.kt        ← Màn hình Home
│   │   │   ├── HomeViewModel.kt       ← ViewModel của Home
│   │   │   └── adapter/
│   │   │       ├── CategoryAdapter.kt       ← Adapter cho danh sách danh mục dọc
│   │   │       └── HorizontalItemAdapter.kt ← Adapter cho items cuộn ngang
│   │   ├── channel/
│   │   │   ├── ChannelFragment.kt
│   │   │   ├── ChannelViewModel.kt
│   │   │   ├── GridSpacingItemDecoration.kt ← Tạo khoảng cách giữa các item lưới
│   │   │   └── adapter/ChannelGridAdapter.kt
│   │   ├── listing/
│   │   │   ├── ListingGridFragment.kt
│   │   │   ├── ListingGridViewModel.kt
│   │   │   └── adapter/ListingGridAdapter.kt
│   │   └── player/
│   │       ├── PlayerFragment.kt
│   │       └── PlayerViewModel.kt
│   │
│   └── OttApplication.kt             ← Điểm khởi động ứng dụng (Application class)
│
└── res/
    ├── layout/                        ← File XML giao diện
    │   ├── activity_main.xml          ← Layout chính (chứa NavHostFragment + BottomNav)
    │   ├── fragment_home.xml          ← Layout màn hình Home (DrawerLayout)
    │   ├── fragment_channel.xml
    │   ├── fragment_listing_grid.xml
    │   ├── fragment_player.xml
    │   ├── item_square.xml            ← Item hình vuông 70x70dp (dùng trong cuộn ngang)
    │   ├── item_rectangle.xml         ← Item hình chữ nhật 162x70dp (cuộn ngang)
    │   ├── item_square_grid.xml       ← Item lưới vuông (Channel screen)
    │   ├── item_rectangle_grid.xml    ← Item lưới chữ nhật (ListingGrid screen)
    │   ├── item_category_row.xml      ← 1 hàng danh mục (tiêu đề + RecyclerView ngang)
    │   └── dialog_global.xml          ← Layout cho GlobalDialog
    ├── navigation/
    │   └── nav_graph.xml              ← Sơ đồ điều hướng giữa các Fragment
    ├── menu/
    │   └── bottom_nav_menu.xml        ← Menu thanh tab dưới (Home, Channel)
    └── values/
        ├── colors.xml                 ← Màu sắc ứng dụng (OTT dark theme)
        ├── strings.xml                ← Chuỗi văn bản
        ├── dimens.xml                 ← Kích thước (spacing, item size)
        └── themes.xml                 ← Chủ đề giao diện
```

---

## 3. Kiến Trúc Tổng Thể

Dự án dùng **MVVM + Clean Architecture** — chia làm 3 lớp hoàn toàn tách biệt nhau:

```
┌─────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                  │
│  (Fragment + ViewModel + Adapter + XML Layout)       │
│  → Chỉ biết cách HIỂN THỊ dữ liệu, không biết       │
│    dữ liệu đến từ đâu                                │
└───────────────────┬─────────────────────────────────┘
                    │ gọi UseCase
┌───────────────────▼─────────────────────────────────┐
│                   DOMAIN LAYER                       │
│  (UseCase + Repository Interface + Model)            │
│  → Chứa LOGIC NGHIỆP VỤ thuần túy                   │
│  → KHÔNG import bất kỳ thứ gì của Android           │
└───────────────────┬─────────────────────────────────┘
                    │ implement
┌───────────────────▼─────────────────────────────────┐
│                    DATA LAYER                        │
│  (MockDataSource + RepositoryImpl)                   │
│  → Cung cấp dữ liệu thực tế (hiện tại là mock)      │
│  → Có thể thay bằng API thật mà không đổi code UI   │
└─────────────────────────────────────────────────────┘
```

### Tại sao lại tách như vậy?

- **Nếu bạn muốn đổi nguồn dữ liệu** từ mock sang API thật → chỉ sửa lớp Data, UI không đổi gì
- **Nếu bạn muốn đổi giao diện** → chỉ sửa lớp Presentation, logic không đổi gì
- **Dễ test từng phần riêng lẻ**

---

## 4. Các Thư Viện Sử Dụng

| Thư viện | Vai trò |
|---|---|
| **Hilt** | Dependency Injection — tự động tạo và quản lý các đối tượng |
| **Navigation Component** | Quản lý việc chuyển màn hình giữa các Fragment |
| **Safe Args** | Truyền dữ liệu giữa các Fragment một cách an toàn (type-safe) |
| **ViewModel + LiveData** | Giữ dữ liệu trong MVVM, tồn tại qua các configuration change |
| **Kotlin Coroutines** | Xử lý tác vụ bất đồng bộ (gọi API, truy vấn DB) |
| **Media3 ExoPlayer** | Phát video HLS |
| **Glide** | Tải và hiển thị ảnh thumbnail |
| **RecyclerView** | Danh sách hiệu suất cao (cuộn ngang/dọc, lưới) |
| **ViewBinding** | Truy cập view XML an toàn, không cần `findViewById` |

---

## 5. Lớp Domain — Trái Tim Của Ứng Dụng

### 5.1. Model (Dữ liệu)

**`MediaItem.kt`** — Đây là đối tượng đại diện cho 1 video/kênh/phim:

```kotlin
data class MediaItem(
    val id: String,           // ID duy nhất
    val title: String,        // Tên hiển thị
    val thumbnailUrl: String, // URL ảnh đại diện
    val streamUrl: String,    // Đường dẫn video để phát
    val type: MediaType       // Loại: VOD, LIVE, hoặc CHANNEL
)
```

**`MediaCategory.kt`** — Một danh mục chứa nhiều MediaItem:

```kotlin
data class MediaCategory(
    val id: String,            // VD: "cat_trending"
    val title: String,         // VD: "Trending Now"
    val items: List<MediaItem> // Danh sách các video trong danh mục
)
```

### 5.2. Repository Interface

**`MediaRepository.kt`** — Định nghĩa CÁC HÀM mà lớp Data phải cung cấp:

```kotlin
interface MediaRepository {
    suspend fun getCategories(): List<MediaCategory>   // Lấy tất cả danh mục
    suspend fun getChannels(): List<MediaItem>         // Lấy danh sách kênh
    suspend fun getListingItems(categoryId: String): List<MediaItem> // Lấy items theo danh mục
}
```

> 💡 **`suspend`** nghĩa là hàm này chạy bất đồng bộ (asynchronous) — không chặn luồng chính (Main Thread), tránh làm đứng ứng dụng.

### 5.3. Use Cases

Mỗi UseCase đại diện cho **một hành động cụ thể** của người dùng:

```kotlin
// Khi màn hình Home cần dữ liệu, nó gọi UseCase này
class GetCategoriesUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<MediaCategory> = repository.getCategories()
}
```

> Tại sao cần UseCase thay vì gọi thẳng Repository? → UseCase là nơi bạn thêm **logic xử lý** (lọc, sắp xếp, validate, ...) mà không ảnh hưởng đến UI hay Data.

---

## 6. Lớp Data — Nguồn Dữ Liệu

### 6.1. MockDataSource

Hiện tại, dữ liệu là **dữ liệu giả (mock)** được tạo trong code:

```
MockDataSource cung cấp:
├── 6 danh mục: Trending Now, Movies, TV Series, Sports, Kids & Family, News
├── Mỗi danh mục có 8–16 MediaItem
├── 24 Channel item
└── Tất cả đều dùng 1 stream URL thật để phát video:
    https://cdn-demo-sigma-livestreaming.sigma.video/.../master.m3u8
```

Thumbnail sử dụng dịch vụ `picsum.photos` — cung cấp ảnh ngẫu nhiên miễn phí.

### 6.2. MediaRepositoryImpl

Cài đặt interface `MediaRepository`, gọi `MockDataSource` và thêm delay giả lập mạng:

```kotlin
override suspend fun getCategories(): List<MediaCategory> = withContext(Dispatchers.IO) {
    delay(500)  // Giả lập thời gian chờ mạng 500ms
    mockDataSource.getCategories()
}
```

> **`Dispatchers.IO`** → chạy code này trên luồng nền (background thread), không ảnh hưởng giao diện.

---

## 7. Lớp Presentation — Giao Diện Người Dùng

### 7.1. Mô hình MVVM

```
Fragment (View)  ←──────────  ViewModel
     │                            │
     │  observe state             │  xử lý logic
     │  (collect Flow)            │  gọi UseCase
     │                            │
     └───── hiển thị UI ──────────┘
```

- **Fragment**: Chỉ biết HIỂN THỊ dữ liệu và bắt sự kiện người dùng (click, scroll)
- **ViewModel**: Chứa logic, gọi UseCase, giữ trạng thái UI, **tồn tại khi xoay màn hình**

### 7.2. UiState — Trạng thái giao diện

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()         // Đang tải dữ liệu
    data class Success<T>(val data: T) : UiState<T>() // Tải thành công, có dữ liệu
    data class Error(val message: String) : UiState<Nothing>() // Có lỗi
}
```

Mỗi màn hình luôn ở 1 trong 3 trạng thái này. Fragment lắng nghe và phản ứng:

```kotlin
when (state) {
    is UiState.Loading  → Hiện loading spinner
    is UiState.Success  → Ẩn loading, hiển thị danh sách
    is UiState.Error    → Ẩn loading, hiện Toast thông báo lỗi
}
```

### 7.3. StateFlow và Coroutine

ViewModel dùng **`StateFlow`** để phát dữ liệu:

```kotlin
// Trong ViewModel
private val _categoriesState = MutableStateFlow<UiState<List<MediaCategory>>>(UiState.Loading)
val categoriesState = _categoriesState.asStateFlow()  // Fragment chỉ được đọc, không ghi
```

Fragment lắng nghe bằng `collect` trong `lifecycleScope`:

```kotlin
// Trong Fragment — chỉ lắng nghe khi Fragment đang hiển thị (STARTED)
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.categoriesState.collect { state -> /* xử lý */ }
    }
}
```

> **`repeatOnLifecycle(STARTED)`** → Tự động dừng lắng nghe khi Fragment vào background (tiết kiệm tài nguyên), tự động tiếp tục khi Fragment quay lại foreground.

---

## 8. Dependency Injection (Hilt)

Hilt tự động tạo và cung cấp các đối tượng mà bạn cần, thay vì bạn phải tự tạo bằng tay.

### Luồng hoạt động của Hilt trong project:

```
1. OttApplication (@HiltAndroidApp)
   → Khởi tạo Hilt cho toàn bộ app

2. MainActivity (@AndroidEntryPoint)
   → Hilt biết đây là điểm cần inject

3. HomeFragment (@AndroidEntryPoint)
   → Hilt tự cung cấp HomeViewModel

4. HomeViewModel (@HiltViewModel)
   → Hilt tự cung cấp GetCategoriesUseCase

5. GetCategoriesUseCase (@Inject constructor)
   → Hilt tự cung cấp MediaRepository (interface)

6. RepositoryModule (@Module)
   → Khai báo: khi cần MediaRepository → dùng MediaRepositoryImpl

7. MediaRepositoryImpl (@Inject constructor)
   → Hilt tự cung cấp MockDataSource

8. MockDataSource (@Singleton, @Inject constructor)
   → Chỉ tạo 1 lần duy nhất trong suốt vòng đời app
```

---

## 9. Luồng Điều Hướng (Navigation)

Toàn bộ điều hướng được định nghĩa trong `nav_graph.xml`:

```
        [HomeFragment] ──── click category title ──→ [ListingGridFragment]
              │                                               │
              │ click item                                    │ click item
              ↓                                               ↓
        [PlayerFragment] ←──────────────────────────────────┘
              ↑
              │ click item
        [ChannelFragment]
```

### Bottom Tab Navigation:
- `MainActivity` dùng `BottomNavigationView.setupWithNavController()` để đồng bộ tab với NavController
- Khi điều hướng đến `PlayerFragment` hoặc `ListingGridFragment` → BottomNav **ẩn đi**
- Khi quay lại HomeFragment hoặc ChannelFragment → BottomNav **hiện lại**

### Safe Args — Truyền dữ liệu giữa màn hình:

```kotlin
// HomeFragment gửi dữ liệu sang PlayerFragment
val action = HomeFragmentDirections.actionHomeFragmentToPlayerFragment(
    streamUrl = item.streamUrl,
    itemTitle = item.title
)
findNavController().navigate(action)

// PlayerFragment nhận dữ liệu
val args: PlayerFragmentArgs by navArgs()
val streamUrl = args.streamUrl
val title = args.itemTitle
```

---

## 10. Luồng Dữ Liệu Từ A → Z

Đây là hành trình đầy đủ từ khi ứng dụng khởi động đến khi người dùng xem video:

```
BƯỚC 1: App khởi động
────────────────────────────────────────────────────────
OttApplication.onCreate()
  └─ @HiltAndroidApp → Hilt khởi tạo toàn bộ dependency graph

BƯỚC 2: MainActivity tạo
────────────────────────────────────────────────────────
MainActivity.onCreate()
  ├─ inflate layout activity_main.xml
  ├─ NavHostFragment tải nav_graph.xml
  │    └─ Fragment mặc định: HomeFragment
  └─ BottomNavigationView gắn với NavController

BƯỚC 3: HomeFragment khởi tạo
────────────────────────────────────────────────────────
HomeFragment.onCreateView()
  └─ View Binding inflate fragment_home.xml (DrawerLayout)

HomeFragment.onViewCreated()
  ├─ setupRecyclerViews() → gắn CategoryAdapter vào rv_categories
  ├─ setupListeners()     → gắn click listener cho btnMenu
  └─ observeState()       → bắt đầu lắng nghe HomeViewModel

BƯỚC 4: HomeViewModel tải dữ liệu
────────────────────────────────────────────────────────
HomeViewModel.init {
  loadCategories()
    ├─ _categoriesState.value = UiState.Loading
    │    → Fragment nhận: hiện loading overlay
    │
    └─ launch coroutine:
         GetCategoriesUseCase.invoke()
           └─ MediaRepository.getCategories()
                └─ MediaRepositoryImpl.getCategories()
                     ├─ Dispatchers.IO (chạy trên background thread)
                     ├─ delay(500ms) (giả lập mạng)
                     └─ MockDataSource.getCategories()
                          └─ trả về List<MediaCategory> (6 danh mục)

BƯỚC 5: Kết quả trả về UI
────────────────────────────────────────────────────────
_categoriesState.value = UiState.Success(categories)
  └─ Fragment nhận:
       ├─ GlobalLoading.hide() → ẩn loading
       ├─ mainCategoryAdapter.submitList(categories) → cập nhật RecyclerView chính
       └─ drawerCategoryAdapter.submitList(categories) → cập nhật RecyclerView trong Drawer

BƯỚC 6: Người dùng nhấn vào một item video
────────────────────────────────────────────────────────
HorizontalItemAdapter.onItemClick(item)
  └─ HomeFragment.navigateToPlayer(item)
       └─ findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToPlayerFragment(
              streamUrl = item.streamUrl,
              itemTitle = item.title
            )
          )

BƯỚC 7: PlayerFragment khởi động
────────────────────────────────────────────────────────
PlayerFragment.onViewCreated()
  ├─ setupUI() → hiển thị tên video, gắn nút Back
  └─ initPlayer()
       ├─ val streamUrl = args.streamUrl
       ├─ viewModel.preparePlayer(streamUrl)
       │    └─ PlayerViewModel:
       │         ExoPlayer.Builder(context).build()
       │           └─ setMediaItem(MediaItem.fromUri(streamUrl))
       │           └─ prepare() → bắt đầu buffer video
       │           └─ playWhenReady = true → tự phát khi sẵn sàng
       └─ binding.playerView.player = viewModel.player
```

---

## 11. Màn Hình Home — Phân Tích Chi Tiết

### Layout: `fragment_home.xml`

```xml
DrawerLayout          ← Container chứa cả nội dung chính lẫn drawer
  ├── ConstraintLayout    ← Nội dung chính (màn hình Home)
  │   ├── Toolbar         ← Header trên cùng
  │   │   └── ImageButton (btnMenu) ← Nút hamburger ☰
  │   └── RecyclerView (rv_categories) ← Danh sách dọc các category row
  │
  └── NavigationView      ← Ngăn kéo (drawer) bên trái
      └── RecyclerView (rv_drawer_categories) ← Nội dung trong drawer
```

### Adapter: `CategoryAdapter`

Mỗi item trong `rv_categories` là một hàng danh mục, layout `item_category_row.xml`:

```xml
LinearLayout (vertical)
  ├── LinearLayout (horizontal)   ← Header của row
  │   ├── TextView (tv_category_title) ← Tên danh mục, click → ListingGrid
  │   └── TextView (tv_see_all)        ← "See All", click → ListingGrid
  └── RecyclerView (rv_items)     ← Danh sách NGANG các item
```

### Adapter: `HorizontalItemAdapter`

- Items layout: `item_rectangle.xml` (162×70dp)
- Cuộn theo chiều ngang
- Tối đa **16 item** mỗi hàng (`category.items.take(16)`)

### Drawer (Ngăn kéo):

```kotlin
binding.btnMenu.setOnClickListener {
    if (drawer.isDrawerOpen(GravityCompat.START))
        drawer.closeDrawer(GravityCompat.START)
    else
        drawer.openDrawer(GravityCompat.START)
}
```

---

## 12. Màn Hình Channel

### Layout: `fragment_channel.xml` → `RecyclerView` với `GridLayoutManager(spanCount = 4)`

```
Item size = (Chiều rộng màn hình - tổng khoảng cách) / 4
Khoảng cách giữa items = 8dp (GridSpacingItemDecoration)
Chiều cao = Chiều rộng item (hình vuông)
```

### `GridSpacingItemDecoration`

Class này tính toán `left`, `right`, `top`, `bottom` margin cho mỗi item trong lưới để tạo khoảng cách đều nhau, kể cả ở cạnh ngoài cùng.

### `ChannelGridAdapter`

```kotlin
// Sau khi view được draw, đặt chiều cao = chiều rộng để tạo hình vuông
binding.root.post {
    val width = binding.root.width
    val params = binding.root.layoutParams
    params.height = width
    binding.root.layoutParams = params
}
```

---

## 13. Màn Hình ListingGrid

Nhận tham số từ màn hình trước qua **Safe Args**:
- `categoryId`: ID của danh mục cần lấy items
- `categoryTitle`: Tiêu đề hiển thị trên toolbar

### Layout: 2 cột, khoảng cách 8dp, item hình chữ nhật

```
Tỷ lệ item = 162:70 (rộng:cao) → tỷ lệ ~2.3:1
Chiều cao item = width * 70/162 + phần text
```

### Luồng dữ liệu:

```
onViewCreated()
  └─ viewModel.loadItems(args.categoryId)
       └─ GetListingItemsUseCase(categoryId)
            └─ MockDataSource.getListingItems(categoryId)
                 └─ generateItems(categoryId, count=20)
                      └─ 20 MediaItem với thumbnail và streamUrl
```

---

## 14. Màn Hình Player

### PlayerViewModel — Quản lý ExoPlayer

```kotlin
class PlayerViewModel(context: Context) : ViewModel() {
    var player: ExoPlayer? = null

    fun preparePlayer(streamUrl: String) {
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(streamUrl))
            prepare()          // Bắt đầu buffer
            playWhenReady = true  // Tự phát khi sẵn sàng
        }
    }

    override fun onCleared() {
        releasePlayer()  // ← GỌI LUÔN khi ViewModel bị destroy để tránh memory leak!
    }
}
```

### Vòng đời Player:

| Sự kiện | PlayerFragment làm gì |
|---|---|
| `onViewCreated` | `preparePlayer()` → tạo ExoPlayer, bắt đầu load |
| `onStart` | `resumePlayer()` → tiếp tục phát |
| `onStop` | `pausePlayer()` → tạm dừng |
| `onDestroyView` | `playerView.player = null` → gỡ player khỏi view |
| ViewModel `onCleared` | `releasePlayer()` → giải phóng bộ nhớ |

### Listener theo dõi trạng thái buffer:

```kotlin
Player.STATE_BUFFERING → Hiện ProgressBar (đang tải)
Player.STATE_READY     → Ẩn ProgressBar (sẵn sàng phát)
Player.STATE_ENDED     → Ẩn ProgressBar (đã xem xong)
```

### Stream URL mặc định:
```
https://cdn-demo-sigma-livestreaming.sigma.video/data/vod/sigma-vod/
168b85fe-3184-41e6-a85b-f491c302a92e/hls-BM/master.m3u8
```
Đây là định dạng **HLS (HTTP Live Streaming)** — file `.m3u8` là playlist chứa danh sách các đoạn video nhỏ được phát tuần tự.

---

## 15. Các Component Dùng Chung (Global)

### GlobalLoading

```kotlin
GlobalLoading.show(activity)  // Hiện lớp phủ mờ + ProgressBar ở giữa màn hình
GlobalLoading.hide(activity)  // Ẩn đi
```

Hoạt động bằng cách tìm `View` có ID `R.id.loading_overlay` trong `activity_main.xml` và thay đổi `visibility`.

### GlobalDialog

```kotlin
GlobalDialog.show(
    context = requireContext(),
    title = "Thông báo",
    message = "Bạn có chắc muốn thoát?",
    positiveText = "Có",
    negativeText = "Không",
    onPositive = { /* xử lý */ },
    onNegative = { /* xử lý */ }
)
```

Hiển thị dialog với style OTT tối (`R.style.OttDialog`).

### GlobalToast

```kotlin
GlobalToast.show(context, "Thông báo thường")
GlobalToast.showLong(context, "Thông báo dài")
GlobalToast.showError(context, "Có lỗi xảy ra")  // Thêm ký tự ⚠️ phía trước
```

---

## 16. Vòng Đời Fragment Và Quản Lý Bộ Nhớ

Đây là phần **cực kỳ quan trọng** để tránh crash và memory leak:

```
Fragment Lifecycle:
onAttach → onCreate → onCreateView → onViewCreated → onStart → onResume
                                                                   ↓ (người dùng rời đi)
                                                               onPause → onStop → onDestroyView → onDestroy → onDetach
```

### ViewBinding Pattern — tránh memory leak:

```kotlin
// Khai báo
private var _binding: FragmentHomeBinding? = null
private val binding get() = _binding!!  // Ném exception ngay nếu dùng sau khi destroy

override fun onCreateView(...): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null  // ← BẮT BUỘC: giải phóng tham chiếu đến View
                     // Nếu không, Fragment giữ View sau khi destroy → Memory Leak!
}
```

### Tại sao ViewModel sống lâu hơn Fragment?

```
Xoay màn hình:
Fragment.onDestroyView() → Fragment.onDestroy() → Fragment.onCreate() → Fragment.onCreateView()
ViewModel: VẪN TỒN TẠI → VẪN TỒN TẠI → VẪN TỒN TẠI → Fragment lấy lại ViewModel cũ
```

Nhờ đó, dữ liệu không bị mất khi xoay màn hình.

---

## 17. Sơ Đồ Kiến Trúc Tổng Quát

```
┌─────────────────────────────────────────────────────────────────────┐
│                        ANDROID APP                                   │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                    PRESENTATION LAYER                        │    │
│  │                                                               │    │
│  │  MainActivity (Single Activity)                               │    │
│  │    └── NavHostFragment                                        │    │
│  │         ├── HomeFragment ──────── HomeViewModel               │    │
│  │         │     ├── CategoryAdapter    └── GetCategoriesUseCase │    │
│  │         │     └── HorizontalItemAdapter                       │    │
│  │         ├── ChannelFragment ─── ChannelViewModel              │    │
│  │         │     └── ChannelGridAdapter  └── GetChannelsUseCase  │    │
│  │         ├── ListingGridFragment ─ ListingGridViewModel         │    │
│  │         │     └── ListingGridAdapter  └── GetListingItemsUC   │    │
│  │         └── PlayerFragment ───── PlayerViewModel              │    │
│  │               └── PlayerView (ExoPlayer)                      │    │
│  │                                                               │    │
│  │  Global: GlobalDialog | GlobalToast | GlobalLoading           │    │
│  └───────────────────────────┬───────────────────────────────────┘    │
│                              │ UseCase gọi Repository                 │
│  ┌───────────────────────────▼───────────────────────────────────┐    │
│  │                     DOMAIN LAYER                              │    │
│  │                                                               │    │
│  │  MediaRepository (interface)                                  │    │
│  │  GetCategoriesUseCase                                         │    │
│  │  GetChannelsUseCase                                           │    │
│  │  GetListingItemsUseCase                                       │    │
│  │  Models: MediaItem, MediaCategory, MediaType                  │    │
│  └───────────────────────────┬───────────────────────────────────┘    │
│                              │ implement                              │
│  ┌───────────────────────────▼───────────────────────────────────┐    │
│  │                      DATA LAYER                               │    │
│  │                                                               │    │
│  │  MediaRepositoryImpl                                          │    │
│  │    └── MockDataSource (6 categories, 24 channels)             │    │
│  │                                                               │    │
│  │  [Tương lai: RemoteDataSource → Retrofit API]                 │    │
│  └───────────────────────────────────────────────────────────────┘    │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Tóm Tắt Nhanh Cho Người Mới

| Khái niệm | Đơn giản hóa |
|---|---|
| **Fragment** | Một "trang" giao diện, nhiều Fragment trong 1 Activity |
| **ViewModel** | Người giữ dữ liệu và logic, sống sót qua xoay màn hình |
| **Repository** | "Kho hàng" — biết dữ liệu lấy từ đâu (mock/API/DB) |
| **UseCase** | 1 hành động cụ thể: "lấy danh mục", "lấy kênh", ... |
| **StateFlow** | Cái loa phát thanh — ViewModel phát, Fragment lắng nghe |
| **UiState** | Trạng thái hiện tại của màn hình: Loading / Success / Error |
| **Hilt** | Robot phục vụ — tự mang đồ dùng đến cho bạn, bạn không cần tự tạo |
| **Safe Args** | Cách an toàn để gửi dữ liệu giữa các trang (Fragment) |
| **ExoPlayer** | Trình phát video tích hợp sẵn, hỗ trợ HLS, DASH, ... |
| **ViewBinding** | Cách truy cập các view trong XML mà không cần `findViewById` |

---

*Tài liệu được tạo ngày 05/03/2026*
