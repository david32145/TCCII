package br.ufc.crateus.tcc.utils

import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.ufc.crateus.tcc.R

class StatusBar(
    private val activity: AppCompatActivity
) {

    fun changeToWhiteBackground(): StatusBar {
        val window = this.activity.window
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(activity, R.color.neutral_08)
        return this
    }

    fun noAppBar(): StatusBar {
        activity.supportActionBar?.hide()
        return this
    }
}