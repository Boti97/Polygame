package hu.aut.bme.android.polygame.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import com.google.android.gms.games.Player
import com.google.android.gms.games.TurnBasedMultiplayerClient
import com.google.android.gms.games.multiplayer.Multiplayer
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch
import com.google.android.gms.tasks.OnSuccessListener
import hu.aut.bme.android.polygame.R
import hu.aut.bme.android.polygame.model.Polygon
import hu.aut.bme.android.polygame.service.BackgroundSoundService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        var mPlayer: Player? = null
    }

    var clientAccount: GoogleSignInAccount? = null
    private var svc: Intent? = null
    private val RC_SIGN_IN = 9001
    private var mTurnBasedMultiplayerClient: TurnBasedMultiplayerClient? = null
    private val RC_LOOK_AT_MATCHES = 10001



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
            val match = intent
                .getParcelableExtra<TurnBasedMatch>(Multiplayer.EXTRA_TURN_BASED_MATCH)

            Toast.makeText(this, "LOL", Toast.LENGTH_LONG).show()
            /*if (match != null) {
                updateMatch(match)
            }*/

        }
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
                Polygon.resetModel()
                Polygon.loadGameField(1)
                startActivity((Intent(this, MultiplayerActivity::class.java)))
            }
        }
    }

    private fun updateUI() {
        tvAccountName.text = mPlayer!!.displayName
        Games.getGamesClient(this, clientAccount!!)
            .setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(svc)
    }

    fun onCheckGamesClicked(view: View) {
        mTurnBasedMultiplayerClient!!.inboxIntent
            .addOnSuccessListener(OnSuccessListener<Intent> { intent ->
                startActivityForResult(
                    intent,
                    RC_LOOK_AT_MATCHES
                )
            })
            .addOnFailureListener{
                android.app.AlertDialog.Builder(this).setMessage(R.string.error_get_inbox_intent)
                    .setNeutralButton(R.string.ok, null).show()
            }
    }
}