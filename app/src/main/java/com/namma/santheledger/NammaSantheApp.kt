package com.namma.santheledger

import android.app.Application
import com.namma.santheledger.data.db.AppDatabase
import com.namma.santheledger.data.repository.LedgerRepository

class NammaSantheApp : Application() {

    lateinit var repository: LedgerRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        repository = LedgerRepository(
            customerDao = database.customerDao(),
            transactionDao = database.transactionDao(),
            vendorDao = database.vendorDao()
        )
    }
}
