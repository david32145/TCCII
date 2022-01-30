package br.ufc.crateus.tcc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import br.ufc.crateus.tcc.services.ActivityAccessibilityService
import br.ufc.crateus.tcc.utils.StatusBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditMetricActivity : AppCompatActivity() {
    private var metricGoal: Int? = null
    private var metricName: MetricName? = null
    private lateinit var activityAccessibilityService: ActivityAccessibilityService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBar(this).changeToWhiteBackground().noAppBar()
        setContentView(R.layout.activity_edit_metric)
        activityAccessibilityService = ActivityAccessibilityService(this, R.id.metric_layout)
        loadExtras()
        setupViews()
    }

    private fun loadExtras() {
        metricName = intent.extras?.getString(MetricConstants.MetricNameKey)?.let {
            MetricName.valueOf(
                it
            )
        }
        metricGoal = intent.extras?.getInt(MetricConstants.MetricGoalKey)
    }

    private fun setupViews() {
        activityAccessibilityService.onViewsChange()
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
        val editMetricTitleText = findViewById<TextView>(R.id.MetricTitle)
        val editText = findViewById<TextInputEditText>(R.id.textInputMetric)
        editText.setText(metricGoal?.toString())
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            Intent().apply {
                putExtra(MetricConstants.EditResultKey, editText.text.toString().toInt())
                setResult(RESULT_OK, this)
                finish()
            }
        }
        setMetricText(editMetricTitleText, metricName)
    }

    private fun setMetricText(editMetricTitleText: TextView?, metricName: MetricName?) {
        if(metricName != null && editMetricTitleText != null) {
            if(metricName === MetricName.WORKOUT) {
                editMetricTitleText.text = "Editar Métrica do Exercício"
            }
            if(metricName === MetricName.KCAL) {
                editMetricTitleText.text = "Editar Métrica do Calorias"
            }
            if(metricName === MetricName.STEP) {
                editMetricTitleText.text = "Editar Métrica de Passos"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityAccessibilityService.onDetach()
    }
}