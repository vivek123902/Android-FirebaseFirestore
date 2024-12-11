// TeaManagementFragment.kt

package com.dwarkadhish.tea.presentation.teamanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.databinding.FragmentTeaManagementBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment
import com.dwarkadhish.tea.presentation.extensions.hide
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TeaManagementFragment : BaseFragment() {
    private var binding: FragmentTeaManagementBinding? = null
    private val viewModel: TeaCountViewModel by viewModel()
    private var currentCount: Int = 0
    private var floorId = ""
    private var officeId = ""
    private val currentDate: String by lazy {
        SimpleDateFormat("d MMMM, yyyy", Locale.getDefault()).format(Date())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTeaManagementBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding!!.root)
        initToolbarSetup()
        initFragmentListener()
        fetchArguments()
        viewModel.uiState.observe(viewLifecycleOwner, ::handleUiState)
        viewModel.getTeaCountsByOffice(floorId, officeId)
    }

    private fun fetchArguments() {
        arguments?.let {
            floorId = it.getString("floorId").toString()
            officeId = it.getString("officeId").toString()
        }
    }

    private fun handleUiState(uiState: TeaCountViewModel.UiState) {
        handleLoading(uiState.loading)
        uiState.responseMessage?.let { handleResponse(it) }

        if (uiState.monthlyTeaCount != null) {
            binding?.tvThisMonthTeaCount?.text = "આ મહિના ના ટોકન : ${uiState.monthlyTeaCount}"


        }

        uiState.teaCounts?.let {
            currentCount = uiState.todayTeaCount
            binding?.tvCount?.text = currentCount.toString()
        }
    }

    private fun initFragmentListener() {
        binding?.apply {
            cvAdd.setOnClickListener { updateTeaCount(1) }
            cvMinus.setOnClickListener { updateTeaCount(-1) }
            btnAddTea.setOnClickListener { addTeaToDatabase() }
            history.setOnClickListener { showMonthYearPicker() }
        }
    }

    private fun updateTeaCount(increment: Int) {
        currentCount += increment
        if (currentCount < 0) currentCount = 0
        binding?.tvCount?.text = currentCount.toString()
    }

    private fun addTeaToDatabase() {
        viewModel.addTeaCount(floorId, officeId ,currentCount)
    }

    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.tea_manage_label)
        binding?.tvcurrentDate?.text = currentDate
    }

    private fun showMonthYearPicker() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, _ ->
            val monthIndex = selectedMonth + 1
            fetchMonthlyTeaCount(selectedYear.toString(), monthIndex.toString())
            val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date(selectedYear - 1900, selectedMonth, 1))
            binding?.tvcurrentDate?.text = "$monthName $selectedYear"

        }, year, month, 1)

        datePickerDialog.show()
    }

    private fun fetchMonthlyTeaCount(year: String, month: String) {
        viewModel.getTeaCountsByMonth(floorId, officeId, year, month)
        binding?.btnAddTea?.hide()
        binding?.cvAdd?.hide()
        binding?.cvMinus?.hide()
        binding?.cvCount?.hide()
    }
}
