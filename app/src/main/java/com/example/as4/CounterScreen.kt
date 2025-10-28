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



//Q2 Counter++ – Reactive UI with StateFlow & Coroutines
data class CounterUi(
    val count: Int = 0,
    val auto: Boolean = false,
    val intervalMs: Long = 3000
)

class CounterVM : ViewModel() {
    val ui = MutableStateFlow(CounterUi())
    private var job: Job? = null

    fun inc() = ui.update { it.copy(count = it.count + 1) }
    fun dec() = ui.update { it.copy(count = it.count - 1) }
    fun reset() = ui.update { it.copy(count = 0) }

    /** Settings: change interval; if auto is running, restart loop with new delay. */
    fun setInterval(ms: Long) {
        ui.update { it.copy(intervalMs = ms) }
        if (ui.value.auto) startAuto()
    }

    fun toggleAuto() { if (ui.value.auto) stopAuto() else startAuto() }

    /** Start a repeating coroutine that delays N ms and then increments. */
    private fun startAuto() {
        ui.update { it.copy(auto = true) }
        job?.cancel()
        job = viewModelScope.launch {
            while (true) {
                delay(ui.value.intervalMs)
                inc() // each tick -> +1
            }
        }
    }

    /** Stop the coroutine loop. */
    private fun stopAuto() {
        ui.update { it.copy(auto = false) }
        job?.cancel(); job = null
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(vm: com.example.as4.CounterVM = viewModel()) {
    val ui = vm.ui.collectAsState().value
    var input by remember { mutableStateOf(ui.intervalMs.toString()) }

    Scaffold(topBar = { TopAppBar(title = { Text("Q2 – Counter++") }) }) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Count: ${ui.count}", style = MaterialTheme.typography.headlineMedium)
            Text("Auto mode: ${if (ui.auto) "ON" else "OFF"}")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = vm::inc) { Text("+1") }
                Button(onClick = vm::dec) { Text("-1") }
                OutlinedButton(onClick = vm::reset) { Text("Reset") }
                FilledTonalButton(onClick = vm::toggleAuto) {
                    Text(if (ui.auto) "Stop Auto" else "Start Auto")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Interval (ms):")
                OutlinedTextField(value = input, onValueChange = { input = it }, singleLine = true)
                Button(onClick = { input.toLongOrNull()?.let(vm::setInterval) }) { Text("Apply") }
            }
        }
    }
}

