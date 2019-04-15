package hu.bme.aut.android.polygame.data

import hu.bme.aut.android.polygame.model.Line
import hu.bme.aut.android.polygame.model.Point

class GameData(var fieldPoints: MutableList<Point> = mutableListOf(),
               var currentLines: MutableList<Line> = mutableListOf()) {}