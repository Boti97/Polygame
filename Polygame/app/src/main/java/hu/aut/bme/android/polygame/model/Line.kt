package hu.aut.bme.android.polygame.model

import android.graphics.Paint

class Line(startP: Point, stopP: Point, color: Paint) {
    val pointB: Point = startP
    val pointA: Point = stopP
    var paint: Paint = color
    var visited: Boolean = false
}