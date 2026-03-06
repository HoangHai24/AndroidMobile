content = """# Hành Trình Học Kiến Trúc

Trong hành trình học này, bạn sẽ tìm hiểu về kiến trúc của ứng dụng Sigma Android TV: các lớp, các class quan trọng và sự tương tác giữa chúng.


## Mục tiêu và yêu cầu

Các mục tiêu đặt ra cho kiến trúc ứng dụng:



*   Tuân thủ [hướng dẫn kiến trúc chính thức](https://developer.android.com/jetpack/guide) của Android càng sát càng tốt.
*   Dễ hiểu với lập trình viên, không dùng những thứ quá thử nghiệm.
*   Hỗ trợ nhiều lập trình viên cùng làm việc trên một codebase.
*   Thuận tiện cho việc viết test cục bộ và test thực thi (instrumented tests), cả trên máy lập trình viên lẫn qua Continuous Integration (CI).
*   Giảm thiểu thời gian build.


## Tổng quan kiến trúc

Kiến trúc ứng dụng có ba lớp: [lớp data](https://developer.android.com/jetpack/guide/data-layer), [lớp domain](https://developer.android.com/jetpack/guide/domain-layer) và [lớp UI](https://developer.android.com/jetpack/guide/ui-layer).


<center>
<img src="images/architecture-1-overall.png" width="600px" alt="Sơ đồ tổng quan kiến trúc ứng dụng" />
</center>

> [!NOTE]  
> Kiến trúc Android chính thức khác với các kiến trúc khác như "Clean Architecture". Các khái niệm từ các kiến trúc khác có thể không áp dụng được ở đây, hoặc được áp dụng theo cách khác. [Thảo luận thêm tại đây](https://github.com/android/sigma/discussions/1273).

Kiến trúc tuân theo mô hình lập trình phản ứng (reactive programming) với [luồng dữ liệu một chiều](https://developer.android.com/jetpack/guide/ui-layer#udf). Với lớp data ở dưới cùng, các khái niệm then chốt là:



*   Các lớp ở trên phản ứng với sự thay đổi từ các lớp bên dưới.
*   Sự kiện (Events) đi xuống.
*   Dữ liệu (Data) đi lên.

Luồng dữ liệu được thực hiện thông qua các stream, được xây dựng bằng [Kotlin Flows](https://developer.android.com/kotlin/flow).


### Ví dụ: Hiển thị tin tức trên màn hình For You

Khi ứng dụng chạy lần đầu, nó sẽ cố tải danh sách tin tức từ máy chủ từ xa (khi chọn build flavor `prod`; các bản `demo` sẽ dùng dữ liệu cục bộ). Sau khi tải xong, tin tức được hiển thị cho người dùng dựa trên sở thích mà họ đã chọn.

Sơ đồ dưới đây mô tả các sự kiện xảy ra và cách dữ liệu chạy qua các đối tượng liên quan để đạt được điều đó.


![Sơ đồ cách tin tức được hiển thị trên màn hình For You](images/architecture-2-example.png "Sơ đồ cách tin tức được hiển thị trên màn hình For You")


Đây là mô tả từng bước. Cách dễ nhất để tìm code tương ứng là tải project vào Android Studio và tìm kiếm đoạn text trong cột Code (phím tắt tiện lợi: nhấn <kbd>⇧ SHIFT</kbd> hai lần).


<table>
  <tr>
   <td><strong>Bước</strong>
   </td>
   <td><strong>Mô tả</strong>
   </td>
   <td><strong>Code</strong>
   </td>
  </tr>
  <tr>
   <td>1
   </td>
   <td>Khi app khởi động, một job <a href="https://developer.android.com/topic/libraries/architecture/workmanager">WorkManager</a> để đồng bộ tất cả repository được đưa vào hàng chờ.
   </td>
   <td><code>Sync.initialize</code>
   </td>
  </tr>
  <tr>
   <td>2
   </td>
   <td><code>ForYouViewModel</code> gọi <code>GetUserNewsResourcesUseCase</code> để lấy stream tin tức kèm trạng thái đã đánh dấu/lưu. Stream này sẽ chưa phát ra item nào cho đến khi cả repository người dùng và repository tin tức đều phát ra một item. Trong lúc chờ, trạng thái feed được đặt thành <code>Loading</code>.
   </td>
   <td>Tìm kiếm usages của <code>NewsFeedUiState.Loading</code>
   </td>
  </tr>
  <tr>
   <td>3
   </td>
   <td>Repository dữ liệu người dùng lấy stream các đối tượng <code>UserData</code> từ nguồn dữ liệu cục bộ được hỗ trợ bởi Proto DataStore.
   </td>
   <td><code>SmPreferencesDataSource.userData</code>
   </td>
  </tr>
  <tr>
   <td>4
   </td>
   <td>WorkManager thực thi job đồng bộ, gọi <code>OfflineFirstNewsRepository</code> để bắt đầu đồng bộ dữ liệu với nguồn từ xa.
   </td>
   <td><code>SyncWorker.doWork</code>
   </td>
  </tr>
  <tr>
   <td>5
   </td>
   <td><code>OfflineFirstNewsRepository</code> gọi <code>RetrofitSmNetwork</code> để thực hiện request API thực tế sử dụng <a href="https://square.github.io/retrofit/">Retrofit</a>.
   </td>
   <td><code>OfflineFirstNewsRepository.syncWith</code>
   </td>
  </tr>
  <tr>
   <td>6
   </td>
   <td><code>RetrofitSmNetwork</code> gọi REST API trên máy chủ từ xa.
   </td>
   <td><code>RetrofitSmNetwork.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>7
   </td>
   <td><code>RetrofitSmNetwork</code> nhận phản hồi mạng từ máy chủ từ xa.
   </td>
   <td><code>RetrofitSmNetwork.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>8
   </td>
   <td><code>OfflineFirstNewsRepository</code> đồng bộ dữ liệu từ xa với <code>NewsResourceDao</code> bằng cách chèn, cập nhật hoặc xoá dữ liệu trong <a href="https://developer.android.com/training/data-storage/room">Room database</a> cục bộ.
   </td>
   <td><code>OfflineFirstNewsRepository.syncWith</code>
   </td>
  </tr>
  <tr>
   <td>9
   </td>
   <td>Khi dữ liệu thay đổi trong <code>NewsResourceDao</code>, nó được phát vào stream dữ liệu tin tức (là một <a href="https://developer.android.com/kotlin/flow">Flow</a>).
   </td>
   <td><code>NewsResourceDao.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>10
   </td>
   <td><code>OfflineFirstNewsRepository</code> đóng vai trò là <a href="https://developer.android.com/kotlin/flow#modify">toán tử trung gian</a> trên stream này, chuyển đổi <code>PopulatedNewsResource</code> đến (là model của database, nội bộ trong lớp data) thành model công khai <code>NewsResource</code> để các lớp khác sử dụng.
   </td>
   <td><code>OfflineFirstNewsRepository.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>11
   </td>
   <td><code>GetUserNewsResourcesUseCase</code> kết hợp danh sách tin tức với dữ liệu người dùng để phát ra danh sách các <code>UserNewsResource</code>.
   </td>
   <td><code>GetUserNewsResourcesUseCase.invoke</code>
   </td>
  </tr>
  <tr>
   <td>12
   </td>
   <td><code>ForYouViewModel</code> nhận được danh sách tin tức có thể lưu, nó cập nhật trạng thái feed thành <code>Success</code>.

  <code>ForYouScreen</code> sau đó dùng danh sách tin tức trong state để render màn hình.
   </td>
   <td>Tìm kiếm instances của <code>NewsFeedUiState.Success</code>
   </td>
  </tr>
</table>



## Lớp Data

Lớp data được xây dựng theo mô hình offline-first — là nguồn dữ liệu và logic nghiệp vụ của ứng dụng. Đây là nguồn sự thật duy nhất (source of truth) cho toàn bộ dữ liệu trong app.



![Sơ đồ kiến trúc lớp data](images/architecture-3-data-layer.png "Sơ đồ kiến trúc lớp data")


Mỗi repository có model riêng của nó. Ví dụ, `TopicsRepository` có model `Topic` và `NewsRepository` có model `NewsResource`.

Các repository là API công khai cho các lớp khác — chúng cung cấp _cách duy nhất_ để truy cập dữ liệu ứng dụng. Các repository thường cung cấp một hoặc nhiều phương thức để đọc và ghi dữ liệu.


### Đọc dữ liệu

Dữ liệu được phát ra dưới dạng stream. Điều này có nghĩa là mỗi client của repository phải sẵn sàng phản ứng với các thay đổi dữ liệu. Dữ liệu không được phát ra dạng snapshot (ví dụ: `getModel`) vì không có gì đảm bảo rằng nó vẫn còn hợp lệ tại thời điểm được sử dụng.

Việc đọc được thực hiện từ bộ nhớ cục bộ vì đây là nguồn sự thật, do đó không mong đợi lỗi khi đọc từ các instance `Repository`. Tuy nhiên, lỗi có thể xảy ra khi cố gắng đồng bộ dữ liệu cục bộ với nguồn từ xa. Để biết thêm về xử lý lỗi đồng bộ, xem phần đồng bộ dữ liệu bên dưới.

_Ví dụ: Đọc danh sách topics_

Danh sách Topics có thể lấy được bằng cách đăng ký (subscribe) flow `TopicsRepository::getTopics`, flow này phát ra `List<Topic>`.

Mỗi khi danh sách topics thay đổi (ví dụ khi thêm topic mới), `List<Topic>` mới sẽ được phát vào stream.


### Ghi dữ liệu

Để ghi dữ liệu, repository cung cấp các hàm suspend. Người gọi có trách nhiệm đảm bảo rằng việc thực thi được đặt trong scope phù hợp.

_Ví dụ: Theo dõi một topic_

Chỉ cần gọi `UserDataRepository.toggleFollowedTopicId` với ID của topic mà người dùng muốn theo dõi và `followed=true` để cho biết topic nên được theo dõi (dùng `false` để bỏ theo dõi).


### Nguồn dữ liệu (Data sources)

Một repository có thể phụ thuộc vào một hoặc nhiều nguồn dữ liệu. Ví dụ, `OfflineFirstTopicsRepository` phụ thuộc vào các nguồn dữ liệu sau:


<table>
  <tr>
   <td><strong>Tên</strong>
   </td>
   <td><strong>Hỗ trợ bởi</strong>
   </td>
   <td><strong>Mục đích</strong>
   </td>
  </tr>
  <tr>
   <td>TopicsDao
   </td>
   <td><a href="https://developer.android.com/training/data-storage/room">Room/SQLite</a>
   </td>
   <td>Dữ liệu quan hệ bền vững liên quan đến Topics
   </td>
  </tr>
  <tr>
   <td>SmPreferencesDataSource
   </td>
   <td><a href="https://developer.android.com/topic/libraries/architecture/datastore">Proto DataStore</a>
   </td>
   <td>Dữ liệu phi cấu trúc bền vững liên quan đến tùy chọn người dùng, cụ thể là những Topics mà người dùng quan tâm. Được định nghĩa và mô hình hóa trong file .proto, sử dụng cú pháp protobuf.
   </td>
  </tr>
  <tr>
   <td>SmNetworkDataSource
   </td>
   <td>Remote API truy cập qua Retrofit
   </td>
   <td>Dữ liệu cho topics, được cung cấp qua các REST API endpoint dưới dạng JSON.
   </td>
  </tr>
</table>



### Đồng bộ dữ liệu

Các repository chịu trách nhiệm đối chiếu dữ liệu trong bộ nhớ cục bộ với nguồn từ xa. Khi dữ liệu lấy được từ nguồn từ xa, nó được ghi ngay vào bộ nhớ cục bộ. Dữ liệu đã cập nhật được phát từ bộ nhớ cục bộ (Room) vào stream dữ liệu tương ứng và được các client đang lắng nghe nhận.

Cách tiếp cận này đảm bảo rằng việc đọc và ghi dữ liệu trong app hoàn toàn tách biệt và không ảnh hưởng lẫn nhau.

Trong trường hợp xảy ra lỗi trong quá trình đồng bộ, chiến lược exponential backoff (thử lại với thời gian chờ tăng dần) được áp dụng. Việc này được ủy quyền cho `WorkManager` thông qua `SyncWorker`, một cài đặt của interface `Synchronizer`.

Xem `OfflineFirstNewsRepository.syncWith` để có ví dụ về đồng bộ dữ liệu.

## Lớp Domain

[Lớp domain](https://developer.android.com/topic/architecture/domain-layer) chứa các use case. Đây là các class có một phương thức duy nhất có thể gọi được (`operator fun invoke`) chứa logic nghiệp vụ.

Các use case này được dùng để đơn giản hóa và loại bỏ logic trùng lặp khỏi ViewModel. Chúng thường kết hợp và biến đổi dữ liệu từ các repository.

Ví dụ, `GetUserNewsResourcesUseCase` kết hợp stream (được xây dựng bằng `Flow`) của `NewsResource` từ `NewsRepository` với stream của `UserData` từ `UserDataRepository` để tạo ra stream của `UserNewsResource`. Stream này được nhiều ViewModel sử dụng để hiển thị tin tức trên màn hình kèm trạng thái đã đánh dấu.

Đáng chú ý, lớp domain trong Sigma Android TV _không_ (hiện tại) chứa bất kỳ use case nào để xử lý sự kiện. Sự kiện được xử lý bởi lớp UI gọi trực tiếp các phương thức trên repository.

## Lớp UI

[Lớp UI](https://developer.android.com/topic/architecture/ui-layer) bao gồm:



*   Các phần tử UI xây dựng bằng [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   [Android ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel)

ViewModel nhận stream dữ liệu từ các use case và repository, rồi biến đổi chúng thành UI state. Các phần tử UI phản ánh state này và cung cấp cách để người dùng tương tác với app. Các tương tác này được chuyển đến ViewModel dưới dạng sự kiện để xử lý.


![Sơ đồ kiến trúc lớp UI](images/architecture-4-ui-layer.png "Sơ đồ kiến trúc lớp UI")


### Mô hình hóa UI state

UI state được mô hình hóa dưới dạng cấu trúc phân cấp sealed sử dụng interface và data class bất biến (immutable). Các đối tượng state chỉ được phát ra thông qua việc biến đổi stream dữ liệu. Cách tiếp cận này đảm bảo rằng:



*   UI state luôn đại diện cho dữ liệu ứng dụng bên dưới — dữ liệu ứng dụng là nguồn sự thật.
*   Các phần tử UI xử lý được tất cả các trạng thái có thể xảy ra.

**Ví dụ: Feed tin tức trên màn hình For You**

Feed (danh sách) tin tức trên màn hình For You được mô hình hóa bằng `NewsFeedUiState`. Đây là một sealed interface tạo ra cấu trúc phân cấp với hai trạng thái có thể xảy ra:



*   `Loading` — cho biết dữ liệu đang tải
*   `Success` — cho biết dữ liệu đã tải thành công; trạng thái Success chứa danh sách tin tức.

`feedState` được truyền vào composable `ForYouScreen`, nơi xử lý cả hai trạng thái này.


### Chuyển đổi stream sang UI state

ViewModel nhận stream dữ liệu dạng cold [flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html) từ một hoặc nhiều use case hay repository. Chúng được [kết hợp](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/combine.html) lại, hoặc đơn giản là được [map](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/map.html), để tạo ra một flow duy nhất của UI state. Flow đơn này sau đó được chuyển đổi thành hot flow bằng [stateIn](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/state-in.html). Việc chuyển đổi sang state flow giúp các phần tử UI có thể đọc state mới nhất từ flow.

**Ví dụ: Hiển thị các topic đang theo dõi**

`InterestsViewModel` phát `uiState` dưới dạng `StateFlow<InterestsUiState>`. Hot flow này được tạo ra bằng cách lấy cold flow của `List<FollowableTopic>` do `GetFollowableTopicsUseCase` cung cấp. Mỗi khi danh sách mới được phát ra, nó được chuyển đổi thành state `InterestsUiState.Interests` và phát ra cho UI.


### Xử lý tương tác người dùng

Các hành động của người dùng được truyền từ các phần tử UI đến ViewModel bằng cách gọi phương thức thông thường. Các phương thức này được truyền vào các phần tử UI dưới dạng lambda expression.

**Ví dụ: Theo dõi một topic**

`InterestsScreen` nhận một lambda expression có tên `followTopic` được cung cấp từ `InterestsViewModel.followTopic`. Mỗi khi người dùng nhấn vào một topic để theo dõi, phương thức này được gọi. Sau đó ViewModel xử lý hành động này bằng cách thông báo cho repository dữ liệu người dùng.


## Đọc thêm

[Hướng dẫn kiến trúc ứng dụng](https://developer.android.com/topic/architecture)

[Jetpack Compose](https://developer.android.com/jetpack/compose)
"""

output_path = r"c:\Users\maiho\CODE\Personal\AndroidMobile\app\documents\ArchitectureLearningJourney.md"
with open(output_path, "w", encoding="utf-8", newline="\r\n") as f:
    f.write(content)

print("Done - file written successfully")
