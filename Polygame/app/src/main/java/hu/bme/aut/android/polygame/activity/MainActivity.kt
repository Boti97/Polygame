package hu.bme.aut.android.polygame.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import hu.bme.aut.android.polygame.R
import hu.bme.aut.android.polygame.service.BackgroundSoundService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import android.util.Log


class MainActivity : AppCompatActivity() {

    private var svc: Intent? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private val TAG = "MainActivity"
    private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        svc = Intent(this, BackgroundSoundService::class.java)
        startService(svc)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
        btnSignIn.setSize(SignInButton.SIZE_STANDARD)
        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btnSignIn -> signIn()
            R.id.btnSettings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.btnSinglePlayer -> startActivity(Intent(this, SingleplayerSettingsActivity::class.java))
            R.id.btnMuliPlayer -> startActivity((Intent(this, MultiplayerActivity::class.java)))
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if(account!=null){
            tvAccountName.text = account.displayName
        }
        else
            tvAccountName.text = getString(R.string.account_problem)
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(svc)
    }
}
