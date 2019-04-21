package hu.aut.bme.android.polygame.model

class Point(x: Float, y: Float, r: Float, playerColor: PlayerColor) {

    val koordX: Float = x
    val koordY: Float = y
    val radius: Float = r

    var playerTouched: PlayerColor = playerColor

    fun playerTouchedPoint(x: Float, y: Float, playerColor: PlayerColor): Boolean{
        if(x > koordX-(radius+10) && x < koordX+(radius+10) && y > koordY-(radius+10) && y < koordY+(radius+10)){
            playerTouched = playerColor
            return true
        }
        return false
    }


}