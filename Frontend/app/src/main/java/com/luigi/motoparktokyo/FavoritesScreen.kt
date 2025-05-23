package com.luigi.motoparktokyo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.call.body
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit
) {
    var favorites by remember { mutableStateOf<List<Spot>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    // Create client ONCE and reuse
    val client = remember {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            error = "Not logged in"
            isLoading = false
            return@LaunchedEffect
        }

        user.getIdToken(true)
            .addOnSuccessListener { tokenResult ->
                coroutineScope.launch {
                    try {
                        val spots: List<Spot> = client.get("http://10.0.2.2:4000/api/favorites") {
                            headers {
                                append("Authorization", "Bearer ${tokenResult.token}")
                            }
                        }.body()
                        favorites = spots
                        error = null
                    } catch (e: Exception) {
                        error = "Failed to fetch favorites: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            }
            .addOnFailureListener {
                error = "Auth failed: ${it.localizedMessage}"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites & Recent Spots") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error ?: "", color = MaterialTheme.colorScheme.error)
                favorites.isEmpty() -> Text("No favorites yet.")
                else -> {
                    LazyColumn {
                        items(favorites) { spot ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(spot.name, style = MaterialTheme.typography.titleMedium)
                                    Text(spot.address)
                                    spot.capacity?.let {
                                        Text("Capacity: $it")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
