package com.example.englishapp.core.util

object AudioUtils {
    /**
     * Tạo URL từ Google Translate TTS API để phát âm từ vựng.
     * @param text Từ cần phát âm
     * @return URL dạng String để truyền vào MediaPlayer
     */
    fun getGoogleTtsUrl(text: String): String {
        return "https://translate.google.com/translate_tts?ie=UTF-8&tl=en&client=tw-ob&q=${text.replace(" ", "+")}"
    }
}
