package br.ufc.crateus.tcc.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import br.ufc.crateus.tcc.R
import android.graphics.DashPathEffect


class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var percent = DEFAULT_PERCENT
        set(value) {
            field = value
            this.requestLayout()
        }

    companion object {
        const val DEFAULT_PERCENT = 25
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            val center = canvas.height / 2f
            val circleRadius = center - 6.toDp(context)
            val grayStrokeWidth = 6.toDp(context)
            val fillStrokeWidth = 10.toDp(context)

            var paint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#E5E5E5")
                style = Paint.Style.STROKE
                strokeWidth = grayStrokeWidth
            }

            canvas.drawCircle(center, center, circleRadius, paint)
            val circumference = 2f * Math.PI * circleRadius
            val filled = circumference * (percent / 100f)
            val blank = circumference * ((100f - percent) / 100f)

            paint.apply {
                strokeWidth = fillStrokeWidth
                pathEffect = DashPathEffect(
                    floatArrayOf(filled.toFloat(), blank.toFloat()),
                    -circumference.toFloat() * 0.25f
                )
                strokeCap = Paint.Cap.ROUND
                shader = LinearGradient(
                    0f,
                    0f,
                    canvas.width.toFloat(),
                    0f,
                    resources.getColor(R.color.primaryColor),
                    resources.getColor(R.color.secondaryColor),
                    Shader.TileMode.REPEAT
                )
            }

            canvas.drawCircle(center, center, circleRadius, paint)
        }
    }

}

private fun Int.toDp(context: Context): Float {
    return this * context.resources.displayMetrics.density
}