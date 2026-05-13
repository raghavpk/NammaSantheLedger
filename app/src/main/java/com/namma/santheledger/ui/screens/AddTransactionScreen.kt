package com.namma.santheledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namma.santheledger.ui.components.NumericKeypad
import com.namma.santheledger.ui.theme.CreditRed
import com.namma.santheledger.ui.theme.PaymentGreen
import com.namma.santheledger.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val customer by viewModel.customer.collectAsStateWithLifecycle()
    var amount by rememberSaveable { mutableStateOf("") }
    var isCredit by rememberSaveable { mutableStateOf(true) }
    var note by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("New Entry", fontWeight = FontWeight.Bold)
                        customer?.let {
                            Text(
                                text = "for ${it.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Amount Display ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCredit) CreditRed.copy(alpha = 0.08f)
                    else PaymentGreen.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCredit) "Credit (Udari)" else "Payment Received",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCredit) CreditRed else PaymentGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (amount.isEmpty()) "₹0" else "₹$amount",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (isCredit) CreditRed else PaymentGreen,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Credit / Payment Toggle ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isCredit,
                    onClick = { isCredit = true },
                    label = { Text("Credit (Udari)", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CreditRed.copy(alpha = 0.2f),
                        selectedLabelColor = CreditRed,
                        selectedLeadingIconColor = CreditRed
                    )
                )
                FilterChip(
                    selected = !isCredit,
                    onClick = { isCredit = false },
                    label = { Text("Payment", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PaymentGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PaymentGreen,
                        selectedLeadingIconColor = PaymentGreen
                    )
                )
            }

            // ── Note Field (optional) ──
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Add a note (optional)") },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        if (isCredit) {
                            viewModel.addCredit(amountValue, note) { onBack() }
                        } else {
                            viewModel.addPayment(amountValue, note) { onBack() }
                        }
                    }
                })
            )

            // ── Numeric Keypad ──
            NumericKeypad(
                onNumberClick = { digit ->
                    if (amount.length < 8) {
                        amount += digit
                    }
                },
                onDeleteClick = {
                    if (amount.isNotEmpty()) {
                        amount = amount.dropLast(1)
                    }
                },
                onClearClick = { amount = "" },
                modifier = Modifier.weight(1f)
            )

            // ── Save Button ──
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        if (isCredit) {
                            viewModel.addCredit(amountValue, note) { onBack() }
                        } else {
                            viewModel.addPayment(amountValue, note) { onBack() }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = amount.isNotEmpty() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCredit) CreditRed else PaymentGreen
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCredit) "Add Credit" else "Record Payment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
