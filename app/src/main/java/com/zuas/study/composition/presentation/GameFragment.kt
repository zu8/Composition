package com.zuas.study.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zuas.study.composition.R
import com.zuas.study.composition.databinding.FragmentGameBinding
import com.zuas.study.composition.domain.entity.GameResult
import com.zuas.study.composition.domain.entity.Level


class GameFragment : Fragment() {

    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }
    private lateinit var level: Level
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setOptionsOnClickListeners()

    }

    private fun observeViewModel() {
        viewModel.isGameStart.observe(viewLifecycleOwner) {
            if (it == false) viewModel.startNewGame(level)
        }
        viewModel.timeLeft.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }
        viewModel.onGameOver.observe(viewLifecycleOwner) {
            if (it != null) launchGameFinishFragment(it)
        }
        viewModel.visibleNumber.observe(viewLifecycleOwner) {
            it?.let { binding.tvLeftNumber.text = it }
        }
        viewModel.sum.observe(viewLifecycleOwner) {
            it?.let { binding.tvSum.text = it }
        }
        viewModel.options.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvOption1.text = it[0]
                binding.tvOption2.text = it[1]
                binding.tvOption3.text = it[2]
                binding.tvOption4.text = it[3]
                binding.tvOption5.text = it[4]
                binding.tvOption6.text = it[5]
            }
        }
        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner){
            binding.progressBar.setProgress(it,true)

        }
        viewModel.labelProgress.observe(viewLifecycleOwner){
            binding.tvAnswersProgress.text = it
        }
    }

    private fun setOptionsOnClickListeners(){
        with(binding){
            tvOption1.setOnClickListener { viewModel.onAnswerQuestion(tvOption1.text.toString()) }
            tvOption2.setOnClickListener { viewModel.onAnswerQuestion(tvOption2.text.toString()) }
            tvOption3.setOnClickListener { viewModel.onAnswerQuestion(tvOption3.text.toString()) }
            tvOption4.setOnClickListener { viewModel.onAnswerQuestion(tvOption4.text.toString()) }
            tvOption5.setOnClickListener { viewModel.onAnswerQuestion(tvOption5.text.toString()) }
            tvOption6.setOnClickListener { viewModel.onAnswerQuestion(tvOption6.text.toString()) }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    private fun launchGameFinishFragment(result: GameResult) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(result))
            .addToBackStack(GameFragment.NAME)
            .commit()
    }

    companion object {

        const val NAME = "GameFragment"
        private const val KEY_LEVEL = "level"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }
}