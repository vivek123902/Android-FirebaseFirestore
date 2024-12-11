package com.dwarkadhish.tea.presentation.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.databinding.FragmentBaseBinding
import com.dwarkadhish.tea.presentation.extensions.hide
import com.dwarkadhish.tea.presentation.extensions.show

open class BaseFragment : Fragment() {

    var baseBinding: FragmentBaseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        baseBinding = FragmentBaseBinding.inflate(layoutInflater)
        return baseBinding!!.root
    }

    fun handleLoading(loading: Boolean){
        if (loading){
            baseBinding?.loader?.show()
        }else{
            baseBinding?.loader?.hide()
        }
    }
    fun handleResponse(responseMessage: Int){
        Toast.makeText(requireContext(),responseMessage,Toast.LENGTH_SHORT).show()
    }
    fun handleResponse(responseMessage: String){
        Toast.makeText(requireContext(),responseMessage,Toast.LENGTH_SHORT).show()
    }
}