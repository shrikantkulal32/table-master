package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val scope: CoroutineScope) {

    var isSfxEnabled: Boolean = true
    var isMusicEnabled: Boolean = false
        set(value) {
            field = value
            if (value) {
                startMusic()
            } else {
                stopMusic()
            }
        }

    private var musicJob: Job? = null
    private var musicTrack: AudioTrack? = null

    // Play a delightful ascending chime for correct answer
    fun playCorrectSound() {
        if (!isSfxEnabled) return
        scope.launch(Dispatchers.Default) {
            playTones(
                frequencies = listOf(659.25f, 880.0f, 1046.50f), // E5, A5, C6 arpeggio
                durationsMs = listOf(80, 80, 160)
            )
        }
    }

    // Play a gentle low tone for wrong answer
    fun playWrongSound() {
        if (!isSfxEnabled) return
        scope.launch(Dispatchers.Default) {
            playTones(
                frequencies = listOf(220.0f, 185.0f),
                durationsMs = listOf(110, 160)
            )
        }
    }

    // Play a crisp click tone for taps
    fun playClickSound() {
        if (!isSfxEnabled) return
        scope.launch(Dispatchers.Default) {
            playTones(
                frequencies = listOf(800.0f),
                durationsMs = listOf(25)
            )
        }
    }

    private fun playTones(frequencies: List<Float>, durationsMs: List<Int>) {
        try {
            val sampleRate = 22050
            val totalDurationMs = durationsMs.sum()
            val totalSamples = (sampleRate * totalDurationMs / 1000)
            val buffer = ShortArray(totalSamples)

            var sampleOffset = 0
            for (i in frequencies.indices) {
                val freq = frequencies[i]
                val durMs = durationsMs[i]
                val numSamples = (sampleRate * durMs / 1000)

                for (s in 0 until numSamples) {
                    val time = s.toDouble() / sampleRate
                    // Smooth envelope attack and decay to prevent popping
                    val attack = (s.toFloat() / (sampleRate * 0.008f)).coerceIn(0f, 1f)
                    val decay = ((numSamples - s).toFloat() / (numSamples * 0.7f)).coerceIn(0f, 1f)
                    val envelope = attack * decay

                    val wave = sin(2.0 * PI * freq * time)
                    val sample = (wave * 26000 * envelope).toInt().toShort()

                    if (sampleOffset + s < buffer.size) {
                        buffer[sampleOffset + s] = sample
                    }
                }
                sampleOffset += numSamples
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            // Release after completion
            Thread.sleep(totalDurationMs.toLong() + 50)
            track.stop()
            track.release()
        } catch (_: Exception) {
            // Audio error non-fatal fallback
        }
    }

    private fun startMusic() {
        stopMusic()
        musicJob = scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 22050
                // Ambient calming pentatonic melody notes (C, E, G, A, B, C)
                val melodyFreqs = listOf(
                    261.63f, 329.63f, 392.00f, 440.00f,
                    392.00f, 329.63f, 293.66f, 261.63f,
                    220.00f, 261.63f, 329.63f, 392.00f,
                    329.63f, 261.63f, 220.00f, 196.00f
                )
                val noteDurationMs = 380

                val totalSamples = melodyFreqs.size * (sampleRate * noteDurationMs / 1000)
                val buffer = ShortArray(totalSamples)

                var offset = 0
                for (freq in melodyFreqs) {
                    val noteSamples = sampleRate * noteDurationMs / 1000
                    for (s in 0 until noteSamples) {
                        val time = s.toDouble() / sampleRate
                        val attack = (s.toFloat() / (sampleRate * 0.04f)).coerceIn(0f, 1f)
                        val decay = ((noteSamples - s).toFloat() / noteSamples.toFloat()).coerceIn(0f, 1f)
                        val envelope = attack * decay

                        // Soft warm synth note with fundamental and gentle 2nd harmonic
                        val wave = (sin(2.0 * PI * freq * time) * 0.75 +
                                sin(2.0 * PI * freq * 2 * time) * 0.25)
                        val sample = (wave * 9000 * envelope).toInt().toShort()

                        if (offset + s < buffer.size) {
                            buffer[offset + s] = sample
                        }
                    }
                    offset += noteSamples
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                musicTrack = track
                track.write(buffer, 0, buffer.size)
                track.setLoopPoints(0, totalSamples, -1) // Infinite loop
                track.play()

                while (isActive && isMusicEnabled) {
                    Thread.sleep(1000)
                }
            } catch (_: Exception) {
                // Ignore audio interrupt
            } finally {
                musicTrack?.let {
                    try {
                        it.stop()
                        it.release()
                    } catch (_: Exception) {}
                }
                musicTrack = null
            }
        }
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
        musicTrack?.let {
            try {
                it.stop()
                it.release()
            } catch (_: Exception) {}
        }
        musicTrack = null
    }

    fun release() {
        stopMusic()
    }
}
