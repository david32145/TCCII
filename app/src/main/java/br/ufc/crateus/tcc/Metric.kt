package br.ufc.crateus.tcc

enum class MetricName {
    STEP {
        override fun getTitle(): String {
            return "passos"
        }
    }, KCAL{
        override fun getTitle(): String {
            return "calorias"
        }
    }, WORKOUT{
        override fun getTitle(): String {
            return "execício"
        }
    };

    abstract fun getTitle(): String
}

object MetricConstants {
    const val AddMetricKey = "AddMetricKey"
    const val MetricValueKey = "MetricValueKey"
    const val EditResultKey = "EditResultKey"
    const val MetricGoalKey = "MetricGoalKey"
    const val MetricNameKey = "MetricNameKey"
}

class Metric(
    var name: MetricName,
    var value: Int,
    var goal: Int,
    var requestCode: Int
) {
    fun increaseValue(increaseValue: Int) {
        value += increaseValue
    }

    val percent
        get() = ((value.toFloat() / goal.toFloat()) * 100f).toInt()
}