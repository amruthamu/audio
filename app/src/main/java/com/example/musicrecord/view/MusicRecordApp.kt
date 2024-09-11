package com.example.musicrecord.view


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicrecord.viewModel.MainViewModel


@Composable
fun MusicRecordApp(viewModel: MainViewModel = viewModel()) {
    val status by viewModel.status.observeAsState("Status")
    val visualizerAmplitudes by viewModel.visualizerAmplitudes.observeAsState(emptyList())
    val playbackProgress by viewModel.playbackProgress.observeAsState(0f)
    val audioDuration by viewModel.audioDuration.observeAsState(0f)

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(playbackProgress)
                    .background(Color.Green)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Playback Progress: ${(playbackProgress * 100).toInt()}% / Duration: ${audioDuration.toInt()}s",
            style = MaterialTheme.typography.bodyMedium
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

            if (viewModel.isRecordingComplete) {
                Button(onClick = { viewModel.saveRecording() }) {
                    Text(text = "Save Recording")
                }
            }

            Button(onClick = { viewModel.playAudio() }) {
                Text(text = if (playbackProgress >= 1.0f) "Replay Audio" else "Play Audio")
            }

            if (playbackProgress < 1.0f && playbackProgress > 0f) {
                Button(onClick = { viewModel.pausePlaying() }) {
                    Text(text = "Pause Audio")
                }
            }

            Button(onClick = { viewModel.stopPlaying() }) {
                Text(text = "Stop Audio")
            }
        }
    }
}
