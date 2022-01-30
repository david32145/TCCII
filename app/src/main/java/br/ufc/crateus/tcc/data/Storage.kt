package br.ufc.crateus.tcc.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences
import br.ufc.crateus.tcc.Metric
import br.ufc.crateus.tcc.MetricName
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Storage(
    private val content: Context
) {
    companion object {
        private val PREF = "app_preferences"
        private const val USER_KEY = "@fitnessApp/UserKey"
        private const val FITNESS_DATA_KEY = "@fitnessApp/FitnessDataKey"
    }

    fun saveUser(user: String) {
        val editor: SharedPreferences.Editor =
            content.getSharedPreferences(PREF, MODE_PRIVATE).edit()
        editor.putString(USER_KEY, user)
        editor.commit()
    }

    fun getUser(): String? {
        return content.getSharedPreferences(PREF, MODE_PRIVATE).getString(USER_KEY, null)
    }

    fun saveFitnessData(metrics: List<Metric>) {
        val editor: SharedPreferences.Editor =
            content.getSharedPreferences(PREF, MODE_PRIVATE).edit()
        editor.putString(FITNESS_DATA_KEY, Gson().toJson(metrics))
        editor.commit()
    }

    fun getFitnessData(): List<Metric> {
        val listType: Type = object : TypeToken<List<Metric?>?>() {}.type
        val jsonData = content.getSharedPreferences(PREF, MODE_PRIVATE).getString(FITNESS_DATA_KEY, null)
        if(jsonData != null) {
            return Gson().fromJson(jsonData, listType)
        }
        return getDefaultFitnessData()
    }

    private fun getDefaultFitnessData(): List<Metric> {
        val stepMetric = Metric(
            name = MetricName.STEP,
            value = 0,
            goal = 10,
            requestCode = 5577
        )
        val kcalMetric = Metric(
            name = MetricName.KCAL,
            value = 0,
            goal = 10,
            requestCode = 5578
        )
         val workoutMetric = Metric(
            name = MetricName.WORKOUT,
            value = 0,
            goal = 10,
            requestCode = 5579
        )

        return listOf(stepMetric, kcalMetric, workoutMetric)
    }
}