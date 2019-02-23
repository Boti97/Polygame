package hu.bme.aut.android.polygame

import android.media.ToneGenerator.MAX_VOLUME
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import hu.bme.aut.android.polygame.service.BackgroundSoundService
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    companion object {
        var curProg = 100
        var switchChecked = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        musicSwitch.isChecked = switchChecked
        musicSeekBar.progress = curProg

        musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = (1 - Math.log(MAX_VOLUME.toDouble() - progress) / Math.log(MAX_VOLUME.toDouble())).toFloat()
                curProg = progress
                BackgroundSoundService.setVolume(volume, volume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun switchMusicClick(view: View){
        if(musicSwitch.isChecked){
            switchChecked = true
            BackgroundSoundService.restartMusic()
        }
        else if(!musicSwitch.isChecked) {
            switchChecked = false
            BackgroundSoundService.pauseMusic()
        }
    }





}
