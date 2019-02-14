package hu.bme.aut.android.polygame

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import hu.bme.aut.android.polygame.service.BackgroundSoundService

import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private var svc: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun switchMusicClick(view: View){
        if(musicSwitch.isChecked){
            startService(Intent(this, BackgroundSoundService::class.java))
        }
        else if(!musicSwitch.isChecked)
            stopService(Intent(this, BackgroundSoundService::class.java))
    }



}
