package com.lucidsoftworksllc.spinthewheel.wheel_view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.lucidsoftworksllc.spinthewheel.R
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.wheel_view.models.WedgeModel
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

class GameWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "GameWheelView"
    }

    // Data
    private var wedgeData: WedgeData? = null
    private var maxProgress: Float = 0f
    private var lastProgress: Float = 0f

    // Graphics
    private val borderPaint = Paint()
    private val mainTextPaint = Paint()
    private val oval = RectF()
    private val circleRect = RectF()
    private val textRect = Rect()
    private var valueAnimator = ValueAnimator()

    // Colors
    private var backColor = ContextCompat.getColor(context, R.color.wheel_item_dark)
    private var lightBackColor = ContextCompat.getColor(context, R.color.wheel_item_light)
    private var textColor = ContextCompat.getColor(context, R.color.white)

    // Listeners
    private var tickerListener : OnTickerTickListener? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        circleRect.set(0f + 20f, 0f + 20f, width.toFloat() - 20f, height.toFloat() - 20f)
        canvas?.drawArc(
            circleRect, 0f, 360f, true, borderPaint
        )
        wedgeData?.wedgeSlices?.let { wedges ->
            wedges.forEach {
                canvas?.drawArc(
                    oval,
                    it.value.startAngle + lastProgress,
                    it.value.sweepAngle,
                    true,
                    it.value.paint
                )
                drawIndicatorText(canvas, it.value)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(minW, widthMeasureSpec, 1)
        setMeasuredDimension(w, w)
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GameWheelView,
            defStyleAttr,
            0
        ).apply {
            try {
                // styleable colors from XML attrs
                backColor = getColor(R.styleable.GameWheelView_backgroundColor, backColor)
                lightBackColor = getColor(
                    R.styleable.GameWheelView_lightBackgroundColor,
                    lightBackColor
                )
                textColor = getColor(R.styleable.GameWheelView_textColor, textColor)
            } finally {
                recycle()
            }
        }

        borderPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            color = lightBackColor
        }
        mainTextPaint.apply {
            isAntiAlias = true
            color = textColor
            alpha = 0
        }
    }

    /**
     * Populates the data object and sets up the view based off the new data
     * @param newData: the new set of data to be represented
     */
    fun setData(newData: WheelSpinnerResponseModel) {
        wedgeData = WedgeData()
        for (model in newData) {
            // Is even or odd
            if (model.id?.toInt()?.rem(2) ?: 1 == 0) {
                wedgeData?.add(model.displayText.toString(), model, lightBackColor)
            }
            else {
                wedgeData?.add(model.displayText.toString(), model, backColor)
            }
        }
        initView()
    }

    private fun initView() {
        var lastAngle = 0f
        wedgeData?.wedgeSlices?.forEach {
            // starting angle is the location of the last angle drawn
            it.value.startAngle = lastAngle
            it.value.sweepAngle = 360f / wedgeData?.totalValue!!
            lastAngle += it.value.sweepAngle

            setIndicatorLocation(it.key)
        }
        invalidate()
        requestLayout()
    }

    /**
     * Use the angle between the start and sweep angles to help get position of the center of the circle
     * formula for x pos: (length of line) * cos(middleAngle) + (distance from left edge of screen)
     * formula for y pos: (length of line) * sin(middleAngle) + (distance from top edge of screen)
     *
     * @param key key of wedge slice being altered
     */
    private fun setIndicatorLocation(key: String) {
        wedgeData?.wedgeSlices?.get(key)?.let {
            val middleAngle = it.sweepAngle / 2 + it.startAngle
            it.indicatorCircleLocation.x = cos(Math.toRadians(middleAngle.toDouble())).toFloat() + width / 2
            it.indicatorCircleLocation.y = sin(Math.toRadians(middleAngle.toDouble())).toFloat() + height / 2
        }
    }

    /**
     * Sets the bounds of the wheel wedges
     *
     * @param top the top bound of the circle.
     * @param bottom the bottom bound of the circle.
     * @param left the left bound of the circle.
     * @param right the right bound of the circle.
     */
    private fun setCircleBounds(
        top: Float = 60f,
        bottom: Float = height.toFloat() - 60f,
        left: Float = (width / 2) - (height / 2).toFloat() + 60f,
        right: Float = (width / 2) + (height / 2).toFloat() - 60f
    ) {
        oval.set(left, top, right, bottom)
    }

    /**
     * Sets the text sizes and thickness of graphics used in the view
     */
    private fun setGraphicSizes() {
        mainTextPaint.textSize = height / 15f
    }

    /**
     * Re-calculates graphic sizes if size of view is changed
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setCircleBounds()
        setGraphicSizes()
        wedgeData?.wedgeSlices?.forEach {
            setIndicatorLocation(it.key)
        }
    }

    /**
     * Draws indicator names onto the canvas
     *
     * @param canvas the canvas to draw onto
     * @param wedgeItem the wedge data to draw
     */
    private fun drawIndicatorText(canvas: Canvas?, wedgeItem: WedgeModel) {
        mainTextPaint.textAlign = Paint.Align.CENTER
        val path = Path().apply {
            moveTo(wedgeItem.indicatorCircleLocation.x + lastProgress, wedgeItem.indicatorCircleLocation.y + lastProgress)
            close()
        }
        canvas?.drawPath(path, mainTextPaint)
        canvas?.drawTextOnPath(wedgeItem.name, path, 0f, 0f, mainTextPaint)
        canvas?.drawText(
            wedgeItem.name, wedgeItem.indicatorCircleLocation.x,
            wedgeItem.indicatorCircleLocation.y, mainTextPaint
        )
    }

    private fun setAnim() {
        valueAnimator.cancel()
        maxProgress = Random.nextDouble(700.0, 1500.0).toFloat()
        valueAnimator = ValueAnimator.ofFloat(lastProgress, maxProgress).apply {
            interpolator = DecelerateInterpolator()
            // 1.5 second for 100% progress, 750ms for 50% progress, etc.
            val animDuration = abs(15 * ((lastProgress + maxProgress))).toLong()
            // set minimum increment of progress duration to 400ms
            duration = if (animDuration >= 400){
                animDuration
            }else{
                400
            }
            addUpdateListener { animation ->
                lastProgress = animation.animatedValue as Float
                Log.d(TAG, "setAnim: $maxProgress, $lastProgress")
                postInvalidate()
                val progressRounded = lastProgress.roundToInt()
                if (progressRounded % (360f / wedgeData?.totalValue!!).toInt() == 0 ){
                    // TODO: 2/25/2021 Update occurs too slowly to accurately calculate
                    tickerListener?.onTick(v = this@GameWheelView)
                }
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    valueAnimator.cancel()
                    // TODO: 2/25/2021 Send ending value to listener
                }
            })
            start()
        }
    }

    private fun resetProgress() {
        lastProgress = 0f
        setAnim()
    }

    fun spin() {
        tickerListener?.onTick(v = this)
        resetProgress()
    }

    /**
     * Interface definition for a callback to be invoked when a wedge ticks the stopper.
     */
    interface OnTickerTickListener {
        /**
         * Called when the GameWheelView ticks a new wedge
         * @param v: The GameWheelView that the user interacted with.
         */
        fun onTick(v: View)

        /**
         * Called when a GameWheelView finished spinning
         * @param v: The GameWheelView that the user interacted with.
         * @param data: The resulting data class when the animation is finished.
         */
        fun onFinished(v: View, data: WheelSpinnerResponseModel.WheelSpinnerResponseModelItem)
    }

    /**
     * Sets the OnTickListener or function to be called when the ticker hits a new wedge
     * or the spin animation ends
     * @param listener The listener to be executed on ticker events
     */
    fun setOnTickListener(listener: OnTickerTickListener?) {
        tickerListener = listener
    }

    fun setOnTickListener(listener: (v: View) -> Unit) {
        tickerListener = object : OnTickerTickListener {
            override fun onTick(v: View) {
                listener(v)
            }

            override fun onFinished(
                v: View,
                data: WheelSpinnerResponseModel.WheelSpinnerResponseModelItem
            ) {
                listener(v)
            }
        }
    }

}