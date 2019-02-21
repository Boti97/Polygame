package hu.bme.aut.android.polygame.model

import android.graphics.Paint

class Point(x: Float, y: Float, r: Float, player: Paint) {
    val koordX: Float = x
    val koordY: Float = y
    val radius: Float = r

    var playerTouched: Paint = player

    fun playerTouchedPoint(x: Float, y: Float, player: Paint): Boolean{
        if(x > koordX-(radius+10) && x < koordX+(radius+10) && y > koordY-(radius+10) && y < koordY+(radius+10)){
            playerTouched = player
            return true
        }
        return false
    }


}