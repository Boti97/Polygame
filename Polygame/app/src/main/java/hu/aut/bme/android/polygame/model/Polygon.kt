package hu.aut.bme.android.polygame.model

import android.graphics.Color
import android.graphics.Paint

object Polygon {

    val Empty: Paint = Paint() 
    val PlayerOne: Paint = Paint() 
    val PlayerTwo: Paint = Paint() 
    var currentPlayer: Paint = PlayerOne
    var fieldPoints: MutableList<Point> = mutableListOf()
    var currentLines: MutableList<Line> = mutableListOf()

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

    fun resetModel() {
        for(p in fieldPoints)
            p.playerTouched = Empty
        currentLines.clear()
        currentPlayer = PlayerOne
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
        if (currentPlayer == PlayerOne) {
            currentPlayer = PlayerTwo
        } else {
            currentPlayer = PlayerOne
        }
    }

    fun pointTouched(x: Float, y: Float): Point{
        for (p in fieldPoints)
            if(p.playerTouchedPoint(x, y, currentPlayer))
                return p
        return Point(0F,0F,0F, Empty)
    }

    fun lineAlreadyExist(line: Line): Boolean{
        for(l in currentLines){
            if(l.pointA == line.pointA && l.pointB == line.pointB || l.pointB == line.pointA && l.pointA == line.pointB)
                return true
        }
        return false
    }

    fun linesContainsPoint(point: Point):Paint{
        for(l in currentLines){
            if(l.pointA == point || l.pointB == point)
                return l.paint
        }
        return Polygon.Empty
    }

    fun allLinesTaken():Boolean{
        for (p1 in fieldPoints){
            for (p2 in fieldPoints){
                if(p1 != p2 && !lineAlreadyExist(Line(p1, p2, Polygon.Empty)))
                    return false
            }
        }
        return true
    }

    private fun easyField(){
        fieldPoints.clear()
        fieldPoints.add(Point(240F, 240F, 20F, Empty))
        fieldPoints.add(Point(240F, 480F, 20F, Empty))
        fieldPoints.add(Point(480F, 240F, 20F, Empty))
        fieldPoints.add(Point(480F, 480F, 20F, Empty))
    }

    private fun mediumField() {
        fieldPoints.clear()
        fieldPoints.add(Point(180F, 360F, 20F, Empty))
        fieldPoints.add(Point(275F, 180F, 20F, Empty))
        fieldPoints.add(Point(275F, 540F, 20F, Empty))
        fieldPoints.add(Point(445F, 180F, 20F, Empty))
        fieldPoints.add(Point(445F, 540F, 20F, Empty))
        fieldPoints.add(Point(540F, 360F, 20F, Empty))
    }

    private fun hardField(){
        fieldPoints.clear()
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
        fieldPoints.clear()
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