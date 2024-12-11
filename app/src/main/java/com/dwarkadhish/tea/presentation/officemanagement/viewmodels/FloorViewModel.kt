package com.dwarkadhish.tea.presentation.officemanagement.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwarkadhish.tea.data.officeManagement.floor.Floor
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FloorViewModel(private val fireStore: FirebaseFirestore) : ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    data class UiState(
        var route: Route? = null,
        var loading: Boolean = false,
        var getFloors: List<Floor>? = null
    )
    sealed class Route {
        data class ResponseMessage(var responseMessage: String): Route()
        data class EmptyFloorFound(var noFloor: String): Route()
    }

    init {
        getAllFloors()
    }
    fun addFloor(floorName: String) {
        try {
            _uiState.update { it.copy(loading = true) }
            val floorRef = fireStore.collection("floors")
            val floorId = floorRef.document().id
            val floorData = Floor(floorId = floorId, floorName = floorName, createdAt = Timestamp.now())

            floorRef.document(floorId).set(floorData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Floor added successfully")
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Floor added successfully")) }
                    getAllFloors() // Fetch updated list after adding
                }
                .addOnFailureListener { e ->
                    _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Error adding floor $e")) }
                    Log.e("Firestore", "Error adding floor", e)
                }
        } catch (e: Exception) {
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
        }
    }

    private fun getAllFloors() {
        try {
            viewModelScope.launch {
                _uiState.update { it.copy(loading = true) }
                fireStore.collection("floors")
                    .orderBy("createdAt")  // Order by createdAt timestamp
                    .get()
                    .addOnSuccessListener { documents ->
                        val floors = documents.mapNotNull { it.toObject(Floor::class.java) }
                        if (floors.isNotEmpty()) {
                            _uiState.update { it.copy(loading = false, getFloors = floors) }
                        } else {
                            _uiState.update { it.copy(loading = false, route = Route.EmptyFloorFound("Please add floors..!")) }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching floors", e)
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
                    }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something went wrong!")) }
        }
    }


    fun updateFloor(floorId: String, newFloorName: String) {
        try {
            viewModelScope.launch {
                val floorRef = fireStore.collection("floors").document(floorId)
                floorRef.update("floorName", newFloorName)
                    .addOnSuccessListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Floor Updated!")) }
                        getAllFloors()
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something Went wrong!")) }
                    }
            }
        }catch (e:Exception){
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something Went wrong!")) }
        }

    }

    fun deleteFloor(floorId: String) {
        try {
            viewModelScope.launch {
                val floorRef = fireStore.collection("floors").document(floorId)
                floorRef.delete()
                    .addOnSuccessListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Floor deleted successfully!")) }
                        getAllFloors()
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something Went wrong!")) }
                    }
            }
        }
        catch (e:Exception){
            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage("Something Went wrong!")) }
        }
    }

    fun onRoute(){
        _uiState.update { it.copy(route = null) }
    }

}
