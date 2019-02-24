package hu.bme.aut.android.polygame.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import hu.bme.aut.android.polygame.R
import hu.bme.aut.android.polygame.model.Polygon
import hu.bme.aut.android.polygame.view.PolygameView
import kotlinx.android.synthetic.main.content_singleplayer.*

class SingleplayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.singleplayer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_check -> PolygameView.instance.playerCheck()
            R.id.menu_back -> PolygameView.instance.playerBack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        Polygon.resetModel()
    }
}
