1)MainViewModel.kt
Purpose: This ViewModel class manages the audio recording and playback logic, including the visualizations.

Key Functions: startRecording(): Initializes and starts a MediaRecorder to record audio. It also starts a coroutine to periodically update the visualizer amplitudes. stopRecording(): Stops and releases the MediaRecorder, and stops the visualizer. playAudio(): Prepares and starts a MediaPlayer to play the recorded audio file. It also initializes the Visualizer for playback and updates the visualizer's amplitude and progress. stopPlaying(): Stops and releases the MediaPlayer, and stops the visualizer. startPlaybackVisualizer(): Configures the Visualizer to capture audio data for visualization during playback. stopVisualizer(): Releases the Visualizer.

LiveData Properties:

status: LiveData for status messages related to recording and playback. visualizerAmplitudes: LiveData for the list of amplitude values used to update the visualizer. playbackProgress: LiveData for the playback progress as a fraction of the total duration.

2)MainActivity.kt
Purpose: This is the main activity of the application, setting up the user interface using Jetpack Compose.
Key Functions:

onCreate(): Sets the content view using a Composable function MusicRecordApp. MusicRecordApp(): A Composable function that defines the user interface, including status text, a visualizer, and control buttons for recording and playback. Components:

AndroidView: Integrates the BarVisualizer custom view with Jetpack Compose, allowing it to display amplitude data. Column: Layout container for arranging UI elements vertically. Button: Buttons for starting/stopping recording and playback. 
3) BarVisualizer.kt

Purpose: Custom view for visualizing audio data as vertical bars.

Key Functions:

onDraw(): Draws bars representing audio amplitude values. The bars' heights are scaled according to the amplitude. updateAmplitudes(): Updates the visualizer with new amplitude data and triggers a redraw. Components:

barPaint: Paint object for drawing bars with color. backgroundPaint: Paint object for drawing the background of the visualizer.
4) AudioRepository.kt

Purpose: Provides file path handling for storing and retrieving audio recordings.

Key Functions:

getOutputFilePath(): Returns the file path for saving audio recordings in the app’s music directory. Components:

context: Provides access to application context for file path operations.
