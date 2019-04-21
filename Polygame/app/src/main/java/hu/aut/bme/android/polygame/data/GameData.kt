package hu.aut.bme.android.polygame.data

import hu.aut.bme.android.polygame.model.Line
import hu.aut.bme.android.polygame.model.Point

class GameData(var fieldPoints: MutableList<Point> = mutableListOf(),
               var currentLines: MutableList<Line> = mutableListOf(),
               var playerOneScore: Int,
               var playerTwoScore: Int)