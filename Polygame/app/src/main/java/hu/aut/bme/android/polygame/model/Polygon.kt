package hu.aut.bme.android.polygame.model

object Polygon {

    var currentPlayer: PlayerColor = PlayerColor.BLUE

    var fieldPoints: MutableList<Point> = mutableListOf()
    var currentLines: MutableList<Line> = mutableListOf()

    fun resetModel() {
        for(p in fieldPoints)
            p.playerTouched = PlayerColor.BLACK
        currentLines.clear()
        currentPlayer = PlayerColor.BLUE
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
        currentPlayer = if (currentPlayer == PlayerColor.BLUE) {
            PlayerColor.RED
        } else {
            PlayerColor.BLUE
        }
    }

    fun pointTouched(x: Float, y: Float): Point{
        for (p in fieldPoints)
            if(p.playerTouchedPoint(x, y, currentPlayer))
                return p
        return Point(0F,0F,0F, PlayerColor.BLACK)
    }

    fun lineAlreadyExist(line: Line): Boolean{
        for(l in currentLines){
            if(pointsEquals(l.pointA,line.pointA) && pointsEquals(l.pointB,line.pointB)
                || pointsEquals(l.pointB,line.pointA) && pointsEquals(l.pointA,line.pointB))
                return true
        }
        return false
    }

    fun linesContainsPoint(point: Point): PlayerColor{
        for(l in currentLines.asReversed()){
            if(pointsEquals(l.pointA,point) || pointsEquals(l.pointB,point))
                return l.paint
        }
        return PlayerColor.BLACK
    }

    private fun pointsEquals(pointA: Point, pointB: Point): Boolean{
        if(pointA.koordX == pointB.koordX && pointA.koordY == pointB.koordY)
            return true
        return false
    }

    private fun easyField(){
        fieldPoints.clear()
        fieldPoints.add(Point(240F, 240F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(240F, 480F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(480F, 240F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(480F, 480F, 20F, PlayerColor.BLACK))
    }

    private fun mediumField() {
        fieldPoints.clear()
        fieldPoints.add(Point(180F, 360F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(275F, 180F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(275F, 540F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(445F, 180F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(445F, 540F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(540F, 360F, 20F, PlayerColor.BLACK))
    }

    private fun hardField(){
        fieldPoints.clear()
        fieldPoints.add(Point(45F, 240F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(45F, 480F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(240F, 45F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(240F, 675F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(480F, 45F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(480F, 675F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(675F, 240F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(675F, 480F, 20F, PlayerColor.BLACK))
    }

    private fun veryHardField(){
        fieldPoints.clear()
        //external polygon
        fieldPoints.add(Point(45F, 240F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(45F, 480F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(240F, 45F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(240F, 675F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(480F, 45F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(480F, 675F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(675F, 240F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(675F, 480F, 20F, PlayerColor.BLACK))

        //internal polygon
        fieldPoints.add(Point(290F, 290F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(290F, 430F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(430F, 290F, 20F, PlayerColor.BLACK))
        fieldPoints.add(Point(430F, 430F, 20F, PlayerColor.BLACK))
    }

    /*fun allLinesTaken():Boolean{
        for (p1 in fieldPoints){
            for (p2 in fieldPoints){
                if(pointsEquals(p1,p2) && !lineAlreadyExist(Line(p1, p2, PlayerColor.BLACK)))
                    return false
            }
        }
        return true
    }*/

}