package com.dwarkadhish.tea.presentation.employeeManagement.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dwarkadhish.tea.data.empManagement.SalaryRecord
import com.dwarkadhish.tea.databinding.EmpSalaryDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEmployeeSalaryDialog(
    private var salaryRecord: SalaryRecord?,
    private val onAddOrUpdateEmpSalary: (SalaryRecord) -> Unit
) : DialogFragment() {

    private var _binding: EmpSalaryDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmpSalaryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initFragmentListener()
    }

    private fun initViews() {
        salaryRecord?.let {
            binding.etDate.setText(it.date)
            binding.etSalary.setText(it.amount)
        }
    }

    private fun validate(): Boolean {
        return when {
            binding.etSalary.text.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please enter salary", Toast.LENGTH_SHORT).show()
                false
            }
            binding.etDate.text.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please select date", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun initFragmentListener() {
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnAddSalary.setOnClickListener {
            if (validate()) {
                val updatedSalaryRecord = salaryRecord?.copy(
                    date = binding.etDate.text.toString(),
                    amount = binding.etSalary.text.toString()
                ) ?: SalaryRecord(
                    id = "",
                    employeeId = "",  // Ensure `employeeId` is set before saving
                    date = binding.etDate.text.toString(),
                    amount = binding.etSalary.text.toString()
                )
                onAddOrUpdateEmpSalary(updatedSalaryRecord)
                dismiss()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            val formattedDate = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault()).format(selectedDate)
            binding.etDate.setText(formattedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
