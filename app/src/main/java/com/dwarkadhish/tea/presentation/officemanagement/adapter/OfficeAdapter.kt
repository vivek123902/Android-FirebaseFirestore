package com.dwarkadhish.tea.presentation.officemanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dwarkadhish.tea.data.officeManagement.office.Office
import com.dwarkadhish.tea.databinding.OfficeItemsBinding

class OfficeAdapter(
    private val officeList: List<Office>,
    private val onOfficeClick: (Office) -> Unit,
    private val onOfficeLongClick: (Office) -> Unit
) : RecyclerView.Adapter<OfficeAdapter.OfficeViewHolder>() {

    inner class OfficeViewHolder(private val binding: OfficeItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(office: Office) {
            binding.tvOfficeNumber.text = office.officeName

            binding.root.setOnClickListener {
                onOfficeClick(office)
            }
            binding.root.setOnLongClickListener {
                onOfficeLongClick(office)
                true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfficeViewHolder {
        val binding = OfficeItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfficeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfficeViewHolder, position: Int) {
        holder.bind(officeList[position])
    }

    override fun getItemCount(): Int = officeList.size
}
