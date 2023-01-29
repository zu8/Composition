package com.zuas.study.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zuas.study.composition.R
import com.zuas.study.composition.data.GameRepositoryImpl
import com.zuas.study.composition.domain.entity.GameResult
import com.zuas.study.composition.domain.entity.GameSettings
import com.zuas.study.composition.domain.entity.Level
import com.zuas.study.composition.domain.usecases.GenerateQuestionUseCase
import com.zuas.study.composition.domain.usecases.GetGameSettingsUseCase
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GameViewModel(
    application: Application) : AndroidViewModel(application) {
    private val repository = GameRepositoryImpl
    private lateinit var settings: GameSettings
    private val context = application
    private var timer: CountDownTimer? = null

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)


    private val _onGameOver = MutableLiveData<GameResult>()
    val onGameOver: LiveData<GameResult>
        get() = _onGameOver

    private val _isGameStart = MutableLiveData<Boolean>(false)
    val isGameStart: LiveData<Boolean>
        get() = _isGameStart

    private val _timeLeft = MutableLiveData<String>()
    val timeLeft: LiveData<String>
        get() = _timeLeft

    private val _visibleNumber = MutableLiveData<String>()
    val visibleNumber: LiveData<String>
        get() = _visibleNumber

    private val _sum = MutableLiveData<String>()
    val sum: LiveData<String>
        get() = _sum

    private val _options = MutableLiveData<List<String>>()
    val options: LiveData<List<String>>
        get() = _options

    private val _rightAnswer = MutableLiveData<Int>()
    val rightAnswer: LiveData<Int>
        get() = _rightAnswer

    private val _rightAnswersQuantity = MutableLiveData<Int>()
    val rightAnswersQuantity: LiveData<Int>
        get() = _rightAnswersQuantity

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _labelProgress = MutableLiveData<String>()
    val labelProgress: LiveData<String>
        get() = _labelProgress

    private var questionsQuantity = 0
    private var rightAnswers = 0


    fun startNewGame(level: Level) {
        _isGameStart.value = true
        _rightAnswersQuantity.value = 0
        _percentOfRightAnswers.value = 0
        fetchGameSettings(level)
        startTimer(settings.gameTimeInSeconds)
        newQuestion()
    }

    private fun newQuestion() {
        val (sum, visibleNum, options) = generateQuestionUseCase(settings.maxSumValue)
        _visibleNumber.value = visibleNum.toString()
        _sum.value = sum.toString()
        val tmpList = mutableListOf<String>()
        for (digit in options) {
            if ((sum - visibleNum) == digit) _rightAnswer.value = digit
            tmpList.add(digit.toString())
        }
        _options.value = tmpList

    }

    fun onAnswerQuestion(option: String) {
        questionsQuantity++
        if (option.toInt() == rightAnswer.value) {
            _rightAnswersQuantity.value = ++rightAnswers
        }
        val percentOfRightAnswers =
            ((rightAnswersQuantity.value)?.times(100))?.div((questionsQuantity))
        percentOfRightAnswers?.let {
            _percentOfRightAnswers.value = it
        }
        _labelProgress.value = String.format(
            context.resources.getString(R.string.progress_answers),
            rightAnswersQuantity.value,
            settings.minCountOfRightAnswers
        )
        newQuestion()
    }

    private fun fetchGameSettings(level: Level) {
        settings = getGameSettingsUseCase(level)
    }

    private fun startTimer(gameTimeInSeconds: Int) {

        val timeInMilliseconds = gameTimeInSeconds * 1000L + 1000L
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
                _onGameOver.value = fetchResult()
            }
        }.start()
    }

    private fun fetchResult(): GameResult? {
        val countOfRightAnswers = rightAnswers
        val percentOfRightAnswers = _percentOfRightAnswers.value
        val winner = countOfRightAnswers.let {
            percentOfRightAnswers?.let { it1 ->
                isWinner(it, it1)
            }
        }
        val result =
            winner?.let { GameResult(it, countOfRightAnswers, questionsQuantity, settings) }
        _isGameStart.value = false
        return result

    }

    private fun isWinner(rightAnswers: Int, countOfQuestions: Int): Boolean {
        return (settings.minCountOfRightAnswers <= rightAnswers
                && settings.minPercentOfRightAnswers <= countOfQuestions)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

}