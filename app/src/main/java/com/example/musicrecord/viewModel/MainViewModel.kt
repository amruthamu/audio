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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _visualizerAmplitudes = MutableLiveData<List<Int>>()
    val visualizerAmplitudes: LiveData<List<Int>> get() = _visualizerAmplitudes

    private val _playbackProgress = MutableLiveData<Float>()
    val playbackProgress: LiveData<Float> get() = _playbackProgress

    private val _audioDuration = MutableLiveData<Float>()
    val audioDuration: LiveData<Float> get() = _audioDuration

    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var mVisualizer: Visualizer? = null

    private val audioRepository = AudioRepository(application)
    private var recordingFileName = "AudioRecording.3gp"
    var isRecordingComplete = false

    fun startRecording() {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = audioRepository.getOutputFilePath(recordingFileName)
            mRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(filePath)
                prepare()
                start()
            }
            _status.postValue("Recording Started")
            isRecordingComplete = false
            startRecordingVisualizer()
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
                    isRecordingComplete = true
                    _status.postValue("Recording Stopped")
                }
            } catch (e: Exception) {
                _status.postValue("Failed to stop recording")
                e.printStackTrace()
            }
            stopVisualizer()
        }
    }

    fun saveRecording() {
        _status.postValue("Recording saved at: ${audioRepository.getOutputFilePath(recordingFileName)}")
    }

    fun playAudio() {
        val filePath = audioRepository.getOutputFilePath(recordingFileName)
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
                        _audioDuration.postValue(duration.toFloat() / 1000f)
                        startPlaybackVisualizer(audioSessionId)

                        setOnCompletionListener {
                            _playbackProgress.postValue(1.0f)
                            stopVisualizer()
                            _status.postValue("Playback Finished")
                            release()
                            mPlayer = null
                        }
                    }
                } catch (e: Exception) {
                    _status.postValue("Failed to play audio")
                    e.printStackTrace()
                }
            }
        } else {
            _status.postValue("No recording found!")
        }
    }

    fun pausePlaying() {
        mPlayer?.pause()
        _status.postValue("Playback Paused")
    }

    fun stopPlaying() {
        mPlayer?.apply {
            stop()
            release()
            mPlayer = null
            _status.postValue("Playback Stopped")
        }
        stopVisualizer()
    }

    private fun startRecordingVisualizer() {
        viewModelScope.launch(Dispatchers.Default) {
            while (mRecorder != null) {
                val amplitude = mRecorder?.maxAmplitude ?: 0
                val scaledAmplitude = if (amplitude > 0) amplitude / 200 else 0
                _visualizerAmplitudes.postValue(List((getApplication<Application>().resources.displayMetrics.widthPixels / (10f + 5f)).toInt()) { scaledAmplitude })
                delay(100)
            }
        }
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
                        _visualizerAmplitudes.postValue(amplitudes.take((getApplication<Application>().resources.displayMetrics.widthPixels / (10f + 5f)).toInt()))

                        val progress = mPlayer?.let { player ->
                            if (player.duration > 0) player.currentPosition.toFloat() / player.duration else 0f
                        } ?: 0f
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

