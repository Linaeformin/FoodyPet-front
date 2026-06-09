package com.example.foodypet.community.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodypet.community.model.ConnectMealPetItem

class CommunityPostSharedViewModel : ViewModel() {

    private val _petItems = MutableLiveData<List<ConnectMealPetItem>>(emptyList())
    val petItems: LiveData<List<ConnectMealPetItem>> = _petItems

    fun setPetItems(items: List<ConnectMealPetItem>) {
        _petItems.value = items
    }

    fun getCurrentPetItems(): List<ConnectMealPetItem> {
        return _petItems.value.orEmpty()
    }

    fun clearPetItems() {
        _petItems.value = emptyList()
    }
}