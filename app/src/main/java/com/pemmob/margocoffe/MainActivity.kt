package com.pemmob.margocoffe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pemmob.margocoffe.navigation.Screen
import com.pemmob.margocoffe.ui.screens.CheckoutScreen
import com.pemmob.margocoffe.ui.screens.DrinkDetailScreen
import com.pemmob.margocoffe.ui.screens.HomeScreen
import com.pemmob.margocoffe.ui.screens.MenuScreen
import com.pemmob.margocoffe.ui.screens.OrderStatusScreen
import com.pemmob.margocoffe.ui.theme.KopiRoyalTheme
import com.pemmob.margocoffe.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KopiRoyalTheme {
                KopiRoyalApp()
            }
        }
    }
}

@Composable
fun KopiRoyalApp() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        // ─── Home Screen ────────────────────────────────────────
        composable<Screen.Home> {
            HomeScreen(
                viewModel = viewModel,
                onCoffeeClick = { coffeeId ->
                    navController.navigate(Screen.Detail(coffeeId = coffeeId))
                },
                onMenuClick = {
                    navController.navigate(Screen.Menu) { launchSingleTop = true }
                },
                onCartClick = {
                    navController.navigate(Screen.Checkout)
                }
            )
        }

        composable<Screen.Menu> {
            MenuScreen(
                viewModel = viewModel,
                onHomeClick = {
                    navController.navigate(Screen.Home) { launchSingleTop = true }
                },
                onCoffeeClick = { coffeeId ->
                    navController.navigate(Screen.Detail(coffeeId = coffeeId))
                },
                onCartClick = {
                    navController.navigate(Screen.Checkout)
                }
            )
        }

        // ─── Drink Detail Screen ────────────────────────────────
        composable<Screen.Detail> { backStackEntry ->
            val detail: Screen.Detail = backStackEntry.toRoute()
            DrinkDetailScreen(
                coffeeId = detail.coffeeId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onAddToCart = { navController.navigate(Screen.Checkout) }
            )
        }

        // ─── Checkout Screen ────────────────────────────────────
        composable<Screen.Checkout> {
            CheckoutScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProceedPayment = {
                    navController.navigate(Screen.OrderStatus) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                }
            )
        }

        // ─── Order Status Screen ────────────────────────────────
        composable<Screen.OrderStatus> {
            OrderStatusScreen(
                viewModel = viewModel,
                onBackToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}