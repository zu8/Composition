package com.zuas.study.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zuas.study.composition.R
import com.zuas.study.composition.data.GameRepositoryImpl
import com.zuas.study.composition.domain.entity.GameResult
import com.zuas.study.composition.domain.entity.GameSettings
import com.zuas.study.composition.domain.entity.Level
import com.zuas.study.composition.domain.entity.Question
import com.zuas.study.composition.domain.usecases.GenerateQuestionUseCase
import com.zuas.study.composition.domain.usecases.GetGameSettingsUseCase
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {
    private val repository = GameRepositoryImpl
    private lateinit var settings: GameSettings

    private var timer: CountDownTimer? = null

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private val _timeLeft = MutableLiveData<String>()
    val timeLeft: LiveData<String>
        get() = _timeLeft


    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var questionsQuantity = 0
    private var rightAnswers = 0

    init {
        startNewGame()
    }

    private fun startNewGame() {
        fetchGameSettings()
        startTimer()
        newQuestion()
        updateProgress()
    }

    private fun fetchGameSettings() {
        settings = getGameSettingsUseCase(level)
    }

    private fun startTimer() {
        val timeInMilliseconds = settings.gameTimeInSeconds * 1000L + 1000L
        val tick = 1000L
        timer = object : CountDownTimer(timeInMilliseconds, tick) {
            override fun onTick(millisUntilFinish: Long) {
                val duration = millisUntilFinish.toDuration(DurationUnit.MILLISECONDS)
                val timeString =
                    duration.toComponents { minutes, seconds, _ ->
                        String.format("%02d:%02d", minutes, seconds)
                    }
                _timeLeft.value = timeString

            }

            override fun onFinish() {
                finishGame()
            }
        }.start()
    }

    private fun finishGame(){
        _gameResult.value = GameResult(
            enoughCount.value == true && enoughPercent.value == true,
            rightAnswers,
            questionsQuantity,
            settings)
    }

    private fun newQuestion() {
        _question.value = generateQuestionUseCase(settings.maxSumValue)
    }

    private fun updateProgress() {
        val percentOfRightAnswers = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percentOfRightAnswers
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            rightAnswers,
            settings.minCountOfRightAnswers
        )
        _enoughCount.value = rightAnswers >= settings.minCountOfRightAnswers
        _enoughPercent.value = percentOfRightAnswers >= settings.minPercentOfRightAnswers

    }

    private fun calculatePercentOfRightAnswers(): Int {
        if (questionsQuantity == 0) return 0
        return ((rightAnswers/questionsQuantity.toDouble())*100).toInt()
    }


    fun onAnswerQuestion(option: String) {
        val rightAnswer = question.value?.rightAnswer
        if (option.toInt() == rightAnswer){
            rightAnswers++
        }
        questionsQuantity++

        updateProgress()
        newQuestion()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}