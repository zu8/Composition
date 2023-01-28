package com.zuas.study.composition.domain.repository

import com.zuas.study.composition.domain.entity.GameSettings
import com.zuas.study.composition.domain.entity.Level
import com.zuas.study.composition.domain.entity.Question

interface GameRepository {

    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question
    fun getGameSettings(level: Level): GameSettings
}