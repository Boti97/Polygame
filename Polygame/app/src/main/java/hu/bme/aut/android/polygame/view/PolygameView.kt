package hu.bme.aut.android.polygame.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import hu.bme.aut.android.polygame.activity.MainActivity
import hu.bme.aut.android.polygame.activity.SingleplayerActivity
import hu.bme.aut.android.polygame.fragment.ResultDialog
import hu.bme.aut.android.polygame.logic.GameLogic
import hu.bme.aut.android.polygame.model.Line
import hu.bme.aut.android.polygame.model.Point
import hu.bme.aut.android.polygame.model.Polygon

class PolygameView : View {

    companion object {
        lateinit var instance: PolygameView
    }

    private val signTouched = Paint()
    var touchedPoints: MutableList<Point> = mutableListOf()
    var gameLogic: GameLogic = GameLogic(context)

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

    private fun drawTouchedSign(canvas: Canvas) {
        for (p in touchedPoints) {
            canvas.drawCircle(p.koordX, p.koordY, p.radius + 10F, signTouched)
        }
    }

    private fun drawPoints(canvas: Canvas) {
        for (p in Polygon.fieldPoints)
            canvas.drawCircle(p.koordX, p.koordY, p.radius, p.playerTouched)
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
                l.paint
            )
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

    fun playerCheck() {
        if (touchedPoints.size == 2) {
            val line = Polygon.currentLines[Polygon.currentLines.size - 1]
            gameLogic.setupAndFindPolygons(line)
            gameLogic.paintInnerPolygons()
            gameLogic.setScore()

            gameLogic.clearGameLogic()

            Polygon.changeNextPlayer()
            touchedPoints.clear()
            ScoreBoard.instance.restart()
            invalidate()

            if (Polygon.allLinesTaken()) {
                SingleplayerActivity.instance.createResultDialog()
            }
        }
    }

    fun playerOutOfTime() {
        playerBack()
        playerBack()
        Polygon.changeNextPlayer()
        ScoreBoard.instance.restart()
        invalidate()
    }

    fun playerBack() {
        if (!touchedPoints.isEmpty()) {
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

    fun clearPolygonAndGameLogic(){
        Polygon.resetModel()
        gameLogic.clearGameLogic()
        invalidate()
    }
}