package com.example.randomexercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.randomexercise.databinding.FragmentExerciseBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class exerciseFragment : Fragment() {
    private val viewModel: RandomExerciseViewModel by activityViewModels {
        RandomExerciseViewModelFactory(
            (activity?.application as RandomExerciseApplication).database.ExDao()
        )
    }
    private val navigationArgs: exerciseFragmentArgs by navArgs()
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    var chapter = 0
    var number = 0
    private fun back() {
        val action = exerciseFragmentDirections.actionExerciseFragmentToHomepageFragment()
        this.findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.drawLot(navigationArgs.subject, navigationArgs.mode)
        viewModel.pickedChapter.observe(this.viewLifecycleOwner) {
            binding.chapter.text = it.toString()
            chapter = it
        }
        viewModel.pickedNumber.observe(this.viewLifecycleOwner) {
            binding.number.text = it.toString()
            number = it
        }
        viewModel.noFlag.observe(this.viewLifecycleOwner) {
            if (it == true) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("此科目已無試題")
                    .setPositiveButton("回主選單") { _, _ -> back() }
                    .show()
                viewModel.noFlag.postValue(false)
                viewModel.setToZero(navigationArgs.subject, navigationArgs.mode)
            }
        }
        binding.correctButton.setOnClickListener {
            viewModel.setCorrect(navigationArgs.subject, chapter, number, navigationArgs.mode)
            viewModel.drawLot(navigationArgs.subject, navigationArgs.mode)
        }
        binding.wrongButton.setOnClickListener {
            viewModel.setWrong(navigationArgs.subject, chapter, number, navigationArgs.mode)
            viewModel.drawLot(navigationArgs.subject, navigationArgs.mode)
        }
        binding.passButton.setOnClickListener {
            viewModel.drawLot(navigationArgs.subject, navigationArgs.mode)
        }
    }
}