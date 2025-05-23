package com.luigi.motoparktokyo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    spots: List<Spot>,
    onBack: () -> Unit,
    onAddSpot: () -> Unit,
    onSpotClick: (Spot) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bike Parking Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
        // No floatingActionButton param here!
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val tokyo = LatLng(35.682839, 139.759455)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(tokyo, 12f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                spots.forEach { spot ->
                    if (spot.lat != null && spot.lng != null) {
                        Marker(
                            state = MarkerState(position = LatLng(spot.lat, spot.lng)),
                            title = spot.name,
                            snippet = spot.address ?: "",
                            onClick = {
                                onSpotClick(spot)
                                false
                            }
                        )
                    }
                }
            }
            // Place the FAB at the bottom left
            FloatingActionButton(
                onClick = onAddSpot,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 24.dp) // change padding if needed
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Spot")
            }
        }
    }
}
