package com.example.englishapp.features.learn.domain.usecase

import com.example.englishapp.core.data.model.SrsCard
import com.example.englishapp.features.learn.domain.repository.ILearnRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDueCardsUseCase @Inject constructor(
    private val repository: ILearnRepository
) {
    operator fun invoke(userId: String, setId: String): Flow<List<SrsCard>> {
        return repository.getDueCards(userId, setId)
    }
}
