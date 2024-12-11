package com.dwarkadhish.tea.presentation.officemanagement.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.officeManagement.floor.Floor
import com.dwarkadhish.tea.databinding.FragmentAddFloorManagementBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.extensions.hide
import com.dwarkadhish.tea.presentation.extensions.show
import com.dwarkadhish.tea.presentation.officemanagement.adapter.FloorAdapter
import com.dwarkadhish.tea.presentation.officemanagement.dialogs.AddFloorDialogFragment
import com.dwarkadhish.tea.presentation.officemanagement.viewmodels.FloorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddFloorManagement : BaseFragment() {

    private val viewModel: FloorViewModel by viewModel()
    private var binding: FragmentAddFloorManagementBinding?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddFloorManagementBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding!!.root)
        viewModel.uiState.observe(viewLifecycleOwner,::handleUiState)
        initToolbarSetup()
        initFragmentListener()
    }

    private fun handleUiState(uiState: FloorViewModel.UiState) {
        handleLoading(uiState.loading)
        handleRoute(uiState.route)
        setupRecyclerView(uiState.getFloors)
    }

    private fun handleRoute(route: FloorViewModel.Route?) {
        route?.let {
            when(route){
                is FloorViewModel.Route.EmptyFloorFound -> handleEmptyMessage(route.noFloor)
                is FloorViewModel.Route.ResponseMessage -> handleResponse(route.responseMessage)
            }
            viewModel.onRoute()
        }
    }

    private fun handleEmptyMessage(noFloor: String) {
        binding?.rvFloorList?.hide()
        binding?.noData?.text = noFloor
        binding?.noData?.show()
    }

    private fun setupRecyclerView(floorList: List<Floor>?) {
        binding?.rvFloorList?.show()
        binding?.noData?.hide()
        binding?.rvFloorList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = floorList?.let {
                FloorAdapter(it,
                    onFloorClick = { floorId ->
                        val args = Bundle()
                        args.putString("floorId", floorId)
                        findNavController().navigate(R.id.action_floorFragment_to_officeFragment, args)
                    },
                    onFloorLongClick = { floorId,floorName -> showUpdateDeleteOptions(floorId,floorName) }
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

    private fun showUpdateFloorDialog(floorId: String,floorName: String) {
        val updateDialog = AddFloorDialogFragment(floorName) { newFloorName ->
            viewModel.updateFloor(floorId, newFloorName)
        }
        updateDialog.show(requireActivity().supportFragmentManager, "UpdateFloorDialogFragment")
    }

    private fun showDeleteConfirmationDialog(floorId: String,floorName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Floor")
            .setMessage("Are you sure you want to delete $floorName?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteFloor(floorId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun initFragmentListener() {
        binding?.idFABAdd?.setOnClickListener{
            val addFloorDialog = AddFloorDialogFragment("") { floorNumber ->
               viewModel.addFloor(floorNumber)
            }
            addFloorDialog.show(requireActivity().supportFragmentManager, "AddFloorDialogFragment")
        }
    }

    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.floor_management_label)
    }
}