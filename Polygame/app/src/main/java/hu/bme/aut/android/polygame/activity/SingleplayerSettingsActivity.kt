package hu.bme.aut.android.polygame.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import hu.bme.aut.android.polygame.R
import hu.bme.aut.android.polygame.model.Polygon

import kotlinx.android.synthetic.main.activity_singleplayer_settings.*

class SingleplayerSettingsActivity : AppCompatActivity() {

    companion object {
        var spinnerPos: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer_settings)

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.game_difficulty))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerPos = position
            }

        }
    }

    fun onStartClick(view: View){
        Polygon.loadGameField(spinnerPos)
        startActivity(Intent(this, SingleplayerActivity::class.java))
    }

}
