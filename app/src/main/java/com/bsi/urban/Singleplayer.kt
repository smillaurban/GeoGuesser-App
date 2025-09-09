package com.bsi.urban

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bsi.urban.ui.theme.UrbanTheme
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import kotlin.random.Random

class SinglePlayer : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Standardwerte: Runde 1, Score 0
        val round = intent.getIntExtra("round", 1)
        val totalScore = intent.getIntExtra("total_score", 0)

        setContent {
            UrbanTheme {
                // trailing lambda = onBack
                SinglePlayerStreetViewScreen(
                    round,
                    totalScore
                ) {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePlayerStreetViewScreen(
    round: Int,
    totalScore: Int,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    // StreetView-View nur einmal erzeugen
    val svView = remember {
        val opts = StreetViewPanoramaOptions()
            .userNavigationEnabled(true)
            .panningGesturesEnabled(true)
            .zoomGesturesEnabled(true)
            .streetNamesEnabled(false)
        StreetViewPanoramaView(ctx, opts)
    }

    // Lifecycle korrekt durchreichen
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, svView) {
        svView.onCreate(null)
        val obs = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_START -> svView.onStart()
                Lifecycle.Event.ON_RESUME -> svView.onResume()
                Lifecycle.Event.ON_PAUSE -> svView.onPause()
                Lifecycle.Event.ON_STOP -> svView.onStop()
                Lifecycle.Event.ON_DESTROY -> svView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(obs)
        onDispose {
            lifecycle.removeObserver(obs)
            svView.onDestroy()
        }
    }

    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var panoRef by remember { mutableStateOf<StreetViewPanorama?>(null) }
    var currentPos by remember { mutableStateOf<LatLng?>(null) }

    // Panorama initialisieren
    LaunchedEffect(Unit) {
        svView.getStreetViewPanoramaAsync { pano ->
            panoRef = pano
            configurePanoramaGestures(pano)

            pano.setOnStreetViewPanoramaChangeListener { loc: StreetViewPanoramaLocation? ->
                loading = false
                currentPos = loc?.position
                if (loc?.position == null) {
                    errorMsg = "Kein Street View hier – neuer Versuch…"
                    setRandomSpot(pano)
                } else {
                    errorMsg = null
                }
            }

            setRandomSpot(pano) // ersten zufälligen Spot setzen
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Singleplayer – Runde $round / ${GameScoring.TOTAL_ROUNDS}") },
                navigationIcon = {
                    TextButton(onClick = { onBack() }) { Text("Zurück") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val pos = currentPos
                    if (pos == null) {
                        Toast.makeText(ctx, "Position lädt noch…", Toast.LENGTH_SHORT).show()
                    } else {
                        val i = Intent(ctx, GuessMapActivity::class.java).apply {
                            putExtra("real_lat", pos.latitude)
                            putExtra("real_lng", pos.longitude)
                            putExtra("round", round)           // aktuelle Runde
                            putExtra("total_score", totalScore) // bisherige Summe
                        }
                        ctx.startActivity(i)
                    }
                }
            ) {
                Icon(Icons.Filled.Place, contentDescription = "Auf Karte tippen")
            }
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            AndroidView(factory = { svView }, modifier = Modifier.fillMaxSize())

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMsg?.let { msg ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                ) {
                    AssistChip(onClick = { errorMsg = null }, label = { Text(msg) })
                }
            }
        }
    }
}

private fun configurePanoramaGestures(pano: StreetViewPanorama) {
    pano.isPanningGesturesEnabled = true
    pano.isZoomGesturesEnabled = true
    pano.isUserNavigationEnabled = true
    pano.isStreetNamesEnabled = false
}

private fun setRandomSpot(pano: StreetViewPanorama) {
    val regions = listOf(
        47.0..55.0 to 5.0..15.5,         // Mitteleuropa
        24.0..49.0 to -125.0..-66.0,     // USA
        30.0..46.0 to 129.0..146.0,      // Japan
        -45.0..-10.0 to 112.0..154.0,    // Australien
        35.0..42.0 to -10.0..5.0         // Iberische Halbinsel
    )
    fun rand(r: ClosedFloatingPointRange<Double>) =
        Random.nextDouble(r.start, r.endInclusive)

    val (latRange, lonRange) = regions.random()
    val lat = rand(latRange)
    val lon = rand(lonRange)

    pano.setPosition(LatLng(lat, lon), 15000) // 15 km Suchradius
}
