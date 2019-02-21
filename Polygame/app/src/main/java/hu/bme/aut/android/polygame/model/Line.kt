package hu.bme.aut.android.polygame.model

import android.graphics.Paint

class Line(startP: Point, stopP: Point, color: Paint) {
    val startPoint: Point = startP
    val stopPoint: Point = stopP
    val paint: Paint = color
}