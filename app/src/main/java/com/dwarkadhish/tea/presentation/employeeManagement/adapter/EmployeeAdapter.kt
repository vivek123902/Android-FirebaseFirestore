package com.dwarkadhish.tea.presentation.employeeManagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dwarkadhish.tea.data.empManagement.Employee
import com.dwarkadhish.tea.databinding.FloorsItemsBinding

class EmployeeAdapter (
    private val empList: List<Employee>,
    private val onEmpClick: (String,String) -> Unit,
    private val onEmpLongClick: (String, String) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.FloorViewHolder>() {

    inner class FloorViewHolder(private val binding: FloorsItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(floor: Employee) {

            binding.tvFloorNumber.text = floor.name
            binding.root.setOnClickListener {
                onEmpClick(floor.id,floor.name)
            }
            binding.root.setOnLongClickListener {
                onEmpLongClick(floor.id,floor.name) // Trigger the callback on long-press
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val binding = FloorsItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        holder.bind(empList[position])
    }

    override fun getItemCount(): Int = empList.size
}