package com.example.randomexercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.randomexercise.databinding.FragmentUpdateBinding

class updateFragment : Fragment() {
    private val viewModel: RandomExerciseViewModel by activityViewModels {
        RandomExerciseViewModelFactory(
            (activity?.application as RandomExerciseApplication).database.ExDao()
        )
    }
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ExListAdapter {
            val action = updateFragmentDirections.actionUpdateFragment2ToDetailFragment2(it.subject)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        viewModel.allSubject.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.floatingActionButton.setOnClickListener {
            val action = updateFragmentDirections.actionUpdateFragment2ToAddExFragment()
            this.findNavController().navigate(action)
        }
    }
}