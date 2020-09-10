package com.funrisestudio.stories

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat

@SuppressLint("ViewConstructor")
class ProgressView(
    context: Context,
    private var storyDuration: Long,
    private val progressAnimator: ProgressAnimator = ProgressAnimator(storyDuration)
) : View(context), ProgressControl by progressAnimator {

    private var progressWidth = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val bgColor = ContextCompat.getColor(context, R.color.grey)
    private val progressColor = ContextCompat.getColor(context, R.color.black)

    private val sizeCornerRatio = 0.8f

    private val bgPaint = fillPaint().apply {
        color = bgColor
    }
    private val progressPaint = fillPaint().apply {
        color = progressColor
    }

    private lateinit var bgRect: RectF
    private lateinit var progressRect: RectF
    private lateinit var progressClipRect: RectF

    init {
        progressAnimator.progressListener = ::onProgressChanged
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val wf = w.toFloat()
        val hf = h.toFloat()

        bgRect = RectF(0f, 0f, wf, hf)
        progressRect = RectF(0f, 0f, wf, hf)
        progressClipRect = RectF(0f, 0f, 0f, hf)
        progressAnimator.width = wf
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val c = canvas ?: return

        progressClipRect.right = progressWidth

        val sizeCorner = bgRect.height() * sizeCornerRatio
        c.drawRoundRect(bgRect, sizeCorner, sizeCorner, bgPaint)
        c.clipRect(progressClipRect)
        c.drawRoundRect(progressRect, sizeCorner, sizeCorner, progressPaint)
    }

    private fun onProgressChanged(value: Float) {
        progressWidth = value
    }

    override fun start(onCompleted: (() -> Unit)?) {
        doOnLayout {
            progressAnimator.start(onCompleted)
        }
    }

    fun setCompleted() {
        doOnLayout {
            progressAnimator.cancel()
            progressWidth = progressAnimator.width
        }
    }

    fun setUncompleted() {
        doOnLayout {
            progressAnimator.cancel()
            progressWidth = 0f
        }
    }

    fun isStarted() = progressAnimator.isStarted()

    class ProgressAnimator(
        private val durationMillis: Long
    ) : ProgressControl, ValueAnimator.AnimatorUpdateListener {

        private var animator: ValueAnimator? = null

        var width = 0f
            set(value) {
                field = value
                animator = createAnimator(value)
            }

        var progressListener: ((Float) -> Unit)? = null

        private var onEnd: (() -> Unit)? = null

        override fun start(onCompleted: (() -> Unit)?) {
            onEnd = onCompleted
            checkNotNull(animator) {  "Set progress width to use animator" }
            animator?.apply {
                addUpdateListener(this@ProgressAnimator)
                addListener(onEnd = { onEnd?.invoke() })
                start()
            }
        }

        override fun pause() {
            checkNotNull(animator) {  "Set progress width to use animator" }
            animator?.pause()
        }

        override fun resume() {
            animator?.resume()
        }

        fun cancel() {
            onEnd = null
            animator?.cancel()
        }

        fun isStarted() = animator?.isStarted == true

        override fun onAnimationUpdate(animation: ValueAnimator) {
            progressListener?.invoke(animation.animatedValue as Float)
        }

        private fun createAnimator(width: Float): ValueAnimator {
            return ValueAnimator.ofFloat(0f, width).apply {
                duration = durationMillis
            }
        }

    }

}

interface ProgressControl {

    fun start(onCompleted: (() -> Unit)? = null)

    fun pause()

    fun resume()

}

private fun fillPaint(enableAntiAlias: Boolean = true): Paint {
    return Paint().apply {
        isAntiAlias = enableAntiAlias
    }
}