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

    var fieldPoints: MutableList<Point> = mutableListOf()

    fun resetModel() {
        fieldPoints.clear()
    }

    fun loadGameField(x: Int) {
        when(x){
            0 -> easyField()
            1 -> mediumField()
            2 -> hardField()
            3 -> veryHardField()
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
        for (p in fieldPoints)
            if(p.playerTouchedPoint(x, y, nextPlayer))
                return p
        return Point(0F,0F,0F, Empty)
    }

    private fun easyField(){
        fieldPoints.add(Point(240F, 240F, 20F, Empty))
        fieldPoints.add(Point(240F, 480F, 20F, Empty))
        fieldPoints.add(Point(480F, 240F, 20F, Empty))
        fieldPoints.add(Point(480F, 480F, 20F, Empty))
    }

    private fun mediumField() {
        fieldPoints.add(Point(180F, 360F, 20F, Empty))
        fieldPoints.add(Point(275F, 180F, 20F, Empty))
        fieldPoints.add(Point(275F, 540F, 20F, Empty))
        fieldPoints.add(Point(445F, 180F, 20F, Empty))
        fieldPoints.add(Point(445F, 540F, 20F, Empty))
        fieldPoints.add(Point(540F, 360F, 20F, Empty))
    }

    private fun hardField(){
        fieldPoints.add(Point(45F, 240F, 20F, Empty))
        fieldPoints.add(Point(45F, 480F, 20F, Empty))
        fieldPoints.add(Point(240F, 45F, 20F, Empty))
        fieldPoints.add(Point(240F, 675F, 20F, Empty))
        fieldPoints.add(Point(480F, 45F, 20F, Empty))
        fieldPoints.add(Point(480F, 675F, 20F, Empty))
        fieldPoints.add(Point(675F, 240F, 20F, Empty))
        fieldPoints.add(Point(675F, 480F, 20F, Empty))
    }

    private fun veryHardField(){
        //external polygon
        fieldPoints.add(Point(45F, 240F, 20F, Empty))
        fieldPoints.add(Point(45F, 480F, 20F, Empty))
        fieldPoints.add(Point(240F, 45F, 20F, Empty))
        fieldPoints.add(Point(240F, 675F, 20F, Empty))
        fieldPoints.add(Point(480F, 45F, 20F, Empty))
        fieldPoints.add(Point(480F, 675F, 20F, Empty))
        fieldPoints.add(Point(675F, 240F, 20F, Empty))
        fieldPoints.add(Point(675F, 480F, 20F, Empty))

        //internal polygon
        fieldPoints.add(Point(290F, 290F, 20F, Empty))
        fieldPoints.add(Point(290F, 430F, 20F, Empty))
        fieldPoints.add(Point(430F, 290F, 20F, Empty))
        fieldPoints.add(Point(430F, 430F, 20F, Empty))
    }

}