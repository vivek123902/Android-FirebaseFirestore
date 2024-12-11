package com.dwarkadhish.tea.koin

import com.dwarkadhish.tea.presentation.employeeManagement.viewmodel.EmpSalaryViewModel
import com.dwarkadhish.tea.presentation.employeeManagement.viewmodel.EmpViewModel
import com.dwarkadhish.tea.presentation.incomeExpense.viewModel.IncomeExpenseViewModel
import com.dwarkadhish.tea.presentation.officemanagement.viewmodels.FloorViewModel
import com.dwarkadhish.tea.presentation.officemanagement.viewmodels.OfficeViewModel
import com.dwarkadhish.tea.presentation.stock.viewmodel.AddStockFragmentViewModel
import com.dwarkadhish.tea.presentation.stock.viewmodel.StockFragmentViewModel
import com.dwarkadhish.tea.presentation.teamanagement.TeaCountViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Provide Firebase Firestore instance
    single { FirebaseFirestore.getInstance() }

    // Provide ViewModel instances
    viewModel { FloorViewModel(get()) }
    viewModel { OfficeViewModel(get()) }
    viewModel { TeaCountViewModel(get()) }
    viewModel { StockFragmentViewModel(get()) }
    viewModel { AddStockFragmentViewModel(get()) }
    viewModel { IncomeExpenseViewModel(get()) }
    viewModel { EmpViewModel(get()) }
    viewModel { EmpSalaryViewModel(get()) }
}
