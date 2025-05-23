package com.luigi.motoparktokyo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpotScreen(
    onBack: () -> Unit,
    onSpotAdded: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Spot") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Capacity") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = { Text("Photo URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isLoading = true
                    error = null
                    success = false
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        error = "Not logged in"
                        isLoading = false
                        return@Button
                    }
                    user.getIdToken(true)
                        .addOnSuccessListener { tokenResult ->
                            coroutineScope.launch {
                                try {
                                    val client = HttpClient(OkHttp) {
                                        install(ContentNegotiation) {
                                            json(Json { ignoreUnknownKeys = true })
                                        }
                                    }
                                    val response = client.post("http://10.0.2.2:4000/api/spots") {
                                        headers {
                                            append("Authorization", "Bearer ${tokenResult.token}")
                                            append("Content-Type", "application/json")
                                        }
                                        setBody(
                                            mapOf(
                                                "name" to name,
                                                "address" to address,
                                                "capacity" to capacity,
                                                "price" to price,
                                                "type" to type,
                                                "photo_url" to photoUrl
                                            )
                                        )
                                    }
                                    if (response.status.value in 200..299) {
                                        success = true
                                        // Reset fields
                                        name = ""
                                        address = ""
                                        capacity = ""
                                        price = ""
                                        type = ""
                                        photoUrl = ""
                                        onSpotAdded?.invoke()
                                    } else {
                                        error = "Server error: ${response.status.value}"
                                    }
                                } catch (e: Exception) {
                                    error = "Failed: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                        .addOnFailureListener {
                            error = "Auth failed: ${it.localizedMessage}"
                            isLoading = false
                        }
                },
                enabled = !isLoading && name.isNotBlank() && address.isNotBlank() && capacity.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Adding..." else "Add Spot")
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
            if (success) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Spot added successfully!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
