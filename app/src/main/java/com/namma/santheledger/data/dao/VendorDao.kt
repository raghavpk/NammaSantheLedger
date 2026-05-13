package com.namma.santheledger.data.dao

import androidx.room.*
import com.namma.santheledger.data.model.VendorProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {

    @Query("SELECT * FROM vendor_profile LIMIT 1")
    fun getVendor(): Flow<VendorProfile?>

    @Query("SELECT * FROM vendor_profile LIMIT 1")
    suspend fun getVendorOnce(): VendorProfile?

    @Query("SELECT * FROM vendor_profile WHERE phone = :phone LIMIT 1")
    suspend fun getVendorByPhone(phone: String): VendorProfile?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVendor(vendor: VendorProfile)

    @Update
    suspend fun updateVendor(vendor: VendorProfile)

    @Query("UPDATE vendor_profile SET password = :newPassword WHERE phone = :phone")
    suspend fun updatePasswordByPhone(phone: String, newPassword: String)

    @Query("UPDATE vendor_profile SET photoUri = :uri WHERE phone = :phone")
    suspend fun updatePhotoByPhone(phone: String, uri: String)
}
