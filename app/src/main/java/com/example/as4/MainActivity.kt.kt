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


import com.example.as4.TemperatureScreen
import com.example.as4.CounterScreen
import com.example.as4.LifeTrackerScreen


class `MainActivity.kt` : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//           LifeTrackerScreen()
//            CounterScreen()
            TemperatureScreen()
        }
    }
}

////Q1 LifeTracker – A Lifecycle-Aware Activity Logger
//
//enum class LifeState { Created, Started, Resumed, Paused, Stopped, Destroyed }
//data class LifeEvent(val label: String, val time: Long, val color: Color)
//data class LifeUi(
//    val current: LifeState,
//    val events: List<LifeEvent>,
//    val snack: Boolean
//)
//
//class LifeVM : ViewModel() {
//    val ui = MutableStateFlow(LifeUi(LifeState.Created, emptyList(), snack = true))
//    fun toggleSnack() = ui.update { it.copy(snack = !it.snack) }
//    fun log(label: String, state: LifeState) {
//        val c = when (state) {
//            LifeState.Created -> Color(0xFF4CAF50)
//            LifeState.Started -> Color(0xFF2196F3)
//            LifeState.Resumed -> Color(0xFF9C27B0)
//            LifeState.Paused -> Color(0xFFFFC107)
//            LifeState.Stopped -> Color(0xFFFF5722)
//            LifeState.Destroyed -> Color(0xFF9E9E9E)
//
//        }
//        ui.update {
//            it.copy(
//                current = state,
//                events = listOf(LifeEvent(label, System.currentTimeMillis(), c)) + it.events
//            )
//        }
//    }
//
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LifeTrackerScreen(vm: LifeVM = viewModel()) {
//    val ui = vm.ui.collectAsState().value
//    val snackbarHost = remember { SnackbarHostState() }
//    val owner = LocalLifecycleOwner.current
//    val scope = rememberCoroutineScope()
//    val fmt =
//        remember { DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault()) }
//
//    DisposableEffect(owner) {
//        val obs = LifecycleEventObserver { _, e ->
//            val pair = when (e) {
//                Lifecycle.Event.ON_CREATE -> "onCreate" to LifeState.Created
//                Lifecycle.Event.ON_START -> "onStart" to LifeState.Started
//                Lifecycle.Event.ON_RESUME -> "onResume" to LifeState.Resumed
//                Lifecycle.Event.ON_PAUSE -> "onPause" to LifeState.Paused
//                Lifecycle.Event.ON_STOP -> "onStop" to LifeState.Stopped
//                Lifecycle.Event.ON_DESTROY -> "onDestroy" to LifeState.Destroyed
//                else -> null
//            } ?: return@LifecycleEventObserver
//
//
//            vm.log(pair.first, pair.second)
//
//
//            if (vm.ui.value.snack) {
//                scope.launch {
//                    snackbarHost.showSnackbar("Transition: ${pair.first}")
//                }
//            }
//        }
//
//        owner.lifecycle.addObserver(obs)
//        onDispose { owner.lifecycle.removeObserver(obs) }
//    }
//    MaterialTheme {
//        Scaffold(
//            topBar = {
//                TopAppBar(title = { Text("Q1 – LifeTracker (state: ${ui.current})") })
//            },
//            snackbarHost = {
//                SnackbarHost(snackbarHost)
//            }
//
//        ) { pad ->
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(pad)
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Row {
//                    Text("Snackbar on transition")
//                    Spacer(Modifier.width(8.dp))
//                    Switch(checked = ui.snack, onCheckedChange = { vm.toggleSnack() })
//                }
//                LazyColumn(
//                    Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(ui.events) { ev ->
//                        Row(
//                            Modifier
//                                .fillMaxWidth()
//                                .background(ev.color)
//                                .padding(12.dp)
//                        ) {
//                            Text(
//                                "${fmt.format(Instant.ofEpochMilli(ev.time))} • ${ev.label}",
//                                color = Color.White
//                            )
//                        }
//                    }
//                }
//
//
//            }
//        }
//    }
//}
//
////Q2 Counter++ – Reactive UI with StateFlow & Coroutines
//data class CounterUi(
//    val count: Int = 0,
//    val auto: Boolean = false,
//    val intervalMs: Long = 3000
//)
//
//class CounterVM : ViewModel() {
//    val ui = MutableStateFlow(CounterUi())
//    private var job: Job? = null
//
//    fun inc() = ui.update { it.copy(count = it.count + 1) }
//    fun dec() = ui.update { it.copy(count = it.count - 1) }
//    fun reset() = ui.update { it.copy(count = 0) }
//
//    /** Settings: change interval; if auto is running, restart loop with new delay. */
//    fun setInterval(ms: Long) {
//        ui.update { it.copy(intervalMs = ms) }
//        if (ui.value.auto) startAuto()
//    }
//
//    fun toggleAuto() { if (ui.value.auto) stopAuto() else startAuto() }
//
//    /** Start a repeating coroutine that delays N ms and then increments. */
//    private fun startAuto() {
//        ui.update { it.copy(auto = true) }
//        job?.cancel()
//        job = viewModelScope.launch {
//            while (true) {
//                delay(ui.value.intervalMs)
//                inc() // each tick -> +1
//            }
//        }
//    }
//
//    /** Stop the coroutine loop. */
//    private fun stopAuto() {
//        ui.update { it.copy(auto = false) }
//        job?.cancel(); job = null
//    }
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CounterScreen(vm: com.example.as4.CounterVM = viewModel()) {
//    val ui = vm.ui.collectAsState().value
//    var input by remember { mutableStateOf(ui.intervalMs.toString()) }
//
//    Scaffold(topBar = { TopAppBar(title = { Text("Q2 – Counter++") }) }) { pad ->
//        Column(
//            Modifier.fillMaxSize().padding(pad).padding(24.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Text("Count: ${ui.count}", style = MaterialTheme.typography.headlineMedium)
//            Text("Auto mode: ${if (ui.auto) "ON" else "OFF"}")
//
//            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                Button(onClick = vm::inc) { Text("+1") }
//                Button(onClick = vm::dec) { Text("-1") }
//                OutlinedButton(onClick = vm::reset) { Text("Reset") }
//                FilledTonalButton(onClick = vm::toggleAuto) {
//                    Text(if (ui.auto) "Stop Auto" else "Start Auto")
//                }
//            }
//
//            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                Text("Interval (ms):")
//                OutlinedTextField(value = input, onValueChange = { input = it }, singleLine = true)
//                Button(onClick = { input.toLongOrNull()?.let(vm::setInterval) }) { Text("Apply") }
//            }
//        }
//    }
//}
//
////Q3 Temperature Dashboard – Simulated Sensor Data with StateFlow and Coroutines
//
///** Single reading with time and value. */
//data class Reading(val t: Long, val v: Float)
//
///** Screen state with helpers for stats computed from the window. */
//data class TempUi(val list: List<Reading> = emptyList(), val running: Boolean = true) {
//    val current: Float? get() = list.lastOrNull()?.v
//    val avg: Float? get() =
//        if (list.isEmpty()) null else (list.sumOf { it.v.toDouble() } / list.size).toFloat()
//    val min: Float? get() = list.minOfOrNull { it.v }
//    val max: Float? get() = list.maxOfOrNull { it.v }
//}
//
//class TempVM: ViewModel(){
//    val ui=MutableStateFlow(TempUi())
//
//    private var job: Job?= null
//
//    init {
//        start()
//    }
//
//    fun start(){
//        if (job!=null) return
//        job=viewModelScope.launch {
//            while(true){
//                delay(2000)
//                val v= Random.nextDouble(65.0,85.0).toFloat()
//                ui.update { s->
//                    val updated=(s.list+Reading(System.currentTimeMillis(),v)).takeLast(20)
//                    s.copy(list = updated, running = true)
//
//                }
//            }
//        }
//    }
//
//    fun pause(){
////        If job is not null, call its cancel() function.
//        job?.cancel()
//        job=null
//        ui.update{it.copy(running = false)}
//    }
//
//}
//
//
//// UI: pause/resume, stats row, tiny Canvas line chart, and the LazyColumn list.
//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TemperatureScreen(vm: com.example.as4.TempVM = viewModel()) {
//    val ui = vm.ui.collectAsState().value
//    val fmt = remember {
//        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault())
//    }
//
//    Scaffold(topBar = { TopAppBar(title = { Text("Q3 – Temperature Dashboard") }) }) { pad ->
//        Column(
//            Modifier.fillMaxSize().padding(pad).padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            // Pause/Resume streaming
//            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                FilledTonalButton(onClick = { vm.start() }, enabled = !ui.running) { Text("Resume") }
//                OutlinedButton(onClick = { vm.pause() }, enabled = ui.running) { Text("Pause") }
//            }
//
//            // Summary stats
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                Text("Current: ${ui.current?.let { String.format("%.1f", it) } ?: "—"}")
//                Text("Avg: ${ui.avg?.let { String.format("%.1f", it) } ?: "—"}")
//                Text("Min: ${ui.min?.let { String.format("%.1f", it) } ?: "—"}")
//                Text("Max: ${ui.max?.let { String.format("%.1f", it) } ?: "—"}")
//            }
//
//            // Simple line chart
//            Chart(ui.list)
//
//            // Scrolling list of readings
//            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
//                items(ui.list) { r ->
//                    ListItem(
//                        headlineContent = { Text(String.format("%.1f °F", r.v)) },
//                        supportingContent = { Text(fmt.format(Instant.ofEpochMilli(r.t))) }
//                    )
//                }
//            }
//        }
//    }
//}
//
//
///** Draw a very small line chart by connecting points with drawLine on Canvas. */
//@Composable
//private fun Chart(list: List<com.example.as4.Reading>) {
//    if (list.size < 2) return
//    val min = list.minOf { it.v }
//    val max = list.maxOf { it.v }
//    val range = (max - min).coerceAtLeast(1f) // avoid divide-by-zero
//
//    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
//        val stepX = size.width / (list.size - 1)
//        fun y(v: Float) = size.height - ((v - min) / range) * size.height
//        for (i in 0 until list.lastIndex) {
//            drawLine(
//                Color(0xFF03A9F4),
//                Offset(i * stepX, y(list[i].v)),
//                Offset((i + 1) * stepX, y(list[i + 1].v)),
//                strokeWidth = 4f
//            )
//        }
//    }
//}
