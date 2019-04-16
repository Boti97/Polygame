package hu.aut.bme.android.polygame.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.games.Games
import com.google.android.gms.games.Player
import com.google.android.gms.games.TurnBasedMultiplayerClient
import com.google.android.gms.games.multiplayer.Multiplayer
import com.google.android.gms.games.multiplayer.realtime.RoomConfig
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.aut.bme.android.polygame.R
import hu.aut.bme.android.polygame.data.GameData
import hu.aut.bme.android.polygame.model.Polygon
import hu.aut.bme.android.polygame.service.BackgroundSoundService
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    companion object{
        var mPlayer: Player? = null
        var mMatch: TurnBasedMatch? = null
    }

    private var clientAccount: GoogleSignInAccount? = null
    private var svc: Intent? = null
    private val RC_SIGN_IN = 9001
    private var mTurnBasedMultiplayerClient: TurnBasedMultiplayerClient? = null
    private val RC_LOOK_AT_MATCHES = 10001
    private val RC_SELECT_PLAYERS = 9010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        svc = Intent(this, BackgroundSoundService::class.java)
        startService(svc)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                clientAccount = result.signInAccount
                Games.getPlayersClient(this, clientAccount!!)
                    .currentPlayer
                    .addOnSuccessListener { player ->
                        mPlayer = player
                        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, clientAccount!!)
                        updateUI()
                    }
                    .addOnFailureListener{
                        AlertDialog.Builder(this).setMessage("There was a problem getting the player!")
                            .setNeutralButton(R.string.ok, null).show()
                    }
            } else {
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }
                AlertDialog.Builder(this).setMessage(message)
                    .setNeutralButton(R.string.ok, null).show()
            }
        }

        else if (requestCode == RC_LOOK_AT_MATCHES){
            if (resultCode != Activity.RESULT_OK) {
                return
            }
            val match = data!!.getParcelableExtra<TurnBasedMatch>(Multiplayer.EXTRA_TURN_BASED_MATCH)

            if(match!=null) {
                mMatch = match
                initializeGameData(mMatch!!.data)
                startActivity((Intent(this, MultiplayerActivity::class.java)))
            }
        }

        else if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }
            val invitees = data!!.getStringArrayListExtra (Games.EXTRA_PLAYER_IDS)

            val minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0)
            val maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0)

            val builder = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(invitees)
            if (minAutoPlayers > 0) {
                builder.setAutoMatchCriteria(
                    RoomConfig.createAutoMatchCriteria(minAutoPlayers, maxAutoPlayers, 0)
                )
            }
            mTurnBasedMultiplayerClient!!.createMatch(builder.build()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mMatch = task.result
                    if(mMatch!!.data!= null) {
                        initializeGameData(mMatch!!.data)
                        startActivity((Intent(this, MultiplayerActivity::class.java)))
                    }
                    else {
                        Polygon.resetModel()
                        Polygon.loadGameField(1)
                        startActivity((Intent(this, MultiplayerActivity::class.java)))
                    }

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

    }

    private fun initializeGameData(data: ByteArray?) {
        val polyTypeToken = object : TypeToken<GameData>() {}.type
        val polyFromJson: GameData = Gson().fromJson(data!!.toString(Charset.forName("UTF-8")), polyTypeToken)
        Polygon.resetModel()
        Polygon.currentLines = polyFromJson.currentLines
        Polygon.fieldPoints = polyFromJson.fieldPoints
        if(mMatch!!.getParticipantId(mPlayer!!.playerId) == "p_1")
            Polygon.currentPlayer = Polygon.PlayerOne
        else
            Polygon.currentPlayer = Polygon.PlayerTwo
    }

    private fun handleError(status: Int, exception: Exception?) {
        android.support.v7.app.AlertDialog.Builder(this).setMessage(exception!!.message)
            .setNeutralButton(R.string.ok, null).show()
    }

    private fun signInSilently() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .build()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            clientAccount = account
            Games.getPlayersClient(this, clientAccount!!)
                .currentPlayer
                .addOnSuccessListener { player ->
                    mPlayer = player
                    mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, clientAccount!!)
                    updateUI()
                }
                .addOnFailureListener{
                    AlertDialog.Builder(this).setMessage("There was a problem getting the player!")
                        .setNeutralButton(R.string.ok, null).show()
                }
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient = GoogleSignIn.getClient(this, signInOptions)
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // The signed in account is stored in the task's result.
                        clientAccount = task.result
                        Games.getPlayersClient(this, clientAccount!!)
                            .currentPlayer
                            .addOnSuccessListener { player ->
                                mPlayer = player
                                mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, clientAccount!!)
                                updateUI()
                            }
                            .addOnFailureListener{
                                AlertDialog.Builder(this).setMessage("There was a problem getting the player!")
                                    .setNeutralButton(R.string.ok, null).show()
                            }
                    } else {
                        startSignInIntent()
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        signInSilently()
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btnSettings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.btnSinglePlayer -> startActivity(Intent(this, SingleplayerSettingsActivity::class.java))
            R.id.btnMuliPlayer -> {
                onStartMatchClicked()
            }
        }
    }

    private fun updateUI() {
        tvAccountName.text = mPlayer!!.displayName
        Games.getGamesClient(this, clientAccount!!).setViewForPopups(findViewById(R.id.main_activity))
        Games.getGamesClient(this, clientAccount!!)
            .setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(svc)
    }

    fun onCheckGamesClicked(view: View) {
        mTurnBasedMultiplayerClient!!.inboxIntent
            .addOnSuccessListener { intent ->
                startActivityForResult(
                    intent,
                    RC_LOOK_AT_MATCHES
                )
            }
            .addOnFailureListener{
                android.app.AlertDialog.Builder(this).setMessage(R.string.error_get_inbox_intent)
                    .setNeutralButton(R.string.ok, null).show()
            }
    }

    private fun onStartMatchClicked() {
        val allowAutoMatch = true
        mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, clientAccount!!)
        mTurnBasedMultiplayerClient!!.getSelectOpponentsIntent(1, 1, allowAutoMatch)
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_SELECT_PLAYERS) }
    }
}