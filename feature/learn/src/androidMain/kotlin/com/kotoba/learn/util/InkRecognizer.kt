package com.kotoba.learn.util

import android.content.Context
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionResult
import com.kotoba.di.ContextProvider
import com.kotoba.learn.model.Stroke
import kotlinx.coroutines.tasks.await

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class InkRecognizer(
    private val context: Context,
) {
    private var recognizer: DigitalInkRecognizer? = null
    private val model: DigitalInkRecognitionModel

    init {
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.JA
        model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
    }

    private fun ensureRecognizer(): DigitalInkRecognizer {
        if (recognizer == null) {
            val options = DigitalInkRecognizerOptions.builder(model).build()
            recognizer = DigitalInkRecognition.getClient(options)
        }
        return recognizer!!
    }

    actual suspend fun isModelDownloaded(): Boolean {
        val remoteModelManager = RemoteModelManager.getInstance()
        return try {
            if (remoteModelManager.isModelDownloaded(model).await()) {
                return true
            }
            remoteModelManager.download(model, DownloadConditions.Builder().build()).await()
            true
        } catch (e: Exception) {
            println("Error preparing model: ${e.localizedMessage}")
            false
        }
    }

    actual suspend fun recognize(strokes: List<Stroke>): List<String> {
        val inkBuilder = Ink.builder()
        strokes.forEach { stroke ->
            val strokeBuilder = Ink.Stroke.builder()
            stroke.points.forEach { point ->
                strokeBuilder.addPoint(Ink.Point.create(point.x, point.y, point.timestamp))
            }
            inkBuilder.addStroke(strokeBuilder.build())
        }
        val ink = inkBuilder.build()

        return try {
            val result: RecognitionResult = ensureRecognizer().recognize(ink).await()
            result.candidates.map { it.text }
        } catch (e: Exception) {
            println("Error recognizing ink: ${e.localizedMessage}")
            emptyList()
        }
    }

    actual fun close() {
        recognizer?.close()
    }
}

actual fun createInkRecognizer(): InkRecognizer = InkRecognizer(ContextProvider.context)
