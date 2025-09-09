package com.bsi.urban

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bsi.urban.ui.theme.UrbanTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

class EndResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val realLat    = intent.getDoubleExtra("real_lat", 0.0)
        val realLng    = intent.getDoubleExtra("real_lng", 0.0)
        val guessLat   = intent.getDoubleExtra("guess_lat", 0.0)
        val guessLng   = intent.getDoubleExtra("guess_lng", 0.0)
        val distance   = intent.getDoubleExtra("distance_km", 0.0)
        val round      = intent.getIntExtra("round", 1)
        val roundPts   = intent.getIntExtra("round_points", 0)
        val totalSoFar = intent.getIntExtra("total_score", 0)

        setContent {
            UrbanTheme {
                EndResultScreen(
                    realLat = realLat,
                    realLng = realLng,
                    guessLat = guessLat,
                    guessLng = guessLng,
                    distanceKm = distance,
                    round = round,
                    roundPoints = roundPts,
                    totalSoFar = totalSoFar,
                    onNext = {
                        val newTotal = totalSoFar + roundPts
                        if (round < GameScoring.TOTAL_ROUNDS) {
                            startActivity(
                                Intent(this, SinglePlayer::class.java).apply {
                                    putExtra("round", round + 1)
                                    putExtra("total_score", newTotal)
                                }
                            )
                        } else {
                            startActivity(
                                Intent(this, FinalScoreActivity::class.java).apply {
                                    putExtra("final_score", newTotal)
                                }
                            )
                        }
                        finish()
                    },
                    onBackToMenu = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndResultScreen(
    realLat: Double,
    realLng: Double,
    guessLat: Double,
    guessLng: Double,
    distanceKm: Double,
    round: Int,
    roundPoints: Int,
    totalSoFar: Int,
    onNext: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val real = LatLng(realLat, realLng)
    val guess = LatLng(guessLat, guessLng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(real, 3f)
    }

    // Karte auf beide Punkte zoomen
    LaunchedEffect(Unit) {
        val bounds = LatLngBounds.builder().include(real).include(guess).build()
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, 120)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ergebnis – Runde $round / ${GameScoring.TOTAL_ROUNDS}") },
                navigationIcon = { TextButton(onClick = onBackToMenu) { Text("Menü") } }
            )
        },
        bottomBar = {
            BottomAppBar {
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onNext,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(if (round < GameScoring.TOTAL_ROUNDS) "Weiter" else "Zum Highscore")
                }
            }
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Tipp
                Marker(
                    state = MarkerState(position = guess),
                    title = "Dein Tipp"
                )
                // Tatsächlicher Ort
                Marker(
                    state = MarkerState(position = real),
                    title = "Tatsächlicher Ort"
                )
                // Linie
                Polyline(points = listOf(guess, real))
            }

            // Info-Karte über der Map
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(androidx.compose.ui.Alignment.BottomCenter)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Distanz: ${"%.1f".format(distanceKm)} km",
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Punkte in dieser Runde: $roundPoints")
                    Text("Bisherige Gesamtpunkte: $totalSoFar")
                }
            }
        }
    }
}
