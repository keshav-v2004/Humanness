package com.example.josh.android.recorder

import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val outputFile: File) {

    private var recorder: MediaRecorder? = null
    private var started = false
    private var startTime = 0L

    fun startRecording() {
        if (started) return
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
                started = true
                startTime = System.currentTimeMillis()
            } catch (e: Exception) {
                release()
                recorder = null
                started = false
            }
        }
    }

    fun stopRecording() {
        val r = recorder ?: return
        if (!started) return
        // Ensure at least minimal recording time to avoid MediaRecorder native crash
        val elapsed = System.currentTimeMillis() - startTime
        try {
            if (elapsed < 300) Thread.sleep(300 - elapsed)
            r.stop()
        } catch (_: Exception) { /* swallow stop failed */ }
        try { r.reset() } catch (_: Exception) {}
        try { r.release() } catch (_: Exception) {}
        recorder = null
        started = false
    }

    fun isStarted(): Boolean = started
}
