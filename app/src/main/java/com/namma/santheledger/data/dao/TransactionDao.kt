package com.namma.santheledger.data.dao

import androidx.room.*
import com.namma.santheledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY date DESC")
    fun getTransactionsForCustomer(customerId: Long): Flow<List<Transaction>>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) -
               COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0)
        FROM transactions
        """
    )
    fun getTotalOutstanding(): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) -
               COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0)
        FROM transactions WHERE customerId = :customerId
        """
    )
    fun getCustomerBalance(customerId: Long): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = 'CREDIT' AND date >= :startOfDay AND date < :endOfDay
        """
    )
    fun getTodayCreditTotal(startOfDay: Long, endOfDay: Long): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = 'PAYMENT' AND date >= :startOfDay AND date < :endOfDay
        """
    )
    fun getTodayPaymentTotal(startOfDay: Long, endOfDay: Long): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE date >= :startOfDay AND date < :endOfDay
        """
    )
    fun getTodayTotalSales(startOfDay: Long, endOfDay: Long): Flow<Double>

    @Query(
        "SELECT * FROM transactions WHERE date >= :startOfDay AND date < :endOfDay ORDER BY date DESC"
    )
    fun getTodayTransactions(startOfDay: Long, endOfDay: Long): Flow<List<Transaction>>

    @Query("DELETE FROM transactions WHERE customerId = :customerId")
    suspend fun deleteTransactionsForCustomer(customerId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
