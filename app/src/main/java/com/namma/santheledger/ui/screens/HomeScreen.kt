package com.namma.santheledger.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.namma.santheledger.R
import com.namma.santheledger.ui.theme.*
import com.namma.santheledger.util.LocaleHelper
import com.namma.santheledger.viewmodel.AuthViewModel
import com.namma.santheledger.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    onNavigateToCustomers: () -> Unit,
    onNavigateToBusinessOverview: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val totalOutstanding by viewModel.totalOutstanding.collectAsStateWithLifecycle()
    val todaySales by viewModel.todaySales.collectAsStateWithLifecycle()
    val todayCredit by viewModel.todayCredit.collectAsStateWithLifecycle()
    val todayPayments by viewModel.todayPayments.collectAsStateWithLifecycle()
    val customerCount by viewModel.customerCount.collectAsStateWithLifecycle()
    val vendor by authViewModel.vendor.collectAsStateWithLifecycle()

    var showLangMenu by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFFE65100),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App title - full name
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            vendor?.shopName ?: stringResource(R.string.app_tagline),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Language selector
                    Box {
                        IconButton(onClick = { showLangMenu = true }) {
                            Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showLangMenu,
                            onDismissRequest = { showLangMenu = false }
                        ) {
                            LocaleHelper.supportedLanguages.forEach { lang ->
                                val currentLang = LocaleHelper.getSelectedLanguage(context)
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(lang.nativeName, fontWeight = if (lang.code == currentLang) FontWeight.Bold else FontWeight.Normal)
                                            if (lang.code != "en") {
                                                Spacer(Modifier.width(8.dp))
                                                Text(lang.name, style = MaterialTheme.typography.labelSmall, color = Color(0xFF757575))
                                            }
                                        }
                                    },
                                    onClick = {
                                        showLangMenu = false
                                        if (lang.code != currentLang) {
                                            LocaleHelper.setSelectedLanguage(context, lang.code)
                                            (context as? Activity)?.recreate()
                                        }
                                    },
                                    trailingIcon = {
                                        if (lang.code == currentLang) {
                                            Icon(Icons.Default.Check, null, tint = Color(0xFFE65100))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Profile
                    IconButton(onClick = onNavigateToProfile) {
                        Surface(Modifier.size(38.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    vendor?.name?.firstOrNull()?.uppercase() ?: "V",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Full-width Bottom Navigation (edge-to-edge)
            NavigationBar(
                containerColor = Color(0xFFFFF3E0),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(stringResource(R.string.nav_home), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE65100),
                        selectedTextColor = Color(0xFFE65100),
                        indicatorColor = Color(0xFFFFCC80),
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
                    selected = false,
                    onClick = onNavigateToBusinessOverview,
                    icon = { Icon(Icons.Default.Assessment, null) },
                    label = { Text(stringResource(R.string.nav_reports), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF8D6E63),
                        unselectedTextColor = Color(0xFF8D6E63)
                    )
                )
            }
        }

    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Welcome Card ──
            vendor?.let { v ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(48.dp), shape = CircleShape, color = Color(0xFFFFF3E0)) {
                            Box(contentAlignment = Alignment.Center) { Text("🙏", fontSize = 24.sp) }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(stringResource(R.string.namaskara, v.name), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            if (v.marketName.isNotBlank()) Text("📍 ${v.marketName}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF5D4037))
                        }
                    }
                }
            }

            // ── Total Outstanding ──
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE65100))) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFFE65100), Color(0xFFFF6D00)))).padding(24.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.total_outstanding), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("₹${String.format("%,.0f", totalOutstanding)}", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.2f)) {
                            Text("👥 " + stringResource(R.string.customers_count, customerCount), Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Today's Summary ──
            Text("📊 " + stringResource(R.string.today_summary), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryMiniCard(stringResource(R.string.sales), todaySales, Icons.Default.TrendingUp, PaymentGreen, Modifier.weight(1f).fillMaxHeight())
                SummaryMiniCard(stringResource(R.string.credit), todayCredit, Icons.Default.ArrowUpward, CreditRed, Modifier.weight(1f).fillMaxHeight())
                SummaryMiniCard(stringResource(R.string.received), todayPayments, Icons.Default.ArrowDownward, PaymentGreen, Modifier.weight(1f).fillMaxHeight())
            }



            Spacer(Modifier.height(100.dp)) // Extra space for bottom bar
        }
    }
}

@Composable
private fun SummaryMiniCard(title: String, amount: Double, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text("₹${String.format("%,.0f", amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color(0xFF5D4037))
        }
    }
}

@Composable
private fun ActionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Surface(Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = iconColor.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, lineHeight = 20.sp, color = Color(0xFF212121))
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = Color(0xFF757575))
        }
    }
}
