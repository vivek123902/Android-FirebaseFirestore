package com.dwarkadhish.tea.presentation.incomeExpense.dialog

import android.app.DatePickerDialog
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.dwarkadhish.tea.data.incomeExpense.Record
import com.dwarkadhish.tea.databinding.FragmentAddIncomeexpenseDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddIncomeExpenseDialogFragment(
    private val onIncomeExpenseCallBack: (Record) -> Unit // Callback lambda to pass office number
) : DialogFragment() {
    private var _binding: FragmentAddIncomeexpenseDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment with view binding
        _binding = FragmentAddIncomeexpenseDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentListener()
        // Handle the button click
    }

    private fun validate(): Boolean {
        return if(binding.etExpense.text?.isEmpty() == true){
            Toast.makeText(requireContext(),"Please enter expense",Toast.LENGTH_SHORT).show()
            false
        } else if(binding.etIncome.text?.isEmpty() == true){
            Toast.makeText(requireContext(),"Please enter income",Toast.LENGTH_SHORT).show()
            false
        } else if(binding.etDate.text?.isEmpty() == true){
            Toast.makeText(requireContext(),"Please select date",Toast.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }

    private fun initFragmentListener() {
        binding?.etDate?.setOnClickListener{
            showDatePicker()
        }
        binding?.btnAddIncomeExpense?.setOnClickListener {
            if (validate()){
                onIncomeExpenseCallBack(Record(
                    date = binding.etDate.text.toString(),
                    income = binding.etIncome.text.toString(),
                    expense = binding.etExpense.text.toString())
                    )
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Set dialog to full-screen dimensions
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            // Format and set date as "1 January, 2024"
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            val formattedDate = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault()).format(selectedDate)
            binding?.etDate?.setText(formattedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
