package com.namma.santheledger.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namma.santheledger.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onPasswordReset: () -> Unit,
    onBack: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val otpSent by authViewModel.otpSent.collectAsStateWithLifecycle()
    val otpVerified by authViewModel.otpVerified.collectAsStateWithLifecycle()
    
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    DisposableEffect(Unit) { onDispose { authViewModel.resetForgotState() } }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80))))) {
        Column(Modifier.fillMaxSize().systemBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))

            Row(Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color(0xFF3E2723)) }
            }

            Surface(Modifier.size(80.dp), shape = CircleShape, color = Color(0xFFE65100), shadowElevation = 6.dp) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.LockReset, null, Modifier.size(40.dp), tint = Color.White) }
            }
            Spacer(Modifier.height(16.dp))
            Text("Reset Password", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))

            Spacer(Modifier.height(28.dp))

            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Step indicator
                    Text(
                        text = if (otpVerified) "Step 3: New Password" else if (otpSent) "Step 2: Verify OTP" else "Step 1: Enter Phone",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE65100)
                    )

                    // ── STEP 1: Phone ──
                    if (!otpSent && !otpVerified) {
                        Text(
                            "Enter your registered phone number to receive a verification code.",
                            style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575)
                        )
                        OutlinedTextField(
                            value = phone, onValueChange = { 
                                if (it.length <= 10 && it.all { c -> c.isDigit() }) { 
                                    phone = it
                                    authViewModel.clearError()
                                } 
                            },
                            modifier = Modifier.fillMaxWidth(), label = { Text("Phone Number") },
                            prefix = { Text("+91  ", fontWeight = FontWeight.SemiBold, color = Color(0xFF212121)) },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFFE65100)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { 
                                focusManager.clearFocus()
                                if (phone.length == 10) {
                                    val activity = context as? Activity
                                    if (activity != null) authViewModel.sendForgotOtp(phone, activity)
                                }
                            }),
                            shape = RoundedCornerShape(14.dp), singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 18.sp, fontWeight = FontWeight.Medium),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                        )
                    }

                    // ── STEP 2: OTP ──
                    if (otpSent && !otpVerified) {
                        Text(
                            "We've sent a 6-digit verification code to +91 $phone",
                            style = MaterialTheme.typography.bodyMedium, color = Color(0xFF757575)
                        )

                        OutlinedTextField(
                            value = otp, onValueChange = { 
                                if (it.length <= 6 && it.all { c -> c.isDigit() }) { 
                                    otp = it
                                    authViewModel.clearError()
                                    if (it.length == 6) {
                                        focusManager.clearFocus()
                                        authViewModel.verifyForgotOtp(it)
                                    }
                                } 
                            },
                            modifier = Modifier.fillMaxWidth(), label = { Text("Enter OTP") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE65100)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { 
                                focusManager.clearFocus()
                                if (otp.length == 6) authViewModel.verifyForgotOtp(otp) 
                            }),
                            shape = RoundedCornerShape(14.dp), singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 8.sp, textAlign = TextAlign.Center, color = Color(0xFF212121)),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                        )

                        // Resend OTP
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                otp = ""
                                val activity = context as? Activity
                                if (activity != null) authViewModel.sendForgotOtp(phone, activity)
                            }) {
                                Text("Resend OTP", color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // ── STEP 3: New Password ──
                    if (otpVerified) {
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFE8F5E9)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp)); Text("Phone verified. Set your new password.", color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        OutlinedTextField(
                            value = newPassword, onValueChange = { newPassword = it; authViewModel.clearError() },
                            modifier = Modifier.fillMaxWidth(), label = { Text("New Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE65100)) },
                            trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color(0xFF757575)) } },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            shape = RoundedCornerShape(14.dp), singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                        )

                        OutlinedTextField(
                            value = confirmPassword, onValueChange = { confirmPassword = it; authViewModel.clearError() },
                            modifier = Modifier.fillMaxWidth(), label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.LockReset, null, tint = Color(0xFFE65100)) },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { 
                                focusManager.clearFocus()
                                authViewModel.resetPassword(phone, newPassword, confirmPassword) { onPasswordReset() } 
                            }),
                            shape = RoundedCornerShape(14.dp), singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                        )
                    }

                    // Error
                    if (errorMessage != null) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Error, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp)); Text(errorMessage!!, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // Action Button
                    Button(
                        onClick = {
                            val activity = context as? Activity
                            when {
                                !otpSent -> if (activity != null) authViewModel.sendForgotOtp(phone, activity)
                                !otpVerified -> authViewModel.verifyForgotOtp(otp)
                                else -> authViewModel.resetPassword(phone, newPassword, confirmPassword) { onPasswordReset() }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100), disabledContainerColor = Color(0xFFBDBDBD)),
                        enabled = when {
                            !otpSent -> phone.length == 10 && !isLoading
                            !otpVerified -> otp.length == 6 && !isLoading
                            else -> newPassword.isNotBlank() && confirmPassword.isNotBlank() && !isLoading
                        }
                    ) {
                        if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        else {
                            val (icon, text) = when {
                                !otpSent -> Icons.Default.Sms to "Send OTP"
                                !otpVerified -> Icons.Default.Verified to "Verify OTP"
                                else -> Icons.Default.LockReset to "Reset Password"
                            }
                            Icon(icon, null); Spacer(Modifier.width(8.dp)); Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(120.dp))
        }
    }
}
