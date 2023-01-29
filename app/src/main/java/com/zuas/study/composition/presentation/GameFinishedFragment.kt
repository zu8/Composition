package com.zuas.study.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.zuas.study.composition.R
import com.zuas.study.composition.databinding.FragmentGameFinishedBinding
import com.zuas.study.composition.domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    private lateinit var result :GameResult
    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retryGame()
            }
        })
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
        initView()
    }

    private fun initView(){

        with(binding) {
            emojiResult.setImageResource(getSmileResId())
            tvRequiredAnswers.text = String.format(
                getString(R.string.required_score),
                result.gameSettings.minCountOfRightAnswers
            )
            tvScoreAnswers.text = String.format(
                getString(R.string.score_answers),
                result.countOfRightAnswers
            )
            tvRequiredPercentage.text = String.format(
                getString(R.string.required_percentage),
                result.gameSettings.minPercentOfRightAnswers
            )
            val percentage = (result.countOfRightAnswers*100)/(result.countOfQuestions)
            tvScorePercentage.text = String.format(
                getString(R.string.score_percentage),
               percentage
            )
        }
    }
    private fun getSmileResId(): Int {
        return if (result.winner) {
            R.drawable.ic_robo_win_or_draw
        } else {
            R.drawable.ic_robo_loose
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retryGame(){
        requireActivity().supportFragmentManager
            .popBackStack(GameFragment.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun parseArgs(){
       requireArguments().getParcelable<GameResult>(GAME_RESULTS)?.let {
           result = it
       }
    }

    companion object{

        private const val GAME_RESULTS = "results"

        fun newInstance(result: GameResult): GameFinishedFragment{
            return GameFinishedFragment().apply {
               arguments = Bundle().apply {
                   putParcelable(GAME_RESULTS, result)
               }
            }

        }
    }
}