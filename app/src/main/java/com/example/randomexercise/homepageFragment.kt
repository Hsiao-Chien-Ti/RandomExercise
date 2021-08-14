package com.example.randomexercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.randomexercise.databinding.FragmentHomepageBinding

class homepageFragment : Fragment() {
    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newExercise.setOnClickListener {
            val action =
                homepageFragmentDirections.actionHomepageFragmentToSubjectFragment(0)//mode0->new,mode1->wrong
            this.findNavController().navigate(action)
        }
        binding.wrongExercise.setOnClickListener {
            val action = homepageFragmentDirections.actionHomepageFragmentToSubjectFragment(1)
            this.findNavController().navigate(action)
        }
        binding.updateExercise.setOnClickListener {
            val action = homepageFragmentDirections.actionHomepageFragmentToUpdateFragment2()
            this.findNavController().navigate(action)
        }
    }
}