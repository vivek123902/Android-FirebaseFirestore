package com.dwarkadhish.tea.presentation.employeeManagement.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dwarkadhish.tea.data.empManagement.Employee
import com.dwarkadhish.tea.data.empManagement.SalaryRecord
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class EmpSalaryViewModel(private var firestore: FirebaseFirestore) : ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    data class UiState(
        var loading: Boolean = false,
        var getEmpSalary: List<SalaryRecord>? = null,
        var route: Route? =null
    )

    sealed class Route {
        data class ResponseMessage(var responseMessage: String): Route()
    }


    fun fetchSalaryRecords(employeeId: String) {
        _uiState.update { it.copy(loading = true) }
        firestore.collection("salaryRecords")
            .whereEqualTo("employeeId", employeeId)
            .get()
            .addOnSuccessListener { documents ->
                val recordsList = documents.map { it.toObject(SalaryRecord::class.java) }
                    .sortedByDescending { record ->
                        SimpleDateFormat("d MMMM, yyyy", Locale.getDefault()).parse(record.date)
                    }
                _uiState.update { it.copy(loading = false, getEmpSalary = recordsList) }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
            }
    }



    fun addSalaryRecord(employeeId: String,date: String, amount: String) {
        _uiState.update { it.copy(loading = true) }
        val recordId = firestore.collection("salaryRecords").document().id
        val newRecord = SalaryRecord(id = recordId, employeeId = employeeId, date = date, amount = amount)
        firestore.collection("salaryRecords").document(recordId).set(newRecord)
            .addOnSuccessListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Added successfully")) }
                fetchSalaryRecords(employeeId)
            }  // Refresh records list
            .addOnFailureListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
            }
    }
    fun updateEmpSalaryRecord(updatedRecord: SalaryRecord) {
        _uiState.update { it.copy(loading = true) }
        firestore.collection("salaryRecords").document(updatedRecord.id)
            .set(updatedRecord)
            .addOnSuccessListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Updated successfully")) }
                fetchSalaryRecords(updatedRecord.employeeId) // Refresh records list
            }
            .addOnFailureListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Update failed! Please try again.")) }
            }
    }

    fun deleteEmpSalaryRecord(empId:String,salaryRecordId: String) {
        firestore.collection("salaryRecords").document(salaryRecordId)
            .delete()
            .addOnSuccessListener {
                _uiState.update { it.copy(route = Route.ResponseMessage("Deleted successfully")) }
                fetchSalaryRecords(empId)
            }
            .addOnFailureListener {
                _uiState.update { it.copy(route = Route.ResponseMessage("Delete failed")) }
            }
    }

}