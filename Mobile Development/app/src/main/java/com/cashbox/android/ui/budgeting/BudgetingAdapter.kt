package com.cashbox.android.ui.budgeting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cashbox.android.data.model.BudgetingResponse
import com.cashbox.android.databinding.ItemListBudgetingBinding
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah
import com.cashbox.android.utils.getImageResource
import com.cashbox.android.utils.toExpenseCategoryText

class BudgetingAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<BudgetingResponse, BudgetingAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<BudgetingResponse>() {
        override fun areItemsTheSame(
            oldItem: BudgetingResponse,
            newItem: BudgetingResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BudgetingResponse,
            newItem: BudgetingResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListBudgetingBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val itemBinding: ItemListBudgetingBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: BudgetingResponse) {
            Glide.with(itemView)
                .load(data.category.toExpenseCategoryText().getImageResource())
                .centerCrop()
                .transform(CircleCrop())
                .into(itemBinding.ivCategory)

            itemBinding.tvTitle.text = data.category.toExpenseCategoryText()
            itemBinding.tvAmountLimit.text = formatToRupiah(data.amount)

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(data.ids)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(ids: MutableList<Int>)
    }
}