package hu.bme.aut.android.polygame.logic

import hu.bme.aut.android.polygame.model.Line
import hu.bme.aut.android.polygame.model.Point
import hu.bme.aut.android.polygame.model.Polygon

class GameLogic {

    class InnerPolygon(lines: MutableList<Line>) {
        var lines: MutableList<Line> = lines
    }

    companion object {
        var polyStartPoint = Point(0F,0F, 0F, Polygon.Empty)
        var polyEndPoint = Point(0F,0F, 0F, Polygon.Empty)
        var lineEndPoint = Point(0F,0F, 0F, Polygon.Empty)
        var lineIntersectedPoint = Point(0F,0F, 0F, Polygon.Empty)
    }

    var playerPoints: MutableList<Int> = mutableListOf()
    var lines: MutableList<Line> = mutableListOf()
    var foundPolygons: MutableList<InnerPolygon> = mutableListOf()

    fun evenBetter(line: Line, intersectPoint: Point, currentEndPoint: Point){
        lines.add(line)
        lineIntersectedPoint = intersectPoint
        lineEndPoint = currentEndPoint
        line.visited = true

        if(lineIntersectedPoint != polyStartPoint && lineEndPoint == polyEndPoint){
            val innerPolygon = InnerPolygon(lines)
            foundPolygons.add(innerPolygon)
            return
        }

        for(l in Polygon.currentLines) {
            if(l!=line && !l.visited) {
                if(linesIntersect(l,line)){
                    if(l.pointA == lineEndPoint){
                        evenBetter(l, l.pointA, l.pointB)
                    }
                    else if(l.pointB == lineEndPoint){
                        evenBetter(l, l.pointB, l.pointA)
                    }
                }
            }
        }
        return
    }

    fun setup(line: Line){
        polyStartPoint = line.pointB
        polyEndPoint = line.pointA
    }

    fun paintInnerPolygons() {
        for (p in foundPolygons)
            for (l in p.lines) {
                l.paint = Polygon.nextPlayer
                l.pointA.playerTouched = Polygon.nextPlayer
                l.pointB.playerTouched = Polygon.nextPlayer
            }
    }

    fun undoVisited() {
        for (l in Polygon.currentLines)
            l.visited = false
    }

    private fun linesIntersect(lineA: Line, lineB: Line): Boolean {
        if (lineA.pointA == lineB.pointA || lineA.pointA == lineB.pointB || lineA.pointB == lineB.pointA || lineA.pointB == lineB.pointB)
            return true
        return false
    }
}