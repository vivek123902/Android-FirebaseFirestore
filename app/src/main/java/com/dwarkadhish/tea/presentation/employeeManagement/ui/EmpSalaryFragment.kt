package com.dwarkadhish.tea.presentation.employeeManagement.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.empManagement.SalaryRecord
import com.dwarkadhish.tea.databinding.FragmentEmpSalaryBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.employeeManagement.adapter.EmpSalaryAdapter
import com.dwarkadhish.tea.presentation.employeeManagement.dialog.AddEmployeeSalaryDialog
import com.dwarkadhish.tea.presentation.employeeManagement.viewmodel.EmpSalaryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmpSalaryFragment : BaseFragment() {

    private var binding: FragmentEmpSalaryBinding? = null
    private val viewModel: EmpSalaryViewModel by viewModel()
    private lateinit var empId: String
    private lateinit var empName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEmpSalaryBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding?.root)

        initSetupToolbar()
        viewModel.uiState.observe(viewLifecycleOwner, ::handleUiState)
        viewModel.fetchSalaryRecords(empId)
        initClickListeners()
    }

    private fun initClickListeners() {
        binding?.idFABAdd?.setOnClickListener {
            showAddOrUpdateDialog(null)
        }
    }

    private fun handleUiState(uiState: EmpSalaryViewModel.UiState) {
        handleLoading(uiState.loading)
        setUpRecyclerView(uiState.getEmpSalary)
    }

    private fun setUpRecyclerView(empSalary: List<SalaryRecord>?) {
        binding?.rvEmpSalaryList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = EmpSalaryAdapter(
                empSalary ?: emptyList(),
                onEmpLongClick = { salaryRecord -> showUpdateDeleteOptions(salaryRecord) }
            )
        }
    }

    private fun showAddOrUpdateDialog(salaryRecord: SalaryRecord?) {
        val dialog = AddEmployeeSalaryDialog(salaryRecord) { updatedRecord ->
            if (salaryRecord == null) {
                viewModel.addSalaryRecord(empId, updatedRecord.date, updatedRecord.amount)
            } else {
                viewModel.updateEmpSalaryRecord(updatedRecord)
            }
        }
        dialog.show(requireActivity().supportFragmentManager, "AddEmployeeSalaryDialog")
    }

    private fun showUpdateDeleteOptions(salaryRecord: SalaryRecord) {
        val options = arrayOf("Update", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAddOrUpdateDialog(salaryRecord)
                    1 -> viewModel.deleteEmpSalaryRecord(empId,salaryRecord.id)
                }
            }
            .show()
    }

    private fun initSetupToolbar() {
        arguments?.let {
            empId = it.getString("empId").toString()
            empName = it.getString("empName").toString()
            baseBinding?.tvTitle?.text = empName
        }
    }
}
