package com.dwarkadhish.tea.presentation.stock.dialog

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dwarkadhish.tea.databinding.FragmentAddCategoryDialogBinding

class AddCategoryDialogFragment(
    private val onStockCategoryAdded: (String) -> Unit // Callback lambda to pass office number
) : DialogFragment() {

    private var _binding: FragmentAddCategoryDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment with view binding
        _binding = FragmentAddCategoryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle the button click
        binding.btnAddCategory.setOnClickListener {
            val floorNumber = binding.etCategory.text.toString()

            if (floorNumber.isEmpty()) {
                // Handle validation if the office number is not entered
                binding.etCategory.error = "Please enter an floor number"
            } else {
                // Pass the office number back via the lambda
                onStockCategoryAdded(floorNumber)

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
