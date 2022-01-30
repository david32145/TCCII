package br.ufc.crateus.tcc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.ufc.crateus.tcc.data.Storage
import br.ufc.crateus.tcc.services.ActivityAccessibilityService

import br.ufc.crateus.tcc.utils.StatusBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class HomeActivity : AppCompatActivity() {
    private lateinit var storage: Storage
    private lateinit var activityAccessibilityService: ActivityAccessibilityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        checkPermissionHome()
        storage = Storage(this)
        activityAccessibilityService = ActivityAccessibilityService(this, R.id.activity_home_layout)
        val statusBar = StatusBar(this)
        statusBar.changeToWhiteBackground()
            .noAppBar()
        setupViews()
    }

    private fun checkPermissionHome() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, listOf(Manifest.permission.RECORD_AUDIO).toTypedArray(), 9876
            )
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, listOf(Manifest.permission.VIBRATE).toTypedArray(), 9877
            )
        }
    }


    private fun setupViews() {
        activityAccessibilityService.onViewsChange()
        if (storage.getUser() != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        val button = findViewById<MaterialButton>(R.id.enterInButton)
        val textInput = findViewById<TextInputEditText>(R.id.textInputMetric)
        button.setOnClickListener {
            if (textInput.text != null && textInput.text!!.trim().isBlank()) {
                Log.d("br.ufc.crateus.tcc.services", "click")
                return@setOnClickListener
            }
            storage.saveUser(textInput.text.toString())
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityAccessibilityService.onDetach()
    }
}