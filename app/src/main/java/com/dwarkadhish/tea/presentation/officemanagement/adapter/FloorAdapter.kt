package com.dwarkadhish.tea.presentation.officemanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dwarkadhish.tea.data.officeManagement.floor.Floor
import com.dwarkadhish.tea.databinding.FloorsItemsBinding

class FloorAdapter(
    private val floorList: List<Floor>,
    private val onFloorClick: (String) -> Unit,
    private val onFloorLongClick: (String,String) -> Unit
) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    inner class FloorViewHolder(private val binding: FloorsItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(floor: Floor) {

            binding.tvFloorNumber.text = floor.floorName
            binding.root.setOnClickListener {
                onFloorClick(floor.floorId)
            }
            binding.root.setOnLongClickListener {
                onFloorLongClick(floor.floorId,floor.floorName) // Trigger the callback on long-press
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val binding = FloorsItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        holder.bind(floorList[position])
    }

    override fun getItemCount(): Int = floorList.size
}
