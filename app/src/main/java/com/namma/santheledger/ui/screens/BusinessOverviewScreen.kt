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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namma.santheledger.R
import com.namma.santheledger.data.model.Transaction
import com.namma.santheledger.data.repository.LedgerRepository
import com.namma.santheledger.ui.theme.CreditRed
import com.namma.santheledger.ui.theme.PaymentGreen
import java.text.SimpleDateFormat
import java.util.*

enum class ReportPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessOverviewScreen(
    repository: LedgerRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToCustomers: () -> Unit
) {
    var selectedPeriod by rememberSaveable { mutableStateOf(ReportPeriod.DAILY) }
    val range = remember(selectedPeriod) { getRange(selectedPeriod) }
    val start = range.first
    val end = range.second

    val sales by repository.getSalesInRange(start, end).collectAsStateWithLifecycle(initialValue = 0.0)
    val credit by repository.getCreditInRange(start, end).collectAsStateWithLifecycle(initialValue = 0.0)
    val payments by repository.getPaymentInRange(start, end).collectAsStateWithLifecycle(initialValue = 0.0)
    val transactions by repository.getTransactionsInRange(start, end).collectAsStateWithLifecycle(initialValue = emptyList())

    val netCashFlow = payments - credit
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val periodLabel = when (selectedPeriod) {
        ReportPeriod.DAILY -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        ReportPeriod.WEEKLY -> stringResource(R.string.this_week)
        ReportPeriod.MONTHLY -> SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        ReportPeriod.YEARLY -> SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
    }

    val periodLabels = mapOf(
        ReportPeriod.DAILY to stringResource(R.string.daily),
        ReportPeriod.WEEKLY to stringResource(R.string.weekly),
        ReportPeriod.MONTHLY to stringResource(R.string.monthly),
        ReportPeriod.YEARLY to stringResource(R.string.yearly)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.business_overview), fontWeight = FontWeight.Bold, color = Color.White)
                        Text(periodLabel, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE65100))
            )
        },
        bottomBar = {
            // Full-width Bottom Navigation (edge-to-edge)
            NavigationBar(
                containerColor = Color(0xFFFFF3E0),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(stringResource(R.string.nav_home), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF8D6E63),
                        unselectedTextColor = Color(0xFF8D6E63)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCustomers,
                    icon = { Icon(Icons.Default.People, null) },
                    label = { Text(stringResource(R.string.nav_customers), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF8D6E63),
                        unselectedTextColor = Color(0xFF8D6E63)
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Assessment, null) },
                    label = { Text(stringResource(R.string.nav_reports), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE65100),
                        selectedTextColor = Color(0xFFE65100),
                        indicatorColor = Color(0xFFFFCC80),
                        unselectedIconColor = Color(0xFF8D6E63)
                    )
                )
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            ScrollableTabRow(
                selectedTabIndex = selectedPeriod.ordinal,
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                ReportPeriod.values().forEach { period ->
                    Tab(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        text = {
                            Text(
                                periodLabels[period] ?: period.name,
                                fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard(stringResource(R.string.total_sales), sales, Icons.Default.TrendingUp, Color(0xFF1565C0), Modifier.weight(1f))
                OverviewCard(stringResource(R.string.credit), credit, Icons.Default.ArrowUpward, CreditRed, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard(stringResource(R.string.payments_in), payments, Icons.Default.ArrowDownward, PaymentGreen, Modifier.weight(1f))
                OverviewCard(stringResource(R.string.net_cash), netCashFlow, Icons.Default.AccountBalance, if (netCashFlow >= 0) PaymentGreen else CreditRed, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.transactions) + " (${transactions.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (transactions.isEmpty()) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.no_transactions), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(transactions) { txn ->
                        Card(
                            Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    Modifier.size(40.dp), shape = RoundedCornerShape(10.dp),
                                    color = if (txn.type == "CREDIT") CreditRed.copy(alpha = 0.1f) else PaymentGreen.copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (txn.type == "CREDIT") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                            null, tint = if (txn.type == "CREDIT") CreditRed else PaymentGreen
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(if (txn.type == "CREDIT") stringResource(R.string.credit_udari) else stringResource(R.string.payment_received), fontWeight = FontWeight.Medium)
                                    Text(dateFormat.format(Date(txn.date)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (txn.note.isNotBlank()) Text(txn.note, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    "${if (txn.type == "CREDIT") "+" else "-"}\u20B9${String.format(Locale.getDefault(), "%,.0f", txn.amount)}",
                                    fontWeight = FontWeight.Bold, color = if (txn.type == "CREDIT") CreditRed else PaymentGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(title: String, amount: Double, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(14.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text("₹${String.format(Locale.getDefault(), "%,.0f", amount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun getRange(period: ReportPeriod): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    // Reset time to midnight
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val start: Long
    val end: Long

    when (period) {
        ReportPeriod.DAILY -> {
            // Today: midnight to midnight
            start = cal.timeInMillis
            cal.add(Calendar.DAY_OF_MONTH, 1)
            end = cal.timeInMillis
        }
        ReportPeriod.WEEKLY -> {
            // Start of current week to end of current week
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            start = cal.timeInMillis
            cal.add(Calendar.WEEK_OF_YEAR, 1)
            end = cal.timeInMillis
        }
        ReportPeriod.MONTHLY -> {
            // 1st of current month to 1st of next month
            cal.set(Calendar.DAY_OF_MONTH, 1)
            start = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            end = cal.timeInMillis
        }
        ReportPeriod.YEARLY -> {
            // Jan 1 of current year to Jan 1 of next year
            cal.set(Calendar.DAY_OF_YEAR, 1)
            start = cal.timeInMillis
            cal.add(Calendar.YEAR, 1)
            end = cal.timeInMillis
        }
    }
    return Pair(start, end)
}
