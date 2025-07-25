package com.kotoba.learn.util

import cocoapods.GoogleMLKitCommon.MLKDownloadConditions
import cocoapods.GoogleMLKitCommon.MLKModelManager
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKDigitalInkRecognitionModel
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKDigitalInkRecognitionModelIdentifier
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKDigitalInkRecognizer
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKDigitalInkRecognizerOptions
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKInk
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKStroke
import cocoapods.GoogleMLKitDigitalInkRecognition.MLKStrokePoint
import com.kotoba.learn.model.Stroke
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class InkRecognizer {
    private val model: MLKDigitalInkRecognitionModel
    private val recognizer: MLKDigitalInkRecognizer

    init {
        // 1. 모델 식별자 및 모델 생성
        val modelIdentifier = MLKDigitalInkRecognitionModelIdentifier.modelIdentifierForLanguageTag(languageTag)
        this.model = MLKDigitalInkRecognitionModel(modelIdentifier)

        // 2. 인식기 옵션 및 인식기 생성
        val options = MLKDigitalInkRecognizerOptions(model)
        this.recognizer = MLKDigitalInkRecognizer.digitalInkRecognizerWithOptions(options)
    }

    actual suspend fun isModelDownloaded(): Boolean {
        val modelManager = MLKModelManager.modelManager()

        // isModelDownloaded는 동기적으로 결과를 반환하지만, download는 비동기입니다.
        if (modelManager.isModelDownloaded(model)) {
            return true
        }

        // 모델이 없다면 다운로드 시도 (suspendCoroutine으로 비동기 콜백을 래핑)
        return suspendCoroutine { continuation ->
            val conditions = MLKDownloadConditions(allowsCellularAccess = true, allowsBackgroundDownloading = true)
            modelManager.downloadModel(model, conditions = conditions) { nsError ->
                if (nsError == null) {
                    println("Model downloaded successfully.")
                    continuation.resume(true)
                } else {
                    println("Error downloading model: ${nsError.localizedDescription}")
                    continuation.resume(false)
                }
            }
        }
    }

    actual suspend fun recognize(strokes: List<Stroke>): List<String> {
        // 1. 공통 데이터 모델(List<Stroke>)을 ML Kit의 MLKInk 모델로 변환
        val ink = strokes.toMLKInk()

        // 2. 비동기 인식 작업을 suspendCoroutine으로 래핑
        return suspendCoroutine { continuation ->
            recognizer.recognizeInk(ink) { result, nsError ->
                if (nsError != null) {
                    println("Error recognizing ink: ${nsError.localizedDescription}")
                    continuation.resume(emptyList())
                    return@recognizeInk
                }

                if (result != null) {
                    val recognizedStrings = result.candidates.mapNotNull { it.text }
                    continuation.resume(recognizedStrings)
                } else {
                    continuation.resume(emptyList())
                }
            }
        }
    }

    actual fun close() {
        // iOS에서는 별도의 close()가 필요 없습니다.
    }
}

/**
 * [공통 모델] List<Stroke>를 [iOS] MLKInk로 변환하는 확장 함수
 */
private fun List<Stroke>.toMLKInk(): MLKInk {
    val mlkStrokes =
        this.map { stroke ->
            val mlkPoints =
                stroke.points.map { point ->
                    MLKStrokePoint(
                        x = point.x,
                        y = point.y,
                        // ML Kit은 타임스탬프를 초 단위로 요구합니다.
                        t = (point.timestamp / 1000.0).toFloat(),
                    )
                }
            MLKStroke(points = mlkPoints)
        }
    return MLKInk(strokes = mlkStrokes)
}

// 팩토리 함수 실제 구현
actual fun createInkRecognizer(): InkRecognizer = InkRecognizer()
