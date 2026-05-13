package com.namma.santheledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendor_profile")
data class VendorProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val shopName: String,
    val phone: String,
    val password: String,
    val marketName: String = "",
    val photoUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
