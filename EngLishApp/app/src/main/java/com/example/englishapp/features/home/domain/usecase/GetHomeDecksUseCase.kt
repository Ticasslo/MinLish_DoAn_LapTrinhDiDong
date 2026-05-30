package com.example.englishapp.features.home.domain.usecase

import com.example.englishapp.features.home.domain.model.HomeNewWordDeck
import com.example.englishapp.features.home.domain.model.HomeRecentDeck
import com.example.englishapp.features.home.domain.model.HomeReviewDeck
import com.example.englishapp.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class HomeDecksData(
    val reviewDecks: List<HomeReviewDeck> = emptyList(),
    val newWordDecks: List<HomeNewWordDeck> = emptyList(),
    val recentDecks: List<HomeRecentDeck> = emptyList()
)

class GetHomeDecksUseCase @Inject constructor(
    private val repository: IHomeRepository
) {
    operator fun invoke(userId: String): Flow<HomeDecksData> {
        return combine(
            repository.getReviewDecks(userId),
            repository.getNewWordDecks(userId),
            repository.getRecentDecks(userId)
        ) { reviewDecks, newWordDecks, recentDecks ->
            HomeDecksData(
                reviewDecks = reviewDecks,
                newWordDecks = newWordDecks,
                recentDecks = recentDecks
            )
        }
    }
}
