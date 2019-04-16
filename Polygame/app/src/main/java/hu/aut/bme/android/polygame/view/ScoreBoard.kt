package hu.aut.bme.android.polygame.view

import android.content.Context
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import hu.aut.bme.android.polygame.R
import hu.aut.bme.android.polygame.activity.MultiplayerActivity
import hu.aut.bme.android.polygame.activity.SingleplayerActivity
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

    init {
        LayoutInflater.from(context).inflate(R.layout.score_board_view, this, true)
        tvPlayerOne.text = resources.getString(R.string.playerOne, playerOnePoint)
        tvPlayerTwo.text = resources.getString(R.string.playerTwo, playerTwoPoint)
    }

    fun setPlayerOneScore(score: Int){
        playerOnePoint+=score
        tvPlayerOne.text = resources.getString(R.string.playerOne, playerOnePoint)
    }

    fun setPlayerTwoScore(score: Int){
        playerTwoPoint+=score
        tvPlayerTwo.text = resources.getString(R.string.playerTwo, playerTwoPoint)
    }

    fun getPlayerOneScore():Int = playerOnePoint

    fun getPlayerTwoScore():Int = playerTwoPoint

    fun resetScoreBoard(){
        playerOnePoint = 0
        playerTwoPoint = 0
        tvPlayerOne.text = resources.getString(R.string.playerOne, playerOnePoint)
        tvPlayerTwo.text = resources.getString(R.string.playerTwo, playerTwoPoint)
    }

    fun setTime(timeStr: String){
        tvTime.text = timeStr
    }

}