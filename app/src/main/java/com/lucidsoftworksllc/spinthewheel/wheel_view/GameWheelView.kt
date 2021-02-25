package com.lucidsoftworksllc.spinthewheel.wheel_view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.lucidsoftworksllc.spinthewheel.R
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.wheel_view.models.WedgeModel
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class GameWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "GameWheelView"
    }

    // Data
    private var wedgeData: WedgeData? = null
    private var progress: Float = 0f
    private var lastProgress: Float = 0f

    // Graphics
    private val borderPaint = Paint()
    private val linePaint = Paint()
    private val indicatorCirclePaint = Paint()
    private var indicatorCircleRadius = 0f
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
        canvas?.drawArc(
            circleRect, 0f, 360f, true, borderPaint
        )
        wedgeData?.wedgeSlices?.let { slices ->
            slices.forEach {
                canvas?.drawArc(oval, it.value.startAngle, it.value.sweepAngle, true, it.value.paint)
                canvas?.drawArc(oval, it.value.startAngle, it.value.sweepAngle, true, borderPaint)
                drawIndicators(canvas, it.value)
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
                lightBackColor = getColor(R.styleable.GameWheelView_lightBackgroundColor, lightBackColor)
                textColor = getColor(R.styleable.GameWheelView_textColor, textColor)
            } finally {
                recycle()
            }
        }
        borderPaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = backColor
        }
        indicatorCirclePaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = lightBackColor
            alpha = 0
        }
        linePaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = lightBackColor
            alpha = 0
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
     * Use the angle between the start and sweep angles to help get position of the indicator circle
     * formula for x pos: (length of line) * cos(middleAngle) + (distance from left edge of screen)
     * formula for y pos: (length of line) * sin(middleAngle) + (distance from top edge of screen)
     *
     * @param key key of wedge slice being altered
     */
    private fun setIndicatorLocation(key: String) {
        wedgeData?.wedgeSlices?.get(key)?.let {
            val middleAngle = it.sweepAngle / 2 + it.startAngle
            it.indicatorCircleLocation.x = (layoutParams.height.toFloat() / 2 - layoutParams.height / 8) *
                    cos(Math.toRadians(middleAngle.toDouble())).toFloat() + width / 2
            it.indicatorCircleLocation.y = (layoutParams.height.toFloat() / 2 - layoutParams.height / 8) *
                    sin(Math.toRadians(middleAngle.toDouble())).toFloat() + layoutParams.height / 2
        }
    }

    /**
     * Sets the bounds of the wheel
     *
     * @param top the top bound of the circle. top of view by default
     * @param bottom the bottom bound of the circle. bottom of view by default
     * @param left the left bound of the circle. half of height by default
     * @param right the right bound of the circle. half of height by default
     */
    private fun setCircleBounds(
        top: Float = 0f,
        bottom: Float = layoutParams.height.toFloat(),
        left: Float = (width / 2) - (layoutParams.height / 2).toFloat(),
        right: Float = (width / 2) + (layoutParams.height / 2).toFloat()
    ) {
        oval.top = top
        oval.bottom = bottom
        oval.left = left
        oval.right = right
    }

    /**
     * Sets the text sizes and thickness of graphics used in the view
     */
    private fun setGraphicSizes() {
        mainTextPaint.textSize = height / 15f
        borderPaint.strokeWidth = height / 80f
        linePaint.strokeWidth = height / 120f
        indicatorCircleRadius = height / 70f
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
     * Draws the indicators for projects displayed on the wheel
     *
     * @param canvas the canvas used to draw onto the screen
     * @param wedgeItem the project information to display
     */
    private fun drawIndicators(canvas: Canvas?, wedgeItem: WedgeModel) {
        // draw line & text for indicator circle if on left side of the wheel
        if (wedgeItem.indicatorCircleLocation.x < width / 2) {
            drawIndicatorLine(canvas, wedgeItem, IndicatorAlignment.LEFT)
            drawIndicatorText(canvas, wedgeItem, IndicatorAlignment.LEFT)
            // draw line & text for indicator circle if on right side of the wheel
        } else {
            drawIndicatorLine(canvas, wedgeItem, IndicatorAlignment.RIGHT)
            drawIndicatorText(canvas, wedgeItem, IndicatorAlignment.RIGHT)
        }
        // draw indicator circles for wedgeItem
        canvas?.drawCircle(wedgeItem.indicatorCircleLocation.x, wedgeItem.indicatorCircleLocation.y,
            indicatorCircleRadius, indicatorCirclePaint)
    }

    /**
     * Draws indicator lines onto the canvas dependent on which side the of the pie the slice is on
     *
     * @param canvas the canvas to draw onto
     * @param wedgeItem the pie data to draw
     * @param alignment which side of the pie chart this particular slice is on
     */
    private fun drawIndicatorLine(canvas: Canvas?, wedgeItem: WedgeModel, alignment: IndicatorAlignment) {
        val xOffset = if (alignment == IndicatorAlignment.LEFT) width / 4 * -1 else width / 4
        canvas?.drawLine(
            wedgeItem.indicatorCircleLocation.x, wedgeItem.indicatorCircleLocation.y,
            wedgeItem.indicatorCircleLocation.x + xOffset, wedgeItem.indicatorCircleLocation.y, linePaint
        )
    }

    /**
     * Draws indicator names onto the canvas dependent on which side the of the pie the slice is on
     *
     * @param canvas the canvas to draw onto
     * @param wedgeItem the pie data to draw
     * @param alignment which side of the pie chart this particular slice is on
     */
    private fun drawIndicatorText(canvas: Canvas?, wedgeItem: WedgeModel, alignment: IndicatorAlignment) {
        val xOffset = if (alignment == IndicatorAlignment.LEFT) width / 4 * -1 else width / 4
        if (alignment == IndicatorAlignment.LEFT) mainTextPaint.textAlign = Paint.Align.LEFT
        else mainTextPaint.textAlign = Paint.Align.RIGHT
        canvas?.drawText(wedgeItem.name, wedgeItem.indicatorCircleLocation.x + xOffset,
            wedgeItem.indicatorCircleLocation.y - 10, mainTextPaint)
    }

    private fun setAnim() {
        valueAnimator.cancel()
        valueAnimator = ValueAnimator.ofFloat(progress, lastProgress).apply {
            interpolator = DecelerateInterpolator()
            // 1.5 second for 100% progress, 750ms for 50% progress, etc.
            val animDuration = abs(1500 * ((lastProgress - progress) / 100)).toLong()
            // set minimum increment of progress duration to 400ms
            duration = if (animDuration >= 400){
                animDuration
            }else{
                400
            }
            addUpdateListener { animation ->
                progress = animation.animatedValue as Float
                postInvalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (progress == 1f){
                        resetProgress()
                    }else{
                        valueAnimator.cancel()
                    }
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
        setAnim()
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

    fun setOnTickListener(listener: (v : View) -> Unit) {
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

enum class IndicatorAlignment {
    LEFT, RIGHT
}