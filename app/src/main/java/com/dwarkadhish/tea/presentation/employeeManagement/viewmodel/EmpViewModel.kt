package com.dwarkadhish.tea.presentation.employeeManagement.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwarkadhish.tea.data.empManagement.Employee
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class EmpViewModel(private var firestore: FirebaseFirestore): ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    data class UiState(
        var loading: Boolean = false,
        var getEmp: List<Employee>? = null,
        var route: Route? =null
    )

    sealed class Route {
        data class ResponseMessage(var responseMessage: String): Route()
    }

    init {
        getEmployee()
    }

    private fun getEmployee() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                firestore.collection("employees")
                    .get()
                    .addOnSuccessListener { documents ->
                        val employeeList = documents.map { it.toObject(Employee::class.java) }
                        _uiState.update { it.copy(loading = false, getEmp = employeeList) }
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
                    }
            }catch (e: Exception){
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
            }
        }
    }
    fun addEmployee(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val employeeId = firestore.collection("employees").document().id
            val newEmployee = Employee(id = employeeId, name = name)

            firestore.collection("employees").document(employeeId).set(newEmployee)
                .addOnSuccessListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Employee added successfully!")) }
                    getEmployee()
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Failed to add employee")) }
                }
        }
    }
    // Function to update employee name
    fun updateEmpName(empId: String, newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            firestore.collection("employees").document(empId)
                .update("name", newName)
                .addOnSuccessListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Employee updated successfully!")) }
                    getEmployee() // Refresh employee list after update
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Failed to update employee")) }
                }
        }
    }

    // Function to delete employee
    fun deleteEmp(empId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            firestore.collection("employees").document(empId)
                .delete()
                .addOnSuccessListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Employee deleted successfully!")) }
                    getEmployee() // Refresh employee list after deletion
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Failed to delete employee")) }
                }
        }
    }
    fun onRoute(){
        _uiState.update { it.copy(route = null) }
    }
}
