package com.dwarkadhish.tea.presentation.employeeManagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dwarkadhish.tea.data.empManagement.SalaryRecord
import com.dwarkadhish.tea.databinding.EmpSalaryItemBinding

class EmpSalaryAdapter (
    private val empSalaryList: List<SalaryRecord>,
    private val onEmpLongClick: (SalaryRecord) -> Unit
) : RecyclerView.Adapter<EmpSalaryAdapter.EmpSalaryViewHolder>() {

    inner class EmpSalaryViewHolder(private val binding: EmpSalaryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(salaryRecord: SalaryRecord) {

            binding.tvSalaryAmount.text = "પગાર / ઉપાડ રકમ : ₹ "+salaryRecord.amount
            binding.tvDate.text = "તારીખ : "+salaryRecord.date

            binding.root.setOnLongClickListener {
                onEmpLongClick(salaryRecord) // Trigger the callback on long-press
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpSalaryViewHolder {
        val binding = EmpSalaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmpSalaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmpSalaryViewHolder, position: Int) {
        holder.bind(empSalaryList[position])
    }

    override fun getItemCount(): Int = empSalaryList.size
}