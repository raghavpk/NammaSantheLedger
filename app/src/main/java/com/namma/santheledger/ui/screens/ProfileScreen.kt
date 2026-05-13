package com.namma.santheledger.ui.screens

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.namma.santheledger.util.LocaleHelper
import com.namma.santheledger.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val vendor by authViewModel.vendor.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    var showChangePassword by rememberSaveable { mutableStateOf(false) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var passwordChanged by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editName by rememberSaveable { mutableStateOf("") }
    var editShopName by rememberSaveable { mutableStateOf("") }
    var editMarketName by rememberSaveable { mutableStateOf("") }
    var profileSaved by rememberSaveable { mutableStateOf(false) }

    // Sync edit fields when vendor data loads or editing starts
    LaunchedEffect(vendor, isEditing) {
        if (isEditing && editName.isEmpty() && editShopName.isEmpty()) {
            editName = vendor?.name ?: ""
            editShopName = vendor?.shopName ?: ""
            editMarketName = vendor?.marketName ?: ""
        }
    }

    // Photo picker
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { authViewModel.updatePhoto(it.toString()) }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_confirm), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.logout_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    authViewModel.logout { onLogout() }
                }) {
                    Text(stringResource(R.string.logout), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_profile), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE65100))
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Profile Photo
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    Modifier.size(110.dp), shape = CircleShape,
                    color = Color(0xFFE65100), shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            vendor?.name?.firstOrNull()?.uppercase() ?: "V",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                // Camera button
                FloatingActionButton(
                    onClick = { photoLauncher.launch("image/*") },
                    modifier = Modifier.size(36.dp),
                    containerColor = Color(0xFF3E2723),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(vendor?.name ?: "Vendor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            Text(vendor?.shopName ?: "", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5D4037))

            Spacer(Modifier.height(24.dp))

            // Details Card
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.profile_details), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        TextButton(onClick = {
                            if (isEditing) {
                                // Save
                                vendor?.let { v ->
                                    val updated = v.copy(
                                        name = editName.trim().ifBlank { v.name },
                                        shopName = editShopName.trim().ifBlank { v.shopName },
                                        marketName = editMarketName.trim()
                                    )
                                    authViewModel.updateProfile(updated) {
                                        profileSaved = true
                                        isEditing = false
                                    }
                                }
                            } else {
                                profileSaved = false
                                isEditing = true
                            }
                        }) {
                            Icon(
                                if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (isEditing) stringResource(R.string.save) else stringResource(R.string.edit),
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (profileSaved) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
                            Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.profile_updated), color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    if (isEditing) {
                        // Editable fields
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.name)) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFE65100)) },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE65100),
                                focusedLabelColor = Color(0xFFE65100),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                        OutlinedTextField(
                            value = editShopName,
                            onValueChange = { editShopName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.shop_business_name)) },
                            leadingIcon = { Icon(Icons.Default.Store, null, tint = Color(0xFFE65100)) },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE65100),
                                focusedLabelColor = Color(0xFFE65100),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                        // Phone - READ ONLY
                        OutlinedTextField(
                            value = vendor?.phone?.let { "+${it.take(2)} ${it.drop(2)}" } ?: "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.phone_number)) },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFF9E9E9E)) },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            enabled = false,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF757575), fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFE0E0E0),
                                disabledLabelColor = Color(0xFF9E9E9E),
                                disabledTextColor = Color(0xFF757575)
                            ),
                            supportingText = { Text(stringResource(R.string.phone_cannot_change), color = Color(0xFF9E9E9E), fontSize = 11.sp) }
                        )
                        OutlinedTextField(
                            value = editMarketName,
                            onValueChange = { editMarketName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.market_santhe_name)) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE65100)) },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121), fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE65100),
                                focusedLabelColor = Color(0xFFE65100),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                    } else {
                        // View mode
                        ProfileRow(Icons.Default.Person, stringResource(R.string.name), vendor?.name ?: "-")
                        HorizontalDivider(color = Color(0xFFE65100).copy(alpha = 0.1f))
                        ProfileRow(Icons.Default.Store, stringResource(R.string.shop_name), vendor?.shopName ?: "-")
                        HorizontalDivider(color = Color(0xFFE65100).copy(alpha = 0.1f))
                        ProfileRow(Icons.Default.Phone, stringResource(R.string.phone), vendor?.phone?.let { "+${it.take(2)} ${it.drop(2)}" } ?: "-")
                        HorizontalDivider(color = Color(0xFFE65100).copy(alpha = 0.1f))
                        ProfileRow(Icons.Default.LocationOn, stringResource(R.string.market), vendor?.marketName?.ifBlank { "-" } ?: "-")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Change Password Card
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.change_password), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                        IconButton(onClick = { showChangePassword = !showChangePassword; passwordChanged = false; newPassword = ""; confirmPassword = "" }) {
                            Icon(if (showChangePassword) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color(0xFFE65100))
                        }
                    }

                    AnimatedVisibility(visible = showChangePassword) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (passwordChanged) {
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E8)) {
                                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.password_changed), color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = newPassword, onValueChange = { newPassword = it; authViewModel.clearError() },
                                    modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.new_password)) },
                                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE65100)) },
                                    trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } },
                                    supportingText = { Text(stringResource(R.string.password_hint)) },
                                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                                    shape = RoundedCornerShape(14.dp), singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121)),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100))
                                )
                                OutlinedTextField(
                                    value = confirmPassword, onValueChange = { confirmPassword = it; authViewModel.clearError() },
                                    modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.confirm_password)) },
                                    leadingIcon = { Icon(Icons.Default.LockReset, null, tint = Color(0xFFE65100)) },
                                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        focusManager.clearFocus()
                                        if (newPassword.isNotBlank() && confirmPassword.isNotBlank()) {
                                            authViewModel.changePassword(newPassword, confirmPassword) { passwordChanged = true }
                                        }
                                    }),
                                    shape = RoundedCornerShape(14.dp), singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121)),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE65100), focusedLabelColor = Color(0xFFE65100))
                                )
                                if (errorMessage != null) {
                                    Text(errorMessage!!, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
                                }
                                Button(
                                    onClick = { authViewModel.changePassword(newPassword, confirmPassword) { passwordChanged = true } },
                                    modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                                    enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank()
                                ) { Text(stringResource(R.string.update_password), fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))




            Spacer(Modifier.height(16.dp))

            // Logout
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.logout), fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
fun ProfileRow(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(20.dp), tint = Color(0xFFE65100))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF757575))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
        }
    }
}
