package hu.aut.bme.android.polygame.model

class Line(startP: Point, stopP: Point, color: PlayerColor) {
    val pointB: Point = startP
    val pointA: Point = stopP
    var paint: PlayerColor = color
    var visited: Boolean = false
}