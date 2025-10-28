package com.example.as4

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.example.as4.ui.theme.As4Theme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ViewAdapter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.random.Random

//Q1 LifeTracker – A Lifecycle-Aware Activity Logger

enum class LifeState { Created, Started, Resumed, Paused, Stopped, Destroyed }
data class LifeEvent(val label: String, val time: Long, val color: Color)
data class LifeUi(
    val current: LifeState,
    val events: List<LifeEvent>,
    val snack: Boolean
)

class LifeVM : ViewModel() {
    val ui = MutableStateFlow(LifeUi(LifeState.Created, emptyList(), snack = true))
    fun toggleSnack() = ui.update { it.copy(snack = !it.snack) }
    fun log(label: String, state: LifeState) {
        val c = when (state) {
            LifeState.Created -> Color(0xFF4CAF50)
            LifeState.Started -> Color(0xFF2196F3)
            LifeState.Resumed -> Color(0xFF9C27B0)
            LifeState.Paused -> Color(0xFFFFC107)
            LifeState.Stopped -> Color(0xFFFF5722)
            LifeState.Destroyed -> Color(0xFF9E9E9E)

        }
        ui.update {
            it.copy(
                current = state,
                events = listOf(LifeEvent(label, System.currentTimeMillis(), c)) + it.events
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTrackerScreen(vm: LifeVM = viewModel()) {
    val ui = vm.ui.collectAsState().value
    val snackbarHost = remember { SnackbarHostState() }
    val owner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val fmt =
        remember { DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault()) }

    DisposableEffect(owner) {
        val obs = LifecycleEventObserver { _, e ->
            val pair = when (e) {
                Lifecycle.Event.ON_CREATE -> "onCreate" to LifeState.Created
                Lifecycle.Event.ON_START -> "onStart" to LifeState.Started
                Lifecycle.Event.ON_RESUME -> "onResume" to LifeState.Resumed
                Lifecycle.Event.ON_PAUSE -> "onPause" to LifeState.Paused
                Lifecycle.Event.ON_STOP -> "onStop" to LifeState.Stopped
                Lifecycle.Event.ON_DESTROY -> "onDestroy" to LifeState.Destroyed
                else -> null
            } ?: return@LifecycleEventObserver


            vm.log(pair.first, pair.second)


            if (vm.ui.value.snack) {
                scope.launch {
                    snackbarHost.showSnackbar("Transition: ${pair.first}")
                }
            }
        }

        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Q1 – LifeTracker (state: ${ui.current})") })
            },
            snackbarHost = {
                SnackbarHost(snackbarHost)
            }

        ) { pad ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row {
                    Text("Snackbar on transition")
                    Spacer(Modifier.width(8.dp))
                    Switch(checked = ui.snack, onCheckedChange = { vm.toggleSnack() })
                }
                LazyColumn(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ui.events) { ev ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(ev.color)
                                .padding(12.dp)
                        ) {
                            Text(
                                "${fmt.format(Instant.ofEpochMilli(ev.time))} • ${ev.label}",
                                color = Color.White
                            )
                        }
                    }
                }


            }
        }
    }
}

