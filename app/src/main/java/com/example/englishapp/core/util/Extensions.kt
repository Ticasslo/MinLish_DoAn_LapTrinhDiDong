package com.example.englishapp.core.util

import java.util.Calendar

// Làm tròn thời gian về 00:00:00:000 của ngày đó.
// Dùng cho logic SRS để tính ngày ôn tập không phụ thuộc vào giờ phút.
fun Long.toStartOfDay(): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toStartOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

// Kiểm tra xem một thời điểm (Long) có phải là ngày hôm nay không.
fun Long.isToday(): Boolean {
    return this.toStartOfDay() == System.currentTimeMillis().toStartOfDay()
}
