package hu.bme.aut.android.polygame.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import hu.bme.aut.android.polygame.logic.GameLogic
import hu.bme.aut.android.polygame.model.Line
import hu.bme.aut.android.polygame.model.Point
import hu.bme.aut.android.polygame.model.Polygon

class PolygameView: View {

    companion object {
        lateinit var instance: PolygameView
            private set
    }

    private val signTouched = Paint()
    var touchedPoints: MutableList<Point> = mutableListOf()
    var gameLogic: GameLogic = GameLogic()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        instance = this

        signTouched.color = Color.BLACK
        signTouched.style = Paint.Style.STROKE
        signTouched.strokeWidth = 3F
    }

    override fun onDraw(canvas: Canvas) {
        drawLines(canvas)
        drawPoints(canvas)
        drawTouchedSign(canvas)
    }

    private fun drawTouchedSign(canvas: Canvas){
        for(p in touchedPoints){
            canvas.drawCircle(p.koordX, p.koordY, p.radius + 10F, signTouched)
        }
    }

    private fun drawPoints(canvas: Canvas) {
        for(p in Polygon.fieldPoints)
            canvas.drawCircle(p.koordX, p.koordY, p.radius, p.playerTouched)
    }

    private fun drawLines(canvas: Canvas){
        for(l in Polygon.currentLines){
            if(touchedPoints.isEmpty()){
                l.pointB.playerTouched = l.paint
                l.pointA.playerTouched = l.paint
            }
            canvas.drawLine(
                l.pointB.koordX,
                l.pointB.koordY,
                l.pointA.koordX,
                l.pointA.koordY,
                l.paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val d: Int

        when {
            w == 0 -> { d = h }
            h == 0 -> { d = w }
            else -> { d = Math.min(w, h)
            }
        }
        setMeasuredDimension(d, d)
    }

    fun playerCheck(){
        if(touchedPoints.size == 2){
            /*gameLogic.somethingNew(Polygon.currentLines[Polygon.currentLines.size - 1])*/
            var line = Polygon.currentLines[Polygon.currentLines.size - 1]
            gameLogic.setup(line)
            gameLogic.evenBetter(line, line.pointA, line.pointB)
            gameLogic.paintInnerPolygons()

            gameLogic.lines.clear()
            gameLogic.undoVisited()
            gameLogic.foundPolygons.clear()

            Polygon.changeNextPlayer()
            touchedPoints.clear()
            invalidate()
        }
    }

    fun playerBack(){
        if(!touchedPoints.isEmpty()){
            if(touchedPoints.size == 2){
                val lastLine = Polygon.currentLines.size - 1
                Polygon.currentLines.removeAt(lastLine)
            }
            val lastPointIn = touchedPoints.size - 1
            touchedPoints[lastPointIn].playerTouched = Polygon.Empty
            touchedPoints.removeAt(lastPointIn)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(touchedPoints.size != 2) {
                    val tX = event.x
                    val tY = event.y
                    var touchedOne = Polygon.pointTouched(tX, tY)
                    if (touchedOne.radius != 0F) {
                        touchedPoints.add(touchedOne)
                        if (touchedPoints.size == 2) {
                            Polygon.currentLines.add(Line(touchedPoints[0], touchedPoints[1], Polygon.nextPlayer))
                        }
                        invalidate()
                    }
                }
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }
}