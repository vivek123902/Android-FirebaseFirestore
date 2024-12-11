package com.dwarkadhish.tea.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.databinding.FragmentHomeBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment


class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding!!.root)

        initToolbarSetup()
        initFragmentListener()
    }

    private fun initFragmentListener() {
        binding?.cvOffice?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_floorFragment)
        }
        binding?.cvStock?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_stockFragment)
        }
        binding?.cvImportExpense?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_incomeExpenseFragment)
        }

        binding?.cvEmpManagement?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_employeeFragment)
        }
    }

    private fun initToolbarSetup() {
        baseBinding?.tvTitle?.text = getString(R.string.app_name)
    }
}