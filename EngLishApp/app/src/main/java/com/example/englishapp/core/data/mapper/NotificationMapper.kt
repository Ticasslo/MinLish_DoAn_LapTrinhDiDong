package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.Notification

// Nếu bạn quyết định tạo NotificationEntity trong tương lai, 
// hãy bổ sung hàm toEntity() ở đây.
// Hiện tại chúng ta map model sang đối tượng thông báo UI nếu cần.

fun Notification.toDomain(): Notification {
    return this
}
