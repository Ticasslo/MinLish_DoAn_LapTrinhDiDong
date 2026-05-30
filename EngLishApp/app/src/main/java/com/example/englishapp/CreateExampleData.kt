package com.example.englishapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.englishapp.core.data.model.*
import com.example.englishapp.core.data.sync.SyncWorker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * CÔNG CỤ BƠM DỮ LIỆU MINLISH ULTRA v2
 * - Đầy đủ 6 bảng: Word, VocabularySet, SrsCard, Streak, Notification, StudySession.
 * - Khối lượng lớn: 4 bộ từ vựng, ~50+ từ vựng học thuật.
 * - Khóa ngoại (FK) chuẩn xác 100%.
 */

@Composable
fun SeedDataTool() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logs = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }
    var targetInput by remember { mutableStateOf("fm7Fm1hJv6NKngOIhmR7xT1DGWE2") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MinLish Data Injector Ultra", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Full Model Injected (6/6 Tables) - High Volume Data", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = targetInput,
            onValueChange = { targetInput = it },
            label = { Text("Nhập Email hoặc User ID") },
            placeholder = { Text("ví dụ: user@gmail.com...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (targetInput.isBlank()) return@Button
                isLoading = true
                scope.launch {
                    try {
                        performMasterSeeding(targetInput) { logs.add(0, it) }
                        logs.add(0, "Kích hoạt Sync dữ liệu...")
                        SyncWorker.startImmediate(context)
                        logs.add(0, "THÀNH CÔNG! Đã bơm ~50 từ & 6 bảng.")
                    } catch (e: Exception) {
                        logs.add(0, "LỖI: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("BẮT ĐẦU ĐỔ DỮ LIỆU TỔNG THỂ")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                items(logs) { log ->
                    Text(text = log, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                    HorizontalDivider(modifier = Modifier.alpha(0.3f))
                }
            }
        }
    }
}

private suspend fun performMasterSeeding(targetInput: String, onLog: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val now = System.currentTimeMillis()
    val day = 86400000L

    var finalUid = targetInput.trim()
    if (finalUid.contains("@")) {
        val userQuery = db.collection("users").whereEqualTo("email", finalUid).get().await()
        if (userQuery.isEmpty) throw Exception("Email không tồn tại!")
        finalUid = userQuery.documents[0].id
    }

    // --- KHO DỮ LIỆU LỚN ---
    val ieltsWords = listOf(
        listOf("Substantial", "/səbˈstænʃl/", "Đáng kể", "Large in amount", "A substantial difference.", "substantial amount", "significant"),
        listOf("Ambiguous", "/æmˈbɪɡjuəs/", "Mơ hồ", "Open to interpretations", "An ambiguous reply.", "ambiguous wording", "vague"),
        listOf("Empirical", "/ɪmˈpɪrɪkl/", "Thực nghiệm", "Based on observation", "Empirical evidence.", "empirical data", "factual"),
        listOf("Advocate", "/ˈædvəkeɪt/", "Ủng hộ", "Publicly support", "Advocate for change.", "human rights advocate", "support"),
        listOf("Fluctuate", "/ˈflʌktʃueɪt/", "Dao động", "Change frequently", "Prices fluctuate.", "fluctuate wildly", "vary"),
        listOf("Comprehensive", "/ˌkɒmprɪˈhensɪv/", "Toàn diện", "Including all elements", "A comprehensive study.", "comprehensive coverage", "complete"),
        listOf("Inevitably", "/ɪnˈevɪtəbli/", "Chắc chắn", "Cannot be avoided", "Inevitably, errors occur.", "inevitably lead to", "certainly"),
        listOf("Mitigate", "/ˈmɪtɪɡeɪt/", "Giảm nhẹ", "Make less harmful", "Mitigate the risks.", "mitigate effects", "alleviate"),
        listOf("Paradigm", "/ˈpærədaɪm/", "Hình mẫu", "Typical example", "A new paradigm.", "paradigm shift", "model"),
        listOf("Sustainable", "/səˈsteɪnəbl/", "Bền vững", "Able to be maintained", "Sustainable energy.", "sustainable growth", "renewable"),
        listOf("Feasible", "/ˈfiːzəbl/", "Khả thi", "Possible to do", "A feasible plan.", "economically feasible", "practicable"),
        listOf("Hierarchical", "/ˌhaɪəˈrɑːkɪkl/", "Phân cấp", "Arranged in grades", "Hierarchical structure.", "hierarchical system", "ranked")
    )

    val dailyWords = listOf(
        listOf("Acquaintance", "/əˈkweɪntəns/", "Người quen", "Known slightly", "Just an acquaintance.", "business acquaintance", "friend"),
        listOf("Optimistic", "/ˌɒptɪˈmɪstɪk/", "Lạc quan", "Hopeful about future", "Stay optimistic.", "cautiously optimistic", "positive"),
        listOf("Hypocrite", "/ˈhɪpəkrɪt/", "Kẻ đạo đức giả", "Pretends to have morals", "He is a hypocrite.", "total hypocrite", "pretender"),
        listOf("Sincere", "/sɪnˈsɪə(r)/", "Chân thành", "Really feel", "Sincere apologies.", "sincere thanks", "genuine"),
        listOf("Intuition", "/ˌɪntjuˈɪʃn/", "Trực giác", "Understand immediately", "Intuition told me.", "gut intuition", "instinct"),
        listOf("Spontaneous", "/spɒnˈteɪniəs/", "Ngẫu hứng", "Not planned", "Spontaneous trip.", "spontaneous reaction", "unplanned"),
        listOf("Meticulous", "/məˈtɪkjələs/", "Tỉ mỉ", "Attention to detail", "Meticulous work.", "meticulous attention", "careful"),
        listOf("Vulnerable", "/ˈvʌlnərəbl/", "Dễ tổn thương", "Easy to hurt", "Vulnerable children.", "vulnerable position", "weak"),
        listOf("Resilient", "/rɪˈzɪliənt/", "Kiên cường", "Recover quickly", "Resilient spirit.", "tough and resilient", "strong"),
        listOf("Apathetic", "/ˌæpəˈθetɪk/", "Thờ ơ", "No interest", "Apathetic voters.", "apathetic attitude", "indifferent"),
        listOf("Nostalgia", "/nɒˈstældʒə/", "Hoài niệm", "Longing for past", "Feel nostalgia.", "sense of nostalgia", "reminiscence"),
        listOf("Benevolent", "/bəˈnevələnt/", "Nhân từ", "Kind and helpful", "A benevolent ruler.", "benevolent smile", "kind")
    )

    val bizWords = listOf(
        listOf("Delegate", "/ˈdelɪɡət/", "Giao phó", "Assign work", "Delegate authority.", "delegate tasks", "assign"),
        listOf("Negotiate", "/nəˈɡəʊʃieɪt/", "Đàm phán", "Reach agreement", "Negotiate a deal.", "negotiate price", "bargain"),
        listOf("Implement", "/ˈɪmplɪment/", "Triển khai", "Start a plan", "Implement policy.", "implement strategy", "execute"),
        listOf("Strategic", "/strəˈtiːdʒɪk/", "Chiến lược", "Long-term plan", "Strategic move.", "strategic planning", "tactical"),
        listOf("Collaborate", "/kəˈlæbəreɪt/", "Cộng tác", "Work together", "Collaborate on project.", "collaborate with", "cooperate"),
        listOf("Liability", "/ˌlaɪəˈbɪləti/", "Trách nhiệm", "Legal responsibility", "Limited liability.", "product liability", "debt"),
        listOf("Profitability", "/ˌprɒfɪtəˈbɪləti/", "Sinh lời", "Yielding profit", "High profitability.", "net profitability", "return"),
        listOf("Feasibility", "/ˌfiːzəˈbɪləti/", "Tính khả thi", "Possibility", "Feasibility study.", "project feasibility", "viability"),
        listOf("Acquisition", "/ˌækwɪˈzɪʃn/", "Mua lại", "Buying company", "New acquisition.", "hostile acquisition", "takeover"),
        listOf("Stakeholder", "/ˈsteɪkhəʊldə(r)/", "Bên liên quan", "Interest in biz", "Key stakeholders.", "consult stakeholders", "partner"),
        listOf("Incentive", "/ɪnˈsentɪv/", "Khuyến khích", "Thing that motivates", "Tax incentives.", "financial incentive", "motivation"),
        listOf("Leverage", "/ˈliːvərɪdʒ/", "Tận dụng", "Use to advantage", "Leverage resources.", "market leverage", "utilize")
    )

    val travelWords = listOf(
        listOf("Itinerary", "/aɪˈtɪnərəri/", "Lịch trình", "Plan of a journey", "Check the itinerary.", "travel itinerary", "schedule"),
        listOf("Picturesque", "/ˌpɪktʃəˈresk/", "Đẹp như tranh", "Visually attractive", "Picturesque village.", "picturesque view", "scenic"),
        listOf("Breathtaking", "/ˈbreθteɪkɪŋ/", "Ngoạn mục", "Very exciting", "Breathtaking scenery.", "breathtaking beauty", "amazing"),
        listOf("Hospitality", "/ˌhɒspɪˈtæləti/", "Lòng hiếu khách", "Friendly reception", "Local hospitality.", "warm hospitality", "kindness"),
        listOf("Souvenir", "/ˌsuːvəˈnɪə(r)/", "Quà lưu niệm", "Thing kept as reminder", "Buy souvenirs.", "souvenir shop", "memento"),
        listOf("Exotic", "/ɪɡˈzɒtɪk/", "Kỳ lạ", "From distant country", "Exotic fruits.", "exotic locations", "unusual"),
        listOf("Destination", "/ˌdestɪˈneɪʃn/", "Điểm đến", "Place going to", "Final destination.", "holiday destination", "goal"),
        listOf("Atmosphere", "/ˈætməsfɪə(r)/", "Bầu không khí", "Feeling of a place", "Relaxed atmosphere.", "friendly atmosphere", "ambience"),
        listOf("Authentic", "/ɔːˈθentɪk/", "Đích thực", "Real/True", "Authentic food.", "authentic experience", "genuine"),
        listOf("Explore", "/ɪkˈsplɔː(r)/", "Khám phá", "Travel to learn", "Explore the city.", "explore options", "discover")
    )

    val allSetsData = listOf(
        Triple("ielts", "IELTS Academic Vocabulary", ieltsWords),
        Triple("daily", "Daily Social English", dailyWords),
        Triple("biz", "Business Professional", bizWords),
        Triple("travel", "Travel & Exploration", travelWords)
    )

    onLog("Bắt đầu quy trình nạp dữ liệu...")

    for ((slug, setName, wordList) in allSetsData) {
        val setId = "set_${slug}_$finalUid"
        var mCount = 0; var lCount = 0; var nCount = 0
        
        onLog("-> Đang tạo bộ: $setName (${wordList.size} từ)")

        wordList.forEachIndexed { i, data ->
            val wordId = "word_${slug}_${i}_$finalUid"
            val status = when {
                i < 3 -> "learning" // 3 từ đầu mỗi bộ cần Review
                i < 7 -> "mastered" // 4 từ tiếp đã thuộc
                else -> "new"       // Còn lại là mới
            }
            if (status == "mastered") mCount++ else if (status == "learning") lCount++ else nCount++

            // BẢNG 1: WORDS
            val word = Word(
                wordId = wordId, setId = setId, userId = finalUid,
                word = data[0], pronunciation = data[1], meaning = data[2],
                description = data[3], example = data[4], collocation = data[5],
                relatedWords = data[6].split(", "), createdAt = now - (day * 15), updatedAt = now
            )
            db.collection("words").document(wordId).set(word).await()

            // BẢNG 2: SRS_CARDS
            val card = SrsCard(
                cardId = "card_$wordId", userId = finalUid, wordId = wordId, setId = setId,
                status = status, easeFactor = 2.5,
                interval = if (status == "mastered") 10 else 1,
                repetitions = when (status) {
                    "mastered" -> 4
                    "learning" -> 1
                    else -> 0
                },
                // LÀM CHO TỪ ĐẾN HẠN: nextReview < now
                nextReview = if (i < 3) now - (day * (i + 1)) else now + (day * (i + 5)),
                lastReview = if (status != "new") now - (day * 5) else null, 
                lastRating = if (status != "new") "good" else null, 
                updatedAt = now
            )
            db.collection("srs_cards").document(card.cardId).set(card).await()
        }

        // BẢNG 3: VOCABULARY_SETS
        val setTags = when(slug) {
            "ielts" -> listOf("IELTS")
            "daily" -> listOf("Communication")
            "biz" -> listOf("Business")
            "travel" -> listOf("Travel")
            else -> emptyList()
        }

        val vSet = VocabularySet(
            setId = setId, userId = finalUid, name = setName,
            description = "Bộ từ vựng $setName chất lượng cao.",
            tags = setTags,
            wordCount = wordList.size, masteredCount = mCount, learningCount = lCount, newCount = nCount,
            createdAt = now - (day * 15),
            updatedAt = now
        )
        db.collection("vocabulary_sets").document(setId).set(vSet).await()

        // BẢNG 4: STUDY_SESSIONS (Tạo 3 session cho mỗi bộ để có biểu đồ)
        for (j in 1..3) {
            val sId = "session_${slug}_${j}_$finalUid"
            val session = StudySession(
                sessionId = sId, userId = finalUid, setId = setId,
                sessionType = if (j % 2 == 0) "review" else "new",
                date = now - (day * j), 
                wordsStudied = 10 + j, accuracy = 80.0 + j, duration = 300 + (j * 60),
                goodCount = 8, easyCount = 2, hardCount = 1, againCount = 0, updatedAt = now
            )
            db.collection("study_sessions").document(sId).set(session).await()
        }
    }

    // BẢNG 5: STREAK
    onLog("-> Cập nhật Streak 15 ngày...")
    val streakHistory = (0..14).map { now - (it * day) }
    val streak = Streak(
        streakId = finalUid, userId = finalUid,
        currentStreak = 15, longestStreak = 30,
        lastStudyDate = now, streakHistory = streakHistory,
        updatedAt = now
    )
    db.collection("streaks").document(finalUid).set(streak).await()

    // BẢNG 6: NOTIFICATIONS
    onLog("-> Gửi thông báo chào mừng...")
    val notifId = "notif_welcome_$now"
    val notif = Notification(
        notificationId = notifId, userId = finalUid,
        title = "Dữ liệu đã sẵn sàng! 🚀",
        body = "Chúng tôi đã nạp 4 bộ từ vựng với hơn 45 từ mới cho bạn. Chúc học tốt!",
        type = "daily_reminder", createdAt = now, updatedAt = now
    )
    db.collection("notifications").document(notifId).set(notif).await()

    onLog("== HOÀN TẤT: 4 BỘ, 46 TỪ, 12 SESSIONS, 6 BẢNG ==")
}

@Preview(showBackground = true)
@Composable
fun SeedDataToolPreview() {
    MaterialTheme { Surface { SeedDataTool() } }
}
