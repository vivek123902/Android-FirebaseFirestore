package com.dwarkadhish.tea.presentation.stock.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.stock.Item
import com.dwarkadhish.tea.databinding.FragmentStockBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.extensions.hide
import com.dwarkadhish.tea.presentation.extensions.show
import com.dwarkadhish.tea.presentation.stock.adapter.StockListAdapter
import com.dwarkadhish.tea.presentation.stock.dialog.AddCategoryDialogFragment
import com.dwarkadhish.tea.presentation.stock.viewmodel.StockFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class StockFragment : BaseFragment() {
    private var binding: FragmentStockBinding?=null
    private val viewModel: StockFragmentViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStockBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding?.root)
        viewModel.uiState.observe(viewLifecycleOwner,::handleUiState)
        initToolbarSetup()
        initFragmentListener()
    }

    private fun handleUiState(uiState: StockFragmentViewModel.UiState) {
        handleLoading(uiState.loading)
        handleRoute(uiState.route)
        setUpRecyclerView(uiState.getStocks)
    }

    private fun handleRoute(route: StockFragmentViewModel.Route?) {
        route?.let {
            when(route){
                is StockFragmentViewModel.Route.EmptyFloorFound -> handleEmptyMessage(route.noStock)
                is StockFragmentViewModel.Route.ResponseMessage -> handleResponse(route.responseMessage)
            }
        }
    }
    private fun handleEmptyMessage(noFloor: String) {
        binding?.rvStockList?.hide()
        binding?.noData?.text = noFloor
        binding?.noData?.show()
    }
    private fun setUpRecyclerView(stockList: List<Item>?) {
        binding?.rvStockList?.show()
        binding?.noData?.hide()
        binding?.rvStockList?.apply {
            adapter = stockList?.let { StockListAdapter(it){ stockItems->
                showUpdateDeleteOptions(stockItems)
            } }
        }
    }

    private fun showUpdateDeleteOptions(item: Item) {
        val options = arrayOf("Update", "Delete")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showUpdateStock(item)
                1 -> showDeleteStockDialog(item)
            }
        }
        builder.show()
    }
    private fun showDeleteStockDialog(items: Item) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Office")
            .setMessage("Are you sure you want to delete this stock?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteStock(items)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showUpdateStock(item: Item) {
        val bundle = Bundle().apply {
            putString("id", item.id)
            putString("categoryName", item.categoryName)
            putString("stockQty", item.stockQty)
            putString("stockPrice", item.stockPrice)
            putString("entryDate", item.entryDate)
        }
        findNavController().navigate(R.id.action_stockFragment_to_addStockFragment, bundle)
    }

    private fun initFragmentListener() {
        binding?.idFABCategory?.setOnClickListener {
            val addCategoryDialog = AddCategoryDialogFragment { categoryName->
                viewModel.addCategory(categoryName)
            }
            addCategoryDialog.show(requireActivity().supportFragmentManager, "AddCategoryDialogFragment")
        }
        binding?.idFABAdd?.setOnClickListener {
            findNavController().navigate(R.id.action_stockFragment_to_addStockFragment)
        }
    }

    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.app_name)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStockList()
    }
}