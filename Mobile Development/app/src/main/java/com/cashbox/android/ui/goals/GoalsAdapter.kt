package com.cashbox.android.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cashbox.android.data.model.ListGoals
import com.cashbox.android.databinding.ItemGoalsBinding
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah

class GoalsAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<ListGoals, GoalsAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<ListGoals>() {
        override fun areItemsTheSame(oldItem: ListGoals, newItem: ListGoals): Boolean {
            return oldItem.idGoals == newItem.idGoals
        }

        override fun areContentsTheSame(oldItem: ListGoals, newItem: ListGoals): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGoalsBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val itemBinding: ItemGoalsBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: ListGoals) {
            itemBinding.tvTitle.text = data.name
            itemBinding.tvCurrent.text = formatToRupiah(data.currentAmount)
            itemBinding.tvTarget.text = formatToRupiah(data.targetAmount)

            val percentageProgress = (data.currentAmount.toDouble() / data.targetAmount) * 100
            itemBinding.pbBudgeting.progress = percentageProgress.toInt()

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(data.idGoals, data.name, data.targetAmount)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String, amount: Long)
    }
}