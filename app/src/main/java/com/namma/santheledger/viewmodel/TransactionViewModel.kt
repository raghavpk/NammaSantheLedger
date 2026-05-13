package com.namma.santheledger.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.namma.santheledger.data.model.Customer
import com.namma.santheledger.data.model.Transaction
import com.namma.santheledger.data.repository.LedgerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: LedgerRepository,
    private val customerId: Long
) : ViewModel() {

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    val transactions: StateFlow<List<Transaction>> =
        repository.getTransactionsForCustomer(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val balance: StateFlow<Double> =
        repository.getCustomerBalance(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            _customer.value = repository.getCustomerById(customerId)
        }
    }

    fun addCredit(amount: Double, note: String = "", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addCredit(customerId, amount, note)
            onSuccess()
        }
    }

    fun addPayment(amount: Double, note: String = "", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addPayment(customerId, amount, note)
            onSuccess()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun deleteCustomer(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _customer.value?.let { repository.deleteCustomer(it) }
            onDeleted()
        }
    }

    fun sendWhatsAppReminder(context: Context) {
        val cust = _customer.value ?: return
        val bal = balance.value
        if (bal <= 0) return

        val message = "\uD83D\uDE4F Namaskara ${cust.name},\n\n" +
                "This is a friendly reminder from your vendor at Santhe.\n" +
                "Your pending due is \u20B9${String.format("%.0f", bal)}.\n\n" +
                "Please clear the amount at your convenience.\n" +
                "Thank you! \uD83D\uDE4F\n\n" +
                "\u2014 Namma Santhe Ledger"

        val phone = cust.phone.replace("+", "").replace(" ", "")
        val url = "https://wa.me/$phone?text=${Uri.encode(message)}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    class Factory(
        private val repository: LedgerRepository,
        private val customerId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionViewModel(repository, customerId) as T
        }
    }
}
