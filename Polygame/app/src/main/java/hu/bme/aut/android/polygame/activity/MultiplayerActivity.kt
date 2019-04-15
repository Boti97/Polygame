package hu.bme.aut.android.polygame.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.games.Games
import com.google.android.gms.games.multiplayer.Multiplayer
import com.google.android.gms.games.multiplayer.realtime.RoomConfig
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchUpdateCallback
import hu.bme.aut.android.polygame.R
import hu.bme.aut.android.polygame.view.PolygameView
import kotlinx.android.synthetic.main.content_multiplayer.*
import com.google.android.gms.games.TurnBasedMultiplayerClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.bme.aut.android.polygame.data.GameData
import hu.bme.aut.android.polygame.logic.GameLogic
import hu.bme.aut.android.polygame.model.Line
import hu.bme.aut.android.polygame.model.Point
import hu.bme.aut.android.polygame.model.Polygon

class MultiplayerActivity : AppCompatActivity() {

    private val RC_SELECT_PLAYERS = 9010
    var clientAccount: GoogleSignInAccount? = null
    private var mTurnBasedMultiplayerClient: TurnBasedMultiplayerClient? = null
    private var match: TurnBasedMatch? = null
    private val RC_SIGN_IN = 9001

    private val mMatchUpdateCallback = object : TurnBasedMatchUpdateCallback() {
        override fun onTurnBasedMatchReceived(turnBasedMatch: TurnBasedMatch) {
            deserializeGameData(turnBasedMatch.data)
            Toast.makeText(this@MultiplayerActivity, "A match was updated.", Toast.LENGTH_LONG).show()
        }

        override fun onTurnBasedMatchRemoved(matchId: String) {
            Toast.makeText(this@MultiplayerActivity, "A match was removed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deserializeGameData(data: ByteArray?) {
        val polyTypeToken = object : TypeToken<GameData>(){}.type
        val polyFromJson: Polygon = Gson().fromJson(data.toString(), polyTypeToken)
        Polygon.currentLines = polyFromJson.currentLines
        Polygon.fieldPoints = polyFromJson.fieldPoints
        PolygameView.instance.invalidate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        signInSilently()

        onStartMatchClicked()

        setSupportActionBar(toolbarMulti)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mTurnBasedMultiplayerClient!!.registerTurnBasedMatchUpdateCallback(mMatchUpdateCallback)
    }

    private fun onStartMatchClicked() {
        val allowAutoMatch = true
        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this@MultiplayerActivity, clientAccount!!)
        mTurnBasedMultiplayerClient!!.getSelectOpponentsIntent(1, 1, allowAutoMatch)
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_SELECT_PLAYERS) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != Activity.RESULT_OK) {
                finish()
                return
            }
            val invitees = data!!.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS)

            // Get automatch criteria
            var autoMatchCriteria: Bundle? = null


            val minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0)
            val maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0)

            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoPlayers,
                maxAutoPlayers, 0)
            val tbmc = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(invitees)
                .setAutoMatchCriteria(autoMatchCriteria).build()

            mTurnBasedMultiplayerClient!!.createMatch(tbmc).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        match = task.result
                    } else {
                        // There was an error. Show the error.
                        var status = CommonStatusCodes.DEVELOPER_ERROR
                        val exception = task.exception
                        if (exception is ApiException) {
                            val apiException = exception as ApiException?
                            status = apiException!!.statusCode
                        }
                        handleError(status, exception)
                    }
                }
        }
        else if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                clientAccount = result.signInAccount
            } else {
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }
                AlertDialog.Builder(this).setMessage(message)
                    .setNeutralButton(R.string.ok, null).show()
            }
        }
    }

    private fun handleError(status: Int, exception: Exception?) {
        AlertDialog.Builder(this).setMessage(exception!!.message)
            .setNeutralButton(R.string.ok, null).show()
        serializeGameData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_check -> {
                if(PolygameView.instance.playerCheckMulti())
                    playTurn()
            }
            R.id.menu_back -> {
                PolygameView.instance.playerBack()
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

        val nextParticipantId = getNextParticipantId(clientAccount!!.id!!, match!!)

        // This calls a game specific method to get the bytes that represent the game state
        // including the current player's turn.
        val gameData = serializeGameData()

        mTurnBasedMultiplayerClient!!.takeTurn(match!!.matchId, gameData, nextParticipantId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val match = task.result
                } else {
                    // Handle exceptions.
                }
            }
    }

    private fun serializeGameData(): ByteArray? {
        val polyTypeToken = object : TypeToken<GameData>(){}.type
        val polyGameData = GameData(Polygon.fieldPoints, Polygon.currentLines)
        val json = Gson().toJson(polyGameData, polyTypeToken).toString()

        return json.toByteArray()
    }

    private fun signInSilently() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestProfile()
            .build()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            clientAccount = account
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient = GoogleSignIn.getClient(this, signInOptions)
            signInClient.silentSignIn().addOnCompleteListener( this)
            { task ->
                if (task.isSuccessful) {
                    // The signed in account is stored in the task's result.
                    clientAccount = task.result
                } else {
                    startSignInIntent()
                }
            }
        }
    }

    private fun startSignInIntent() {
        val opt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestProfile()
            .build()
        val signInClient = GoogleSignIn.getClient(
            this,
            opt
        )
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }
}

