package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.OgRepository
import com.example.ui.admin.AdminPanelScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.editing.EditingScreen
import com.example.ui.faq.FaqScreen
import com.example.ui.home.HomeScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.search.SearchScreen
import com.example.ui.team.TeamScreen
import com.example.ui.theme.OgWarriorsTheme
import com.example.ui.tips.TipsScreen
import com.example.ui.tournaments.TournamentsScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            OgWarriorsTheme(darkTheme = isDarkTheme) {
                val repository = remember { OgRepository(this) }
                var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
                val coroutineScope = rememberCoroutineScope()

                DisposableEffect(Unit) {
                    val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
                        currentUser = auth.currentUser
                        if (auth.currentUser != null) {
                            coroutineScope.launch {
                                try {
                                    repository.syncUserProfile()
                                } catch (_: Exception) {}
                            }
                        }
                    }
                    Firebase.auth.addAuthStateListener(listener)
                    onDispose {
                        Firebase.auth.removeAuthStateListener(listener)
                    }
                }

                if (currentUser == null) {
                    AuthScreen(onAuthSuccess = {
                        currentUser = Firebase.auth.currentUser
                    })
                } else {
                    MainAppScaffold(
                        repository = repository,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = it },
                        onSignOut = { currentUser = null }
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Tips : Screen("tips", "Tips", Icons.Default.Psychology)
    object Tournaments : Screen("tournaments", "Tournaments", Icons.Default.EmojiEvents)
    object Team : Screen("team", "Teams", Icons.Default.Group)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Admin : Screen("admin", "Admin Panel", Icons.Default.Person)
    object Search : Screen("search", "Search", Icons.Default.Home)
    object Faq : Screen("faq", "FAQ & Rules", Icons.Default.Help)
}

@Composable
fun MainAppScaffold(
    repository: OgRepository,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Tips, Screen.Tournaments, Screen.Team, Screen.Profile)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute != Screen.Admin.route && currentRoute != Screen.Search.route && currentRoute != Screen.Faq.route) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    repository = repository,
                    onOpenSearch = { navController.navigate(Screen.Search.route) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Tips.route) { TipsScreen(repository) }
            composable(Screen.Tournaments.route) { TournamentsScreen(repository) }
            composable(Screen.Team.route) { TeamScreen(repository) }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    repository = repository,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onSignOut = onSignOut,
                    onOpenAdminPanel = { navController.navigate(Screen.Admin.route) },
                    onOpenFaq = { navController.navigate(Screen.Faq.route) }
                )
            }
            composable(Screen.Admin.route) {
                AdminPanelScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Faq.route) {
                FaqScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
