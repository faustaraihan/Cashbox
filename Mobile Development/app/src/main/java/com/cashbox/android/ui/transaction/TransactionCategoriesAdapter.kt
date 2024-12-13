package com.cashbox.android.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cashbox.android.databinding.ItemCategoriesBinding
import com.cashbox.android.utils.getImageResource

class TransactionCategoriesAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<String, TransactionCategoriesAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoriesBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val itemBinding: ItemCategoriesBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: String) {
            itemBinding.ivCategory.setImageResource(data.getImageResource())
            itemBinding.tvCategory.text = data

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(data)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(transactionCategory: String)
    }
}