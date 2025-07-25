package com.kotoba.learn.model

data class Point(
    val x: Float,
    val y: Float,
    val timestamp: Long,
)

data class Stroke(
    val points: List<Point>,
)
