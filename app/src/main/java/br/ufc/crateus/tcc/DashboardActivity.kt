package br.ufc.crateus.tcc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import br.ufc.crateus.tcc.data.Storage
import br.ufc.crateus.tcc.services.ActivityAccessibilityService
import br.ufc.crateus.tcc.ui.GraphView
import br.ufc.crateus.tcc.utils.StatusBar
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {
    private lateinit var metricList: List<Metric>
    private lateinit var storage: Storage
    private lateinit var activityAccessibilityService: ActivityAccessibilityService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        activityAccessibilityService = ActivityAccessibilityService(this, R.id.dashboard_layout)
        storage = Storage(this)
        metricList = storage.getFitnessData()

        val statusBar = StatusBar(this)
        statusBar.changeToWhiteBackground()
            .noAppBar()

        setupViews()
    }

    private fun setupViews() {
        activityAccessibilityService.onViewsChange()
        storage.saveFitnessData(metricList)
        var stepCard = findViewById<ConstraintLayout>(R.id.stepCard)
        var kcalCard = findViewById<ConstraintLayout>(R.id.kcalCard)
        var workoutCard = findViewById<ConstraintLayout>(R.id.workoutCard)

        metricList.find { it.name === MetricName.STEP }?.let {
            setMetric(stepCard, it)
        }
        metricList.find { it.name === MetricName.KCAL }?.let {
            setMetric(kcalCard, it)
        }
        metricList.find { it.name === MetricName.WORKOUT }?.let {
            setMetric(workoutCard, it)
        }

        val addStepButton = findViewById<MaterialButton>(R.id.addStepButton)
        val addKcalButton = findViewById<MaterialButton>(R.id.addKcalButton)
        val addWorkoutButton = findViewById<MaterialButton>(R.id.addWorkoutButton)

        addStepButton.setOnClickListener {
            metricList.find { it.name === MetricName.STEP }?.let {
                increaseMetric(it)
            }
        }

        addKcalButton.setOnClickListener {
            metricList.find { it.name === MetricName.KCAL }?.let {
                increaseMetric(it)
            }
        }

        addWorkoutButton.setOnClickListener {
            metricList.find { it.name === MetricName.WORKOUT }?.let {
                increaseMetric(it)
            }
        }
    }

    private fun increaseMetric(metric: Metric) {
        val intent = Intent(this, IncreaseMetricActivity::class.java).apply {
            putExtra(MetricConstants.MetricNameKey, metric.name.name)
            putExtra(MetricConstants.MetricValueKey, metric.value)
        }
        startActivityForResult(intent, metric.requestCode)
    }

    private fun setMetric(card: ConstraintLayout, metric: Metric) {
        val textViewMetricValue = card.findViewById<TextView>(R.id.metricValue)
        val graphView = card.findViewById<GraphView>(R.id.graphView)
        val editButton = card.findViewById<ImageButton>(R.id.editButton)

        editButton.setOnClickListener {
            val intent = Intent(this, EditMetricActivity::class.java).apply {
                putExtra(MetricConstants.MetricNameKey, metric.name.name)
                putExtra(MetricConstants.MetricGoalKey, metric.goal)
            }

            startActivityForResult(intent, metric.requestCode)
        }

        textViewMetricValue.text = metric.value.toString()
        textViewMetricValue.contentDescription = "O valor atual da meta de ${metric.name.getTitle()} é ${metric.value}"
        graphView.percent = metric.percent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            data?.getIntExtra(MetricConstants.EditResultKey, -1)?.let { newGoal ->
                if (newGoal == -1) {
                    return@let
                }
                metricList.find {  it.requestCode == requestCode }?.let { metric ->
                    metric.goal = newGoal
                    setupViews()
                }
            }

            data?.getIntExtra(MetricConstants.AddMetricKey, -1)?.let { increaseValue ->
                if (increaseValue == -1) {
                    return@let
                }
                metricList.find {  it.requestCode == requestCode }?.let { metric ->
                    metric.increaseValue(increaseValue)
                    setupViews()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityAccessibilityService.onDetach()
    }
}