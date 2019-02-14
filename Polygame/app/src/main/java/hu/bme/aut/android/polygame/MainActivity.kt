package hu.bme.aut.android.polygame

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import hu.bme.aut.android.polygame.service.BackgroundSoundService


class MainActivity : AppCompatActivity() {

    private var svc: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        svc = Intent(this, BackgroundSoundService::class.java)
        startService(svc)
    }

    override fun onRestart() {
        super.onRestart()
        startService(svc)
    }

    override fun onResume() {
        super.onResume()
        startService(svc)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(svc)
    }

    fun onSingleplayerClick(view: View){
        startActivity(Intent(this, SingleplayerSettingsActivity::class.java))
    }

    fun onMultiplayerClick(view: View){
        startActivity((Intent(this, MultiplayerActivity::class.java)))
    }

    fun onSettingsClick(view: View){
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun onExitClick(view: View){
        finish()
    }
}
