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
import com.google.android.material.textfield.TextInputLayout

class IncreaseMetricActivity : AppCompatActivity() {
    private var metricValue: Int? = null
    private var metricName: MetricName? = null
    private lateinit var activityAccessibilityService: ActivityAccessibilityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_increase_metric)
        activityAccessibilityService =
            ActivityAccessibilityService(this, R.id.increase_metric_layout)

        StatusBar(this).changeToWhiteBackground().noAppBar()
        loadExtras()
        setupViews()
    }

    private fun loadExtras() {
        activityAccessibilityService.onViewsChange()
        metricName = intent.extras?.getString(MetricConstants.MetricNameKey)?.let {
            MetricName.valueOf(
                it
            )
        }
        metricValue = intent.extras?.getInt(MetricConstants.MetricValueKey)
    }

    private fun setupViews() {
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
        val editMetricTitleText = findViewById<TextView>(R.id.MetricTitle)
        val editText = findViewById<TextInputEditText>(R.id.textInputMetric)
        val editTextLayout = findViewById<TextInputLayout>(R.id.textInputLayoutForMetric)
        editTextLayout.apply {
            if (metricName === MetricName.WORKOUT) {
                hint = "Horas"
            }
            if (metricName === MetricName.KCAL) {
                hint = "Kcal"
            }
            if (metricName === MetricName.STEP) {
                hint = "Passos"
            }
        }
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            Intent().apply {
                putExtra(MetricConstants.AddMetricKey, editText.text.toString().toInt())
                setResult(RESULT_OK, this)
                finish()
            }
        }
        setMetricText(editMetricTitleText, metricName)
    }

    private fun setMetricText(editMetricTitleText: TextView?, metricName: MetricName?) {
        if (metricName != null && editMetricTitleText != null) {
            if (metricName === MetricName.WORKOUT) {
                editMetricTitleText.text = "Adicionar horas de Exercício"
            }
            if (metricName === MetricName.KCAL) {
                editMetricTitleText.text = "Adicionar Calorias"
            }
            if (metricName === MetricName.STEP) {
                editMetricTitleText.text = "Adicionar Calorias Passos"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityAccessibilityService.onDetach()
    }
}