package com.jmm.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jmm.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ShoppingAuthViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    val userID = userPreferencesRepository.userId.asLiveData()
}