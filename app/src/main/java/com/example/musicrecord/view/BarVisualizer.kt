package com.example.musicrecord.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BarVisualizer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var amplitudes: List<Int> = emptyList()
    private val barPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val backgroundPaint = Paint().apply {
        color = Color.BLACK
    }
    private val barWidth = 10f
    private val spacing = 5f

    init {
        setBackgroundColor(Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = measuredWidth
        val height = measuredHeight
        val barCount = (width / (barWidth + spacing)).toInt()

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        amplitudes.take(barCount).forEachIndexed { index, amplitude ->
            val left = index * (barWidth + spacing)
            val barHeight = (height * amplitude / 256f)
            val top = height - barHeight
            val right = left + barWidth
            val bottom = height.toFloat()

            barPaint.color = if (amplitude > 0) Color.GREEN else Color.GRAY
            canvas.drawRect(left, top, right, bottom, barPaint)
        }
    }

    fun updateAmplitudes(newAmplitudes: List<Int>) {
        amplitudes = newAmplitudes
        invalidate()
    }
}

