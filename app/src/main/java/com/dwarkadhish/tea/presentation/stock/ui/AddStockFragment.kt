package com.dwarkadhish.tea.presentation.stock.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.stock.Item
import com.dwarkadhish.tea.databinding.FragmentAddStockBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.stock.viewmodel.AddStockFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddStockFragment : BaseFragment() {

    private var binding: FragmentAddStockBinding?=null
    private val viewModel by viewModel<AddStockFragmentViewModel>()
    private var stockId = ""
    private var categoryName = ""
    private var stockQty = ""
    private var stockPrice = ""
    private var entryDate = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddStockBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding!!.root)
        viewModel.uiState.observe(viewLifecycleOwner,::handleUiState)
        initToolbarSetup()
        initFragmentListener()

        handleArguments()
    }

    private fun handleArguments() {
        arguments?.let { bundle ->
            stockId = bundle.getString("id").toString()
            categoryName = bundle.getString("categoryName").toString()
            stockQty = bundle.getString("stockQty").toString()
            stockPrice = bundle.getString("stockPrice").toString()
            entryDate = bundle.getString("entryDate").toString()

            // Set the values to your UI elements if not null
            binding?.spCategory?.setText(categoryName ?: "")
            binding?.etQty?.setText(stockQty ?: "")
            binding?.etPrice?.setText(stockPrice ?: "")
            binding?.etDate?.setText(entryDate ?: "")
        }

        // Save updated stock details when "Save" is clicked
        binding?.btnAddStock?.setOnClickListener {
            // Update stock if an ID is present, otherwise add as new
            if (stockId.isNotEmpty()) {
                viewModel.updateStock(Item(stockId, binding?.spCategory?.text.toString(), binding?.etQty?.text.toString(),  binding?.etPrice?.text.toString(), binding?.etDate?.text.toString()))
            } else {
                viewModel.addStock(binding?.spCategory?.text.toString(), binding?.etQty?.text.toString(), binding?.etPrice?.text.toString(), binding?.etDate?.text.toString())
            }
        }
    }

    private fun initFragmentListener() {
        binding?.btnAddStock?.setOnClickListener{
            viewModel.addStock(
                binding?.spCategory?.text.toString(),
                binding?.etQty?.text.toString(),
                binding?.etPrice?.text.toString(),
                binding?.etDate?.text.toString()
            )
        }

        binding?.etDate?.setOnClickListener{
            showDatePicker()
        }
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
    private fun handleUiState(uiState: AddStockFragmentViewModel.UiState) {
        handleLoading(uiState.loading)
        handleSpinner(uiState.categories)
        handleAddStock(uiState.addStockRoute)
    }

    private fun handleAddStock(addStockRoute: AddStockFragmentViewModel.AddStockRoute?) {
        addStockRoute?.let {
            when(addStockRoute){
                is AddStockFragmentViewModel.AddStockRoute.OnRouteToStockList -> {
                    if (addStockRoute.isRoute == true){
                        findNavController().popBackStack()
                    }
                }
                is AddStockFragmentViewModel.AddStockRoute.ValidationError -> {
                    handleResponse(addStockRoute.validationError)
                }
            }
            viewModel.onAddStockRouted()
        }
    }

    private fun handleSpinner(categories: List<String>) {
        binding?.spCategory?.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding?.spCategory!!)
            categories.forEachIndexed { index, category ->
                popupMenu.menu.add(0, index, 0, category)
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                binding?.spCategory?.setText(menuItem.title)
                true
            }
            popupMenu.show()
        }
    }


    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.stock_management_label)
    }
}