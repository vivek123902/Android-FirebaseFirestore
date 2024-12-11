package com.dwarkadhish.tea.presentation.employeeManagement.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dwarkadhish.tea.databinding.FragmentAddFloorDialogBinding

class AddEmployeeDialog(private val empName: String,
                        private val onEmpAdded: (String) -> Unit // Callback lambda to pass office number
) : DialogFragment() {

    private var _binding: FragmentAddFloorDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFloorDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "કારિગરો ને એડ કારો"
        binding.ilFloorNumber.hint = "કારિગરો નું નામ"
        binding.btnAddFloor.text = "ઉમેરો"
        if (empName != "" || empName == null){
            binding.etFloorNumber.setText(empName)
        }
        // Handle the button click
        binding.btnAddFloor.setOnClickListener {
            val empName = binding.etFloorNumber.text.toString()

            if (empName.isEmpty()) {
                binding.etFloorNumber.error = "કારીગર નું નામ નાખો"
            } else {
                onEmpAdded(empName)
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
