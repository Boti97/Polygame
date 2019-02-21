package hu.bme.aut.android.polygame.model

import android.graphics.Color
import android.graphics.Paint

object Polygon {

    val Empty: Paint = Paint() /*Nem válaszotta ki még senki*/
    val PlayerOne: Paint = Paint() /*Az első játékos választotta ki utoljára*/
    val PlayerTwo: Paint = Paint() /*A második játékos választotta ki utoljára*/
    var nextPlayer: Paint = PlayerOne

    init {
        Empty.color = Color.BLACK
        Empty.style = Paint.Style.FILL

        PlayerOne.color = Color.BLUE
        PlayerOne.style = Paint.Style.FILL
        PlayerOne.strokeWidth = 10F

        PlayerTwo.color = Color.RED
        PlayerTwo.style = Paint.Style.FILL
        PlayerTwo.strokeWidth = 10F
    }

    var points: List<Point> = listOf(
        Point(240F, 240F, 20F, Empty),
        Point(240F, 480F, 20F, Empty),
        Point(480F, 240F, 20F, Empty),
        Point(480F, 480F, 20F, Empty)
    )

    fun resetModel() {
        for (p in points) {
            p.playerTouched = Empty
        }
    }

    fun changeNextPlayer() {
        if (nextPlayer == PlayerOne) {
            nextPlayer = PlayerTwo
        } else {
            nextPlayer = PlayerOne
        }
    }

    fun pointTouched(x: Float, y: Float): Point{
        for (p in points)
            if(p.playerTouchedPoint(x, y, nextPlayer))
                return p
        return Point(0F,0F,0F, Empty)
    }

}