package com.example.randomexercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.randomexercise.data.Subject
import com.example.randomexercise.databinding.FragmentDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class detailFragment : Fragment() {
    private val viewModel: RandomExerciseViewModel by activityViewModels {
        RandomExerciseViewModelFactory(
            (activity?.application as RandomExerciseApplication).database.ExDao()
        )
    }
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val navigationArgs: detailFragmentArgs by navArgs()
    private fun bind(item: Subject) {
        binding.apply {
            subject.text = item.subject
            total.text = item.total.toString()
            remain.text = item.remain.toString()
            wrong.text = item.wrong.toString()
            restart.setOnClickListener { viewModel.restart(item.subject, item.total) }
            delete.setOnClickListener { showConfirmationDialog(item.subject) }
        }
    }

    lateinit var sub: Subject
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = navigationArgs.subject
        viewModel.getSubject(arg).observe(this.viewLifecycleOwner) { selectedItem ->
            sub = selectedItem
            bind(sub)
        }
    }

    private fun showConfirmationDialog(subject: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteSubject(subject)
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}