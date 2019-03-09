package hu.bme.aut.android.polygame.view

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.LayoutInflater
import hu.bme.aut.android.polygame.R
import hu.bme.aut.android.polygame.fragment.ResultDialog
import kotlinx.android.synthetic.main.score_board_view.view.*

class ScoreBoard: ConstraintLayout{

    private var playerOnePoint: Int = 0
    private var playerTwoPoint: Int = 0
    private lateinit var scoreContext: Context

    constructor(context: Context) : super(context){
        scoreContext = context
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        lateinit var instance: ScoreBoard
        object Timer: CountDownTimer(30000, 1000){
            override fun onFinish() {
                PolygameView.instance.playerOutOfTime()
            }

            override fun onTick(millisUntilFinished: Long) {
                instance.changeTimeRemaining((millisUntilFinished/1000))
            }

        }
    }

    init {
        instance = this

        Timer.start()

        LayoutInflater.from(context).inflate(R.layout.score_board_view, this, true)
        tvPlayerOne.text = resources.getString(R.string.playerOne, playerOnePoint)
        tvPlayerTwo.text = resources.getString(R.string.playerTwo, playerTwoPoint)
        /*tvTime.text = resources.getString(R.string.time_remaining, 0)*/
    }

    fun setPlayerOneScore(score: Int){
        playerOnePoint+=score
        tvPlayerOne.text = resources.getString(R.string.playerOne, playerOnePoint)
    }

    fun setPlayerTwoScore(score: Int){
        playerTwoPoint+=score
        tvPlayerTwo.text = resources.getString(R.string.playerTwo, playerTwoPoint)
    }

    fun restart(){
        Timer.cancel()
        Timer.start()
    }

    fun changeTimeRemaining(remainingTime: Long){
        tvTime.text = resources.getString(R.string.time_remaining, remainingTime)
    }

    fun getPlayerOneScore():Int = playerOnePoint

    fun getPlayerTwoScore():Int = playerTwoPoint

    fun resetScoreBoard(){
        playerOnePoint = 0
        playerTwoPoint = 0
        tvPlayerOne.text = resources.getString(R.string.playerOne, playerOnePoint)
        tvPlayerTwo.text = resources.getString(R.string.playerTwo, playerTwoPoint)
        restart()
    }
}