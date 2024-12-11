package com.dwarkadhish.tea.presentation.stock.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.stock.Item
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class StockFragmentViewModel(private val fireStore: FirebaseFirestore) : ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    data class UiState(
        var route: Route? = null,
        var loading: Boolean = false,
        var getStocks: List<Item>? = null
    )

    sealed class Route {
        data class ResponseMessage(var responseMessage: Int): Route()
        data class EmptyFloorFound(var noStock: String): Route()
    }

    fun getStockList() {
        _uiState.update { it.copy(loading = true) }
        fireStore.collection("stocks")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING) // Order by createdAt descending
            .get()
            .addOnSuccessListener { documents ->
                val stockList = documents.map { document ->
                    document.toObject(Item::class.java).copy(id = document.id)
                }
                if (stockList.isEmpty()) {
                    _uiState.update { it.copy(loading = false, route = Route.EmptyFloorFound("Please add stock")) }
                } else {
                    _uiState.update { it.copy(loading = false, getStocks = stockList) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
            }
    }


    fun addCategory(categoryName: String) {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            try {
                val categoryData = hashMapOf("name" to categoryName)
                fireStore.collection("categories")
                    .add(categoryData)
                    .addOnSuccessListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.category_added_successfully)) }
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
            }
        }
    }

    fun deleteStock(item: Item) {
        fireStore.collection("stocks").document(item.id)
            .delete()
            .addOnSuccessListener {
                _uiState.update { it.copy(route = Route.ResponseMessage(R.string.stock_deleted_successfully)) }
                getStockList()
            }
            .addOnFailureListener {
                _uiState.update { it.copy(route = Route.ResponseMessage(R.string.something_went_wrong)) }
            }
    }
}
