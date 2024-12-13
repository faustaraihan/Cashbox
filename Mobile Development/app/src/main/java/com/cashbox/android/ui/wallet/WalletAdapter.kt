package com.cashbox.android.ui.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cashbox.android.data.model.WalletData
import com.cashbox.android.databinding.ItemWalletBinding
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah

class WalletAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<WalletData, WalletAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<WalletData>() {
        override fun areItemsTheSame(oldItem: WalletData, newItem: WalletData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WalletData, newItem: WalletData): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val itemBinding: ItemWalletBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: WalletData) {
            itemBinding.tvTitle.text = data.name
            itemBinding.tvAmount.text = formatToRupiah(data.amount)

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(data.id, data.name)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String)
    }
}
