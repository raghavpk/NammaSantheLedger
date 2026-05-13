package com.namma.santheledger.ui.navigation

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.namma.santheledger.data.repository.LedgerRepository
import com.namma.santheledger.ui.screens.*
import com.namma.santheledger.viewmodel.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object CreateAccount : Screen("create_account")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object BusinessOverview : Screen("business_overview")
    object CustomerList : Screen("customer_list")
    object AddCustomer : Screen("add_customer")
    object CustomerLedger : Screen("customer_ledger/{customerId}") {
        fun createRoute(customerId: Long) = "customer_ledger/$customerId"
    }
    object AddTransaction : Screen("add_transaction/{customerId}") {
        fun createRoute(customerId: Long) = "add_transaction/$customerId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: LedgerRepository,
    prefs: SharedPreferences
) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(repository, prefs))

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── Splash ──
        composable(Screen.Splash.route) {
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            SplashScreen(
                isLoggedIn = isLoggedIn,
                onSplashFinished = { loggedIn ->
                    navController.navigate(if (loggedIn) Screen.Home.route else Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ──
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoggedIn = { navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } } },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onCreateAccount = { navController.navigate(Screen.CreateAccount.route) }
            )
        }

        // ── Create Account ──
        composable(Screen.CreateAccount.route) {
            CreateAccountScreen(
                authViewModel = authViewModel,
                onRegistered = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.CreateAccount.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ── Forgot Password ──
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onPasswordReset = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Home ──
        composable(Screen.Home.route) {
            val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = vm,
                authViewModel = authViewModel,
                onNavigateToCustomers = { 
                    navController.navigate(Screen.CustomerList.route) { 
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    } 
                },
                onNavigateToBusinessOverview = { 
                    navController.navigate(Screen.BusinessOverview.route) { 
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    } 
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ── Profile ──
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        // ── Business Overview (Reports) ──
        composable(Screen.BusinessOverview.route) {
            BusinessOverviewScreen(
                repository = repository,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { 
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true 
                    } 
                },
                onNavigateToCustomers = { 
                    navController.navigate(Screen.CustomerList.route) { 
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    } 
                }
            )
        }

        // ── Customer List ──
        composable(Screen.CustomerList.route) {
            val vm: CustomerViewModel = viewModel(factory = CustomerViewModel.Factory(repository))
            CustomerListScreen(
                viewModel = vm,
                onCustomerClick = { id -> navController.navigate(Screen.CustomerLedger.createRoute(id)) },
                onAddCustomer = { navController.navigate(Screen.AddCustomer.route) },
                onNavigateToHome = { 
                    navController.navigate(Screen.Home.route) { 
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true 
                    } 
                },
                onNavigateToReports = { 
                    navController.navigate(Screen.BusinessOverview.route) { 
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    } 
                },
                repository = repository
            )
        }

        // ── Add Customer ──
        composable(Screen.AddCustomer.route) {
            val vm: CustomerViewModel = viewModel(factory = CustomerViewModel.Factory(repository))
            AddCustomerScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        // ── Customer Ledger ──
        composable(
            route = Screen.CustomerLedger.route,
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("customerId") ?: 0L
            val vm: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory(repository, id))
            CustomerLedgerScreen(
                viewModel = vm,
                onAddTransaction = { navController.navigate(Screen.AddTransaction.createRoute(id)) },
                onBack = { navController.popBackStack() },
                onDeleteCustomer = { navController.popBackStack() }
            )
        }

        // ── Add Transaction ──
        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("customerId") ?: 0L
            val vm: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory(repository, id))
            AddTransactionScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
