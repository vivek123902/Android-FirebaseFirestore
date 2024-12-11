// TeaCountViewModel.kt

package com.dwarkadhish.tea.presentation.teamanagement

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.teaManagement.TeaCount
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeaCountViewModel(private val fireStore: FirebaseFirestore) : ViewModel() {

    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState
    data class UiState(
        val loading: Boolean = false,
        val responseMessage: Int? = null,
        val teaCounts: List<TeaCount>? = null,
        val todayTeaCount: Int = 0,
        val monthlyTeaCount: Int = 0
    )

    fun addTeaCount(floorId: String, officeId: String, teaCount: Int) {
        _uiState.update { it.copy(loading = true) }

        // Get the current date as a Date object
        val currentDate = Date()

        // Create a formatted string for the document ID
        val dateString = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault()).format(currentDate)

        val teaCountRef = fireStore.collection("floors")
            .document(floorId)
            .collection("offices")
            .document(officeId)
            .collection("teaCount")
            .document(dateString) // Use formatted date as document ID

        // Store the current date as a Timestamp
        val teaCountData = TeaCount(date = Timestamp(currentDate), teaCount = teaCount)

        teaCountRef.set(teaCountData)
            .addOnSuccessListener {
                Log.d("Firestore", "Tea count added successfully")
                _uiState.update { it.copy(loading = false, responseMessage = R.string.success_tea_added) }
                getTeaCountsByOffice(floorId, officeId)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding tea count", e)
                _uiState.update { it.copy(loading = false, responseMessage = R.string.something_went_wrong) }
            }
    }



    fun getTeaCountsByOffice(floorId: String, officeId: String) {
        _uiState.update { it.copy(loading = true) }

        fireStore.collection("floors")
            .document(floorId)
            .collection("offices")
            .document(officeId)
            .collection("teaCount")
            .get()
            .addOnSuccessListener { documents ->
                val teaCounts = documents.mapNotNull { it.toObject(TeaCount::class.java) }

                // Current date formatting
                val dateFormat = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault())
                val currentMonthFormat = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
                val todayDateString = dateFormat.format(Date())
                val currentMonthString = currentMonthFormat.format(Date())

                // Get today's tea count
                val todayCount = teaCounts.filter { tea ->
                    val teaDate = (tea.date as? Timestamp)?.toDate()
                    teaDate != null && dateFormat.format(teaDate) == todayDateString
                }.sumOf { it.teaCount ?: 0 }

                // Get monthly tea count
                val monthlyCount = teaCounts.filter { tea ->
                    val teaDate = (tea.date as? Timestamp)?.toDate()
                    teaDate != null && currentMonthFormat.format(teaDate) == currentMonthString
                }.sumOf { it.teaCount ?: 0 }

                // Update UI state
                _uiState.update { it.copy(
                    loading = false,
                    teaCounts = teaCounts,
                    todayTeaCount = todayCount,
                    monthlyTeaCount = monthlyCount,
                    responseMessage = null
                )}
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching tea counts", e)
                _uiState.update { it.copy(loading = false, responseMessage = R.string.something_went_wrong) }
            }
    }


// TeaCountViewModel.kt

    fun getTeaCountsByMonth(floorId: String, officeId: String, year: String, month: String) {
        _uiState.update { it.copy(loading = true) }

        fireStore.collection("floors")
            .document(floorId)
            .collection("offices")
            .document(officeId)
            .collection("teaCount")
            .get()
            .addOnSuccessListener { documents ->
                val teaCounts = documents.mapNotNull { it.toObject(TeaCount::class.java) }

                // Filter for selected month and year
                val selectedMonthCount = teaCounts.filter { tea ->
                    val teaDate = (tea.date as? Timestamp)?.toDate()
                    teaDate != null && SimpleDateFormat("yyyy", Locale.getDefault()).format(teaDate) == year &&
                            SimpleDateFormat("MM", Locale.getDefault()).format(teaDate) == month
                }.sumOf { it.teaCount ?: 0 }

                _uiState.update { it.copy(
                    loading = false,
                    monthlyTeaCount = selectedMonthCount
                )}
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching monthly tea counts", e)
                _uiState.update { it.copy(loading = false, responseMessage = R.string.something_went_wrong) }
            }
    }

}
