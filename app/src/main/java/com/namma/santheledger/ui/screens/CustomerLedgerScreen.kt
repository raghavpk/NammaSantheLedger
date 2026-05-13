package com.namma.santheledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namma.santheledger.ui.components.TransactionItem
import com.namma.santheledger.ui.theme.CreditRed
import com.namma.santheledger.ui.theme.PaymentGreen
import com.namma.santheledger.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLedgerScreen(
    viewModel: TransactionViewModel,
    onAddTransaction: () -> Unit,
    onBack: () -> Unit,
    onDeleteCustomer: () -> Unit = {}
) {
    val customer by viewModel.customer.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFD32F2F)) },
            title = { Text("Delete Customer?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete \"${customer?.name}\" and all their transactions. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteCustomer { onDeleteCustomer() }
                }) {
                    Text("Delete", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = customer?.name ?: "Ledger", fontWeight = FontWeight.Bold)
                        customer?.phone?.let {
                            if (it.isNotBlank()) Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    // WhatsApp Reminder
                    if (balance > 0 && customer?.phone?.isNotBlank() == true) {
                        IconButton(onClick = { viewModel.sendWhatsAppReminder(context) }) {
                            Icon(Icons.Default.Send, "WhatsApp Reminder", tint = PaymentGreen)
                        }
                    }
                    // Delete customer (only when balance is 0 = all settled)
                    if (balance == 0.0) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete Customer", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTransaction,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Entry") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            // Balance Card
            Card(
                Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (balance > 0) CreditRed.copy(alpha = 0.1f) else PaymentGreen.copy(alpha = 0.1f))
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Net Balance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "₹${String.format("%,.0f", kotlin.math.abs(balance))}",
                        style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold,
                        color = if (balance > 0) CreditRed else PaymentGreen
                    )
                    Text(
                        when { balance > 0 -> "Due from customer"; balance < 0 -> "Advance paid"; else -> "All settled ✅" },
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // WhatsApp button
                    if (balance > 0 && customer?.phone?.isNotBlank() == true) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { viewModel.sendWhatsAppReminder(context) }, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp)); Text("Send WhatsApp Reminder")
                        }
                    }

                    // Delete option when settled
                    if (balance == 0.0 && transactions.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Account settled — Delete customer", color = Color(0xFFD32F2F), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Transactions
            if (transactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReceiptLong, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Spacer(Modifier.height(12.dp))
                        Text("No transactions yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Text("Transaction History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(transactions, key = { it.id }) { TransactionItem(transaction = it) }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
