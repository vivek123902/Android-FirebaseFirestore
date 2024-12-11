package com.dwarkadhish.tea.presentation.extensions

import androidx.lifecycle.MutableLiveData


fun <T> MutableLiveData<T>.update(updateFunc: (T) -> T) {
    this.value = this.value?.let(updateFunc)
}
