package com.example.randomexercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.randomexercise.data.Subject
import com.example.randomexercise.databinding.ExListBinding

class ExListAdapter(private val onSubjectClicked: (Subject) -> Unit) :
    ListAdapter<Subject, ExListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ExListBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onSubjectClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: ExListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sub: Subject) {
            binding.apply {
                subject.text = sub.subject
                remain.text = sub.remain.toString()
                wrong.text = sub.wrong.toString()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Subject>() {
            override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
                return oldItem.subject == newItem.subject
            }
        }
    }
}