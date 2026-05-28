# Kế hoạch triển khai tính năng Vocabulary

Tài liệu này mô tả chi tiết kế hoạch thiết kế và hiện thực hóa tính năng quản lý từ vựng (Vocabulary Module) cho ứng dụng học tiếng Anh MinLish. Kế hoạch này tuân thủ phong cách lập trình Kotlin & Jetpack Compose cơ bản, có kèm chú thích tiếng Việt dễ hiểu cho người mới bắt đầu, kết hợp giữa cơ sở dữ liệu local (Room) và cloud (Firestore) thông qua Repository và UseCases.

## Điểm nhấn Thiết kế (Design & Theme)
Theo tài liệu thiết kế `DESIGN_List_vocab.md` và mã giao diện mẫu (`code_List_vocab.html`, `code_create_set.html`, `code_my_set.html`):
- **Academic Modern Style:** Giao diện sử dụng hệ màu Indigo (chủ đạo) kết hợp Teal (nhấn mạnh tiến trình, streak).
- **Thành phần giao diện:** Trực quan hóa tiến trình dưới dạng phần trăm và ProgressBar xanh lá cây (Success), các thẻ từ vựng được tô điểm bởi các dấu chấm trạng thái SRS (Đã thuộc: Success, Đang học: Warning, Từ mới: Error, Chưa học: Neutral).
- **Trải nghiệm người dùng:** Hỗ trợ tìm kiếm từ vựng nhanh, lọc theo trạng thái (Tất cả, Chưa học, Đang học, Đã thuộc), hỗ trợ import/export CSV tiện lợi.

---

## Các Thay đổi Đề xuất (Proposed Changes)

### 1. Data Layer (Tầng Dữ liệu)

#### [MODIFY] [IVocabRepository.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/repository/IVocabRepository.kt)
Định nghĩa các hàm CRUD cho Bộ từ vựng và Từ vựng.
- `fun getSets(userId: String): Flow<List<VocabularySet>>`
- `suspend fun getSetById(setId: String): VocabularySet?`
- `suspend fun insertOrUpdateSet(set: VocabularySet)`
- `suspend fun deleteSet(setId: String, userId: String)` (Xóa cascade cả Set và Word liên quan)
- `fun getWords(setId: String): Flow<List<Word>>`
- `suspend fun getWordById(wordId: String): Word?`
- `suspend fun insertOrUpdateWord(word: Word)`
- `suspend fun deleteWord(word: Word)`
- `suspend fun searchWords(setId: String, query: String): Flow<List<Word>>`

#### [MODIFY] [VocabRepository.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/data/repository/VocabRepository.kt)
Hiện thực hóa `IVocabRepository` kế thừa `BaseRepository`. Lưu trữ offline-first vào local Room DB trước, sau đó đồng bộ lên Firestore thông qua `FirebaseService` và WorkManager khi có kết nối mạng.
- **Quy tắc Xóa dây chuyền (Manual Cascade):** Khi xóa 1 bộ từ, sẽ thực hiện xóa bộ từ đó và toàn bộ từ vựng đi kèm ở cả Local (Room tự động qua ForeignKey CASCADE) và Remote (Firestore xóa thủ công từng từ có `setId` tương ứng).

#### [MODIFY] [IDictionaryRepository.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/repository/IDictionaryRepository.kt) & [DictionaryRepository.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/data/repository/DictionaryRepository.kt)
Định nghĩa hàm tra cứu từ điển trực tuyến (`lookupWord(word: String): DictionaryResponse?`) để tự động điền phiên âm, nghĩa tiếng Anh và ví dụ khi người dùng nhập từ mới.

---

### 2. Domain Layer (Tầng Nghiệp vụ - Use Cases)

Các Use Case sẽ bọc lại các chức năng nghiệp vụ đơn lẻ để ViewModel gọi dễ dàng, giữ code sạch và tách biệt:
- **[MODIFY] [GetSetsUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/GetSetsUseCase.kt):** Lấy danh sách bộ từ.
- **[MODIFY] [CreateSetUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/CreateSetUseCase.kt):** Tạo/Cập nhật bộ từ.
- **[MODIFY] [DeleteSetUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/DeleteSetUseCase.kt):** Xóa bộ từ.
- **[MODIFY] [AddWordUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/AddWordUseCase.kt):** Thêm từ mới, tự động khởi tạo thẻ ghi nhớ SRS (`SrsCardEntity`) với trạng thái mặc định là `"new"`.
- **[MODIFY] [EditWordUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/EditWordUseCase.kt):** Cập nhật thông tin từ.
- **[MODIFY] [DeleteWordUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/DeleteWordUseCase.kt):** Xóa từ vựng khỏi bộ từ.
- **[MODIFY] [LookupWordUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/LookupWordUseCase.kt):** Tra cứu từ vựng trực tuyến.
- **[MODIFY] [ImportCsvUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/ImportCsvUseCase.kt):** Import danh sách từ từ CSV.
- **[MODIFY] [ExportCsvUseCase.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/domain/usecase/ExportCsvUseCase.kt):** Export danh sách từ sang file CSV.

---

### 3. Utilities & DI (Tiện ích & Tiêm phụ thuộc)

#### [MODIFY] [CsvParser.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/core/util/CsvParser.kt)
Viết hàm tiện ích phân tích cú pháp chuỗi CSV (tách dòng, tách cột dựa trên dấu phẩy hoặc chấm phẩy, hỗ trợ xử lý dấu ngoặc kép) để phục vụ cho tính năng Import/Export từ vựng.

#### [MODIFY] [VocabModule.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/di/VocabModule.kt)
Cung cấp các Repository và Use Cases thông qua Dagger Hilt:
- `@Binds IVocabRepository` -> `VocabRepository`
- `@Binds IDictionaryRepository` -> `DictionaryRepository`
- Cung cấp các UseCase cụ thể bằng `@Provides` hoặc `@Inject constructor` trực tiếp.

---

### 4. Presentation Layer (Tầng Hiển thị - Giao diện)

#### [MODIFY] [SetsViewModel.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/presentation/viewmodel/SetsViewModel.kt)
Quản lý trạng thái danh sách bộ từ của người dùng.
- Theo dõi thông tin `User` hiện tại để lấy `userId` tương ứng.
- Lắng nghe danh sách bộ từ vựng qua `Flow` từ `GetSetsUseCase`.
- Hỗ trợ thao tác nhanh: tạo nhanh bộ từ mới, xóa bộ từ vựng trực tiếp.

#### [MODIFY] [VocabViewModel.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/presentation/viewmodel/VocabViewModel.kt)
Quản lý trạng thái danh sách từ vựng thuộc về một bộ từ cụ thể (`setId`).
- Lấy thông tin chi tiết bộ từ vựng và danh sách từ vựng.
- Tìm kiếm từ vựng thời gian thực (`searchWords`).
- Hỗ trợ thao tác: Thêm từ mới (kết hợp tra cứu API), Sửa từ, Xóa từ, Import/Export CSV.

#### [MODIFY] [MySetsScreen.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/presentation/mysets/MySetsScreen.kt)
Màn hình "Bộ từ của tôi".
- Thiết kế dạng danh sách dọc (LazyColumn) các thẻ bộ từ (`SetCard`).
- Mỗi thẻ gồm: Tên bộ, Nhãn chủ đề (IELTS, TOEIC,...), Số lượng từ, ProgressBar tiến độ học tập (Success color), Nút "Xem từ" và "Học".
- Nút FAB "Thêm bộ từ mới" nằm ở góc phải dưới.

#### [MODIFY] [CreateSetScreen.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/presentation/create_edit/CreateSetScreen.kt)
Màn hình "Tạo/Sửa bộ từ mới".
- Biểu mẫu nhập: Tên bộ từ, Mô tả, Chọn Nhãn chủ đề.
- Danh sách từ vựng hiển thị trực tiếp bên dưới biểu mẫu, cho phép thêm từ nhanh, sửa nhanh và xóa trực tiếp trên danh sách trước khi lưu.
- Hỗ trợ nút "Import CSV" trực tiếp trong màn hình tạo.

#### [MODIFY] [VocabListScreen.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/presentation/vocab_list/VocabListScreen.kt)
Màn hình "Chi tiết bộ từ vựng".
- Thống kê trên cùng: Tổng số từ, % thông thạo, Nút "Học ngay" (SRS) và "Ôn tập".
- Thanh tìm kiếm và các Chip bộ lọc trạng thái (Tất cả, Chưa học, Đang học, Đã thuộc).
- Danh sách từ vựng hiển thị dạng thẻ chi tiết (Thuật ngữ, Phiên âm, Định nghĩa) kèm chấm tròn trạng thái màu SRS.
- Hỗ trợ thao tác xem chi tiết từ vựng và quản lý từ vựng.

#### [MODIFY] [AddWordBottomSheet.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/vocab/presentation/vocab_list/AddWordBottomSheet.kt)
Sheet kéo lên cho phép tra từ điển nhanh hoặc điền thông tin từ vựng thủ công và lưu trực tiếp vào bộ từ hiện tại.

---

### 5. Navigation Layer (Tầng Điều hướng)

#### [MODIFY] [Screen.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/navigation/Screen.kt)
Thêm các định tuyến mới cho các màn hình thuộc Vocabulary:
```kotlin
    object MySets : Screen("my_sets_screen")
    object CreateEditSet : Screen("create_edit_set_screen?setId={setId}") {
        fun createRoute(setId: String?) = "create_edit_set_screen?setId=$setId"
    }
    object VocabList : Screen("vocab_list_screen/{setId}") {
        fun createRoute(setId: String) = "vocab_list_screen/$setId"
    }
```

#### [MODIFY] [AppNavigation.kt](file:///c:/Users/nhan2/AndroidStudioProjects/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/navigation/AppNavigation.kt)
Liên kết các màn hình mới vào cây điều hướng NavHost:
- Tích hợp `MySetsScreen`, `CreateSetScreen`, và `VocabListScreen`.
- Chuyển hướng khi người dùng nhấp vào mục **Library (index 1)** ở BottomBar của `HomeScreen` và `ProfileScreen` sang màn hình `MySetsScreen`.

---

## Kế hoạch Xác minh (Verification Plan)

### Kiểm thử Tự động & Biên dịch
- Chạy tác vụ biên dịch Gradle để kiểm tra lỗi cú pháp Kotlin:
  `./gradlew assembleDebug`
- Kiểm tra lỗi Dependency Injection (Hilt compile time check).

### Xác minh Thủ công
- **Bộ từ của tôi (MySets):** Đăng nhập ứng dụng, truy cập thanh điều hướng thư viện để xem danh sách bộ từ vựng cá nhân, tiến trình ProgressBar học tập hiển thị chính xác.
- **Tạo bộ từ mới (CreateSet):** Nhấn nút FAB để tạo bộ từ mới, nhập đầy đủ thông tin, bấm "Lưu", xác minh bộ từ mới hiển thị ngay trên danh sách.
- **Danh sách từ vựng (VocabList):** Nhấp vào một bộ từ cụ thể, tìm kiếm từ vựng, bấm lọc theo trạng thái học, kiểm tra các màu sắc SRS hiển thị đúng theo đặc tả thiết kế.
- **Đồng bộ hóa:** Kiểm tra xem dữ liệu được lưu cục bộ Room và tự động đồng bộ lên Firestore thành công khi điện thoại có kết nối Internet.
