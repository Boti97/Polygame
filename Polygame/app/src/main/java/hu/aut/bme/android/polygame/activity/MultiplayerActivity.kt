package hu.aut.bme.android.polygame.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.games.Games
import com.google.android.gms.games.TurnBasedMultiplayerClient
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchUpdateCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.aut.bme.android.polygame.R
import hu.aut.bme.android.polygame.data.GameData
import hu.aut.bme.android.polygame.fragment.ResultDialog
import hu.aut.bme.android.polygame.logic.GameLogic
import hu.aut.bme.android.polygame.model.PlayerColor
import hu.aut.bme.android.polygame.model.Polygon
import kotlinx.android.synthetic.main.content_multiplayer.*
import java.nio.charset.Charset

class MultiplayerActivity : AppCompatActivity(){

    var clientAccount: GoogleSignInAccount? = null
    private val resultDialog = ResultDialog()
    private var mTurnBasedMultiplayerClient: TurnBasedMultiplayerClient? = null
    private var match: TurnBasedMatch? = null
    private var gameLogic: GameLogic = GameLogic(this)
    private val MATCH_TURN_STATUS_MY_TURN = 1

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)
        setSupportActionBar(toolbarMulti)

        clientAccount = GoogleSignIn.getLastSignedInAccount(this)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, clientAccount!!)
        mTurnBasedMultiplayerClient!!.registerTurnBasedMatchUpdateCallback(matchUpdateCallback)

        match = MainActivity.mMatch
        MainActivity.mMatch = null
        if(MainActivity.gameDataByte!=null)
            initializeGameData(MainActivity.gameDataByte)
        else
            startGameTurn()

        multiplayerScore.setTime("∞")

        if (match!!.turnStatus == MATCH_TURN_STATUS_MY_TURN) onTurnStatusChange(false)
        else onTurnStatusChange(true)
    }

    private fun onTurnStatusChange(status: Boolean){
        if(status){
            pbWaiting.visibility = View.VISIBLE
            multiplayerPolyView.viewTouchable = false
            tvTurnStatus.text = resources.getString(R.string.their_turn)
        }
        else {
            tvTurnStatus.text = resources.getString(R.string.your_turn)
            multiplayerPolyView.viewTouchable = true
            pbWaiting.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_check -> {
                if (playerCheckMulti())
                    playTurn()
            }
            R.id.menu_back -> {
                multiplayerPolyView.playerBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getNextParticipantId(myPlayerId: String, match: TurnBasedMatch): String? {
        val myParticipantId = match.getParticipantId(myPlayerId)

        val participantIds = match.participantIds

        var desiredIndex = -1

        for (i in 0 until participantIds.size) {
            if (participantIds[i] == myParticipantId) {
                desiredIndex = i + 1
            }
        }

        if (desiredIndex < participantIds.size) {
            return participantIds[desiredIndex]
        }

        return if (match.availableAutoMatchSlots <= 0) {
            // You've run out of automatch slots, so we start over.
            participantIds[0]
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            null
        }
    }

    private fun playTurn() {

        val nextParticipantId = getNextParticipantId(MainActivity.mPlayer!!.playerId, match!!)

        // This calls a game specific method to get the bytes that represent the game state
        // including the current player's turn.
        val gameData = serializeGameData()

        mTurnBasedMultiplayerClient!!.takeTurn(match!!.matchId, gameData, nextParticipantId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    match = task.result
                    onTurnStatusChange(true)
                } else {
                    val exception = task.exception
                    AlertDialog.Builder(this).setMessage(exception!!.message)
                        .setNeutralButton(R.string.ok, null).show()
                }
            }
    }

    private fun startGameTurn() {

        val myPlayerId = match!!.getParticipantId(MainActivity.mPlayer!!.playerId)

        // This calls a game specific method to get the bytes that represent the game state
        // including the current player's turn.
        val gameData = serializeGameData()

        mTurnBasedMultiplayerClient!!.takeTurn(match!!.matchId, gameData, myPlayerId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    match = task.result
                } else {
                    val exception = task.exception
                    AlertDialog.Builder(this).setMessage(exception!!.message)
                        .setNeutralButton(R.string.ok, null).show()
                }
            }
    }

    private fun serializeGameData(): ByteArray? {
        val polyTypeToken = object : TypeToken<GameData>() {}.type
        val polyGameData = GameData(Polygon.fieldPoints, Polygon.currentLines,
            multiplayerScore.getPlayerOneScore(), multiplayerScore.getPlayerTwoScore())
        val json = Gson().toJson(polyGameData, polyTypeToken).toString()
        return json.toByteArray(Charset.forName("UTF-8"))
    }

    private fun initializeGameData(data: ByteArray?) {
        val polyTypeToken = object : TypeToken<GameData>() {}.type
        val gameData: GameData = Gson().fromJson(data!!.toString(Charset.forName("UTF-8")), polyTypeToken)
        Polygon.currentLines = gameData.currentLines
        /*multiplayerPolyView.touchedPoints.add(Polygon.currentLines[Polygon.currentLines.size-1].pointA)
        multiplayerPolyView.touchedPoints.add(Polygon.currentLines[Polygon.currentLines.size-1].pointB)*/
        Polygon.fieldPoints = gameData.fieldPoints
        if(match!!.getParticipantId(MainActivity.mPlayer!!.playerId) == "p_1")
            Polygon.currentPlayer = PlayerColor.BLUE
        else
            Polygon.currentPlayer = PlayerColor.RED
        multiplayerScore.setPlayerOneScore(gameData.playerOneScore)
        multiplayerScore.setPlayerTwoScore(gameData.playerTwoScore)
        multiplayerPolyView.invalidate()
    }

    private val matchUpdateCallback = object : TurnBasedMatchUpdateCallback() {
        override fun onTurnBasedMatchReceived(turnBasedMatch: TurnBasedMatch) {
            match = turnBasedMatch
            if (turnBasedMatch.turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                initializeGameData(turnBasedMatch.data)
                onTurnStatusChange(false)
                Toast.makeText(this@MultiplayerActivity, "A match was updated.", Toast.LENGTH_LONG).show()
                checkWinner()
            }
            else
                Toast.makeText(this@MultiplayerActivity, "Player joined.", Toast.LENGTH_LONG).show()
        }

        override fun onTurnBasedMatchRemoved(matchId: String) {
            Toast.makeText(this@MultiplayerActivity, "A match was removed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playerCheckMulti(): Boolean{
        var isChecked = false
        if(multiplayerPolyView.touchedPoints.size == 2){
            gameLogic.setupAndFindPolygons()
            gameLogic.paintInnerPolygons()
            paintPoints()
            Polygon.fieldPoints
            Polygon.currentLines
            val sum = gameLogic.setScore()
            if(Polygon.currentPlayer == PlayerColor.BLUE)
                multiplayerScore.setPlayerOneScore(sum)
            else
                multiplayerScore.setPlayerTwoScore(sum)
            gameLogic.clearGameLogic()
            multiplayerPolyView.touchedPoints.clear()
            multiplayerPolyView.invalidate()

            isChecked = true
            checkWinner()
        }
        return isChecked
    }

    private fun checkWinner(){
        if (multiplayerScore.getPlayerOneScore() >= 10) {
            if (match!!.getParticipantId(MainActivity.mPlayer!!.playerId) == "p_1") {
                Games.getAchievementsClient(this, clientAccount!!).unlock(getString(R.string.first_win))
                Games.getAchievementsClient(this, clientAccount!!).increment(getString(R.string.beginner), 1)
            }
            createResultDialog()
        } else if(multiplayerScore.getPlayerTwoScore() >= 10) {
            if (match!!.getParticipantId(MainActivity.mPlayer!!.playerId) == "p_2") {
                Games.getAchievementsClient(this, clientAccount!!).unlock(getString(R.string.first_win))
                Games.getAchievementsClient(this, clientAccount!!).increment(getString(R.string.beginner), 1)
            }
            createResultDialog()
        }
    }

    private fun paintPoints() {
        for(point in Polygon.fieldPoints){
            point.playerTouched = Polygon.linesContainsPoint(point)
        }
    }

    private fun createResultDialog() {
        val fm = supportFragmentManager
        resultDialog.setupResults(multiplayerScore.getPlayerOneScore(), multiplayerScore.getPlayerTwoScore())
        resultDialog.show(fm, "resultdialog_tag")
    }

    fun onOkClick(view: View){
        resultDialog.dismiss()
        finish()
    }

    fun onRematchClick(view: View){
        clearPolygonAndGameLogic()
        multiplayerScore.resetScoreBoard()
        resultDialog.dismiss()
        //nincs implementálva
        finish()
    }

    private fun clearPolygonAndGameLogic(){
        Polygon.resetModel()
        gameLogic.clearGameLogic()
        multiplayerPolyView.invalidate()
    }

    /*fun playerOutOfTime() {
        multiplayerPolyView.playerBack()
        multiplayerPolyView.playerBack()
        Polygon.changeNextPlayer()
        restartTimer()
        multiplayerPolyView.invalidate()
        Toast.makeText(this, "Lejárt az időd!", Toast.LENGTH_LONG).show()
    }*/

    /*private val timer = object: CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            multiplayerScore.setTime(resources.getString(R.string.time_remaining, millisUntilFinished/1000))
        }

        override fun onFinish() {
            playerOutOfTime()
        }
    }*/

    /*private fun restartTimer(){
        timer.cancel()
        timer.start()
    }*/


}

