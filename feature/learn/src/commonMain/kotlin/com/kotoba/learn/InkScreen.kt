package com.kotoba.learn // commonMain/kotlin/ui/InkScreen.kt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kotoba.learn.model.Point
import com.kotoba.learn.model.Stroke
import com.kotoba.learn.util.createInkRecognizer
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import androidx.compose.ui.graphics.drawscope.Stroke as ComposeStroke

@OptIn(ExperimentalTime::class)
@Composable
fun InkScreen() {
    val scope = rememberCoroutineScope()
    val recognizer = remember { createInkRecognizer() }

    // UI 상태 관리
    var statusText by remember { mutableStateOf("Download model first") }
    var recognitionResult by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var strokes by remember { mutableStateOf<List<Stroke>>(emptyList()) }
    var currentStroke by remember { mutableStateOf<Stroke?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            recognizer.close()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
        )
        Text(
            text = "Result: ${recognitionResult.firstOrNull() ?: "N/A"}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.LightGray)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // 드래그 시작: 새로운 획 생성
                                currentStroke =
                                    Stroke(
                                        points =
                                            listOf(
                                                Point(
                                                    offset.x,
                                                    offset.y,
                                                    Clock.System.now().epochSeconds,
                                                ),
                                            ),
                                    )
                            },
                            onDrag = { change, _ ->
                                // 드래그 중: 현재 획에 점 추가
                                val newPoint =
                                    Point(
                                        change.position.x,
                                        change.position.y,
                                        Clock.System.now().epochSeconds,
                                    )
                                currentStroke =
                                    currentStroke?.copy(
                                        points = currentStroke!!.points + newPoint,
                                    )
                            },
                            onDragEnd = {
                                // 드래그 종료: 완성된 획을 전체 획 리스트에 추가
                                currentStroke?.let { strokes = strokes + it }
                                currentStroke = null
                            },
                        )
                    },
        ) {
            // 모든 획 그리기
            strokes.forEach { stroke ->
                val path = Path()
                stroke.points.forEachIndexed { index, point ->
                    if (index == 0) {
                        path.moveTo(point.x, point.y)
                    } else {
                        path.lineTo(point.x, point.y)
                    }
                }
                drawPath(path, Color.Black, style = ComposeStroke(width = 4.dp.toPx()))
            }
            // 현재 그리고 있는 획 그리기
            currentStroke?.let { stroke ->
                val path = Path()
                stroke.points.forEachIndexed { index, point ->
                    if (index == 0) {
                        path.moveTo(point.x, point.y)
                    } else {
                        path.lineTo(point.x, point.y)
                    }
                }
                drawPath(path, Color.DarkGray, style = ComposeStroke(width = 4.dp.toPx()))
            }
        }

        // 로딩 인디케이터
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        // 제어 버튼
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(onClick = {
                scope.launch {
                    isLoading = true
                    statusText = "Downloading model..."
                    val success = recognizer.isModelDownloaded()
                    statusText =
                        if (success) "Model ready. You can draw." else "Model download failed."
                    isLoading = false
                }
            }) { Text("Download Model") }

            Button(onClick = {
                if (strokes.isNotEmpty()) {
                    scope.launch {
                        isLoading = true
                        recognitionResult = recognizer.recognize(strokes)
                        isLoading = false
                    }
                }
            }) { Text("Recognize") }

            Button(onClick = {
                strokes = emptyList()
                recognitionResult = emptyList()
            }) { Text("Clear") }
        }
    }
}
