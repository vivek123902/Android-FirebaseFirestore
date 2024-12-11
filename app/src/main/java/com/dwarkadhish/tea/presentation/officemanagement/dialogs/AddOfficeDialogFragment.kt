package com.dwarkadhish.tea.presentation.officemanagement.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dwarkadhish.tea.databinding.FragmentAddOfficeDialogBinding

class AddOfficeDialogFragment(
    private val officeName: String,
    private val onOfficeAdded: (String) -> Unit
): DialogFragment(){
    private var _binding: FragmentAddOfficeDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment with view binding
        _binding = FragmentAddOfficeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!(officeName==""|| officeName == null)){
            binding.etOfficeNumber.setText(officeName)
        }

        // Handle the button click
        binding.btnAddOffice.setOnClickListener {
            val officeNumber = binding.etOfficeNumber.text.toString()

            if (officeNumber.isEmpty()) {
                // Handle validation if the office number is not entered
                binding.etOfficeNumber.error = "Please enter an office number"
            } else {
                // Here you can pass the office number back to the activity or fragment
                // You can use an interface or shared ViewModel to pass data
                onOfficeAdded(officeNumber)
                // Dismiss the dialog after adding
                dismiss()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        // Set dialog to full-screen dimensions
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}