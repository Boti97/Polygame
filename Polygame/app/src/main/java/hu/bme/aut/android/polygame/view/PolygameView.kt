package hu.bme.aut.android.polygame.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import hu.bme.aut.android.polygame.model.Line
import hu.bme.aut.android.polygame.model.Point
import hu.bme.aut.android.polygame.model.Polygon

class PolygameView: View {

    private val signTouched = Paint()
    var touchedPoints: MutableList<Point> = mutableListOf()
    var lines: MutableList<Line> = mutableListOf()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
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
        canvas.drawCircle(240F, 240F, 20F, Polygon.points[0].playerTouched)
        canvas.drawCircle(240F, 480F, 20F, Polygon.points[1].playerTouched)
        canvas.drawCircle(480F, 240F, 20F, Polygon.points[2].playerTouched)
        canvas.drawCircle(480F, 480F, 20F, Polygon.points[3].playerTouched)
    }

    private fun drawLines(canvas: Canvas){
        for(l in lines){
            canvas.drawLine(
                l.startPoint.koordX,
                l.startPoint.koordY,
                l.stopPoint.koordX,
                l.stopPoint.koordY,
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
                            lines.add(Line(touchedPoints[0], touchedPoints[1], Polygon.nextPlayer))
                            /*Polygon.changeNextPlayer()
                        touchedPoints.clear()*/
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