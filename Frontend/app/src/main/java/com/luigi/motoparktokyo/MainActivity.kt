package com.luigi.motoparktokyo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luigi.motoparktokyo.ui.theme.MotoParkTokyoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Divider
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
            var navScreen by remember { mutableStateOf("home") }

            // Global spot list, reload when needed
            var spots by remember { mutableStateOf<List<Spot>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            val client = remember {
                HttpClient(OkHttp) {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                }
            }

            // --- Helper: load spots from backend
            suspend fun fetchSpots() {
                isLoading = true
                error = null
                try {
                    val response = client.get("http://10.0.2.2:4000/api/spots")
                    spots = Json.decodeFromString<List<Spot>>(response.bodyAsText())
                } catch (e: Exception) {
                    error = "Failed to load spots: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
            }

            // Fetch spots once at login and after adding a spot
            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) fetchSpots()
            }

            // Reload after adding
            fun reloadSpotsAfterAdd() {
                navScreen = "parking"
                // Triggered in Composable, launch suspend!
                // Workaround: trigger via LaunchedEffect
                // Use a key that will change (here, navScreen)
            }
            LaunchedEffect(navScreen) {
                if (navScreen == "parking" && isLoggedIn) fetchSpots()
            }

            MotoParkTokyoTheme {
                if (isLoggedIn) {
                    when (navScreen) {
                        "home" -> HomeScreen(
                            email = auth.currentUser?.email ?: "Unknown",
                            onLogout = {
                                auth.signOut()
                                isLoggedIn = false
                            },
                            onFindParking = { navScreen = "parking" },
                            onShowMap = { navScreen = "map" },
                            onShowFavorites = { navScreen = "favorites" }
                        )
                        "parking" -> ParkingListScreen(
                            spots = spots,
                            isLoading = isLoading,
                            error = error,
                            onBack = { navScreen = "home" },
                            onShowMap = { navScreen = "map" },
                            onAddSpot = { navScreen = "addSpot" }
                        )
                        "map" -> MapScreen(
                            spots = spots,
                            onBack = { navScreen = "parking" },
                            onAddSpot = { navScreen = "addSpot" }
                        )
                        "addSpot" -> AddSpotScreen(
                            onBack = { navScreen = "parking" },
                            onSpotAdded = { reloadSpotsAfterAdd() }
                        )
                        "favorites" -> FavoritesScreen(
                            onBack = { navScreen = "home" }
                        )
                    }
                } else {
                    AuthScreen(
                        auth = auth,
                        onAuthSuccess = {
                            isLoggedIn = true
                            navScreen = "home"
                        }
                    )
                }
            }
        }
    }
}

// HomeScreen: updated with a "Show Map" button and "Favorites" button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    email: String,
    onLogout: () -> Unit,
    onFindParking: () -> Unit,
    onShowMap: () -> Unit,
    onShowFavorites: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MotoPark Tokyo") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to MotoPark Tokyo!",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("You are signed in as:")
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onFindParking,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Find Parking (List)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onShowMap,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Map")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onShowFavorites,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Favorites & Recent Spots")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MotoPark Tokyo",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Code Chrysalis 2025", color = MaterialTheme.colorScheme.secondary)
        }
    }
}
