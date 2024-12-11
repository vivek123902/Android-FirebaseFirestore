package com.dwarkadhish.tea.presentation.employeeManagement.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.empManagement.Employee
import com.dwarkadhish.tea.databinding.FragmentEmployeBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.employeeManagement.viewmodel.EmpViewModel
import com.dwarkadhish.tea.presentation.employeeManagement.adapter.EmployeeAdapter
import com.dwarkadhish.tea.presentation.employeeManagement.dialog.AddEmployeeDialog
import com.dwarkadhish.tea.presentation.extensions.show
import com.dwarkadhish.tea.presentation.officemanagement.dialogs.AddFloorDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeFragment : BaseFragment() {

    private var binding: FragmentEmployeBinding? = null
    private val viewModel: EmpViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEmployeBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding!!.root)
        viewModel.uiState.observe(viewLifecycleOwner,::handleUiState)
        initToolbarSetup()
        initFragmentListener()
    }
    private fun handleUiState(uiState: EmpViewModel.UiState) {
        handleLoading(uiState.loading)
        handleRoute(uiState.route)
        setupRecyclerView(uiState.getEmp)
    }

    private fun setupRecyclerView(emp: List<Employee>?) {
        binding?.rvEmpList?.show()
        binding?.rvEmpList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = emp?.let {
                EmployeeAdapter(it,
                    onEmpClick = { empId,empName ->
                        val args = Bundle()
                        args.putString("empId", empId)
                        args.putString("empName", empName)
                        findNavController().navigate(R.id.action_employeeFragment_to_empSalaryManagement, args)
                    },
                    onEmpLongClick = { floorId,floorName -> showUpdateDeleteOptions(floorId,floorName) }
                )
            }
        }
    }

    private fun showUpdateDeleteOptions(floorId: String,floorName: String) {
        val options = arrayOf("Update", "Delete")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showUpdateFloorDialog(floorId,floorName)
                1 -> showDeleteConfirmationDialog(floorId,floorName)
            }
        }
        builder.show()
    }

    private fun showUpdateFloorDialog(empId: String, empName: String) {
        val updateDialog = AddFloorDialogFragment(empName) { newFloorName ->
            viewModel.updateEmpName(empId, newFloorName)
        }
        updateDialog.show(requireActivity().supportFragmentManager, "UpdateFloorDialogFragment")
    }

    private fun showDeleteConfirmationDialog(empId: String, empName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Employee Name")
            .setMessage("Are you sure you want to delete $empName?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteEmp(empId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun handleRoute(route: EmpViewModel.Route?) {
        route?.let {
            when(route){
                is EmpViewModel.Route.ResponseMessage -> handleResponse(route.responseMessage)
            }
            viewModel.onRoute()
        }
    }
    private fun initFragmentListener() {
        binding?.idFABAdd?.setOnClickListener{
            val addFloorDialog = AddEmployeeDialog("") { empName ->
                viewModel.addEmployee(empName)
            }
            addFloorDialog.show(requireActivity().supportFragmentManager, "AddFloorDialogFragment")
        }
    }

    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = "કારિગરો નું લિસ્ટ"
    }
}
