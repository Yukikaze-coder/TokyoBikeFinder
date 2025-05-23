package com.luigi.motoparktokyo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.Alignment

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Star



import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import androidx.glance.appwidget.compose
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingListScreen(
    spots: List<Spot>,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onShowMap: () -> Unit,
    onAddSpot: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val client = remember {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    // Store IDs of favorites
    var favoriteIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Load user's favorites (runs once)
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.getIdToken(true)
                .addOnSuccessListener { tokenResult ->
                    coroutineScope.launch {
                        try {
                            val response = client.get("http://10.0.2.2:4000/api/favorites") {
                                header("Authorization", "Bearer ${tokenResult.token}")
                            }
                            val favSpots: List<Spot> = kotlinx.serialization.json.Json.decodeFromString(
                                response.bodyAsText()
                            )
                            favoriteIds = favSpots.mapNotNull { it.id }.toSet()
                        } catch (_: Exception) { /* ignore for now */ }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bike Parking Spots") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSpot) {
                Icon(Icons.Default.Add, contentDescription = "Add Spot")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(error, color = MaterialTheme.colorScheme.error)
                spots.isEmpty() -> Text("No spots found.")
                else -> LazyColumn {
                    items(spots) { spot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Spot info
                                    Text(spot.name, style = MaterialTheme.typography.titleMedium)
                                    Text(spot.address)
                                    Text("Capacity: ${spot.capacity}")
                                }
                                // Favorite icon: Top right, inside the Card
                                IconButton(
                                    onClick = {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        if (user != null && spot.id != null) {
                                            user.getIdToken(true)
                                                .addOnSuccessListener { tokenResult ->
                                                    coroutineScope.launch {
                                                        try {
                                                            if (favoriteIds.contains(spot.id)) {
                                                                // Unfavorite
                                                                client.delete("http://10.0.2.2:4000/api/favorites/${spot.id}") {
                                                                    header("Authorization", "Bearer ${tokenResult.token}")
                                                                }
                                                                favoriteIds = favoriteIds - spot.id
                                                            } else {
                                                                // Favorite
                                                                client.post("http://10.0.2.2:4000/api/favorites") {
                                                                    header("Authorization", "Bearer ${tokenResult.token}")
                                                                    contentType(ContentType.Application.Json)
                                                                    setBody(mapOf("spot_id" to spot.id))
                                                                }
                                                                favoriteIds = favoriteIds + spot.id
                                                            }
                                                        } catch (_: Exception) { /* Show error if needed */ }
                                                    }
                                                }
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (spot.id != null && favoriteIds.contains(spot.id))
                                            Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Favorite"
                                    )

                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onShowMap,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Map")
            }
        }
    }
}

