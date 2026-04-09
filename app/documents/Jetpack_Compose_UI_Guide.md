# Hướng dẫn xây dựng giao diện Android với Jetpack Compose

## Mục lục

1. [Jetpack Compose là gì và tại sao nên dùng?](#1-jetpack-compose-là-gì-và-tại-sao-nên-dùng)
2. [Cài đặt và cấu hình dự án](#2-cài-đặt-và-cấu-hình-dự-án)
3. [Composable Function - nền tảng của Compose](#3-composable-function---nền-tảng-của-compose)
4. [Text, Image và Icon](#4-text-image-và-icon)
5. [Layout cơ bản: Column, Row, Box](#5-layout-cơ-bản-column-row-box)
6. [Modifier - công cụ tùy chỉnh giao diện](#6-modifier---công-cụ-tùy-chỉnh-giao-diện)
7. [State và Recomposition](#7-state-và-recomposition)
8. [Button, TextField và tương tác người dùng](#8-button-textfield-và-tương-tác-người-dùng)
9. [Danh sách: LazyColumn và LazyRow](#9-danh-sách-lazycolumn-và-lazyrow)
10. [Material Design 3 và Theming](#10-material-design-3-và-theming)
11. [Navigation trong Compose](#11-navigation-trong-compose)
12. [Animation](#12-animation)
13. [Side Effects: LaunchedEffect, rememberCoroutineScope](#13-side-effects-launchedeffect-remembercoroutinescope)
14. [Tích hợp ViewModel](#14-tích-hợp-viewmodel)
15. [Case study: Xây dựng màn hình ứng dụng OTT](#15-case-study-xây-dựng-màn-hình-ứng-dụng-ott)
16. [Best practices và lỗi thường gặp](#16-best-practices-và-lỗi-thường-gặp)
17. [Lộ trình học 8 tuần](#17-lộ-trình-học-8-tuần)
18. [Tổng kết](#18-tổng-kết)

---

## 1. Jetpack Compose là gì và tại sao nên dùng?

### Jetpack Compose là gì?

Jetpack Compose là **bộ công cụ UI hiện đại của Google** dành cho Android, được ra mắt chính thức vào năm 2021. Thay vì viết XML layout như cách truyền thống, Compose cho phép bạn **mô tả giao diện trực tiếp bằng code Kotlin**.

Đây là sự chuyển dịch lớn: từ cách tiếp cận **imperative** (mệnh lệnh - "làm gì, khi nào") sang **declarative** (khai báo - "trông như thế nào").

### So sánh: XML truyền thống vs Jetpack Compose

**Cách cũ - XML Layout:**

```xml
<!-- activity_main.xml -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <TextView
        android:id="@+id/tvName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Xin chào" />

    <Button
        android:id="@+id/btnClick"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Bấm vào đây" />
</LinearLayout>
```

```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvName = findViewById<TextView>(R.id.tvName)
        val btnClick = findViewById<Button>(R.id.btnClick)

        btnClick.setOnClickListener {
            tvName.text = "Đã bấm!"
        }
    }
}
```

**Cách mới - Jetpack Compose:**

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var text by remember { mutableStateOf("Xin chào") }

            Column {
                Text(text = text)
                Button(onClick = { text = "Đã bấm!" }) {
                    Text("Bấm vào đây")
                }
            }
        }
    }
}
```

### Ưu điểm của Jetpack Compose

| Tiêu chí | XML truyền thống | Jetpack Compose |
|---|---|---|
| Ngôn ngữ | XML + Kotlin | Kotlin thuần |
| Số file | 2 file/màn hình | 1 file/màn hình |
| Preview | Chậm, cần build | Nhanh, real-time |
| Tái sử dụng | Khó | Rất dễ |
| State management | Phức tạp | Đơn giản hơn |
| Animation | Phức tạp | Đơn giản hơn |
| Testing | Khó hơn | Dễ hơn |

### Nếu bạn biết React/Vue

Compose rất giống React:

| Khái niệm | React | Jetpack Compose |
|---|---|---|
| Component | `function MyComponent()` | `@Composable fun MyComponent()` |
| State | `useState()` | `remember { mutableStateOf() }` |
| Props | `function Card({ title })` | `fun Card(title: String)` |
| Re-render | State thay đổi → re-render | State thay đổi → recompose |
| Conditional render | `{isVisible && <View />}` | `if (isVisible) { View() }` |
| List render | `items.map(item => <Item />)` | `items.forEach { Item(it) }` |
| useEffect | `useEffect(() => {}, [dep])` | `LaunchedEffect(dep) { }` |

---

## 2. Cài đặt và cấu hình dự án

### Yêu cầu hệ thống

- **Android Studio** Hedgehog (2023.1.1) trở lên (khuyến nghị phiên bản mới nhất)
- **Kotlin** 1.9.0+
- **compileSdk** 34+
- **minSdk** 21+

### Tạo project mới với Compose

1. Mở Android Studio → **New Project**
2. Chọn template **Empty Activity** (đây là template Compose mặc định)
3. Đặt tên project, package name
4. Language: **Kotlin**
5. Minimum SDK: **API 24** (khuyến nghị)
6. Nhấn **Finish**

### Cấu hình `build.gradle.kts`

```kotlin
// app/build.gradle.kts
android {
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    buildFeatures {
        compose = true  // Bật Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)

    // Compose UI core
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")  // Preview

    // Material Design 3
    implementation("androidx.compose.material3:material3")

    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.2")

    // ViewModel + Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
}
```

> **Lưu ý:** Compose BOM (Bill of Materials) giúp bạn quản lý version của tất cả thư viện Compose mà không cần chỉ định từng version riêng.

---

## 3. Composable Function - nền tảng của Compose

### Composable Function là gì?

Mọi thứ trong Compose đều là **Composable Function** - hàm có annotation `@Composable`. Đây là đơn vị cơ bản nhất để xây dựng UI.

```kotlin
@Composable
fun ChaoHoi(ten: String) {
    Text(text = "Xin chào, $ten!")
}
```

Quy tắc đặt tên: **PascalCase** (giống tên class), ví dụ: `UserCard`, `ProductList`, `LoginScreen`.

### Quy tắc của Composable Function

```kotlin
// ✅ ĐÚNG - Composable thuần túy, không có side effect
@Composable
fun ProductCard(product: Product) {
    Card {
        Text(product.name)
        Text(product.price.toString())
    }
}

// ❌ SAI - Không được gọi composable từ hàm thường
fun buildUI() {
    ProductCard(product) // Lỗi biên dịch!
}

// ✅ ĐÚNG - Chỉ gọi composable từ trong composable
@Composable
fun ProductList(products: List<Product>) {
    products.forEach { product ->
        ProductCard(product) // Gọi composable trong composable: OK
    }
}
```

### Preview - xem trước giao diện ngay trong IDE

`@Preview` giúp bạn xem trước UI mà không cần chạy app:

```kotlin
@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    MyAppTheme {
        ProductCard(
            product = Product(
                id = "1",
                name = "Phim hành động",
                price = 50000,
                imageUrl = ""
            )
        )
    }
}

// Preview nhiều kích thước màn hình
@Preview(name = "Phone", device = Devices.PHONE)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
fun HomeScreenPreview() {
    MyAppTheme {
        HomeScreen()
    }
}
```

### Cấu trúc một màn hình hoàn chỉnh

```kotlin
// Màn hình đăng nhập - ví dụ hoàn chỉnh
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit  // Callback để navigate
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Đăng nhập",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                // Xử lý đăng nhập...
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Đăng nhập")
            }
        }
    }
}
```

---

## 4. Text, Image và Icon

### Text

```kotlin
// Text cơ bản
Text(text = "Xin chào Android!")

// Text với style
Text(
    text = "Tiêu đề lớn",
    style = MaterialTheme.typography.headlineLarge,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    textAlign = TextAlign.Center,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis  // "..." khi text quá dài
)

// Text với style inline (annotated string)
val annotatedText = buildAnnotatedString {
    append("Giá: ")
    withStyle(style = SpanStyle(
        color = Color.Red,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )) {
        append("50.000đ")
    }
    append(" /tháng")
}
Text(text = annotatedText)

// Typography system - Material Design 3
Text(text = "Display Large",   style = MaterialTheme.typography.displayLarge)
Text(text = "Headline Medium", style = MaterialTheme.typography.headlineMedium)
Text(text = "Title Large",     style = MaterialTheme.typography.titleLarge)
Text(text = "Body Medium",     style = MaterialTheme.typography.bodyMedium)
Text(text = "Label Small",     style = MaterialTheme.typography.labelSmall)
```

### Image

```kotlin
// Image từ resource (drawable)
Image(
    painter = painterResource(id = R.drawable.ic_launcher_foreground),
    contentDescription = "Logo ứng dụng",  // Quan trọng cho accessibility
    modifier = Modifier.size(100.dp)
)

// Image với contentScale
Image(
    painter = painterResource(R.drawable.movie_thumbnail),
    contentDescription = "Ảnh thumbnail phim",
    contentScale = ContentScale.Crop,  // Cắt ảnh cho vừa khung
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(12.dp))  // Bo góc
)

// Image từ URL với Coil (thư viện load ảnh phổ biến nhất)
// Thêm dependency: implementation("io.coil-kt:coil-compose:2.5.0")
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data("https://example.com/movie.jpg")
        .crossfade(true)  // Animation fade khi ảnh load xong
        .build(),
    contentDescription = "Ảnh phim",
    contentScale = ContentScale.Crop,
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error_image),
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)  // Tỉ lệ 16:9
        .clip(RoundedCornerShape(8.dp))
)

// Image tròn - ví dụ avatar người dùng
AsyncImage(
    model = user.avatarUrl,
    contentDescription = "Avatar của ${user.name}",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
)
```

### Icon

```kotlin
// Icon từ Material Icons
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = "Yêu thích",
    tint = Color.Red
)

// Một số icon thường dùng
Icon(Icons.Default.Home, "Trang chủ")
Icon(Icons.Default.Search, "Tìm kiếm")
Icon(Icons.Default.Person, "Tài khoản")
Icon(Icons.Default.Settings, "Cài đặt")
Icon(Icons.Default.ArrowBack, "Quay lại")
Icon(Icons.Default.Add, "Thêm mới")
Icon(Icons.Default.Delete, "Xóa")
Icon(Icons.Outlined.Favorite, "Yêu thích (outline)")
Icon(Icons.Filled.Star, "Đánh giá", tint = Color(0xFFFFD700))  // Màu vàng

// Icon từ drawable resource
Icon(
    painter = painterResource(R.drawable.ic_custom),
    contentDescription = "Icon tùy chỉnh",
    modifier = Modifier.size(24.dp)
)
```

---

## 5. Layout cơ bản: Column, Row, Box

### Column - xếp dọc

```kotlin
// Column cơ bản - giống LinearLayout vertical
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,  // Căn giữa theo chiều ngang
    verticalArrangement = Arrangement.spacedBy(8.dp)     // Khoảng cách giữa các item
) {
    Text("Item 1")
    Text("Item 2")
    Text("Item 3")
}

// Các giá trị Arrangement thường dùng
Column(verticalArrangement = Arrangement.Top)           // Mặc định, từ trên xuống
Column(verticalArrangement = Arrangement.Center)        // Căn giữa
Column(verticalArrangement = Arrangement.Bottom)        // Từ dưới lên
Column(verticalArrangement = Arrangement.SpaceEvenly)   // Khoảng cách đều nhau
Column(verticalArrangement = Arrangement.SpaceBetween)  // Đầu và cuối sát biên
Column(verticalArrangement = Arrangement.spacedBy(12.dp)) // Khoảng cách cố định

// Ví dụ thực tế: Profile Card
@Composable
fun ProfileCard(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Thành viên từ ${user.joinYear}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
```

### Row - xếp ngang

```kotlin
// Row cơ bản - giống LinearLayout horizontal
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,  // Hai đầu
    verticalAlignment = Alignment.CenterVertically     // Căn giữa dọc
) {
    Text("Bên trái")
    Text("Bên phải")
}

// Ví dụ thực tế: App Bar tùy chỉnh
@Composable
fun CustomTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, "Quay lại")
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)  // Chiếm hết không gian còn lại
        )

        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, "Tìm kiếm")
        }
    }
}

// Ví dụ: Rating stars
@Composable
fun RatingBar(rating: Float, maxRating: Int = 5) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$rating",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
```

### Box - xếp chồng (z-axis)

```kotlin
// Box - giống FrameLayout, các element chồng lên nhau
Box(
    modifier = Modifier
        .size(200.dp)
        .background(Color.LightGray)
) {
    // Layer 1: Ảnh nền
    Image(
        painter = painterResource(R.drawable.bg),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    // Layer 2: Gradient overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                )
            )
    )

    // Layer 3: Text ở góc dưới
    Text(
        text = "Tiêu đề phim",
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp)
    )

    // Layer 4: Badge "HOT" ở góc trên phải
    Badge(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
    ) {
        Text("HOT")
    }
}

// Ví dụ thực tế: Movie Thumbnail Card
@Composable
fun MovieThumbnailCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient từ trong suốt xuống đen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.6f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.8f)
                    )
                )
        )

        // Tên phim ở dưới
        Text(
            text = movie.title,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )

        // Badge thể loại ở trên
        if (movie.isNew) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "MỚI",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
```

### Spacer - tạo khoảng cách

```kotlin
Column {
    Text("Phần 1")

    Spacer(modifier = Modifier.height(16.dp))  // Khoảng cách dọc

    Text("Phần 2")
}

Row {
    Text("Trái")

    Spacer(modifier = Modifier.width(8.dp))   // Khoảng cách ngang
    Spacer(modifier = Modifier.weight(1f))    // Đẩy item về 2 phía

    Text("Phải")
}
```

---

## 6. Modifier - công cụ tùy chỉnh giao diện

### Modifier là gì?

`Modifier` là cách để thêm decoration, behavior và layout cho composable. Có thể chain (nối) nhiều modifier lại với nhau.

> **Quan trọng:** Thứ tự của modifier **ảnh hưởng đến kết quả**!

```kotlin
// Thứ tự modifier quan trọng!

// Padding trước, rồi mới background → padding không có màu
Box(
    modifier = Modifier
        .padding(16.dp)
        .background(Color.Blue)
        .size(100.dp)
)

// Background trước, rồi mới padding → padding trong vùng màu xanh
Box(
    modifier = Modifier
        .background(Color.Blue)
        .padding(16.dp)
        .size(100.dp)
)
```

### Modifier phổ biến nhất

```kotlin
// ===== KÍCH THƯỚC =====
Modifier.size(100.dp)                    // Chiều rộng và cao bằng nhau
Modifier.size(width = 200.dp, height = 100.dp)
Modifier.width(200.dp)
Modifier.height(100.dp)
Modifier.fillMaxWidth()                  // Chiều rộng tối đa (match_parent)
Modifier.fillMaxHeight()                 // Chiều cao tối đa
Modifier.fillMaxSize()                   // Cả 2 chiều tối đa
Modifier.wrapContentSize()               // Wrap content
Modifier.weight(1f)                      // Chia đều không gian (trong Row/Column)

// ===== KHOẢNG CÁCH =====
Modifier.padding(16.dp)                  // Tất cả 4 phía
Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)

// ===== HÌNH DẠNG & MÀU SẮC =====
Modifier.background(Color.Blue)
Modifier.background(
    Brush.linearGradient(listOf(Color.Red, Color.Blue))
)
Modifier.clip(RoundedCornerShape(12.dp))  // Bo góc
Modifier.clip(CircleShape)               // Hình tròn
Modifier.clip(CutCornerShape(8.dp))      // Góc cắt
Modifier.border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
Modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))

// ===== TƯƠNG TÁC =====
Modifier.clickable { /* xử lý click */ }
Modifier.clickable(
    indication = null,      // Không có ripple effect
    interactionSource = remember { MutableInteractionSource() }
) { /* xử lý click */ }
Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = { /* single tap */ },
        onDoubleTap = { /* double tap */ },
        onLongPress = { /* long press */ }
    )
}

// ===== SCROLL =====
Modifier.verticalScroll(rememberScrollState())
Modifier.horizontalScroll(rememberScrollState())

// ===== KHÁC =====
Modifier.alpha(0.5f)                     // Độ trong suốt
Modifier.rotate(45f)                     // Xoay
Modifier.scale(1.2f)                     // Phóng to/thu nhỏ
Modifier.offset(x = 10.dp, y = 5.dp)    // Dịch chuyển vị trí
Modifier.zIndex(1f)                      // Thứ tự z
```

### Ví dụ thực tế: Card đẹp với Modifier

```kotlin
@Composable
fun VideoCard(
    video: Video,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Thumbnail
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // Thông tin
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = video.channelName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${video.viewCount} lượt xem",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = video.uploadedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
```

---

## 7. State và Recomposition

### State là gì?

**State** là dữ liệu có thể thay đổi theo thời gian. Khi state thay đổi, Compose tự động vẽ lại (recompose) các composable sử dụng state đó.

```kotlin
// remember: ghi nhớ giá trị qua các lần recompose
// mutableStateOf: tạo state có thể quan sát

@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Số đếm: $count",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(onClick = { count++ }) {
            Text("Tăng")
        }
    }
}
```

### rememberSaveable - giữ state khi xoay màn hình

```kotlin
// mutableStateOf bị mất khi xoay màn hình
var count by remember { mutableStateOf(0) }  // ❌ Mất khi xoay

// rememberSaveable giữ state khi xoay màn hình
var count by rememberSaveable { mutableStateOf(0) }  // ✅ Giữ nguyên
```

### State Hoisting - nâng state lên

**State Hoisting** là pattern quan trọng nhất trong Compose: di chuyển state lên composable cha để tái sử dụng và test dễ hơn.

```kotlin
// ❌ Stateful - khó test và tái sử dụng
@Composable
fun SearchBarStateful() {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        label = { Text("Tìm kiếm") }
    )
}

// ✅ Stateless - dễ test, tái sử dụng, và preview
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Tìm kiếm") },
        leadingIcon = { Icon(Icons.Default.Search, "Tìm kiếm") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Xóa")
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

// Parent quản lý state
@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        // Dùng searchQuery để filter danh sách...
    }
}
```

### Các loại State thông dụng

```kotlin
// 1. State cơ bản
var text by remember { mutableStateOf("") }
var count by remember { mutableStateOf(0) }
var isVisible by remember { mutableStateOf(true) }

// 2. StateFlow từ ViewModel (xem phần 14)
val uiState by viewModel.uiState.collectAsState()

// 3. List state
var items by remember { mutableStateOf(listOf("A", "B", "C")) }
// Thêm item:
items = items + "D"
// Xóa item:
items = items.filter { it != "B" }

// 4. LazyList state - kiểm soát scroll position
val listState = rememberLazyListState()
LazyColumn(state = listState) { ... }
// Scroll đến item đầu tiên
LaunchedEffect(Unit) {
    listState.animateScrollToItem(0)
}

// 5. Scroll state
val scrollState = rememberScrollState()
Column(modifier = Modifier.verticalScroll(scrollState)) { ... }
// Scroll đến đầu trang
LaunchedEffect(Unit) {
    scrollState.animateScrollTo(0)
}
```

### Ví dụ thực tế: Form nhập liệu

```kotlin
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val birthYear: String = ""
)

@Composable
fun EditProfileForm(
    onSave: (UserProfile) -> Unit
) {
    var profile by remember { mutableStateOf(UserProfile()) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val isFormValid = profile.name.isNotBlank() && profile.email.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = profile.name,
            onValueChange = {
                profile = profile.copy(name = it)
                nameError = if (it.isBlank()) "Tên không được để trống" else null
            },
            label = { Text("Họ và tên") },
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = profile.email,
            onValueChange = {
                profile = profile.copy(email = it)
                emailError = if (!it.contains("@")) "Email không hợp lệ" else null
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = profile.phone,
            onValueChange = { profile = profile.copy(phone = it) },
            label = { Text("Số điện thoại") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSave(profile) },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu thay đổi")
        }
    }
}
```

---

## 8. Button, TextField và tương tác người dùng

### Button

```kotlin
// Button chuẩn Material 3
Button(
    onClick = { /* xử lý */ },
    enabled = true,
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
    ),
    shape = RoundedCornerShape(8.dp),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
) {
    Icon(Icons.Default.PlayArrow, contentDescription = null)
    Spacer(modifier = Modifier.width(8.dp))
    Text("Xem ngay")
}

// OutlinedButton - viền, không nền
OutlinedButton(onClick = { }) {
    Text("Hủy")
}

// TextButton - chỉ text
TextButton(onClick = { }) {
    Text("Bỏ qua")
}

// ElevatedButton - có shadow
ElevatedButton(onClick = { }) {
    Text("Thêm vào danh sách")
}

// FilledTonalButton - màu nhạt hơn
FilledTonalButton(onClick = { }) {
    Text("Chia sẻ")
}

// IconButton
IconButton(onClick = { }) {
    Icon(Icons.Default.Favorite, "Yêu thích")
}

// FloatingActionButton
FloatingActionButton(onClick = { }) {
    Icon(Icons.Default.Add, "Thêm mới")
}

// Extended FAB
ExtendedFloatingActionButton(
    onClick = { },
    icon = { Icon(Icons.Default.Add, null) },
    text = { Text("Thêm video") }
)
```

### TextField

```kotlin
// OutlinedTextField - khuyến nghị dùng
var text by remember { mutableStateOf("") }

OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Tiêu đề") },
    placeholder = { Text("Nhập tiêu đề...") },
    leadingIcon = { Icon(Icons.Default.Title, null) },
    trailingIcon = {
        if (text.isNotEmpty()) {
            IconButton(onClick = { text = "" }) {
                Icon(Icons.Default.Clear, "Xóa")
            }
        }
    },
    singleLine = true,
    maxLines = 1,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next  // Nút "Tiếp theo" trên bàn phím
    ),
    keyboardActions = KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Down) }
    ),
    modifier = Modifier.fillMaxWidth()
)

// TextField mật khẩu
var password by remember { mutableStateOf("") }
var passwordVisible by remember { mutableStateOf(false) }

OutlinedTextField(
    value = password,
    onValueChange = { password = it },
    label = { Text("Mật khẩu") },
    visualTransformation = if (passwordVisible)
        VisualTransformation.None
    else
        PasswordVisualTransformation(),
    trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
                imageVector = if (passwordVisible)
                    Icons.Default.VisibilityOff
                else
                    Icons.Default.Visibility,
                contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
            )
        }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    modifier = Modifier.fillMaxWidth()
)
```

### Checkbox, Switch, RadioButton

```kotlin
// Checkbox
var isChecked by remember { mutableStateOf(false) }
Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.clickable { isChecked = !isChecked }
) {
    Checkbox(
        checked = isChecked,
        onCheckedChange = { isChecked = it }
    )
    Text("Ghi nhớ đăng nhập")
}

// Switch
var isEnabled by remember { mutableStateOf(true) }
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text("Thông báo")
    Switch(
        checked = isEnabled,
        onCheckedChange = { isEnabled = it }
    )
}

// RadioButton
val options = listOf("720p", "1080p", "4K")
var selectedOption by remember { mutableStateOf("1080p") }

Column {
    options.forEach { option ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedOption = option }
                .padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = selectedOption == option,
                onClick = { selectedOption = option }
            )
            Text(option)
        }
    }
}
```

### Slider

```kotlin
var sliderValue by remember { mutableStateOf(0.5f) }

Column {
    Text("Âm lượng: ${(sliderValue * 100).toInt()}%")
    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        valueRange = 0f..1f,
        steps = 9  // 10 bước
    )
}
```

---

## 9. Danh sách: LazyColumn và LazyRow

### LazyColumn - danh sách dọc

`LazyColumn` tương đương `RecyclerView` - chỉ render các item đang hiển thị trên màn hình.

```kotlin
// LazyColumn cơ bản
@Composable
fun MovieList(movies: List<Movie>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),    // Padding toàn bộ list
        verticalArrangement = Arrangement.spacedBy(12.dp)  // Khoảng cách giữa item
    ) {
        items(
            items = movies,
            key = { it.id }  // Key giúp Compose optimize animation
        ) { movie ->
            MovieCard(movie = movie)
        }
    }
}

// LazyColumn với header và footer
LazyColumn {
    // Header
    item {
        Text(
            text = "Phim nổi bật",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
    }

    // Danh sách items
    items(movies) { movie ->
        MovieCard(movie)
    }

    // Footer - loading indicator
    item {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// LazyColumn với sticky headers
LazyColumn {
    moviesByGenre.forEach { (genre, movies) ->
        stickyHeader {
            // Header dính khi scroll
            Text(
                text = genre,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(movies) { movie ->
            MovieCard(movie)
        }
    }
}
```

### LazyRow - danh sách ngang

```kotlin
@Composable
fun HorizontalMovieList(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Column {
        // Tiêu đề section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* Xem tất cả */ }) {
                Text("Xem tất cả")
                Icon(Icons.Default.ArrowForward, null)
            }
        }

        // Danh sách ngang
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieThumbnailCard(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }
    }
}
```

### LazyVerticalGrid - lưới

```kotlin
// Grid 2 cột
LazyVerticalGrid(
    columns = GridCells.Fixed(2),  // 2 cột cố định
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(movies) { movie ->
        MovieGridCard(movie)
    }
}

// Grid adaptive - tự điều chỉnh số cột theo kích thước màn hình
LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 160.dp)  // Mỗi cột tối thiểu 160dp
) {
    items(movies) { movie ->
        MovieGridCard(movie)
    }
}
```

### Infinite scroll - load thêm khi cuộn đến cuối

```kotlin
@Composable
fun InfiniteMovieList(
    movies: List<Movie>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // Phát hiện khi cuộn gần đến cuối
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }

    LazyColumn(state = listState) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(movie)
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
```

---

## 10. Material Design 3 và Theming

### Thiết lập Theme

```kotlin
// ui/theme/Color.kt
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

// Màu tùy chỉnh cho app OTT
val OttPrimary = Color(0xFF1A1A2E)        // Xanh đen tối
val OttSecondary = Color(0xFF16213E)
val OttAccent = Color(0xFFE94560)          // Đỏ nổi bật
val OttSurface = Color(0xFF0F3460)
```

```kotlin
// ui/theme/Theme.kt
private val DarkColorScheme = darkColorScheme(
    primary = OttAccent,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = OttPrimary,
    surface = OttSecondary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE)
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // Material You - Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

```kotlin
// ui/theme/Type.kt
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    // ... thêm các style khác
)
```

### Sử dụng theme trong composable

```kotlin
@Composable
fun ThemedCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = "Card được theme",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// Sử dụng màu sắc từ theme
val primary = MaterialTheme.colorScheme.primary
val onPrimary = MaterialTheme.colorScheme.onPrimary
val background = MaterialTheme.colorScheme.background
val error = MaterialTheme.colorScheme.error
```

### Material 3 Components

```kotlin
// TopAppBar
TopAppBar(
    title = { Text("Trang chủ") },
    navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, "Quay lại")
        }
    },
    actions = {
        IconButton(onClick = { }) {
            Icon(Icons.Default.Search, "Tìm kiếm")
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, "Thêm")
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
)

// BottomNavigationBar
val items = listOf(
    BottomNavItem("home", "Trang chủ", Icons.Default.Home),
    BottomNavItem("search", "Tìm kiếm", Icons.Default.Search),
    BottomNavItem("saved", "Đã lưu", Icons.Default.Bookmark),
    BottomNavItem("profile", "Tài khoản", Icons.Default.Person)
)

NavigationBar {
    val currentRoute = navController.currentDestination?.route
    items.forEach { item ->
        NavigationBarItem(
            selected = currentRoute == item.route,
            onClick = {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(item.icon, item.label) },
            label = { Text(item.label) }
        )
    }
}

// Snackbar
val snackbarHostState = remember { SnackbarHostState() }
val coroutineScope = rememberCoroutineScope()

Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) }
) { paddingValues ->
    Button(
        onClick = {
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Đã thêm vào yêu thích",
                    actionLabel = "Hoàn tác",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    // Xử lý hoàn tác
                }
            }
        },
        modifier = Modifier.padding(paddingValues)
    ) {
        Text("Thêm vào yêu thích")
    }
}

// Dialog
var showDialog by remember { mutableStateOf(false) }

if (showDialog) {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("Xác nhận xóa") },
        text = { Text("Bạn có chắc muốn xóa video này?") },
        confirmButton = {
            TextButton(
                onClick = {
                    showDialog = false
                    // Xử lý xóa
                }
            ) {
                Text("Xóa", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog = false }) {
                Text("Hủy")
            }
        }
    )
}
```

---

## 11. Navigation trong Compose

### Cài đặt Navigation

```kotlin
// build.gradle.kts
implementation("androidx.navigation:navigation-compose:2.7.6")
```

### Thiết lập NavGraph

```kotlin
// navigation/AppNavGraph.kt

// Định nghĩa routes dưới dạng sealed class
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: String) = "movie_detail/$movieId"
    }
    object Profile : Screen("profile")
    object Login : Screen("login")
    object VideoPlayer : Screen("video_player/{videoUrl}") {
        fun createRoute(videoUrl: String) = "video_player/${Uri.encode(videoUrl)}"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMovieClick = { movie ->
                    navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen()
        }

        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            MovieDetailScreen(
                movieId = movieId,
                onBackClick = { navController.popBackStack() },
                onPlayClick = { videoUrl ->
                    navController.navigate(Screen.VideoPlayer.createRoute(videoUrl))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        // Xóa back stack khi logout
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
```

### Scaffold với BottomNavigation

```kotlin
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Ẩn bottom bar ở một số màn hình
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {
        val items = listOf(
            Triple(Screen.Home.route, "Trang chủ", Icons.Default.Home),
            Triple(Screen.Search.route, "Tìm kiếm", Icons.Default.Search),
            Triple(Screen.Profile.route, "Tài khoản", Icons.Default.Person)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, label) },
                label = { Text(label) }
            )
        }
    }
}
```

---

## 12. Animation

### animate*AsState - animation đơn giản nhất

```kotlin
// Animation giá trị đơn
var isExpanded by remember { mutableStateOf(false) }

// Animate kích thước
val height by animateDpAsState(
    targetValue = if (isExpanded) 200.dp else 60.dp,
    animationSpec = tween(durationMillis = 300),  // 300ms
    label = "height"
)

// Animate màu sắc
val backgroundColor by animateColorAsState(
    targetValue = if (isExpanded) Color.Blue else Color.Gray,
    label = "bgColor"
)

// Animate độ trong suốt
val alpha by animateFloatAsState(
    targetValue = if (isVisible) 1f else 0f,
    animationSpec = tween(500),
    label = "alpha"
)

Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .background(backgroundColor)
        .alpha(alpha)
        .clickable { isExpanded = !isExpanded }
)
```

### AnimatedVisibility - hiện/ẩn với animation

```kotlin
var isVisible by remember { mutableStateOf(true) }

// Hiện/ẩn với animation
AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn() + slideInVertically(),
    exit = fadeOut() + slideOutVertically()
) {
    Card {
        Text("Nội dung có animation")
    }
}

// Animation tùy chỉnh
AnimatedVisibility(
    visible = isVisible,
    enter = expandVertically(
        expandFrom = Alignment.Top,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    ) + fadeIn(),
    exit = shrinkVertically(
        shrinkTowards = Alignment.Top
    ) + fadeOut()
) {
    // Content
}
```

### Crossfade - chuyển đổi giữa các màn hình

```kotlin
var currentTab by remember { mutableStateOf("home") }

Crossfade(
    targetState = currentTab,
    animationSpec = tween(300),
    label = "tab_crossfade"
) { tab ->
    when (tab) {
        "home" -> HomeContent()
        "search" -> SearchContent()
        "profile" -> ProfileContent()
    }
}
```

### AnimatedContent - animation phức tạp hơn

```kotlin
var count by remember { mutableStateOf(0) }

AnimatedContent(
    targetState = count,
    transitionSpec = {
        if (targetState > initialState) {
            slideInVertically { -it } + fadeIn() togetherWith
            slideOutVertically { it } + fadeOut()
        } else {
            slideInVertically { it } + fadeIn() togetherWith
            slideOutVertically { -it } + fadeOut()
        }
    },
    label = "count_animation"
) { targetCount ->
    Text(
        text = "$targetCount",
        style = MaterialTheme.typography.displayLarge
    )
}

Row {
    Button(onClick = { count-- }) { Text("-") }
    Spacer(modifier = Modifier.width(16.dp))
    Button(onClick = { count++ }) { Text("+") }
}
```

### Infinite Animation - animation lặp vô hạn

```kotlin
// Loading shimmer effect
val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
val shimmerOffset by infiniteTransition.animateFloat(
    initialValue = -1f,
    targetValue = 2f,
    animationSpec = infiniteRepeatable(
        animation = tween(1200, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_offset"
)

Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .background(
            Brush.horizontalGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.6f),
                    Color.White.copy(alpha = 0.8f),
                    Color.LightGray.copy(alpha = 0.6f)
                ),
                startX = shimmerOffset * 1000f - 1000f,
                endX = shimmerOffset * 1000f
            )
        )
)

// Pulse animation cho live badge
val pulseFraction by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
        animation = tween(600),
        repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
)

Box(
    modifier = Modifier
        .size(8.dp)
        .scale(pulseFraction)
        .background(Color.Red, CircleShape)
)
```

---

## 13. Side Effects: LaunchedEffect, rememberCoroutineScope

### LaunchedEffect - chạy code khi composable hiển thị

```kotlin
// Chạy 1 lần khi composable lần đầu hiển thị
LaunchedEffect(Unit) {
    viewModel.loadMovies()
}

// Chạy lại khi movieId thay đổi
LaunchedEffect(movieId) {
    viewModel.loadMovieDetail(movieId)
}

// Chạy lại khi searchQuery thay đổi (với delay để tránh gọi API liên tục)
LaunchedEffect(searchQuery) {
    delay(300)  // Debounce 300ms
    if (searchQuery.isNotEmpty()) {
        viewModel.search(searchQuery)
    }
}
```

### rememberCoroutineScope - coroutine trong event handler

```kotlin
// Dùng khi cần coroutine trong callback (onClick, etc.)
val coroutineScope = rememberCoroutineScope()
val snackbarHostState = remember { SnackbarHostState() }

Button(
    onClick = {
        coroutineScope.launch {
            // Không thể dùng LaunchedEffect ở đây vì đây là event handler
            val result = snackbarHostState.showSnackbar(
                message = "Đã thêm vào danh sách",
                actionLabel = "Hoàn tác"
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoAdd()
            }
        }
    }
) {
    Text("Thêm")
}
```

### DisposableEffect - cleanup khi composable bị remove

```kotlin
// Đăng ký và hủy đăng ký listener
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> videoPlayer.play()
            Lifecycle.Event.ON_PAUSE -> videoPlayer.pause()
            else -> {}
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
    }
}
```

### derivedStateOf - tính toán state phụ

```kotlin
// Tránh tính toán đắt tiền mỗi lần recompose
val cartItems by remember { mutableStateOf(listOf<CartItem>()) }

// ❌ Tính toán mỗi lần recompose, dù cartItems không đổi
val total = cartItems.sumOf { it.price * it.quantity }

// ✅ Chỉ tính toán khi cartItems thay đổi
val total by remember(cartItems) {
    derivedStateOf { cartItems.sumOf { it.price * it.quantity } }
}

// ✅ Hoặc dùng derivedStateOf khi state quan sát được
val isScrolledToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex == 0 }
}
```

---

## 14. Tích hợp ViewModel

### Thiết lập ViewModel với UiState

```kotlin
// data/model/Movie.kt
data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String,
    val rating: Float,
    val genre: String,
    val isNew: Boolean = false
)

// ui/home/HomeUiState.kt
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val featuredMovies: List<Movie>,
        val trendingMovies: List<Movie>,
        val newMovies: List<Movie>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

// ui/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadHomeContent()
    }

    fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val featured = movieRepository.getFeaturedMovies()
                val trending = movieRepository.getTrendingMovies()
                val newMovies = movieRepository.getNewMovies()
                _uiState.value = HomeUiState.Success(
                    featuredMovies = featured,
                    trendingMovies = trending,
                    newMovies = newMovies
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.message ?: "Có lỗi xảy ra"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            movieRepository.toggleFavorite(movie.id)
        }
    }
}
```

### Kết nối ViewModel với Composable

```kotlin
// ui/home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::updateSearchQuery
        )

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                HomeContent(
                    state = state,
                    onMovieClick = onMovieClick,
                    onFavoriteClick = viewModel::toggleFavorite
                )
            }

            is HomeUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = viewModel::loadHomeContent
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    state: HomeUiState.Success,
    onMovieClick: (Movie) -> Unit,
    onFavoriteClick: (Movie) -> Unit
) {
    LazyColumn {
        // Featured banner
        item {
            FeaturedBanner(
                movies = state.featuredMovies,
                onMovieClick = onMovieClick
            )
        }

        // Trending section
        item {
            HorizontalMovieList(
                title = "Đang thịnh hành",
                movies = state.trendingMovies,
                onMovieClick = onMovieClick
            )
        }

        // New movies section
        item {
            HorizontalMovieList(
                title = "Mới cập nhật",
                movies = state.newMovies,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đã xảy ra lỗi",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thử lại")
        }
    }
}
```

---

## 15. Case study: Xây dựng màn hình ứng dụng OTT

Chúng ta sẽ xây dựng màn hình Home của một ứng dụng xem phim OTT (Over-The-Top) hoàn chỉnh, bao gồm:
- Banner xoay tự động
- Danh sách phim theo từng thể loại
- Bottom Navigation
- Dark theme

### Cấu trúc project

```
app/
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── components/
│   │       ├── FeaturedBanner.kt
│   │       ├── MovieCard.kt
│   │       └── CategorySection.kt
│   └── navigation/
│       └── AppNavGraph.kt
├── data/
│   ├── model/
│   │   └── Movie.kt
│   └── repository/
│       └── MovieRepository.kt
└── MainActivity.kt
```

### FeaturedBanner với auto-scroll

```kotlin
// ui/home/components/FeaturedBanner.kt
@Composable
fun FeaturedBanner(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { movies.size })

    // Auto-scroll mỗi 4 giây
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % movies.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // ViewPager ngang
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) { page ->
            BannerItem(
                movie = movies[page],
                onClick = { onMovieClick(movies[page]) }
            )
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(movies.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(if (isSelected) 24.dp else 8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isSelected) Color.White
                            else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Composable
fun BannerItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        // Ảnh nền
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay từ dưới lên
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.5f to Color.Black.copy(alpha = 0.3f),
                        1.0f to Color.Black.copy(alpha = 0.85f)
                    )
                )
        )

        // Thông tin phim ở dưới
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            // Badge thể loại
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = movie.genre.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = movie.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = movie.description,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Xem ngay", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thêm vào DS")
                }
            }
        }
    }
}
```

### CategorySection - section phim theo thể loại

```kotlin
// ui/home/components/CategorySection.kt
@Composable
fun CategorySection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column {
        // Header của section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onSeeAllClick) {
                Text(
                    "Xem tất cả",
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Danh sách ngang
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                CompactMovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }
    }
}

@Composable
fun CompactMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            // Poster
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Favorite button
            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isFavorite)
                        Icons.Filled.Favorite
                    else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = "Yêu thích",
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // NEW badge
            if (movie.isNew) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(Color(0xFFE94560), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        "MỚI",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Filled.Star,
                null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = movie.rating.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### HomeScreen - màn hình chính hoàn chỉnh

```kotlin
// ui/home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit,
    onSeeAllClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    // TopBar ẩn khi scroll xuống
    val showTopBar by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                ShimmerLoading()
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Featured Banner
                    item {
                        FeaturedBanner(
                            movies = state.featuredMovies,
                            onMovieClick = onMovieClick
                        )
                    }

                    // Category sections
                    item {
                        CategorySection(
                            title = "Đang thịnh hành",
                            movies = state.trendingMovies,
                            onMovieClick = onMovieClick,
                            onSeeAllClick = { onSeeAllClick("trending") }
                        )
                    }

                    item {
                        CategorySection(
                            title = "Phim mới nhất",
                            movies = state.newMovies,
                            onMovieClick = onMovieClick,
                            onSeeAllClick = { onSeeAllClick("new") }
                        )
                    }

                    item {
                        CategorySection(
                            title = "Phim hành động",
                            movies = state.actionMovies,
                            onMovieClick = onMovieClick,
                            onSeeAllClick = { onSeeAllClick("action") }
                        )
                    }

                    item {
                        CategorySection(
                            title = "Phim tình cảm",
                            movies = state.romanceMovies,
                            onMovieClick = onMovieClick,
                            onSeeAllClick = { onSeeAllClick("romance") }
                        )
                    }

                    // Padding cuối trang
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
            is HomeUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = viewModel::loadHomeContent
                )
            }
        }

        // Transparent TopBar phủ lên trên
        AnimatedVisibility(
            visible = showTopBar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            HomeTopBar(
                onSearchClick = { /* navigate to search */ },
                onNotificationClick = { /* navigate to notifications */ }
            )
        }
    }
}

@Composable
fun HomeTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Text(
            text = "OTT",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFE94560),
            fontWeight = FontWeight.ExtraBold
        )

        // Action buttons
        Row {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Tìm kiếm", tint = Color.White)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Default.Notifications, "Thông báo", tint = Color.White)
            }
        }
    }
}

// Shimmer loading placeholder
@Composable
fun ShimmerLoading() {
    val shimmerColors = listOf(
        Color(0xFF1A1A2E),
        Color(0xFF2A2A4E),
        Color(0xFF1A1A2E)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing)
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Column {
        // Banner placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row placeholders
        repeat(3) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(170.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(brush)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
```

---

## 16. Best practices và lỗi thường gặp

### Best Practices

#### 1. Tách composable nhỏ và có trách nhiệm đơn lẻ

```kotlin
// ❌ Composable quá lớn
@Composable
fun MovieScreen() {
    // 200 dòng code...
}

// ✅ Tách thành composable nhỏ
@Composable
fun MovieScreen() {
    Scaffold(
        topBar = { MovieTopBar() },
        bottomBar = { MovieBottomBar() }
    ) {
        MovieContent()
    }
}

@Composable
private fun MovieTopBar() { /* ... */ }

@Composable
private fun MovieContent() { /* ... */ }
```

#### 2. Luôn cung cấp Modifier parameter cho composable reusable

```kotlin
// ❌ Không có modifier - khó customize từ bên ngoài
@Composable
fun MovieCard(movie: Movie) {
    Card(modifier = Modifier.padding(8.dp)) { }
}

// ✅ Có modifier với default value
@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier  // Tham số modifier luôn đứng sau business params
) {
    Card(modifier = modifier) { }
}
```

#### 3. Dùng `key` trong LazyList để tối ưu performance

```kotlin
// ❌ Không có key - Compose không thể tối ưu recompose
LazyColumn {
    items(movies) { movie ->
        MovieCard(movie)
    }
}

// ✅ Có key - Compose biết item nào thay đổi
LazyColumn {
    items(movies, key = { it.id }) { movie ->
        MovieCard(movie)
    }
}
```

#### 4. Tránh tạo object/lambda trong composable body

```kotlin
// ❌ Tạo lambda mới mỗi lần recompose
@Composable
fun MovieList(onMovieClick: (Movie) -> Unit) {
    LazyColumn {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie) }  // Lambda mới mỗi lần recompose
            )
        }
    }
}

// ✅ Lambda ổn định hơn (stable)
@Composable
fun MovieList(onMovieClick: (Movie) -> Unit) {
    LazyColumn {
        items(movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie) }
            )
        }
    }
}
```

#### 5. Sử dụng `collectAsStateWithLifecycle` thay vì `collectAsState`

```kotlin
// ❌ Thu thập state kể cả khi app ở background
val uiState by viewModel.uiState.collectAsState()

// ✅ Tự động dừng khi app ở background - tiết kiệm tài nguyên
// Cần: implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

### Lỗi thường gặp

#### Lỗi 1: Gọi composable từ bên ngoài composable context

```kotlin
// ❌ LỖI: @Composable invocations can only happen from the context of a @Composable function
fun buildCard(): View {
    return Card { Text("Hello") }  // Lỗi!
}

// ✅ ĐÚNG
@Composable
fun MyCard() {
    Card { Text("Hello") }
}
```

#### Lỗi 2: Thay đổi state trong composition (không trong event handler)

```kotlin
// ❌ LỖI: State không được thay đổi trực tiếp trong composition
@Composable
fun BadExample(count: Int) {
    var display by remember { mutableStateOf(0) }
    display = count  // ❌ Side effect trong composition!

    Text("$display")
}

// ✅ ĐÚNG: Dùng LaunchedEffect hoặc derivedStateOf
@Composable
fun GoodExample(count: Int) {
    val display by remember(count) { derivedStateOf { count } }
    Text("$display")
}
```

#### Lỗi 3: Không dùng `remember` cho object phức tạp

```kotlin
// ❌ Tạo CoroutineScope mới mỗi lần recompose → memory leak
@Composable
fun BadExample() {
    val scope = CoroutineScope(Dispatchers.Main)  // ❌ Không dùng remember
    Button(onClick = { scope.launch { } }) { Text("Click") }
}

// ✅ ĐÚNG
@Composable
fun GoodExample() {
    val scope = rememberCoroutineScope()  // ✅ Được quản lý bởi Compose
    Button(onClick = { scope.launch { } }) { Text("Click") }
}
```

#### Lỗi 4: Quên xử lý padding từ Scaffold

```kotlin
// ❌ Content bị che bởi TopBar/BottomBar
Scaffold(
    topBar = { TopBar() }
) { _ ->  // Bỏ qua paddingValues
    LazyColumn { }
}

// ✅ ĐÚNG
Scaffold(
    topBar = { TopBar() }
) { paddingValues ->
    LazyColumn(
        contentPadding = paddingValues  // Áp dụng padding
    ) { }
}
```

#### Lỗi 5: Recomposition không cần thiết do unstable parameter

```kotlin
// ❌ List không stable → recompose mỗi lần parent recompose
@Composable
fun MovieList(movies: List<Movie>) { }

// ✅ Wrap trong ImmutableList (kotlinx.collections.immutable) để stable
@Composable
fun MovieList(movies: ImmutableList<Movie>) { }

// Hoặc dùng annotation @Stable/@Immutable cho data class
@Immutable
data class Movie(val id: String, val title: String)
```

### Performance Checklist

| Kiểm tra | Tốt | Không tốt |
|---|---|---|
| Composable function | Nhỏ, một nhiệm vụ | Quá lớn, nhiều nhiệm vụ |
| Key trong LazyList | Luôn có | Thiếu key |
| State hoisting | State ở đúng cấp | State quá cao/thấp |
| Modifier | Có default Modifier | Không có Modifier param |
| Preview | Có @Preview | Không có preview |
| Stable state | collectAsStateWithLifecycle | collectAsState |

---

## 17. Lộ trình học 8 tuần

### Tuần 1: Nền tảng Compose

**Mục tiêu:** Hiểu tư duy declarative UI

- [ ] Cài đặt Android Studio, tạo project Compose đầu tiên
- [ ] Viết 5 composable function đơn giản
- [ ] Thực hành Preview với nhiều kích thước
- [ ] Làm quen với Text, Image, Box, Column, Row

**Bài tập:** Xây dựng màn hình "Thẻ liên hệ" hiển thị avatar, tên, số điện thoại, email

---

### Tuần 2: Modifier và Layout

**Mục tiêu:** Thuần thục tùy chỉnh giao diện

- [ ] Nắm rõ thứ tự modifier
- [ ] Thực hành với Brush gradient
- [ ] Sử dụng constraint-based layouts
- [ ] Tạo các card phức tạp với Box

**Bài tập:** Clone giao diện một màn hình trong app quen thuộc (Spotify, Netflix...)

---

### Tuần 3: State Management

**Mục tiêu:** Hiểu và áp dụng state đúng cách

- [ ] remember, rememberSaveable
- [ ] State hoisting pattern
- [ ] Thực hành form validation
- [ ] derivedStateOf, snapshotFlow

**Bài tập:** Xây dựng form đăng ký với validation đầy đủ

---

### Tuần 4: Lists và Navigation

**Mục tiêu:** Xây dựng app có nhiều màn hình

- [ ] LazyColumn, LazyRow, LazyVerticalGrid
- [ ] Infinite scroll
- [ ] Navigation Compose với arguments
- [ ] Bottom Navigation + Scaffold

**Bài tập:** App danh sách sản phẩm có giỏ hàng, có thể xem chi tiết từng sản phẩm

---

### Tuần 5: Theming và Material Design 3

**Mục tiêu:** Tạo app có giao diện nhất quán và đẹp

- [ ] Thiết lập custom theme (màu, font, shape)
- [ ] Dark/Light mode
- [ ] Sử dụng đúng Material 3 components
- [ ] Typography system

**Bài tập:** Áp dụng custom theme cho app từ tuần 4

---

### Tuần 6: Animation

**Mục tiêu:** Thêm chuyển động mượt mà

- [ ] animate*AsState cơ bản
- [ ] AnimatedVisibility, AnimatedContent
- [ ] Infinite animation (shimmer, pulse)
- [ ] Shared element transition

**Bài tập:** Thêm animation vào tất cả transitions trong app

---

### Tuần 7: ViewModel và Side Effects

**Mục tiêu:** Kết nối UI với business logic

- [ ] StateFlow + collectAsStateWithLifecycle
- [ ] UiState pattern (Loading/Success/Error)
- [ ] LaunchedEffect, DisposableEffect
- [ ] Error handling và retry

**Bài tập:** Kết nối app với API thực (có thể dùng JSONPlaceholder)

---

### Tuần 8: Tổng hợp và Best Practices

**Mục tiêu:** Hoàn thiện kỹ năng

- [ ] Performance optimization
- [ ] Testing composables
- [ ] Accessibility (contentDescription, semantics)
- [ ] Code review và refactor

**Bài tập cuối khóa:** Xây dựng mini app OTT hoàn chỉnh với: danh sách phim, tìm kiếm, chi tiết phim, yêu thích, dark mode

---

## 18. Tổng kết

### Những điểm cốt lõi cần ghi nhớ

1. **Declarative UI**: Mô tả "giao diện trông như thế nào" thay vì "làm gì để thay đổi giao diện"
2. **Recomposition**: Khi state thay đổi, Compose tự động vẽ lại UI - bạn không cần gọi `invalidate()` hay `notifyDataSetChanged()`
3. **State Hoisting**: Luôn nâng state lên đúng cấp cần thiết - stateless composable dễ tái sử dụng và test hơn
4. **Modifier**: Luôn để `modifier: Modifier = Modifier` là tham số cho mọi composable dùng lại được
5. **Key trong LazyList**: Không bao giờ bỏ qua key khi render danh sách
6. **ViewModel + StateFlow**: Luôn dùng `collectAsStateWithLifecycle()` thay vì `collectAsState()`

### So sánh nhanh - Compose vs XML

| Tình huống | XML + View | Jetpack Compose |
|---|---|---|
| Hiển thị/ẩn view | `view.visibility = View.GONE` | `if (isVisible) { View() }` |
| Cập nhật text | `textView.text = "..."` | `Text(text = state.value)` |
| Click listener | `btn.setOnClickListener { }` | `Button(onClick = { })` |
| RecyclerView | Adapter + ViewHolder + XML | `LazyColumn { items(list) }` |
| Animation | `ObjectAnimator`, `TransitionManager` | `animate*AsState`, `AnimatedVisibility` |
| Custom view | Extend `View`, override `onDraw` | `@Composable` function với Canvas |
| Dark mode | Duplicate resources | `isSystemInDarkTheme()` + colorScheme |

### Tài nguyên học thêm

- **Official Documentation**: [developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose)
- **Compose Pathway**: [developer.android.com/courses/pathways/jetpack-compose](https://developer.android.com/courses/pathways/jetpack-compose)
- **Compose Samples**: [github.com/android/compose-samples](https://github.com/android/compose-samples)
- **Compose Playground**: [foso.github.io/Jetpack-Compose-Playground](https://foso.github.io/Jetpack-Compose-Playground)
- **Now in Android** (app mẫu của Google): [github.com/android/nowinandroid](https://github.com/android/nowinandroid)
- **Jetpack Compose Camp** (video tiếng Việt): Tìm trên YouTube

---

*Tài liệu này được biên soạn cho dự án AndroidMobile. Cập nhật lần cuối: 04/2026*
