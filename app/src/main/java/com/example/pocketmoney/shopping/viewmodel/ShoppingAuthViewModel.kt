package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.shopping.repository.CartRepository
import com.example.pocketmoney.shopping.repository.ShoppingAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ShoppingAuthViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    val userID = userPreferencesRepository.userId.asLiveData()
}