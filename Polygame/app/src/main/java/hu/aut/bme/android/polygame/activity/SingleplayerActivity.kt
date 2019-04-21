package hu.aut.bme.android.polygame.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import hu.aut.bme.android.polygame.R
import hu.aut.bme.android.polygame.fragment.ResultDialog
import hu.aut.bme.android.polygame.logic.GameLogic
import hu.aut.bme.android.polygame.model.PlayerColor
import hu.aut.bme.android.polygame.model.Polygon
import kotlinx.android.synthetic.main.content_singleplayer.*

class SingleplayerActivity : AppCompatActivity(){

    private val resultDialog = ResultDialog()
    private var gameLogic: GameLogic = GameLogic(this)

    private val timer = object: CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            singleplayerScore.setTime(resources.getString(R.string.time_remaining, millisUntilFinished/1000))
        }
        override fun onFinish() {
            playerOutOfTime()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_singleplayer)
        timer.start()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        Polygon.resetModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_check -> playerCheck()
            R.id.menu_back -> {
                singleplayerPolyView.playerBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        Polygon.resetModel()
    }

    private fun createResultDialog() {
        val fm = supportFragmentManager
        resultDialog.setupResults(singleplayerScore.getPlayerOneScore(), singleplayerScore.getPlayerTwoScore())
        resultDialog.show(fm, "resultdialog_tag")
    }

    fun onOkClick(view: View){
        resultDialog.dismiss()
        finish()
    }

    fun onRematchClick(view: View){
        clearPolygonAndGameLogic()
        singleplayerScore.resetScoreBoard()
        restartTimer()
        resultDialog.dismiss()
    }

    private fun playerCheck() {
        if (singleplayerPolyView.touchedPoints.size == 2) {
            gameLogic.setupAndFindPolygons()
            gameLogic.paintInnerPolygons()
            val sum = gameLogic.setScore()
            if(Polygon.currentPlayer == PlayerColor.BLUE)
                singleplayerScore.setPlayerOneScore(sum)
            else
                singleplayerScore.setPlayerTwoScore(sum)
            gameLogic.clearGameLogic()
            Polygon.changeNextPlayer()
            singleplayerPolyView.touchedPoints.clear()
            restartTimer()
            singleplayerPolyView.invalidate()
            if (singleplayerScore.getPlayerOneScore() >= 10 || singleplayerScore.getPlayerTwoScore() >= 10) {
                createResultDialog()
            }
        }
    }

    fun playerOutOfTime() {
        singleplayerPolyView.playerBack()
        singleplayerPolyView.playerBack()
        Polygon.changeNextPlayer()
        restartTimer()
        singleplayerPolyView.invalidate()
    }

    private fun clearPolygonAndGameLogic(){
        Polygon.resetModel()
        gameLogic.clearGameLogic()
        singleplayerPolyView.invalidate()
    }

    private fun restartTimer(){
        timer.cancel()
        timer.start()
    }
}
