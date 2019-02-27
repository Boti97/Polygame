package hu.bme.aut.android.polygame.view

import android.content.Context
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import hu.bme.aut.android.polygame.R
import kotlinx.android.synthetic.main.score_board_view.view.*

class ScoreBoard: ConstraintLayout{
    constructor(context: Context) : super(context)
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


    private var playerOnePoint: Int = 0
    private var playerTwoPoint: Int = 0
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

}