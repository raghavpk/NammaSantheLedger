package com.namma.santheledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.namma.santheledger.data.repository.LedgerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: LedgerRepository) : ViewModel() {

    val totalOutstanding: StateFlow<Double> = repository.getTotalOutstanding()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todaySales: StateFlow<Double> = repository.getTodayTotalSales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayCredit: StateFlow<Double> = repository.getTodayCreditTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayPayments: StateFlow<Double> = repository.getTodayPaymentTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val customerCount: StateFlow<Int> = repository.getCustomerCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    class Factory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
