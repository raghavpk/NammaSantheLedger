package com.namma.santheledger.viewmodel

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.namma.santheledger.data.model.VendorProfile
import com.namma.santheledger.data.repository.LedgerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel(
    private val repository: LedgerRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // OTP states
    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    private val _otpVerified = MutableStateFlow(false)
    val otpVerified: StateFlow<Boolean> = _otpVerified.asStateFlow()

    // Firebase verification
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _vendor = MutableStateFlow<VendorProfile?>(null)
    val vendor: StateFlow<VendorProfile?> = _vendor.asStateFlow()

    init { checkSession() }

    private fun checkSession() {
        viewModelScope.launch {
            val active = prefs.getBoolean("session_active", false)
            val phone = prefs.getString("logged_in_phone", null)
            if (active && phone != null) {
                val v = repository.getVendorByPhone(phone)
                _vendor.value = v
                _isLoggedIn.value = v != null
            } else {
                _isLoggedIn.value = false
            }
        }
    }

    private fun refreshVendor(phone: String) {
        viewModelScope.launch {
            _vendor.value = repository.getVendorByPhone(phone)
        }
    }

    private fun saveSession(phone: String) {
        prefs.edit()
            .putBoolean("session_active", true)
            .putString("logged_in_phone", phone)
            .apply()
    }

    private fun clearSession() {
        prefs.edit()
            .putBoolean("session_active", false)
            .remove("logged_in_phone")
            .apply()
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 4) return "Password must be at least 4 characters"
        if (!password.any { it.isUpperCase() }) return "Password must contain at least one uppercase letter"
        if (!password.any { it.isLowerCase() }) return "Password must contain at least one lowercase letter"
        if (!password.any { it.isDigit() }) return "Password must contain at least one number"
        if (!password.any { !it.isLetterOrDigit() }) return "Password must contain at least one special character"
        return null
    }

    // ── Login with Phone + Password ─────────────────────────────
    fun login(phone: String, password: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        if (phone.length != 10) { _errorMessage.value = "Enter valid 10-digit phone number"; return }
        if (password.isBlank()) { _errorMessage.value = "Enter your password"; return }

        _isLoading.value = true
        viewModelScope.launch {
            val fullPhone = "91$phone"
            val v = repository.getVendorByPhone(fullPhone)
            if (v == null) {
                _errorMessage.value = "No account found with this number"
            } else if (v.password != password) {
                _errorMessage.value = "Incorrect password"
            } else {
                saveSession(fullPhone)
                _vendor.value = v
                _isLoggedIn.value = true
                onSuccess()
            }
            _isLoading.value = false
        }
    }

    // ── Create Account ──────────────────────────────────────────
    fun createAccount(name: String, phone: String, password: String, shopName: String, marketName: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        if (name.isBlank()) { _errorMessage.value = "Name is required"; return }
        if (phone.length != 10) { _errorMessage.value = "Enter valid 10-digit phone number"; return }

        val passwordError = validatePassword(password)
        if (passwordError != null) {
            _errorMessage.value = passwordError
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val fullPhone = "91$phone"
            val existing = repository.getVendorByPhone(fullPhone)
            if (existing != null) {
                _errorMessage.value = "Account already exists with this number"
                _isLoading.value = false
                return@launch
            }
            repository.createVendor(name.trim(), shopName.trim().ifBlank { "${name.trim()}'s Shop" }, fullPhone, password, marketName.trim())
            _isLoading.value = false
            onSuccess()
        }
    }

    // ── Forgot Password: Send OTP via Firebase ──────────────────
    fun sendForgotOtp(phone: String, activity: Activity) {
        _errorMessage.value = null
        if (phone.length != 10) { _errorMessage.value = "Enter valid 10-digit phone number"; return }

        _isLoading.value = true
        viewModelScope.launch {
            val vendor = repository.getVendorByPhone("91$phone")
            if (vendor == null) {
                _errorMessage.value = "No account found with this number"
                _isLoading.value = false
                return@launch
            }

            val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+91$phone")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // Auto-verification on same device
                        _otpSent.value = true
                        _otpVerified.value = true
                        _isLoading.value = false
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        _errorMessage.value = e.message ?: "Failed to send OTP. Please try again."
                        _isLoading.value = false
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        storedVerificationId = verificationId
                        resendToken = token
                        _otpSent.value = true
                        _isLoading.value = false
                    }
                })

            // Use resend token if available (for resend OTP)
            resendToken?.let { optionsBuilder.setForceResendingToken(it) }

            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        }
    }

    // ── Verify OTP via Firebase ─────────────────────────────────
    fun verifyForgotOtp(entered: String) {
        _errorMessage.value = null
        if (entered.length != 6) { _errorMessage.value = "Enter 6-digit OTP"; return }

        val verificationId = storedVerificationId
        if (verificationId == null) {
            _errorMessage.value = "Verification expired. Please resend OTP."
            return
        }

        _isLoading.value = true
        val credential = PhoneAuthProvider.getCredential(verificationId, entered)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _otpVerified.value = true
                    firebaseAuth.signOut() // Only used for OTP, not session
                } else {
                    _errorMessage.value = "Wrong OTP. Please try again."
                }
                _isLoading.value = false
            }
    }

    fun resetPassword(phone: String, newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        val passwordError = validatePassword(newPassword)
        if (passwordError != null) { _errorMessage.value = passwordError; return }
        if (newPassword != confirmPassword) { _errorMessage.value = "Passwords do not match"; return }

        _isLoading.value = true
        viewModelScope.launch {
            repository.updatePasswordByPhone("91$phone", newPassword)
            _isLoading.value = false
            resetForgotState()
            onSuccess()
        }
    }

    fun resetForgotState() {
        _otpSent.value = false
        _otpVerified.value = false
        _errorMessage.value = null
        storedVerificationId = null
        resendToken = null
    }

    // ── Change Password (from Profile) ──────────────────────────
    fun changePassword(newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        val passwordError = validatePassword(newPassword)
        if (passwordError != null) { _errorMessage.value = passwordError; return }
        if (newPassword != confirmPassword) { _errorMessage.value = "Passwords do not match"; return }

        viewModelScope.launch {
            val phone = _vendor.value?.phone ?: return@launch
            repository.updatePasswordByPhone(phone, newPassword)
            onSuccess()
        }
    }

    // ── Update Profile ──────────────────────────────────────────
    fun updateProfile(vendor: VendorProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateVendor(vendor)
            _vendor.value = vendor
            onSuccess()
        }
    }

    fun updatePhoto(uri: String) {
        viewModelScope.launch {
            val phone = _vendor.value?.phone ?: return@launch
            repository.updatePhotoByPhone(phone, uri)
            refreshVendor(phone)
        }
    }

    // ── Logout ──────────────────────────────────────────────────
    fun logout(onDone: () -> Unit) {
        clearSession()
        _vendor.value = null
        _isLoggedIn.value = false
        onDone()
    }

    fun clearError() { _errorMessage.value = null }

    class Factory(private val repository: LedgerRepository, private val prefs: SharedPreferences) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository, prefs) as T
    }
}
