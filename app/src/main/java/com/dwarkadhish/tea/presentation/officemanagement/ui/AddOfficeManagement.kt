package com.dwarkadhish.tea.presentation.officemanagement.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.officeManagement.office.Office
import com.dwarkadhish.tea.databinding.FragmentAddOfficeManagementBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.extensions.hide
import com.dwarkadhish.tea.presentation.extensions.show
import com.dwarkadhish.tea.presentation.officemanagement.adapter.OfficeAdapter
import com.dwarkadhish.tea.presentation.officemanagement.dialogs.AddOfficeDialogFragment
import com.dwarkadhish.tea.presentation.officemanagement.viewmodels.OfficeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddOfficeManagement : BaseFragment() {

    private val viewModel: OfficeViewModel by viewModel()
    private var binding: FragmentAddOfficeManagementBinding?=null
    var floorId = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddOfficeManagementBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding!!.root)
        viewModel.uiState.observe(viewLifecycleOwner,::handleUiState)
        initToolbarSetup()
        fetchArguments()
        initFragmentListener()
        viewModel.getOfficesByFloor(floorId)
    }

    private fun fetchArguments() {
        if (arguments!=null){
            floorId = arguments?.getString("floorId").toString()
        }
    }

    private fun handleUiState(uiState: OfficeViewModel.UiState) {
        handleLoading(uiState.loading)
        handleRoute(uiState.route)
        setupRecyclerView(uiState.getOffices)
    }

    private fun handleRoute(route: OfficeViewModel.Route?) {
        route?.let {
            when(route){
                is OfficeViewModel.Route.EmptyFloorFound -> handleEmptyMessage(route.noFloor)
                is OfficeViewModel.Route.ResponseMessage -> handleResponse(route.responseMessage)
            }
            viewModel.onRoute()
        }
    }
    private fun initFragmentListener() {
        binding?.idFABAdd?.setOnClickListener{
            val addOfficeDialog = AddOfficeDialogFragment("") { officeNumber ->
                println("Office number added: $officeNumber")
                viewModel.addOffice(floorId,officeNumber)
            }
            addOfficeDialog.show(requireActivity().supportFragmentManager, "AddOfficeDialogFragment")
        }
    }
    private fun handleEmptyMessage(noFloor: String) {
        binding?.rvOfficeList?.hide()
        binding?.noData?.text = noFloor
        binding?.noData?.show()
    }
    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.office_management_label)
    }
    private fun setupRecyclerView(offices: List<Office>?) {
        binding?.rvOfficeList?.show()
        binding?.noData?.hide()
        binding?.rvOfficeList?.apply {
            adapter = offices?.let {
                OfficeAdapter(
                    it,
                    onOfficeClick = { office ->
                        val args = Bundle().apply {
                            putString("officeName", office.officeName)
                            putString("officeId", office.officeId)
                            putString("floorId", office.floorId) }
                        findNavController().navigate(R.id.action_officeFragment_to_teaManagement, args)
                    },
                    onOfficeLongClick = {office: Office -> showUpdateDeleteOptions(office) }
                )
            }
        }
    }

    private fun showUpdateDeleteOptions(office: Office) {
        val options = arrayOf("Update", "Delete")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showUpdateOfficeDialog(office)
                1 -> showDeleteConfirmationDialog(office)
            }
        }
        builder.show()
    }
    private fun showDeleteConfirmationDialog(office: Office) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Office")
            .setMessage("Are you sure you want to delete ${office.officeName}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteOffice(office)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showUpdateOfficeDialog(office: Office) {
        val updateOfficeDialog = AddOfficeDialogFragment(office.officeName) { updatedOfficeName ->
            viewModel.updateOffice(office.copy(officeName = updatedOfficeName))
        }
        updateOfficeDialog.show(requireActivity().supportFragmentManager, "UpdateOfficeDialogFragment")
    }

}