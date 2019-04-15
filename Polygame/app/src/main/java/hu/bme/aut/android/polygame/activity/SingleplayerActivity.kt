package hu.bme.aut.android.polygame.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import hu.bme.aut.android.polygame.R
import hu.bme.aut.android.polygame.fragment.ResultDialog
import hu.bme.aut.android.polygame.model.Polygon
import hu.bme.aut.android.polygame.view.PolygameView
import hu.bme.aut.android.polygame.view.ScoreBoard
import kotlinx.android.synthetic.main.content_singleplayer.*

class SingleplayerActivity : AppCompatActivity() {

    private val resultDialog = ResultDialog()
    private val PLAYER_ONE_KEY = "player_one_key"
    private val PLAYER_TWO_KEY = "player_two_key"


    companion object {
        lateinit var instance: SingleplayerActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        setContentView(R.layout.activity_singleplayer)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_check -> PolygameView.instance.playerCheck()
            R.id.menu_back -> {
                PolygameView.instance.playerBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        Polygon.resetModel()
    }

    fun createResultDialog() {
        val fm = supportFragmentManager
        resultDialog.setupResults(ScoreBoard.instance.getPlayerOneScore(), ScoreBoard.instance.getPlayerTwoScore())
        resultDialog.show(fm, "resultdialog_tag")
    }

    fun onOkClick(view: View){
        resultDialog.dismiss()
        finish()
    }

    fun onRematchClick(view: View){
        PolygameView.instance.clearPolygonAndGameLogic()
        ScoreBoard.instance.resetScoreBoard()
        resultDialog.dismiss()
    }


}
