package com.dwarkadhish.tea.presentation.stock.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.data.stock.Item
import com.dwarkadhish.tea.presentation.extensions.update
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AddStockFragmentViewModel(private val fireStore: FirebaseFirestore) : ViewModel() {
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState

    data class UiState(
        var route: Route? = null,
        var loading: Boolean = false,
        var addStockRoute: AddStockRoute? = null,
        var categories: List<String> = emptyList()
    )

    sealed class Route {
        data class ResponseMessage(var responseMessage: Int): Route()
    }
    sealed class AddStockRoute{
        data class ValidationError(@StringRes var validationError: Int): AddStockRoute()
        data class OnRouteToStockList(var isRoute: Boolean? = false): AddStockRoute()
    }

    init {
        fetchCategories()
    }
    private fun fetchCategories() {
        _uiState.update { it.copy(loading = true) }

        fireStore.collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                val categoryList = documents.mapNotNull { it.getString("name") }
                _uiState.update { it.copy(loading = false, categories = categoryList) }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
            }
    }
    fun updateStock(item: Item) {
        viewModelScope.launch {
            try {
                val stockData = hashMapOf(
                    "categoryName" to item.categoryName,
                    "stockQty" to item.stockQty,
                    "stockPrice" to item.stockPrice,
                    "entryDate" to item.entryDate
                )

                fireStore.collection("stocks").document(item.id)
                    .set(stockData)
                    .addOnSuccessListener {
                        _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.OnRouteToStockList(true)) }
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
            }
        }
    }
    fun addStock(
        categoryName: String,
        stockQty: String,
        stockPrice: String,
        entryDate: String
    ) {
        viewModelScope.launch {
            try {
                if (categoryName.isEmpty() || categoryName == "Select Category") {
                    _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.ValidationError(R.string.please_select_category)) }
                } else if (stockQty.isEmpty()) {
                    _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.ValidationError(R.string.enter_qty)) }
                } else if (stockPrice.isEmpty()) {
                    _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.ValidationError(R.string.enter_price)) }
                } else if (entryDate.isEmpty()) {
                    _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.ValidationError(R.string.enter_date)) }
                } else {
                    val stockData = mapOf(
                        "categoryName" to categoryName,
                        "stockQty" to stockQty,
                        "stockPrice" to stockPrice,
                        "entryDate" to entryDate,
                        "createdAt" to System.currentTimeMillis()
                    )

                    fireStore.collection("stocks")
                        .add(stockData)
                        .addOnSuccessListener {
                            _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.OnRouteToStockList(true)) }
                        }
                        .addOnFailureListener {
                            _uiState.update { it.copy(loading = false, route = Route.ResponseMessage(R.string.something_went_wrong)) }
                        }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, addStockRoute = AddStockRoute.ValidationError(R.string.something_went_wrong)) }
            }
        }
    }



    fun onAddStockRouted(){
        _uiState.update { it.copy(addStockRoute = null) }
    }
}