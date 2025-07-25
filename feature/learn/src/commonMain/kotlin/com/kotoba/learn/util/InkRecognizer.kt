package com.kotoba.learn.util

import com.kotoba.learn.model.Stroke

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class InkRecognizer {
    /**
     * 지정된 언어 모델 다운여부 확인
     */
    suspend fun isModelDownloaded(): Boolean

    /**
     * 그려진 획들을 인식하여 결과 리스트 반환
     * @param strokes
     * @return 인식된 List<String>
     */
    suspend fun recognize(strokes: List<Stroke>): List<String>

    fun close()
}

expect fun createInkRecognizer(): InkRecognizer
