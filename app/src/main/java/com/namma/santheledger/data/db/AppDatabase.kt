package com.namma.santheledger.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.namma.santheledger.data.dao.CustomerDao
import com.namma.santheledger.data.dao.TransactionDao
import com.namma.santheledger.data.dao.VendorDao
import com.namma.santheledger.data.model.Customer
import com.namma.santheledger.data.model.Transaction
import com.namma.santheledger.data.model.VendorProfile

@Database(
    entities = [Customer::class, Transaction::class, VendorProfile::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun vendorDao(): VendorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namma_santhe_ledger.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
