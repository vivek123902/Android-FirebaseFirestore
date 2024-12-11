package com.dwarkadhish.tea.presentation.incomeExpense.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwarkadhish.tea.data.incomeExpense.Record
import com.dwarkadhish.tea.presentation.extensions.update
import com.dwarkadhish.tea.presentation.officemanagement.viewmodels.FloorViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class IncomeExpenseViewModel(private val fireStore: FirebaseFirestore) : ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    // UiState class to handle loading and route states
    data class UiState(
        val loading: Boolean = false,
        val route: Route? = null,
        val records: List<Record>? = null
    )

    sealed class Route {
        data class ResponseMessage(val message: String) : Route()
    }

    init {
        getRecords()
    }
    // Function to add a record
    fun addRecord(income: String, expense: String, date: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                val recordCollection = fireStore.collection("incomeExpenseRecords")
                val recordId = recordCollection.document().id
                val recordData = Record(recordId, income, expense, date, Timestamp.now())

                recordCollection.document(recordId).set(recordData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Record added successfully")
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Record added successfully")) }
                        getRecords() // Fetch updated list after adding
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding record", e)
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error adding record: $e")) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
            }
        }
    }

    // Function to retrieve all records
    private fun getRecords() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                fireStore.collection("incomeExpenseRecords")
                    .get()
                    .addOnSuccessListener { documents ->
                        val records = documents.mapNotNull { it.toObject(com.dwarkadhish.tea.data.incomeExpense.Record::class.java) }
                        _uiState.update { it.copy(loading = false, records = records) }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching records", e)
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error fetching records")) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
            }
        }
    }

    fun onRoute(){
        _uiState.update { it.copy(route = null) }
    }
}
