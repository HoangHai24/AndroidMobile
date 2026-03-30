# Hướng dẫn học Kotlin từ đầu (Dành cho JS dev & người mới bắt đầu)

## Mục lục

1. [Kotlin là gì và tại sao nên học?](#1-kotlin-là-gì-và-tại-sao-nên-học)
2. [Cài đặt môi trường](#2-cài-đặt-môi-trường)
3. [Biến, hằng và kiểu dữ liệu](#3-biến-hằng-và-kiểu-dữ-liệu)
4. [Hàm (Function)](#4-hàm-function)
5. [Điều kiện và vòng lặp](#5-điều-kiện-và-vòng-lặp)
6. [Null safety - không còn "Cannot read property of undefined"](#6-null-safety---không-còn-cannot-read-property-of-undefined)
7. [Class và lập trình hướng đối tượng](#7-class-và-lập-trình-hướng-đối-tượng)
8. [Collections - List, Map, Set](#8-collections---list-map-set)
9. [Lambda và Higher-order Function](#9-lambda-và-higher-order-function)
10. [Xử lý bất đồng bộ: Coroutines (tương tự async/await)](#10-xử-lý-bất-đồng-bộ-coroutines-tương-tự-asyncawait)
11. [Sealed class và when expression](#11-sealed-class-và-when-expression)
12. [Extension function](#12-extension-function)
13. [Generics](#13-generics)
14. [Case study thực tế: gọi API và hiển thị dữ liệu](#14-case-study-thực-tế-gọi-api-và-hiển-thị-dữ-liệu)
15. [Lộ trình học 6 tuần](#15-lộ-trình-học-6-tuần)
16. [Best practices và lỗi thường gặp](#16-best-practices-và-lỗi-thường-gặp)
17. [Tổng kết](#17-tổng-kết)
18. [Phụ lục: So sánh nhanh JS vs Kotlin](#18-phụ-lục-so-sánh-nhanh-js-vs-kotlin)

---

## 1. Kotlin là gì và tại sao nên học?

### Kotlin là gì?

Kotlin là ngôn ngữ lập trình hiện đại được JetBrains (công ty tạo ra IntelliJ IDEA) phát triển, và là ngôn ngữ **chính thức để viết ứng dụng Android** từ năm 2017.

Kotlin chạy trên **JVM (Java Virtual Machine)** - bạn có thể hình dung JVM giống như Node.js: là môi trường để chạy code Kotlin/Java, tương tự Node.js là môi trường chạy JavaScript ở phía server.

### Tại sao Kotlin được ưa chuộng?

- **An toàn hơn**: hầu như loại bỏ lỗi `NullPointerException` (tương tự `Cannot read property of undefined` trong JS).
- **Ngắn gọn hơn**: cùng logic nhưng ít code hơn so với Java.
- **Dễ đọc hơn**: code thể hiện rõ ý định nghiệp vụ.
- **Async/await thân thiện**: hệ thống Coroutines của Kotlin rất giống `async/await` trong JavaScript.
- **Tương thích hoàn toàn với Java**: có thể dùng chung thư viện Java.

### Nếu bạn là JavaScript/TypeScript dev

Bạn sẽ thấy nhiều điểm quen thuộc:

| Khái niệm | JavaScript / TypeScript | Kotlin |
| --- | --- | --- |
| String template | `` `Hello ${name}` `` | `"Hello $name"` |
| Arrow function | `(x) => x * 2` | `{ x -> x * 2 }` |
| Array methods | `.map()`, `.filter()` | `.map {}`, `.filter {}` |
| async/await | `async function`, `await` | `suspend fun`, `coroutines` |
| Type annotation | `name: string` (TypeScript) | `name: String` |
| Destructuring | `const { id, name } = user` | `val (id, name) = user` |
| Optional chaining | `user?.address?.city` | `user?.address?.city` |
| Nullish coalescing | `value ?? "default"` | `value ?: "default"` |

---

## 2. Cài đặt môi trường

### Cách 1: IntelliJ IDEA (khuyến nghị, miễn phí bản Community)

1. Tải IntelliJ IDEA tại [jetbrains.com/idea](https://www.jetbrains.com/idea/download).
2. Tạo project mới → chọn **Kotlin** → **JVM / Console Application**.
3. Kotlin plugin đã được cài sẵn.

### Cách 2: Android Studio (nếu mục tiêu là Android)

1. Tải Android Studio tại [developer.android.com/studio](https://developer.android.com/studio).
2. Tạo project **Empty Activity** → ngôn ngữ chọn **Kotlin**.

### Cách 3: Playground online (không cần cài gì)

Truy cập [play.kotlinlang.org](https://play.kotlinlang.org) để thử code ngay trên trình duyệt.

### Hello World đầu tiên

```kotlin
fun main() {
    println("Hello, Kotlin!")
}
```

Chạy và bạn sẽ thấy `Hello, Kotlin!` in ra terminal - tương tự `console.log("Hello, Kotlin!")` trong JavaScript.

---

## 3. Biến, hằng và kiểu dữ liệu

### 3.1 `val` và `var`

Kotlin có hai từ khóa khai báo biến:

- `val` - **immutable** (không thay đổi được sau khi gán), tương tự `const` trong JS.
- `var` - **mutable** (có thể thay đổi), tương tự `let` trong JS.

```kotlin
val name = "Mai"       // không thể gán lại, giống const trong JS
var counter = 0        // có thể thay đổi, giống let trong JS
counter = counter + 1  // OK
// name = "Khác"       // Lỗi biên dịch! val không thể gán lại
```

> **Quy tắc vàng**: luôn dùng `val` trước, chỉ đổi sang `var` khi thực sự cần thay đổi giá trị.

### 3.2 Khai báo kiểu tường minh

Kotlin là ngôn ngữ **kiểu tĩnh** (statically typed) - tương tự TypeScript, khác với JavaScript thuần.

```kotlin
val name: String = "Mai"      // String
val age: Int = 25             // Số nguyên
val height: Double = 1.70     // Số thực
val isActive: Boolean = true  // true/false
```

Nhưng Kotlin có **type inference** (tự suy kiểu), nên thường bạn không cần viết kiểu:

```kotlin
val name = "Mai"     // Kotlin tự hiểu là String
val age = 25         // Kotlin tự hiểu là Int
val height = 1.70    // Kotlin tự hiểu là Double
```

### 3.3 So sánh với JavaScript / TypeScript

```javascript
// JavaScript
const name = "Mai"
let counter = 0
```

```typescript
// TypeScript
const name: string = "Mai"
let counter: number = 0
```

```kotlin
// Kotlin
val name: String = "Mai"
var counter: Int = 0
```

### 3.4 String template

Kotlin dùng cú pháp `$` để chèn biến vào chuỗi - **rất giống template literal của JavaScript**.

```javascript
// JavaScript
const message = `Hello ${user.name}, bạn có ${count} tin nhắn.`
```

```kotlin
// Kotlin - dùng " thay vì backtick, $ thay vì ${}
val message = "Hello ${user.name}, bạn có $count tin nhắn."
// Nếu chỉ là biến đơn giản, có thể bỏ {}
val greeting = "Xin chào $name"
```

### 3.5 Kiểu dữ liệu cơ bản

| Kotlin | JavaScript | Mô tả |
| --- | --- | --- |
| `String` | `string` | Chuỗi ký tự |
| `Int` | `number` | Số nguyên |
| `Long` | `number` (BigInt) | Số nguyên lớn |
| `Double` | `number` | Số thực |
| `Boolean` | `boolean` | true/false |
| `List<T>` | `Array` | Danh sách |
| `Map<K, V>` | `Object` / `Map` | Từ điển key-value |
| `Unit` | `void` / `undefined` | Hàm không trả về giá trị |

---

## 4. Hàm (Function)

### 4.1 Khai báo hàm cơ bản

```kotlin
fun greet(name: String): String {
    return "Hello $name"
}

// Gọi hàm
val result = greet("Mai")
println(result) // Hello Mai
```

So sánh với JavaScript:

```javascript
// JavaScript
function greet(name) {
    return `Hello ${name}`
}
```

```typescript
// TypeScript
function greet(name: string): string {
    return `Hello ${name}`
}
```

```kotlin
// Kotlin
fun greet(name: String): String {
    return "Hello $name"
}
```

### 4.2 Hàm một dòng (Expression body)

Khi hàm chỉ có một biểu thức trả về, bạn có thể viết gọn:

```kotlin
fun add(a: Int, b: Int): Int = a + b

fun greet(name: String) = "Hello $name"
```

### 4.3 Tham số mặc định (Default parameter)

```javascript
// JavaScript
function greet(name = "bạn") {
    return `Hello ${name}`
}
```

```kotlin
// Kotlin
fun greet(name: String = "bạn"): String = "Hello $name"

greet()         // Hello bạn
greet("Mai")    // Hello Mai
```

### 4.4 Named argument - gọi hàm với tên tham số

Tính năng độc đáo này giúp code dễ đọc hơn:

```kotlin
fun createUser(id: String, name: String, age: Int): String =
    "User: $name (id=$id, tuổi=$age)"

// Gọi theo thứ tự truyền thống
createUser("u1", "Mai", 25)

// Gọi bằng tên - không cần nhớ thứ tự
createUser(name = "Mai", age = 25, id = "u1")
```

---

## 5. Điều kiện và vòng lặp

### 5.1 `if / else`

```kotlin
val score = 85

if (score >= 90) {
    println("Xuất sắc")
} else if (score >= 70) {
    println("Khá")
} else {
    println("Trung bình")
}
```

**Điểm đặc biệt**: trong Kotlin, `if` là **biểu thức** (trả về giá trị), không chỉ là câu lệnh:

```kotlin
val grade = if (score >= 90) "Xuất sắc" else if (score >= 70) "Khá" else "Trung bình"
```

Tương tự ternary operator trong JS: `score >= 90 ? "Xuất sắc" : "Khá"` nhưng mạnh hơn.

### 5.2 `when` expression (tương tự switch-case)

`when` trong Kotlin mạnh hơn `switch` rất nhiều:

```javascript
// JavaScript switch
switch (day) {
    case "Monday":
        console.log("Đầu tuần");
        break;
    case "Saturday":
    case "Sunday":
        console.log("Cuối tuần");
        break;
    default:
        console.log("Ngày thường");
}
```

```kotlin
// Kotlin when
when (day) {
    "Monday" -> println("Đầu tuần")
    "Saturday", "Sunday" -> println("Cuối tuần")
    else -> println("Ngày thường")
}
```

`when` cũng có thể trả về giá trị:

```kotlin
val message = when (day) {
    "Monday" -> "Đầu tuần"
    "Saturday", "Sunday" -> "Cuối tuần"
    else -> "Ngày thường"
}
```

`when` còn có thể kiểm tra khoảng:

```kotlin
val grade = when (score) {
    in 90..100 -> "Xuất sắc"
    in 70..89 -> "Khá"
    in 50..69 -> "Trung bình"
    else -> "Yếu"
}
```

### 5.3 Vòng lặp `for`

```kotlin
// Lặp từ 1 đến 5
for (i in 1..5) {
    println(i) // 1, 2, 3, 4, 5
}

// Lặp danh sách
val names = listOf("Mai", "Lan", "Hoa")
for (name in names) {
    println(name)
}

// Lặp với index
for ((index, name) in names.withIndex()) {
    println("$index: $name")
}
```

### 5.4 Vòng lặp `while`

```kotlin
var count = 0
while (count < 5) {
    println(count)
    count++
}
```

---

## 6. Null safety - không còn "Cannot read property of undefined"

Đây là **tính năng quan trọng nhất** của Kotlin. Nếu bạn đã từng thấy lỗi `TypeError: Cannot read properties of null` trong JavaScript, Kotlin giải quyết vấn đề này triệt để ngay từ lúc biên dịch.

### 6.1 Vấn đề với null trong JS

```javascript
// JavaScript - lỗi này chỉ xuất hiện khi CHẠY
const user = null
console.log(user.name) // Crash: TypeError: Cannot read properties of null
```

### 6.2 Kotlin phân biệt tường minh: có null và không có null

```kotlin
// Kotlin phân biệt rõ ràng ngay khi viết code
var name: String = "Mai"   // KHÔNG bao giờ null
var email: String? = null  // CÓ THỂ null, thêm dấu ?

name = null   // Lỗi biên dịch! Không cho phép
email = null  // OK
```

Trình biên dịch Kotlin **từ chối biên dịch** nếu bạn có thể gây ra lỗi null - lỗi được phát hiện khi viết code, không phải khi chạy.

### 6.3 Safe call `?.` - tương tự optional chaining JS

```javascript
// JavaScript optional chaining (ES2020)
const city = user?.address?.city
```

```kotlin
// Kotlin - cú pháp giống hệt!
val city = user?.address?.city // nếu user hoặc address là null → city = null
```

### 6.4 Elvis operator `?:` - tương tự nullish coalescing JS

```javascript
// JavaScript nullish coalescing
const city = user?.address?.city ?? "Unknown"
```

```kotlin
// Kotlin Elvis operator - tương tự ??
val city = user?.address?.city ?: "Unknown"
// Nếu vế trái null → lấy giá trị mặc định "Unknown"
```

### 6.5 `let` - chỉ chạy nếu không null

```kotlin
val email: String? = getEmailFromServer()

// Chỉ gửi email nếu không null
email?.let { validEmail ->
    sendEmail(validEmail)
    println("Đã gửi đến: $validEmail")
}
```

Tương tự trong JavaScript:

```javascript
if (email !== null && email !== undefined) {
    sendEmail(email)
}
```

### 6.6 `!!` - ép buộc (nguy hiểm, tránh dùng)

```kotlin
val name = user.name!! // Ép buộc: "tôi chắc name không null"
// Nếu name thực sự null → crash tương tự JS
```

**Không nên dùng `!!`** trừ khi bạn hoàn toàn chắc chắn. Nếu thấy mình dùng `!!` nhiều, có nghĩa là đang thiết kế code chưa tốt.

### 6.7 Tóm tắt null safety

```kotlin
val a: String = "hello"   // KHÔNG null, không cần check
val b: String? = null     // CÓ THỂ null

println(a.length)         // OK, compiler biết a không null
// println(b.length)      // Lỗi biên dịch! Phải xử lý null trước

println(b?.length)        // Safe: trả về null nếu b null
println(b?.length ?: 0)   // Trả về 0 nếu b null
```

---

## 7. Class và lập trình hướng đối tượng

### 7.1 Class cơ bản

```kotlin
class Person(
    val name: String,
    var age: Int
) {
    fun greet(): String = "Xin chào, tôi là $name"
    fun isAdult(): Boolean = age >= 18
}

// Tạo object - không cần từ khóa `new` như Java
val person = Person(name = "Mai", age = 25)
println(person.greet())   // Xin chào, tôi là Mai
println(person.isAdult()) // true
```

So sánh với JavaScript class:

```javascript
// JavaScript
class Person {
    constructor(name, age) {
        this.name = name
        this.age = age
    }
    greet() {
        return `Xin chào, tôi là ${this.name}`
    }
}
const person = new Person("Mai", 25)
```

### 7.2 `data class` - model dữ liệu

Khi bạn chỉ cần một class để lưu dữ liệu (như object trong JS), dùng `data class`:

```javascript
// JavaScript object
const user = { id: "u1", name: "Mai", email: "mai@example.com" }
```

```kotlin
// Kotlin data class
data class User(
    val id: String,
    val name: String,
    val email: String?
)

val user = User(id = "u1", name = "Mai", email = "mai@example.com")
println(user)  // User(id=u1, name=Mai, email=mai@example.com)
```

`data class` tự động tạo:

- `toString()` - in thông tin đẹp
- `equals()` / `hashCode()` - so sánh theo giá trị, không theo địa chỉ bộ nhớ
- `copy()` - tạo bản sao có thể thay đổi một vài field

### 7.3 `copy()` - thay đổi từng phần

```kotlin
val user = User(id = "u1", name = "Mai", email = "mai@gmail.com")

// Tạo user mới với name khác, giữ nguyên các field còn lại
val renamedUser = user.copy(name = "Mai Ho")
println(renamedUser) // User(id=u1, name=Mai Ho, email=mai@gmail.com)
```

Tương tự spread operator trong JavaScript:

```javascript
const renamedUser = { ...user, name: "Mai Ho" }
```

### 7.4 Kế thừa (Inheritance)

```kotlin
open class Animal(val name: String) {
    open fun sound(): String = "..."
}

class Dog(name: String) : Animal(name) {
    override fun sound(): String = "Gâu gâu"
}

class Cat(name: String) : Animal(name) {
    override fun sound(): String = "Meo meo"
}

val dog = Dog("Milu")
println("${dog.name} nói: ${dog.sound()}") // Milu nói: Gâu gâu
```

> Từ khóa `open` trong Kotlin: mặc định class trong Kotlin là `final` (không thể kế thừa). Phải đánh dấu `open` để cho phép kế thừa.

### 7.5 Interface

```kotlin
interface Printable {
    fun print()
    fun preview(): String = "Preview chưa triển khai" // có thể có default implementation
}

class Document(val title: String) : Printable {
    override fun print() = println("In tài liệu: $title")
}
```

### 7.6 `object` - Singleton

Khi cần một instance duy nhất toàn ứng dụng (ví dụ: config, logger):

```kotlin
object AppConfig {
    const val BASE_URL = "https://api.example.com"
    const val TIMEOUT = 30

    fun getFullUrl(path: String) = "$BASE_URL$path"
}

// Dùng trực tiếp, không cần tạo instance
println(AppConfig.BASE_URL)
println(AppConfig.getFullUrl("/users"))
```

### 7.7 `companion object` - thay thế static

```kotlin
class User(val id: String, val name: String) {
    companion object {
        // Hàm factory - tương tự static method
        fun guest(): User = User(id = "guest", name = "Khách")
        fun fromJson(json: String): User {
            // parse json...
            return User(id = "1", name = "Mai")
        }
    }
}

val guestUser = User.guest()
```

---

## 8. Collections - List, Map, Set

Kotlin có hai loại collection:

- **Immutable** (chỉ đọc): `listOf`, `mapOf`, `setOf`
- **Mutable** (đọc và ghi): `mutableListOf`, `mutableMapOf`, `mutableSetOf`

### 8.1 List

```kotlin
// Immutable list
val fruits = listOf("Apple", "Banana", "Mango")
println(fruits[0]) // Apple
println(fruits.size) // 3

// Mutable list
val items = mutableListOf("Item 1", "Item 2")
items.add("Item 3")
items.removeAt(0)
```

So sánh với JavaScript:

```javascript
// JavaScript - mặc định mutable
const fruits = ["Apple", "Banana", "Mango"]
fruits.push("Orange")
```

### 8.2 Map (từ điển key-value)

```kotlin
// Immutable map
val scores = mapOf(
    "Mai" to 90,
    "Lan" to 85,
    "Hoa" to 78
)
println(scores["Mai"]) // 90

// Mutable map
val config = mutableMapOf<String, String>()
config["theme"] = "dark"
config["language"] = "vi"
```

So sánh với JavaScript:

```javascript
// JavaScript
const scores = { Mai: 90, Lan: 85, Hoa: 78 }
scores["Mai"] // 90
```

### 8.3 Các thao tác hay dùng với List

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// filter - lấy phần tử thỏa điều kiện (giống JS .filter())
val evenNumbers = numbers.filter { it % 2 == 0 }
// [2, 4, 6, 8, 10]

// map - biến đổi từng phần tử (giống JS .map())
val doubled = numbers.map { it * 2 }
// [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

// find - tìm phần tử đầu tiên thỏa điều kiện (giống JS .find())
val firstBig = numbers.find { it > 5 }
// 6

// any - kiểm tra có bất kỳ phần tử nào thỏa không (giống JS .some())
val hasEven = numbers.any { it % 2 == 0 }
// true

// all - kiểm tra tất cả phần tử có thỏa không (giống JS .every())
val allPositive = numbers.all { it > 0 }
// true

// count - đếm số phần tử thỏa điều kiện
val countEven = numbers.count { it % 2 == 0 }
// 5

// sum - tổng
val total = numbers.sum()
// 55

// sortedBy - sắp xếp theo trường
val users = listOf(User("u1", "Hoa"), User("u2", "An"), User("u3", "Mai"))
val sorted = users.sortedBy { it.name } // sắp xếp theo tên A-Z
```

### 8.4 Ví dụ thực tế: xử lý danh sách sản phẩm

```kotlin
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val inStock: Boolean
)

val products = listOf(
    Product("p1", "Áo thun", 199_000.0, true),
    Product("p2", "Quần jean", 450_000.0, false),
    Product("p3", "Giày thể thao", 800_000.0, true),
    Product("p4", "Mũ lưỡi trai", 120_000.0, true)
)

// Lấy sản phẩm còn hàng, giá dưới 500k, sắp xếp theo giá
val affordable = products
    .filter { it.inStock && it.price < 500_000.0 }
    .sortedBy { it.price }
    .map { "${it.name}: ${it.price}" }

affordable.forEach { println(it) }
// Mũ lưỡi trai: 120000.0
// Áo thun: 199000.0
```

So sánh cú pháp với JavaScript:

```javascript
// JavaScript
const affordable = products
    .filter(p => p.inStock && p.price < 500_000)
    .sort((a, b) => a.price - b.price)
    .map(p => `${p.name}: ${p.price}`)
```

---

## 9. Lambda và Higher-order Function

### 9.1 Lambda là gì?

Lambda là **hàm vô danh** (anonymous function) - hàm không có tên, thường được truyền như tham số.

Bạn đã dùng lambda ở trên: `filter { it.inStock }` - phần `{ it.inStock }` chính là lambda.

```kotlin
// Khai báo lambda
val double = { x: Int -> x * 2 }
println(double(5)) // 10

// Lambda với nhiều tham số
val add = { a: Int, b: Int -> a + b }
println(add(3, 4)) // 7
```

So sánh với JavaScript arrow function:

```javascript
// JavaScript
const double = (x) => x * 2
const add = (a, b) => a + b
```

### 9.2 Truyền lambda vào hàm

```kotlin
fun process(numbers: List<Int>, operation: (Int) -> Int): List<Int> {
    return numbers.map { operation(it) }
}

val numbers = listOf(1, 2, 3, 4, 5)
val doubled = process(numbers) { it * 2 }    // [2, 4, 6, 8, 10]
val squared = process(numbers) { it * it }   // [1, 4, 9, 16, 25]
```

### 9.3 `it` - tham số ngầm định

Khi lambda chỉ có **một tham số**, bạn dùng `it` thay vì khai báo tên:

```kotlin
val names = listOf("Mai", "Lan", "Hoa")

// Đầy đủ
names.filter { name -> name.length > 2 }

// Dùng `it` (cùng ý nghĩa, ngắn hơn)
names.filter { it.length > 2 }
```

### 9.4 Scope functions: `let`, `apply`, `also`, `run`

Đây là những hàm đặc biệt giúp viết code rõ ràng hơn.

**`let`** - thực thi block nếu không null, kết quả là giá trị cuối của block:

```kotlin
val email: String? = getUserEmail()
val result = email?.let {
    it.lowercase().trim() // kết quả của let là giá trị này
}
```

**`apply`** - cấu hình object, trả về chính object đó:

```kotlin
val user = User(id = "u1", name = "Mai", email = null).apply {
    // this = user bên trong block
    println("Đang cấu hình user: $name")
}
```

**`also`** - thực thi thêm một hành động, trả về chính object:

```kotlin
val list = mutableListOf(1, 2, 3)
    .also { println("Danh sách ban đầu: $it") }
    .also { it.add(4) }
```

---

## 10. Xử lý bất đồng bộ: Coroutines (tương tự async/await)

### 10.1 Tại sao cần async?

Khi gọi API hoặc đọc file, nếu chờ đồng bộ thì ứng dụng bị **freeze** (đóng băng UI). JavaScript giải quyết bằng `async/await`. Kotlin giải quyết bằng **Coroutines**.

### 10.2 So sánh trực tiếp

```javascript
// JavaScript async/await
async function loadUserProfile(userId) {
    try {
        const user = await fetchUser(userId)
        const orders = await fetchOrders(user.id)
        displayData(user, orders)
    } catch (error) {
        showError(error.message)
    }
}
```

```kotlin
// Kotlin Coroutines - cú pháp rất giống!
suspend fun loadUserProfile(userId: String) {
    try {
        val user = fetchUser(userId)         // tự động await
        val orders = fetchOrders(user.id)    // tự động await
        displayData(user, orders)
    } catch (e: Exception) {
        showError(e.message ?: "Lỗi không xác định")
    }
}
```

Từ khóa `suspend` giống như `async` trong JavaScript - đánh dấu hàm có thể "tạm dừng" và chờ đợi mà không block thread.

### 10.3 Chạy coroutine - `launch` và `scope`

Coroutine phải được chạy trong một **scope** (phạm vi):

```kotlin
// Trong Android ViewModel - dùng viewModelScope
viewModelScope.launch {
    loadUserProfile("u123")
}

// Trong test hoặc code thông thường
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        loadUserProfile("u123")
    }
}
```

### 10.4 Chạy song song với `async`

```javascript
// JavaScript - chạy song song
const [user, notifications] = await Promise.all([
    fetchUser(id),
    fetchNotifications(id)
])
```

```kotlin
// Kotlin - chạy song song
val userDeferred = async { fetchUser(id) }
val notificationsDeferred = async { fetchNotifications(id) }

val user = userDeferred.await()
val notifications = notificationsDeferred.await()
```

### 10.5 Dispatcher - chạy trên thread nào?

```kotlin
withContext(Dispatchers.IO) {
    // Chạy trên thread IO (phù hợp gọi API, đọc file)
    val data = readFromDisk()
}

withContext(Dispatchers.Main) {
    // Chạy trên main thread (cập nhật UI)
    textView.text = data
}
```

### 10.6 Xử lý lỗi với `runCatching`

```kotlin
val result = runCatching {
    fetchUser(id) // có thể throw exception
}

result.fold(
    onSuccess = { user -> displayUser(user) },
    onFailure = { error -> showError(error.message) }
)
```

---

## 11. Sealed class và when expression

### 11.1 Vấn đề cần giải quyết

Khi gọi API, có ba trạng thái: **đang tải**, **thành công**, **lỗi**. Làm thế nào biểu diễn ba trạng thái này an toàn?

Trong JavaScript thường dùng:

```javascript
// JavaScript - dễ bị thiếu case
const state = { status: "loading" | "success" | "error", data: ..., error: ... }
if (state.status === "success") { ... }
// Nếu quên xử lý "error" thì JS không báo lỗi!
```

### 11.2 Sealed class trong Kotlin

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

`sealed class` = class "đóng kín" - chỉ có các subclass được khai báo trong cùng file. Compiler biết **đầy đủ** tất cả các trường hợp có thể xảy ra.

### 11.3 `when` exhaustive - compiler kiểm tra đủ case

```kotlin
fun render(state: UiState<User>) {
    when (state) {
        UiState.Loading -> showSpinner()
        is UiState.Success -> showUser(state.data)
        is UiState.Error -> showError(state.message)
        // Nếu bỏ sót case nào → Compiler báo lỗi ngay!
    }
}
```

Đây là **lợi thế lớn**: không thể quên xử lý một case nào đó - compiler sẽ nhắc bạn.

### 11.4 Ví dụ thực tế: màn hình danh sách sản phẩm

```kotlin
sealed class ProductListState {
    data object Loading : ProductListState()
    data class Success(val products: List<Product>) : ProductListState()
    data class Empty(val message: String) : ProductListState()
    data class Error(val message: String, val retryable: Boolean) : ProductListState()
}

fun renderProductList(state: ProductListState) {
    when (state) {
        ProductListState.Loading -> {
            progressBar.show()
            listView.hide()
        }
        is ProductListState.Success -> {
            progressBar.hide()
            listView.show()
            adapter.submitList(state.products)
        }
        is ProductListState.Empty -> {
            progressBar.hide()
            listView.hide()
            emptyView.show(state.message)
        }
        is ProductListState.Error -> {
            progressBar.hide()
            errorView.show(state.message)
            if (state.retryable) retryButton.show()
        }
    }
}
```

---

## 12. Extension function

### 12.1 Extension function là gì?

Extension function cho phép bạn **thêm hàm vào class có sẵn** mà không cần sửa class gốc, không cần kế thừa.

```kotlin
// Thêm hàm vào class String
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }

// Dùng như thể đây là hàm của String
println("hello world kotlin".capitalizeWords())
// Hello World Kotlin
```

### 12.2 So sánh với prototype trong JavaScript

```javascript
// JavaScript prototype (tương tự, nhưng ít an toàn hơn)
String.prototype.capitalizeWords = function() {
    return this.split(" ")
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ")
}
"hello world".capitalizeWords() // Hello World
```

Kotlin extension function an toàn hơn vì không thay đổi class gốc, chỉ là "cú pháp đường" (syntactic sugar).

### 12.3 Extension function thực tế

```kotlin
// Chuyển số thành định dạng tiền tệ VND
fun Double.toVnd(): String = "%,.0f₫".format(this)

// Kiểm tra email hợp lệ
fun String.isValidEmail(): Boolean =
    Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}").matches(this)

// Lấy ảnh thumb từ URL YouTube
fun String.toYoutubeThumbnail(): String =
    "https://img.youtube.com/vi/$this/hqdefault.jpg"
```

Sử dụng:

```kotlin
println(199_000.0.toVnd())              // 199,000₫
println("mai@gmail.com".isValidEmail()) // true
println("dQw4w9WgXcQ".toYoutubeThumbnail())
```

---

## 13. Generics

### 13.1 Generics là gì?

Generics cho phép viết code dùng được cho **nhiều kiểu dữ liệu khác nhau** mà vẫn an toàn kiểu.

```kotlin
// Không dùng generics - phải viết riêng cho từng kiểu
fun printString(value: String) = println(value)
fun printInt(value: Int) = println(value)

// Dùng generics - một hàm dùng cho mọi kiểu
fun <T> printValue(value: T) = println(value)

printValue("Hello")  // T = String
printValue(42)       // T = Int
printValue(3.14)     // T = Double
```

### 13.2 Generics với class

```kotlin
// Generic wrapper cho kết quả API
data class ApiResponse<T>(
    val data: T?,
    val error: String?,
    val success: Boolean
)

// Dùng với User
val userResponse: ApiResponse<User> = ApiResponse(
    data = User("u1", "Mai", null),
    error = null,
    success = true
)

// Dùng với List<Product>
val productResponse: ApiResponse<List<Product>> = ApiResponse(
    data = listOf(Product("p1", "Áo", 200_000.0, true)),
    error = null,
    success = true
)
```

### 13.3 Reified - lấy type tại runtime

Trong Kotlin có thể lấy thông tin kiểu T ngay trong code:

```kotlin
inline fun <reified T> parseJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

// Dùng
val user: User = parseJson("""{"id":"u1","name":"Mai"}""")
val products: List<Product> = parseJson("""[...]""")
```

---

## 14. Case study thực tế: gọi API và hiển thị dữ liệu

Hãy xem một luồng hoàn chỉnh từ gọi API đến hiển thị UI trong Android.

### 14.1 Định nghĩa model

```kotlin
// DTO (Data Transfer Object) - dữ liệu nhận từ API
data class UserDto(
    val id: String,
    val display_name: String?,  // snake_case từ server
    val email: String?,
    val avatar_url: String?
)

// Domain model - dữ liệu dùng trong app
data class User(
    val id: String,
    val name: String,       // luôn có giá trị
    val email: String?,     // có thể null
    val avatarUrl: String?  // camelCase trong app
)
```

### 14.2 Mapping DTO sang Domain

```kotlin
fun UserDto.toDomain(): User = User(
    id = id,
    name = display_name?.trim().takeUnless { it.isNullOrEmpty() } ?: "Người dùng ẩn danh",
    email = email,
    avatarUrl = avatar_url
)
```

### 14.3 UI State

```kotlin
sealed class UserProfileState {
    data object Loading : UserProfileState()
    data class Success(val user: User) : UserProfileState()
    data class Error(val message: String) : UserProfileState()
}
```

### 14.4 ViewModel (Android)

```kotlin
class UserProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val state = _state.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.value = UserProfileState.Loading
            _state.value = runCatching {
                userRepository.getUser(userId).toDomain()
            }.fold(
                onSuccess = { UserProfileState.Success(it) },
                onFailure = { UserProfileState.Error(it.message ?: "Không tải được hồ sơ") }
            )
        }
    }
}
```

### 14.5 Render UI (Android Compose)

```kotlin
@Composable
fun UserProfileScreen(viewModel: UserProfileViewModel) {
    val state by viewModel.state.collectAsState()

    when (state) {
        UserProfileState.Loading -> CircularProgressIndicator()
        is UserProfileState.Success -> {
            val user = (state as UserProfileState.Success).user
            Column {
                Text(text = user.name)
                user.email?.let { Text(text = it) }
            }
        }
        is UserProfileState.Error -> {
            val error = (state as UserProfileState.Error).message
            Text(text = error, color = Color.Red)
        }
    }
}
```

### 14.6 Tóm tắt luồng dữ liệu

```text
API Server (JSON)
    ↓
UserDto (parse JSON)
    ↓ .toDomain()
User (domain model)
    ↓ sealed class
UserProfileState.Success(user)
    ↓ StateFlow
UI (Compose/View)
```

---

## 15. Lộ trình học 6 tuần

### Tuần 1 - Nền tảng ngôn ngữ

**Mục tiêu**: đọc và viết code Kotlin cơ bản.

- `val`, `var`, kiểu dữ liệu, String template.
- Hàm, tham số mặc định, named argument.
- `if/when`, vòng lặp `for`.

**Bài tập**:

- Viết hàm tính BMI, nhận cân nặng và chiều cao.
- Viết hàm phân loại điểm học sinh bằng `when`.
- In bảng cửu chương bằng nested `for`.

### Tuần 2 - Null Safety

**Mục tiêu**: hiểu và xử lý null an toàn.

- Phân biệt `String` và `String?`.
- `?.`, `?:`, `!!`, `let`.
- Thực hành với dữ liệu từ API (nhiều field nullable).

**Bài tập**:

- Viết hàm format địa chỉ từ struct `Address?` (street, city, country đều nullable).
- Viết hàm lấy avatar URL, fallback về ảnh mặc định nếu null.

### Tuần 3 - Class và OOP

**Mục tiêu**: thiết kế model dữ liệu.

- `class`, `data class`, `copy()`.
- `interface`, kế thừa.
- `object`, `companion object`.

**Bài tập**:

- Tạo hệ thống model cho app quản lý sản phẩm: `Product`, `Category`, `Cart`, `Order`.
- Thêm các hàm tính toán (tổng giá, số lượng).

### Tuần 4 - Collections và Lambda

**Mục tiêu**: xử lý danh sách dữ liệu thành thạo.

- `listOf`, `mapOf`, `mutableListOf`.
- `filter`, `map`, `find`, `groupBy`, `sortedBy`.
- Lambda, `it`, scope functions.

**Bài tập**:

- Cho danh sách 20 sản phẩm: lọc theo danh mục, sắp xếp theo giá, nhóm theo tình trạng hàng.
- Tính tổng giá trị giỏ hàng, tìm sản phẩm rẻ nhất/đắt nhất.

### Tuần 5 - Sealed class và Coroutines

**Mục tiêu**: xử lý state và async chuyên nghiệp.

- Sealed class cho UI state.
- `suspend fun`, `launch`, `async/await`.
- `withContext`, `runCatching`.

**Bài tập**:

- Mô phỏng gọi API (dùng `delay()`) và hiển thị 3 state: Loading/Success/Error.
- Gọi 2 API song song, kết hợp kết quả.

### Tuần 6 - Android thực chiến

**Mục tiêu**: xây dựng màn hình Android hoàn chỉnh.

- MVVM pattern với ViewModel + StateFlow.
- Retrofit để gọi API thực.
- Coroutines trong Android lifecycle.

**Bài tập**:

- Xây dựng màn hình danh sách người dùng từ `https://reqres.in/api/users`.
- Xây dựng màn hình chi tiết người dùng.
- Xử lý đầy đủ 3 state: loading spinner, hiển thị dữ liệu, thông báo lỗi.

---

## 16. Best practices và lỗi thường gặp

### 16.1 Best practices

**Ưu tiên immutable:**

```kotlin
// Tốt
val users = listOf(...)

// Chỉ dùng khi thực sự cần thay đổi
var mutableList = mutableListOf(...)
```

**Dùng named argument cho hàm nhiều tham số:**

```kotlin
// Khó đọc - phải nhớ thứ tự
createUser("u1", "Mai", 25, true, "admin")

// Dễ đọc - rõ ý nghĩa từng tham số
createUser(id = "u1", name = "Mai", age = 25, active = true, role = "admin")
```

**Sealed class cho mọi trạng thái có thể:**

```kotlin
// Không nên
var isLoading = true
var data: User? = null
var error: String? = null

// Nên dùng sealed class
var state: UserState = UserState.Loading
```

**Tránh `!!` - luôn có cách an toàn hơn:**

```kotlin
// Xấu
val city = user.address!!.city!!

// Tốt
val city = user.address?.city ?: "Chưa có địa chỉ"
```

### 16.2 Lỗi thường gặp khi mới học Kotlin

**Nhầm `val` và `var`:**

```kotlin
// Lỗi này compiler báo ngay
val list = listOf(1, 2, 3)
list = listOf(4, 5, 6) // Lỗi! val không gán lại được

// Muốn thêm phần tử → cần mutableList
val mutableList = mutableListOf(1, 2, 3)
mutableList.add(4) // OK, val chỉ nghĩa là reference không đổi
```

**Quên dấu `?` khi khai báo nullable:**

```kotlin
var email: String = null // Lỗi biên dịch!
var email: String? = null // Đúng
```

**Lạm dụng scope function gây khó đọc:**

```kotlin
// Quá nhiều scope function lồng nhau - khó đọc
user?.let { u ->
    u.address?.apply {
        println(city?.also { println("City: $it") } ?: "Unknown")
    }
}

// Đơn giản hơn
val city = user?.address?.city ?: "Unknown"
println("City: $city")
```

**Coroutine sai scope:**

```kotlin
// Sai - tạo coroutine không có lifecycle → leak
GlobalScope.launch { ... }

// Đúng trong ViewModel
viewModelScope.launch { ... }

// Đúng trong Fragment/Activity
lifecycleScope.launch { ... }
```

---

## 17. Tổng kết

Kotlin được thiết kế để viết ứng dụng **an toàn hơn, ngắn gọn hơn và dễ đọc hơn**.

Nếu bạn là **JavaScript dev**, bạn đã có lợi thế vì:

- String template cú pháp gần giống.
- `?.` và `?:` giống optional chaining và nullish coalescing.
- Lambda và collection operators (`map/filter`) quen thuộc.
- `async/await` tư duy giống Coroutines.

Ba điều bạn cần tập trung nhất:

1. **Null safety**: luôn nghĩ đến khả năng null, dùng `?` và `?:` thay vì `!!`.
2. **Immutable first**: ưu tiên `val`, tránh state thay đổi không cần thiết.
3. **Sealed class + when**: thiết kế state rõ ràng, không bỏ sót case.

Kotlin không chỉ là học ngôn ngữ mới - đây là cơ hội thay đổi tư duy viết code **chắc chắn và bền vững hơn**.

---

## 18. Phụ lục: So sánh nhanh JS vs Kotlin

### A - Bảng so sánh cú pháp

| Chủ đề | JavaScript | Kotlin |
| --- | --- | --- |
| Biến không đổi | `const name = "Mai"` | `val name = "Mai"` |
| Biến thay đổi | `let count = 0` | `var count = 0` |
| String template | `` `Hello ${name}` `` | `"Hello $name"` |
| Arrow function | `(x) => x * 2` | `{ x -> x * 2 }` |
| Optional chaining | `user?.address?.city` | `user?.address?.city` |
| Nullish coalescing | `value ?? "default"` | `value ?: "default"` |
| Array filter | `arr.filter(x => ...)` | `list.filter { ... }` |
| Array map | `arr.map(x => ...)` | `list.map { ... }` |
| Array find | `arr.find(x => ...)` | `list.find { ... }` |
| Async/await | `async function f() { await ... }` | `suspend fun f() { ... }` |
| Promise.all | `Promise.all([p1, p2])` | `async { }.await()` x2 |
| Switch/case | `switch (x) { case "a": ... }` | `when (x) { "a" -> ... }` |
| Spread object | `{ ...obj, key: val }` | `obj.copy(key = val)` |
| typeof check | `typeof x === "string"` | `x is String` |
| Destructuring | `const { name, age } = user` | `val (name, age) = user` |

### B - Bài tập thực hành theo cấp độ

**Cấp 1 - Nhập môn**:

1. Viết hàm chuyển đổi nhiệt độ Celsius sang Fahrenheit.
2. Viết hàm kiểm tra số nguyên tố.
3. Viết hàm đảo ngược chuỗi.
4. Viết hàm tính tổng các số chẵn trong danh sách.

**Cấp 2 - Collections**:

1. Cho danh sách học sinh có điểm, in ra danh sách xếp loại.
2. Nhóm sản phẩm theo danh mục và tính tổng giá trị mỗi nhóm.
3. Tìm 3 sản phẩm bán chạy nhất từ danh sách đơn hàng.

**Cấp 3 - OOP + Sealed class**:

1. Xây dựng hệ thống tính tiền điện: `HouseholdType` (nhà ở/kinh doanh), tính giá theo bậc thang.
2. Mô phỏng ATM: sealed class `TransactionResult` (Success/InsufficientFunds/CardBlocked/NetworkError).
3. Thiết kế model cho app đặt đồ ăn: `Restaurant`, `MenuItem`, `Order`, `OrderStatus`.

**Cấp 4 - Coroutines + Android**:

1. Gọi API `https://reqres.in/api/users` và hiển thị danh sách.
2. Thêm tính năng tìm kiếm theo tên (debounce 300ms).
3. Cache kết quả vào local storage, hiển thị cache khi offline.
