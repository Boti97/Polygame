package hu.aut.bme.android.polygame.activity

import android.content.Intent
import android.graphics.Color
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
import hu.aut.bme.android.polygame.logic.GameLogic
import hu.aut.bme.android.polygame.model.Polygon
import kotlinx.android.synthetic.main.content_multiplayer.*
import java.nio.charset.Charset

class MultiplayerActivity : AppCompatActivity(){

    var clientAccount: GoogleSignInAccount? = null
    private var mTurnBasedMultiplayerClient: TurnBasedMultiplayerClient? = null
    private var match: TurnBasedMatch? = null
    private var gameLogic: GameLogic = GameLogic(this)
    private val MATCH_TURN_STATUS_MY_TURN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)
        setSupportActionBar(toolbarMulti)

        clientAccount = GoogleSignIn.getLastSignedInAccount(this)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, clientAccount!!)
        mTurnBasedMultiplayerClient!!.registerTurnBasedMatchUpdateCallback(mMatchUpdateCallback)
        match = MainActivity.mMatch
        multiplayerScore.setTime("∞")
        if(match!!.turnStatus == MATCH_TURN_STATUS_MY_TURN) onTurnStatusChange(false)
        else onTurnStatusChange(true)
        if(match!!.data==null)
            startGameTurn()

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
        multiplayerPolyView.touchedPoints.clear()
        multiplayerPolyView.invalidate()

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
        val polyGameData = GameData(Polygon.fieldPoints, Polygon.currentLines)
        val json = Gson().toJson(polyGameData, polyTypeToken).toString()
        return json.toByteArray(Charset.forName("UTF-8"))
    }

    private fun initializeGameData(data: ByteArray?) {
        val polyTypeToken = object : TypeToken<GameData>() {}.type
        val polyFromJson: GameData = Gson().fromJson(data!!.toString(Charset.forName("UTF-8")), polyTypeToken)
        Polygon.currentLines = polyFromJson.currentLines
        Polygon.fieldPoints = polyFromJson.fieldPoints
        if(match!!.getParticipant(MainActivity.mPlayer!!.playerId).participantId == "p_1")
            Polygon.currentPlayer = Polygon.PlayerOne
        else
            Polygon.currentPlayer = Polygon.PlayerTwo
        multiplayerPolyView.invalidate()
    }

    private val mMatchUpdateCallback = object : TurnBasedMatchUpdateCallback() {
        override fun onTurnBasedMatchReceived(turnBasedMatch: TurnBasedMatch) {
            initializeGameData(turnBasedMatch.data)
            onTurnStatusChange(false)
            Toast.makeText(this@MultiplayerActivity, "A match was updated.", Toast.LENGTH_LONG).show()
        }

        override fun onTurnBasedMatchRemoved(matchId: String) {
            Toast.makeText(this@MultiplayerActivity, "A match was removed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playerCheckMulti(): Boolean{
        var isChecked = false
        if(multiplayerPolyView.touchedPoints.size == 2){
            val line = Polygon.currentLines[Polygon.currentLines.size - 1]
            gameLogic.setupAndFindPolygons(line)
            gameLogic.paintInnerPolygons()
            gameLogic.setScore()

            gameLogic.clearGameLogic()//kérdéses
            isChecked = true
        }
        return isChecked
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

