package com.pemmob.margocoffe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pemmob.margocoffe.navigation.Screen
import com.pemmob.margocoffe.ui.screens.AuthScreen
import com.pemmob.margocoffe.ui.screens.CheckoutScreen
import com.pemmob.margocoffe.ui.screens.DrinkDetailScreen
import com.pemmob.margocoffe.ui.screens.HomeScreen
import com.pemmob.margocoffe.ui.screens.MenuScreen
import com.pemmob.margocoffe.ui.screens.OrderStatusScreen
import com.pemmob.margocoffe.ui.screens.ProfileScreen
import com.pemmob.margocoffe.ui.screens.RewardsScreen
import com.pemmob.margocoffe.ui.theme.KopiRoyalTheme
import com.pemmob.margocoffe.viewmodel.AuthViewModel
import com.pemmob.margocoffe.viewmodel.CheckoutViewModel
import com.pemmob.margocoffe.viewmodel.DetailViewModel
import com.pemmob.margocoffe.viewmodel.HomeViewModel
import com.pemmob.margocoffe.viewmodel.MenuViewModel
import com.pemmob.margocoffe.viewmodel.NotificationViewModel
import com.pemmob.margocoffe.viewmodel.OrderViewModel
import com.pemmob.margocoffe.viewmodel.ProfileViewModel
import com.pemmob.margocoffe.viewmodel.RewardsViewModel

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

    val homeViewModel: HomeViewModel = viewModel()
    val menuViewModel: MenuViewModel = viewModel()
    val rewardsViewModel: RewardsViewModel = viewModel()
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    // AuthViewModel harus diambil di dalam @Composable
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                authViewModel = authViewModel,
                onAuthenticated = {
                    val prev = navController.previousBackStackEntry
                    if (prev != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.Home) {
                            popUpTo<Screen.Auth> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // ─── Home Screen ────────────────────────────────────────
        composable<Screen.Home> {
            HomeScreen(
                homeViewModel = homeViewModel,
                menuViewModel = menuViewModel,
                rewardsViewModel = rewardsViewModel,
                checkoutViewModel = checkoutViewModel,
                notificationViewModel = notificationViewModel,
                profileViewModel = profileViewModel,
                onCoffeeClick = { coffeeId ->
                    navController.navigate(Screen.Detail(coffeeId = coffeeId))
                },
                onMenuClick = {
                    navController.navigate(Screen.Menu) { launchSingleTop = true }
                },
                onRewardsClick = {
                    navController.navigate(Screen.Rewards) { launchSingleTop = true }
                },
                onOrdersClick = {
                    navController.navigate(Screen.OrderStatus) { launchSingleTop = true }
                },
                onCartClick = {
                    navController.navigate(Screen.Checkout)
                },
                onProfileClick = {
                    if (authViewModel.uiState.value.isAuthenticated) {
                        navController.navigate(Screen.Profile)
                    } else {
                        navController.navigate(Screen.Auth)
                    }
                }
            )
        }

        composable<Screen.Menu> {
            MenuScreen(
                menuViewModel = menuViewModel,
                checkoutViewModel = checkoutViewModel,
                onHomeClick = {
                    navController.navigate(Screen.Home) { launchSingleTop = true }
                },
                onCoffeeClick = { coffeeId ->
                    navController.navigate(Screen.Detail(coffeeId = coffeeId))
                },
                onRewardsClick = {
                    navController.navigate(Screen.Rewards) { launchSingleTop = true }
                },
                onOrdersClick = {
                    navController.navigate(Screen.OrderStatus) { launchSingleTop = true }
                },
                onCartClick = {
                    navController.navigate(Screen.Checkout)
                }
            )
        }

        // ─── Rewards Screen ─────────────────────────────────────
        composable<Screen.Rewards> {
            RewardsScreen(
                viewModel = rewardsViewModel,
                profileViewModel = profileViewModel,
                onHomeClick = {
                    navController.navigate(Screen.Home) { launchSingleTop = true }
                },
                onMenuClick = {
                    navController.navigate(Screen.Menu) { launchSingleTop = true }
                },
                onOrdersClick = {
                    navController.navigate(Screen.OrderStatus) { launchSingleTop = true }
                }
            )
        }

        // ─── Drink Detail Screen ────────────────────────────────
        composable<Screen.Detail> { backStackEntry ->
            val detail: Screen.Detail = backStackEntry.toRoute()
            val detailViewModel: DetailViewModel = viewModel()

            DrinkDetailScreen(
                coffeeId = detail.coffeeId,
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onAddToCart = {
                    navController.navigate(Screen.Menu) {
                        popUpTo(Screen.Home) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ─── Checkout Screen ────────────────────────────────────
        composable<Screen.Checkout> {
            val authState by authViewModel.uiState.collectAsState()

            CheckoutScreen(
                viewModel = checkoutViewModel,
                orderViewModel = orderViewModel,
                profileViewModel = profileViewModel,
                isAuthenticated = authState.isAuthenticated,
                onRequireLogin = {
                    navController.navigate(Screen.Auth)
                },
                onBackClick = { navController.popBackStack() },
                onProceedPayment = {
                    navController.navigate(Screen.OrderStatus) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                }
            )
        }

        // ─── Order Status Screen (Pesanan Saya) ─────────────────
        composable<Screen.OrderStatus> {
            OrderStatusScreen(
                viewModel = orderViewModel,
                onHomeClick = {
                    navController.navigate(Screen.Home) { launchSingleTop = true }
                },
                onMenuClick = {
                    navController.navigate(Screen.Menu) { launchSingleTop = true }
                },
                onRewardsClick = {
                    navController.navigate(Screen.Rewards) { launchSingleTop = true }
                }
            )
        }

        // ─── Profile Screen ─────────────────────────────────────
        composable<Screen.Profile> {
            ProfileScreen(
                profileViewModel = profileViewModel,
                authViewModel = authViewModel,
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}