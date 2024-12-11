package com.dwarkadhish.tea.presentation.stock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dwarkadhish.tea.data.stock.Item
import com.dwarkadhish.tea.databinding.StockItemBinding

class StockListAdapter(
    private val itemList: List<Item>,
    private val onStockLongClick: (Item) -> Unit
) : RecyclerView.Adapter<StockListAdapter.ItemViewHolder>() {

    // Update constructor to accept onStockLongClick
    class ItemViewHolder(
        private val binding: StockItemBinding,
        private val onStockLongClick: (Item) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.tvCatagoryName.text = "નામ : " + item.categoryName
            binding.tvQty.text = "જથ્થો : " + item.stockQty
            binding.tvPrice.text = "પૈસા : " + item.stockPrice
            binding.tvDate.text = "તારીખ : " + item.entryDate

            // Set long click listener
            binding.root.setOnLongClickListener {
                onStockLongClick(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StockItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding, onStockLongClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = itemList.size
}
