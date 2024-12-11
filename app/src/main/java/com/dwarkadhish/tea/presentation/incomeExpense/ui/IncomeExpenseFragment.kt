package com.dwarkadhish.tea.presentation.incomeExpense.ui

import android.os.Bundle
import android.view.View
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.incomeExpense.Record
import com.dwarkadhish.tea.databinding.FragmentIncomeExpenseBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.extensions.hide
import com.dwarkadhish.tea.presentation.extensions.show
import com.dwarkadhish.tea.presentation.incomeExpense.adapter.IncomeExpenseAdapter
import com.dwarkadhish.tea.presentation.incomeExpense.dialog.AddIncomeExpenseDialogFragment
import com.dwarkadhish.tea.presentation.incomeExpense.viewModel.IncomeExpenseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class IncomeExpenseFragment : BaseFragment() {

    private var binding: FragmentIncomeExpenseBinding?=null
    private val viewModel: IncomeExpenseViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIncomeExpenseBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding?.root)
        viewModel.uiState.observe(viewLifecycleOwner,::handleUiState)
        initToolbarSetup()
        initFragmentListeners()
    }
    private fun handleUiState(uiState: IncomeExpenseViewModel.UiState) {
        handleLoading(uiState.loading)
        handleRoute(uiState.route)
        setupRecyclerView(uiState.records)
    }

    private fun setupRecyclerView(recordList: List<Record>?) {
        binding?.rvRecords?.apply {
            val recordAdapter = recordList?.let { IncomeExpenseAdapter(it) }
            adapter = recordAdapter
        }
    }

    private fun handleRoute(route: IncomeExpenseViewModel.Route?) {
        route?.let {
            when(route){
                is IncomeExpenseViewModel.Route.ResponseMessage -> handleResponse(route.message)
            }
            viewModel.onRoute()
        }
    }
    private fun initFragmentListeners() {
        binding?.idFABAdd?.setOnClickListener{
            val addIncomeExpenseDialogFragment = AddIncomeExpenseDialogFragment {
                viewModel.addRecord(
                    it.income,
                    it.expense,
                    it.date
                )
            }
            addIncomeExpenseDialogFragment.show(requireActivity().supportFragmentManager, "AddIncomeExpenseDialogFragment")
        }
    }

    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.income_expense)
    }
}