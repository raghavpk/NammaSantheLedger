package com.namma.santheledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namma.santheledger.R
import com.namma.santheledger.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoggedIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            Surface(Modifier.size(100.dp), shape = CircleShape, color = Color(0xFFE65100), shadowElevation = 8.dp) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MenuBook, null, Modifier.size(50.dp), tint = Color.White)
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.namma_santhe), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Text(stringResource(R.string.app_tagline), style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5D4037))

            Spacer(Modifier.height(40.dp))

            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.login), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))

                    // Phone
                    OutlinedTextField(
                        value = phone, onValueChange = { 
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) { 
                                phone = it
                                authViewModel.clearError()
                                if (it.length == 10) focusManager.moveFocus(FocusDirection.Down)
                            } 
                        },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.phone_number)) },
                        prefix = { Text("+91  ", fontWeight = FontWeight.SemiBold, color = Color(0xFF212121), fontSize = 16.sp) },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFFE65100)) },
                        placeholder = { Text(stringResource(R.string.enter_10_digit), color = Color(0xFF9E9E9E)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(16.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), cursorColor = Color(0xFFE65100), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    // Password
                    OutlinedTextField(
                        value = password, onValueChange = { password = it; authViewModel.clearError() },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE65100)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color(0xFF757575))
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            if (phone.length == 10 && password.isNotBlank()) {
                                authViewModel.login(phone, password) { onLoggedIn() }
                            }
                        }),
                        shape = RoundedCornerShape(16.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), cursorColor = Color(0xFFE65100), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    // Forgot Password
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onForgotPassword) {
                            Text(stringResource(R.string.forgot_password), color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    // Error
                    if (errorMessage != null) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Error, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(errorMessage!!, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // Login Button
                    Button(
                        onClick = { authViewModel.login(phone, password) { onLoggedIn() } },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100), disabledContainerColor = Color(0xFFBDBDBD)),
                        enabled = phone.length == 10 && password.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) { CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp) }
                        else { Icon(Icons.Default.Login, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.login), fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.new_vendor) + " ", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5D4037))
                TextButton(onClick = onCreateAccount) { Text(stringResource(R.string.create_account), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE65100)) }
            }
            Spacer(Modifier.height(120.dp))
        }
    }
}
