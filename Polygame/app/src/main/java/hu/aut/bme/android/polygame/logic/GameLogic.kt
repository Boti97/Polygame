package hu.aut.bme.android.polygame.logic

import android.content.Context
import hu.aut.bme.android.polygame.model.Line
import hu.aut.bme.android.polygame.model.Point
import hu.aut.bme.android.polygame.model.Polygon
import hu.aut.bme.android.polygame.view.ScoreBoard


class GameLogic(context: Context) {

    private class InternalPolygon(lines: MutableList<Line>) {
        var lines: MutableList<Line> = lines
    }

    private lateinit var polyStartPoint:Point
    private lateinit var polyEndPoint:Point

    private var lines: MutableList<Line> = mutableListOf()
    private var allFoundPolygons: MutableList<InternalPolygon> = mutableListOf()
    private var foundPolygons: MutableList<InternalPolygon> = mutableListOf()
    private var sortedPolygons: MutableList<InternalPolygon> = mutableListOf()
    private var cleanedPolygons: MutableList<InternalPolygon> = mutableListOf()

    private fun findInternalPolygons(line: Line, lineEndPoint: Point) {
        lines.add(line)
        line.visited = true

        if (lineEndPoint == polyEndPoint && lines.size > 2) {
            val foundlines = lines.toMutableList()
            val innerPolygon = InternalPolygon(foundlines)
            lines.remove(line)
            foundPolygons.add(innerPolygon)
            line.visited = false
            return
        }

        for (l in Polygon.currentLines) {
            if (l != line && !l.visited) {
                if (linesConnect(l, line)) {
                    if (l.pointA == lineEndPoint)
                        findInternalPolygons(l, l.pointB)
                    else if (l.pointB == lineEndPoint)
                        findInternalPolygons(l, l.pointA)
                }
            }
        }
        lines.remove(line)
        line.visited = false
        return
    }

    fun setupAndFindPolygons(line: Line) {
        polyStartPoint = line.pointB
        polyEndPoint = line.pointA
        findInternalPolygons(line, polyStartPoint)
    }

    fun paintInnerPolygons() {
        deleteInappropriatePolygons()
        for (p in cleanedPolygons) {
            for (l in p.lines) {
                l.paint = Polygon.currentPlayer
                l.pointA.playerTouched = Polygon.currentPlayer
                l.pointB.playerTouched = Polygon.currentPlayer
            }
        }
    }

    fun setScore(){
        for(p in cleanedPolygons){
            if(Polygon.currentPlayer == Polygon.PlayerOne){
                ScoreBoard.instance.setPlayerOneScore(p.lines.size)
            }
            else{
                ScoreBoard.instance.setPlayerTwoScore(p.lines.size)
            }
        }
    }

    private fun undoVisited() {
        for (l in Polygon.currentLines)
            l.visited = false
    }

    private fun linesConnect(lineA: Line, lineB: Line): Boolean {
        if (lineA.pointA == lineB.pointA || lineA.pointA == lineB.pointB || lineA.pointB == lineB.pointA || lineA.pointB == lineB.pointB)
            return true
        return false
    }

    private fun deleteInappropriatePolygons() {
        for (fp in foundPolygons)
            if (!polygonIsAlreadyFound(fp))
                sortedPolygons.add(fp)
        for (sp in sortedPolygons)
            if(!selfIntersectingPolygon(sp))
                cleanedPolygons.add(sp)

    }

    private fun selfIntersectingPolygon(intPolygon: InternalPolygon): Boolean {
        for (examinedLine in intPolygon.lines){
            for (comparedLine in intPolygon.lines)
                if(examinedLine != comparedLine && !linesConnect(examinedLine, comparedLine) && linesIntersect(examinedLine, comparedLine))
                    return true
        }
        return false
    }

    private fun linesIntersect(lineA: Line, lineB: Line): Boolean {
        val aLineSlope = findLineSlope(lineA)
        val bLineSlope = findLineSlope(lineB)

        val aLineYIntercept = findYIntercept(lineA, aLineSlope)
        val bLineYIntercept = findYIntercept(lineB, bLineSlope)

        val yInterceptDiff: Float
        val slopeDiff: Float

        if  (bLineYIntercept > aLineYIntercept)
            yInterceptDiff = bLineYIntercept - aLineYIntercept
        else
            yInterceptDiff = aLineYIntercept - bLineYIntercept

        if (aLineSlope > bLineSlope)
            slopeDiff = aLineSlope - bLineSlope
        else
            slopeDiff = bLineSlope - aLineSlope


        val x: Float
        if(slopeDiff == 0F) {
            x = 0F
        }
        else {
            x = yInterceptDiff / slopeDiff
        }
        val x1 = lineA.pointA.koordX
        val x2 = lineA.pointB.koordX
        if  (x1 > x2){
            if(x2 > x || x > x1)
                return false
        }
        else{
            if(x1 > x || x > x2)
                return false
        }

        val y = aLineSlope * x + aLineYIntercept
        val y1 = lineA.pointA.koordY
        val y2 = lineA.pointB.koordY
        if (y1 > y2){
            if(y > y1 || y < y2)
                return false
        }
        else{
            if(y < y1 || y > y2)
                return false
        }
        if((x == lineA.pointA.koordX && y == lineA.pointA.koordY)
            || (x == lineA.pointB.koordX && y == lineA.pointB.koordY)
            || (x == lineB.pointA.koordX && y == lineB.pointA.koordY)
            || (x == lineB.pointB.koordX && y == lineB.pointB.koordY))
            return false
        return true
    }

    private fun findYIntercept(line: Line, lineASlope: Float): Float {
        return line.pointA.koordY-lineASlope*line.pointA.koordX
    }

    private fun findLineSlope(line: Line): Float {
        val x1 = line.pointA.koordX
        val x2 = line.pointB.koordX
        val y1 = line.pointA.koordY
        val y2 = line.pointB.koordY
        if(x1-x2 == 0F)
            return 0F
        return (y1-y2)/(x1-x2)
    }

    private fun polygonIsAlreadyFound(polygon: InternalPolygon): Boolean {
        for(afp in allFoundPolygons) //nem biztos, hogy a kívánt eredményt adja -> 2 for ciklus, vonalanként megvizsgálni
            if(afp == polygon)
                return true
        return false
    }

    fun clearGameLogic(){
        allFoundPolygons.addAll(cleanedPolygons)
        foundPolygons.clear()
        cleanedPolygons.clear()
        sortedPolygons.clear()
        lines.clear()
        undoVisited()
    }

}
