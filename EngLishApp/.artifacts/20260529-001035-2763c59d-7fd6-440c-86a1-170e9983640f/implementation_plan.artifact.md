# Kế hoạch triển khai - Thuật toán SRS và Giao diện Flashcard 3D

Kế hoạch này phác thảo việc triển khai logic Hệ thống lặp lại ngắt quãng (SRS) bằng thuật toán SM-2 và tạo giao diện thẻ flashcard lật 3D để học và ôn tập từ vựng.

## Các thay đổi đề xuất

### 1. Tầng Domain - Logic SRS

#### [CalculateSrsUseCase.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/learn/domain/usecase/CalculateSrsUseCase.kt)
- Triển khai thuật toán SM-2 để tính toán `interval` (khoảng cách), `easeFactor` (hệ số độ dễ) và `repetitions` (số lần lặp lại) dựa trên đánh giá của người dùng ("again", "hard", "good", "easy").
- Chi tiết logic:
    - **Again**: Đặt lại số lần lặp lại về 0, khoảng cách về 1, giảm hệ số độ dễ.
    - **Hard**: Tăng số lần lặp lại, nhân khoảng cách với 1.2, giảm nhẹ hệ số độ dễ.
    - **Good**: Tăng số lần lặp lại, tính toán khoảng cách dựa trên hệ số độ dễ, giữ nguyên hệ số độ dễ.
    - **Easy**: Tăng số lần lặp lại, nhân khoảng cách với hệ số độ dễ * 1.3, tăng hệ số độ dễ.

### 2. Tầng Data - Triển khai Repository

#### [ILearnRepository.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/learn/domain/repository/ILearnRepository.kt)
- Chuyển từ class sang interface.
- Thêm các phương thức:
    - `getDueCards(userId: String, setId: String): Flow<List<SrsCard>>`
    - `getNewCards(userId: String, setId: String, limit: Int): suspend () -> List<SrsCard>`
    - `updateSrsCard(card: SrsCard): suspend () -> Unit`
    - `getWordById(wordId: String): suspend () -> Word?`

#### [MỚI] [LearnRepository.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/learn/data/repository/LearnRepository.kt)
- Triển khai `ILearnRepository` bằng cách sử dụng `SrsCardDao`, `WordDao` và `FirebaseService`.
- Tuân theo mô hình offline-first sử dụng `syncItem` từ `BaseRepository`.

### 3. Tầng Presentation - UI và ViewModel

#### [LearnViewModel.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/learn/presentation/viewmodel/LearnViewModel.kt)
- Quản lý trạng thái cho phiên học flashcard:
    - `cardsToStudy`: Danh sách các cặp `SrsCard` và `Word`.
    - `currentIndex`: Chỉ số của thẻ đang hiển thị.
    - `isFlipped`: Trạng thái lật của thẻ hiện tại.
- Xử lý hành động của người dùng:
    - `onRatingSelected(rating: String)`: Tính toán các giá trị SRS mới, cập nhật DB và chuyển sang thẻ tiếp theo.
    - `onFlip()`: Chuyển đổi trạng thái lật.

#### [FlashcardScreen.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/features/learn/presentation/flashcard/FlashcardScreen.kt)
- Tạo giao diện Composable với:
    - Hiệu ứng lật 3D sử dụng `graphicsLayer` và `animateFloatAsState`.
    - Mặt trước thẻ: Từ vựng, Phiên âm, nút Âm thanh.
    - Mặt sau thẻ: Nghĩa, Giải thích, Ví dụ, Collocation.
    - Các nút đánh giá: Again, Hard, Good, Easy với nhãn và màu sắc tương ứng.
    - Thanh tiến trình và bộ đếm.
- Tích hợp Android `TextToSpeech` để tự động phát âm.

### 4. Điều hướng (Navigation)

#### [Screen.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/navigation/Screen.kt)
- Thêm route `Flashcard`: `flashcard_screen/{setId}/{mode}`.

#### [AppNavigation.kt](file:///D:/laptrinhdidong/MinLish_DoAn_LapTrinhDiDong/EngLishApp/app/src/main/java/com/example/englishapp/navigation/AppNavigation.kt)
- Thêm đích đến composable cho `Flashcard`.
- Cập nhật các callback trong `VocabListScreen` để điều hướng đến màn hình flashcard.

---

## Kế hoạch xác minh

### Kiểm thử tự động
- Tạo unit test cho `CalculateSrsUseCase` để xác minh tính toán SM-2 cho cả 4 mức đánh giá.
- Chạy: `gradlew :app:testDebugUnitTest --tests "com.example.englishapp.features.learn.domain.usecase.CalculateSrsUseCaseTest"`

### Xác minh thủ công
- Triển khai ứng dụng lên trình giả lập hoặc thiết bị.
- Điều hướng đến một bộ từ vựng.
- Nhấp vào "Học" hoặc "Ôn tập".
- Xác minh hiệu ứng lật 3D hoạt động mượt mà khi chạm vào thẻ.
- Xác minh rằng việc nhấp vào các nút đánh giá sẽ cập nhật tiến trình và chuyển sang thẻ tiếp theo.
- Xác minh TTS tự động phát (nếu âm thanh được bật).
- Kiểm tra cơ sở dữ liệu cục bộ hoặc log để đảm bảo `nextReview` và `status` được cập nhật chính xác.
