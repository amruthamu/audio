package com.example.musicrecord.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicrecord.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicRecordApp()
        }
    }
}

@Composable
fun MusicRecordApp(viewModel: MainViewModel = viewModel()) {
    val status by viewModel.status.observeAsState("Status")
    val visualizerAmplitudes by viewModel.visualizerAmplitudes.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))


        AndroidView(
            factory = { context ->
                BarVisualizer(context).apply {
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            update = { view ->
                view.updateAmplitudes(visualizerAmplitudes)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { viewModel.startRecording() }) {
                Text(text = "Start Recording")
            }
            Button(onClick = { viewModel.stopRecording() }) {
                Text(text = "Stop Recording")
            }
            Button(onClick = { viewModel.playAudio() }) {
                Text(text = "Play Audio")
            }
            Button(onClick = { viewModel.stopPlaying() }) {
                Text(text = "Stop Audio")
            }
        }
    }
}

