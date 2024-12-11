package com.dwarkadhish.tea.presentation.officemanagement.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dwarkadhish.tea.data.officeManagement.office.Office
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.firestore.FirebaseFirestore

class OfficeViewModel(private val fireStore: FirebaseFirestore) : ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    data class UiState(
        var route: Route? = null,
        var loading: Boolean = false,
        var getOffices: List<Office>? = null
    )


    sealed class Route {
        data class ResponseMessage(var responseMessage: String): Route()
        data class EmptyFloorFound(var noFloor: String): Route()
    }
    fun addOffice(floorId: String, officeName: String) {
        try {
            _uiState.update { it.copy(loading = true) }
            val officeRef = fireStore.collection("floors")
                .document(floorId)
                .collection("offices")
            val officeId = officeRef.document().id
            val officeData = Office(officeId = officeId, officeName = officeName, floorId = floorId, createdAt = System.currentTimeMillis())

            officeRef.document(officeId).set(officeData)
                .addOnSuccessListener {
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Office added successfully..!")) }
                    Log.d("Firestore", "Office added successfully")
                    getOfficesByFloor(floorId)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding office", e)
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error adding office")) }
                }
        } catch (e: Exception) {
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong..!")) }
        }
    }


    fun getOfficesByFloor(floorId: String) {
        try {
            _uiState.update { it.copy(loading = true) }
            fireStore.collection("floors")
                .document(floorId)
                .collection("offices")
                .orderBy("createdAt") // Ordering by createdAt field
                .get()
                .addOnSuccessListener { documents ->
                    val offices = documents.mapNotNull { it.toObject(Office::class.java) }
                    if (offices.isNotEmpty()) {
                        _uiState.update { it.copy(loading = false, getOffices = offices) }
                    } else {
                        _uiState.update { it.copy(loading = false, route = Route.EmptyFloorFound("Please add offices..!")) }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching offices", e)
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error fetching offices")) }
                }
        } catch (e: Exception) {
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
        }
    }

    fun updateOffice(updatedOffice: Office) {
        _uiState.update { it.copy(loading = true) }
        val officeRef = fireStore.collection("floors")
            .document(updatedOffice.floorId)
            .collection("offices")
            .document(updatedOffice.officeId)

        fireStore.runTransaction { transaction ->
            val officeSnapshot = transaction.get(officeRef)
            if (officeSnapshot.exists()) {
                transaction.update(officeRef, "officeName", updatedOffice.officeName)
            }
        }.addOnSuccessListener {
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Office updated successfully..!")) }
            getOfficesByFloor(updatedOffice.floorId)
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error updating office", e)
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error updating office")) }
        }
    }

    fun deleteOffice(office: Office) {
        _uiState.update { it.copy(loading = true) }
        fireStore.collection("floors")
            .document(office.floorId)
            .collection("offices")
            .document(office.officeId)
            .delete()
            .addOnSuccessListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Office Deleted Successfully..!")) }
                getOfficesByFloor(office.floorId)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting office", e)
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error deleting office")) }
            }
    }

    fun onRoute(){
        _uiState.update { it.copy(route = null) }
    }
}
