package com.zuas.study.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.zuas.study.composition.R
import com.zuas.study.composition.databinding.FragmentChooseLevelBinding
import com.zuas.study.composition.domain.entity.Level


class ChooseLevelFragment : Fragment() {

    private var _binding: FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
    get() = _binding ?: throw RuntimeException("FragmentChooseLevelBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLaunchersOnButtons()
    }

    private fun setLaunchersOnButtons() {
        with(binding) {
            buttonLevelTest.setOnClickListener {
                launchGameFragment(Level.TEST)
            }
            binding.buttonLevelEasy.setOnClickListener {
                launchGameFragment(Level.EASY)
            }
            binding.buttonLevelNormal.setOnClickListener {
                launchGameFragment(Level.NORMAL)
            }
            binding.buttonLevelHard.setOnClickListener {
                launchGameFragment(Level.HARD)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchGameFragment(level: Level){
      findNavController().navigate(
          ChooseLevelFragmentDirections.actionChooseLevelFragmentToGameFragment(level))
    }

}