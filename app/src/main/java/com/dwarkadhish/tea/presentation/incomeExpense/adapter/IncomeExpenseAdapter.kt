package com.dwarkadhish.tea.presentation.incomeExpense.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dwarkadhish.tea.data.incomeExpense.Record
import com.dwarkadhish.tea.databinding.IncomeExpenseItemBinding

class IncomeExpenseAdapter(
    private val records: List<Record>
) : RecyclerView.Adapter<IncomeExpenseAdapter.IncomeExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeExpenseViewHolder {
        val binding = IncomeExpenseItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IncomeExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeExpenseViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int = records.size

    inner class IncomeExpenseViewHolder(private val binding: IncomeExpenseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: Record) {
            binding.tvIncome.text = "આવક : ₹${record.income}"
            binding.tvExpense.text = "ખર્ચો: ₹${record.expense}"
            binding.tvDate.text = "તારીખ : ${record.date}"

            val income = record.income.toIntOrNull() ?: 0
            val expense = record.expense.toIntOrNull() ?: 0
            val profit = income - expense

            // Display profit in Gujarati font
            binding.tvProfit.text = "નફો : ₹$profit"
        }
    }
}
