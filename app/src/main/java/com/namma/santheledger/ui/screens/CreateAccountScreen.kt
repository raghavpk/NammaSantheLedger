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
fun CreateAccountScreen(
    authViewModel: AuthViewModel,
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var shopName by rememberSaveable { mutableStateOf("") }
    var marketName by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80))))) {
        Column(Modifier.fillMaxSize().systemBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))

            Surface(Modifier.size(80.dp), shape = CircleShape, color = Color(0xFFE65100), shadowElevation = 8.dp) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.PersonAdd, null, Modifier.size(40.dp), tint = Color.White) }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.create_account), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Text(stringResource(R.string.register_vendor), style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5D4037))

            Spacer(Modifier.height(28.dp))

            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(stringResource(R.string.vendor_details), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))

                    // Name
                    OutlinedTextField(
                        value = name, onValueChange = { name = it; authViewModel.clearError() },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.your_name)) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFE65100)) },
                        shape = RoundedCornerShape(14.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    // Phone
                    OutlinedTextField(
                        value = phone, onValueChange = { 
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) { 
                                phone = it
                                authViewModel.clearError()
                                if (it.length == 10) focusManager.moveFocus(FocusDirection.Down)
                            } 
                        },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.phone_number_star)) },
                        prefix = { Text("+91  ", fontWeight = FontWeight.SemiBold, color = Color(0xFF212121), fontSize = 16.sp) },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFFE65100)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(14.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    // Password
                    OutlinedTextField(
                        value = password, onValueChange = { password = it; authViewModel.clearError() },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.password_star)) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE65100)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color(0xFF757575))
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        supportingText = { Text(stringResource(R.string.password_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(14.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    // Shop Name
                    OutlinedTextField(
                        value = shopName, onValueChange = { shopName = it },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.shop_business_name)) },
                        leadingIcon = { Icon(Icons.Default.Store, null, tint = Color(0xFFE65100)) },
                        placeholder = { Text("e.g. Raju Vegetables", color = Color(0xFF9E9E9E)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(14.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    // Market Name
                    OutlinedTextField(
                        value = marketName, onValueChange = { marketName = it },
                        modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.market_santhe_name)) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE65100)) },
                        placeholder = { Text("e.g. Ramanagara Santhe", color = Color(0xFF9E9E9E)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { 
                            focusManager.clearFocus()
                            authViewModel.createAccount(name.trim(), phone, password, shopName.trim(), marketName.trim()) { 
                                onRegistered() 
                            }
                        }),
                        shape = RoundedCornerShape(14.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100), unfocusedBorderColor = Color(0xFFBDBDBD), focusedTextColor = Color(0xFF212121), unfocusedTextColor = Color(0xFF212121))
                    )

                    if (errorMessage != null) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Error, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp)); Text(errorMessage!!, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = { 
                            authViewModel.createAccount(name.trim(), phone, password, shopName.trim(), marketName.trim()) { 
                                onRegistered() 
                            } 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)), enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        else { Icon(Icons.Default.HowToReg, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.create_account), fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.already_have_account) + " ", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5D4037))
                TextButton(onClick = onNavigateToLogin) { Text(stringResource(R.string.login), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE65100)) }
            }
            Spacer(Modifier.height(120.dp))
        }
    }
}
