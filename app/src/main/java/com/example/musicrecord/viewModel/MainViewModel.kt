package com.example.musicrecord.viewModel

import android.app.Application
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.audiofx.Visualizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicrecord.model.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _visualizerAmplitudes = MutableLiveData<List<Int>>()
    val visualizerAmplitudes: LiveData<List<Int>> get() = _visualizerAmplitudes

    private val _playbackProgress = MutableLiveData<Float>()

    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var mVisualizer: Visualizer? = null

    private val audioRepository = AudioRepository(application)

    fun startRecording() {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = audioRepository.getOutputFilePath()

            mRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(filePath)
                prepare()
                start()
            }
            _status.postValue("Recording Started")

            startRecordingVisualizer()
        }
    }

    private fun startRecordingVisualizer() {
        viewModelScope.launch(Dispatchers.Default) {
            while (mRecorder != null) {
                val amplitude = mRecorder?.maxAmplitude ?: 0
                val scaledAmplitude = if (amplitude > 0) amplitude / 200 else 0
                _visualizerAmplitudes.postValue(List((getApplication<Application>().resources.displayMetrics.widthPixels / (10f + 5f)).toInt()) { scaledAmplitude })
                Thread.sleep(100)
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mRecorder?.apply {
                    stop()
                    reset()
                    release()
                    mRecorder = null
                    _status.postValue("Recording Stopped")
                }
            } catch (e: IllegalStateException) {
                _status.postValue("Failed to stop recording: ${e.message}")
                e.printStackTrace()
            } catch (e: RuntimeException) {
                _status.postValue("Error occurred while stopping recording: ${e.message}")
                e.printStackTrace()
            }
            stopVisualizer()
        }
    }

    fun playAudio() {
        val filePath = audioRepository.getOutputFilePath()
        if (File(filePath).exists()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (mPlayer == null) {
                        mPlayer = MediaPlayer()
                    } else {
                        mPlayer?.reset()
                    }

                    mPlayer?.apply {
                        setDataSource(filePath)
                        prepare()
                        start()
                        _status.postValue("Playback Started")

                        val audioSessionId = audioSessionId
                        startPlaybackVisualizer(audioSessionId)
                    }

                    mPlayer?.setOnCompletionListener {
                        stopVisualizer()
                        _status.postValue("Playback Finished")
                        mPlayer?.release()
                        mPlayer = null
                    }
                } catch (e: Exception) {
                    _status.postValue("Failed to play audio")
                    mPlayer?.release()
                    mPlayer = null
                    e.printStackTrace()
                }
            }
        } else {
            _status.postValue("No recording found!")
        }
    }

    fun stopPlaying() {
        _status.postValue("Playback Stopped")
        mPlayer?.apply {
            stop()
            release()
            mPlayer = null
        }
        stopVisualizer()
    }

    private fun startPlaybackVisualizer(audioSessionId: Int) {
        mVisualizer = Visualizer(audioSessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]
            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer?,
                    waveform: ByteArray?,
                    samplingRate: Int
                ) {
                    waveform?.let {
                        val amplitudes = it.map { byte -> (byte.toInt() + 128) }

                        val progress = mPlayer?.let { player ->
                            if (player.duration > 0) player.currentPosition.toFloat() / player.duration else 0f
                        } ?: 0f

                        _visualizerAmplitudes.postValue(amplitudes.take((getApplication<Application>().resources.displayMetrics.widthPixels / (10f + 5f)).toInt()))
                        _playbackProgress.postValue(progress)
                    }
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false)
            enabled = true
        }
    }

    private fun stopVisualizer() {
        mVisualizer?.release()
        mVisualizer = null
    }
}
