package com.cashbox.android.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cashbox.android.data.model.SaveData
import com.cashbox.android.databinding.ItemSaveBinding
import com.cashbox.android.utils.DateHelper.convertDateToIndonesianFormat
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah

class SaveAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<SaveData, SaveAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<SaveData>() {
        override fun areItemsTheSame(oldItem: SaveData, newItem: SaveData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SaveData, newItem: SaveData): Boolean {
            return newItem == oldItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSaveBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val itemBinding: ItemSaveBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: SaveData) {
            itemBinding.tvTitle.text = data.description
            itemBinding.tvDate.text = convertDateToIndonesianFormat(data.date.substring(0, 10))
            itemBinding.tvAmount.text = formatToRupiah(data.amount)

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(data.id, data.description, data.amount, data.date)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int, description: String, amount: Long, date: String)
    }
}