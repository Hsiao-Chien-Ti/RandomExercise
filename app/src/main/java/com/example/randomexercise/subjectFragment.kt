package com.example.randomexercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.randomexercise.databinding.FragmentSubjectBinding

class subjectFragment : Fragment() {
    private val viewModel: RandomExerciseViewModel by activityViewModels {
        RandomExerciseViewModelFactory(
            (activity?.application as RandomExerciseApplication).database.ExDao()
        )
    }

    private var _binding: FragmentSubjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val navigationArgs: subjectFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val m_Adapter =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item)
        if (navigationArgs.mode == 0) {
            viewModel.subjectNames.observe(this.viewLifecycleOwner) { items ->
                run {
                    m_Adapter.clear()
                    items.forEach { m_Adapter.add(it) }
                }
            }
        } else {
            viewModel.subjectWrongNames.observe(this.viewLifecycleOwner) { items ->
                run {
                    m_Adapter.clear()
                    items.forEach { m_Adapter.add(it) }
                }
            }
        }

        binding.subjectSpinner.adapter = m_Adapter
        binding.drawLot.setOnClickListener {
            val subject = binding.subjectSpinner.getSelectedItem().toString()
//            viewModel.drawLot(subject)
            val action = subjectFragmentDirections.actionSubjectFragmentToExerciseFragment(
                subject,
                navigationArgs.mode
            )
            this.findNavController().navigate(action)
        }
    }
}