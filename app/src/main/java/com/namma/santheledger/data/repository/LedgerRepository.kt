package com.namma.santheledger.data.repository

import com.namma.santheledger.data.dao.CustomerDao
import com.namma.santheledger.data.dao.TransactionDao
import com.namma.santheledger.data.dao.VendorDao
import com.namma.santheledger.data.model.Customer
import com.namma.santheledger.data.model.Transaction
import com.namma.santheledger.data.model.VendorProfile
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class LedgerRepository(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao,
    private val vendorDao: VendorDao
) {
    // ── Vendor / Auth ────────────────────────────────────────────

    fun getVendor(): Flow<VendorProfile?> = vendorDao.getVendor()

    suspend fun getVendorOnce(): VendorProfile? = vendorDao.getVendorOnce()

    suspend fun getVendorByPhone(phone: String): VendorProfile? = vendorDao.getVendorByPhone(phone)

    suspend fun createVendor(name: String, shopName: String, phone: String, password: String, marketName: String) {
        vendorDao.insertVendor(
            VendorProfile(name = name, shopName = shopName, phone = phone, password = password, marketName = marketName)
        )
    }

    suspend fun updateVendor(vendor: VendorProfile) = vendorDao.updateVendor(vendor)

    suspend fun updatePasswordByPhone(phone: String, newPassword: String) = vendorDao.updatePasswordByPhone(phone, newPassword)

    suspend fun updatePhotoByPhone(phone: String, uri: String) = vendorDao.updatePhotoByPhone(phone, uri)

    // ── Customer Operations ──────────────────────────────────────

    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)

    suspend fun getCustomerById(id: Long): Customer? = customerDao.getCustomerById(id)

    suspend fun addCustomer(name: String, phone: String): Long {
        return customerDao.insertCustomer(Customer(name = name, phone = phone))
    }

    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) {
        transactionDao.deleteTransactionsForCustomer(customer.id)
        customerDao.deleteCustomer(customer)
    }

    fun getCustomerCount(): Flow<Int> = customerDao.getCustomerCount()

    // ── Transaction Operations ───────────────────────────────────

    fun getTransactionsForCustomer(customerId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsForCustomer(customerId)

    fun getTotalOutstanding(): Flow<Double> = transactionDao.getTotalOutstanding()

    fun getCustomerBalance(customerId: Long): Flow<Double> =
        transactionDao.getCustomerBalance(customerId)

    fun getTodayCreditTotal(): Flow<Double> {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayCreditTotal(start, end)
    }

    fun getTodayPaymentTotal(): Flow<Double> {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayPaymentTotal(start, end)
    }

    fun getTodayTotalSales(): Flow<Double> {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayTotalSales(start, end)
    }

    fun getTodayTransactions(): Flow<List<Transaction>> {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayTransactions(start, end)
    }

    // Period-based queries for Business Overview
    fun getTransactionsInRange(start: Long, end: Long): Flow<List<Transaction>> =
        transactionDao.getTodayTransactions(start, end)

    fun getCreditInRange(start: Long, end: Long): Flow<Double> =
        transactionDao.getTodayCreditTotal(start, end)

    fun getPaymentInRange(start: Long, end: Long): Flow<Double> =
        transactionDao.getTodayPaymentTotal(start, end)

    fun getSalesInRange(start: Long, end: Long): Flow<Double> =
        transactionDao.getTodayTotalSales(start, end)

    suspend fun addCredit(customerId: Long, amount: Double, note: String = ""): Long {
        return transactionDao.insertTransaction(
            Transaction(customerId = customerId, amount = amount, type = "CREDIT", note = note)
        )
    }

    suspend fun addPayment(customerId: Long, amount: Double, note: String = ""): Long {
        return transactionDao.insertTransaction(
            Transaction(customerId = customerId, amount = amount, type = "PAYMENT", note = note)
        )
    }

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    // ── Helpers ──────────────────────────────────────────────────

    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return Pair(startOfDay, calendar.timeInMillis)
    }
}
