package hu.aut.bme.android.polygame.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import hu.aut.bme.android.polygame.activity.SingleplayerActivity
import hu.aut.bme.android.polygame.logic.GameLogic
import hu.aut.bme.android.polygame.model.Line
import hu.aut.bme.android.polygame.model.PlayerColor
import hu.aut.bme.android.polygame.model.Point
import hu.aut.bme.android.polygame.model.Polygon

class PolygameView : View {

    private val signTouched = Paint()
    var touchedPoints: MutableList<Point> = mutableListOf()
    var viewTouchable: Boolean = true
    private val Empty: Paint = Paint()
    private val PlayerOne: Paint = Paint()
    private val PlayerTwo: Paint = Paint()
    private val PlayerUnknown: Paint = Paint()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        Empty.color = Color.BLACK
        Empty.style = Paint.Style.FILL

        PlayerOne.color = Color.BLUE
        PlayerOne.style = Paint.Style.FILL
        PlayerOne.strokeWidth = 10F

        PlayerTwo.color = Color.RED
        PlayerTwo.style = Paint.Style.FILL
        PlayerTwo.strokeWidth = 10F

        signTouched.color = Color.BLACK
        signTouched.style = Paint.Style.STROKE
        signTouched.strokeWidth = 3F

        PlayerUnknown.color = Color.CYAN
        PlayerUnknown.style = Paint.Style.FILL
        PlayerUnknown.strokeWidth = 10F
    }

    override fun onDraw(canvas: Canvas) {
        drawLines(canvas)
        drawPoints(canvas)
        drawTouchedSign(canvas)
    }

    private fun drawTouchedSign(canvas: Canvas) {
        for (p in touchedPoints) {
            canvas.drawCircle(p.koordX, p.koordY, p.radius + 10F, signTouched)
        }
    }

    private fun drawPoints(canvas: Canvas) {
        for (p in Polygon.fieldPoints)
            canvas.drawCircle(p.koordX, p.koordY, p.radius, findPlayerColor(p.playerTouched))
    }

    private fun drawLines(canvas: Canvas) {
        for (l in Polygon.currentLines) {
            if (touchedPoints.isEmpty()) {
                l.pointB.playerTouched = l.paint
                l.pointA.playerTouched = l.paint
            }
            canvas.drawLine(
                l.pointB.koordX,
                l.pointB.koordY,
                l.pointA.koordX,
                l.pointA.koordY,
                findPlayerColor(l.paint)
            )
        }
    }

    private fun findPlayerColor(playerColor: PlayerColor): Paint{
        return when(playerColor){
            PlayerColor.BLACK -> Empty
            PlayerColor.BLUE -> PlayerOne
            PlayerColor.RED -> PlayerTwo
            else -> PlayerUnknown
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val d: Int

        when {
            w == 0 -> {
                d = h
            }
            h == 0 -> {
                d = w
            }
            else -> {
                d = Math.min(w, h)
            }
        }
        setMeasuredDimension(d, d)
    }

    fun playerBack() {
        if (touchedPoints.isNotEmpty()) {
            if (touchedPoints.size == 2) {
                val lastLine = Polygon.currentLines.size - 1
                Polygon.currentLines.removeAt(lastLine)
            }
            val lastIndex = touchedPoints.size - 1
            touchedPoints[lastIndex].playerTouched = Polygon.linesContainsPoint(touchedPoints[lastIndex])
            touchedPoints.removeAt(touchedPoints.size - 1)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!viewTouchable){
            return true
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (touchedPoints.size != 2) {
                    val tX = event.x
                    val tY = event.y
                    val touchedOne = Polygon.pointTouched(tX, tY)
                    if (touchedOne.radius != 0F) {
                        touchedPoints.add(touchedOne)
                        if (touchedPoints.size == 2) {
                            if (touchedPoints[0] == touchedOne)
                                touchedPoints.remove(touchedOne)
                            else {
                                val line = Line(touchedPoints[0], touchedPoints[1], Polygon.currentPlayer)
                                if (Polygon.lineAlreadyExist(line)) {
                                    Polygon.currentLines.add(line)
                                    playerBack()
                                } else
                                    Polygon.currentLines.add(line)
                            }
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