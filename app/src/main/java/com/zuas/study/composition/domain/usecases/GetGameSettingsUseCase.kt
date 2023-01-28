package com.zuas.study.composition.domain.usecases

import com.zuas.study.composition.domain.entity.GameSettings
import com.zuas.study.composition.domain.entity.Level
import com.zuas.study.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val gameRepository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return gameRepository.getGameSettings(level)
    }

}