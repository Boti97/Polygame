package hu.bme.aut.android.polygame.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.bme.aut.android.polygame.R
import kotlinx.android.synthetic.main.result_layout.*


class ResultDialog: DialogFragment() {

    val TAG = "ResultDialog"
    var playerOne = 0
    var playerTwo = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.result_layout, container)
    }

    fun setupResults(pointOne: Int, pointTwo: Int){
        playerOne = pointOne
        playerTwo = pointTwo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(playerOne > playerTwo)
            tvResult.text = getString(R.string.playerOneWon)
        else if(playerOne < playerTwo)
            tvResult.text = getString(R.string.playerTwoWon)
        else
            tvResult.text = getString(R.string.draw)
    }

}