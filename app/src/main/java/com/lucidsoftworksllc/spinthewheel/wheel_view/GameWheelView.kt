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
import java.util.*
import kotlin.collections.ArrayList
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
    private var valueAnimator = ValueAnimator()

    // Colors
    private var backColor = ContextCompat.getColor(context, R.color.wheel_item_dark)
    private var lightBackColor = ContextCompat.getColor(context, R.color.wheel_item_light)
    private var textColor = ContextCompat.getColor(context, R.color.white)

    // Listeners
    private var tickerListener : OnTickerTickListener? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        circleRect.set((width / 2) - (height / 2).toFloat() + 20f, 0f + 20f, (width / 2) + (height / 2) - 20f, height.toFloat() - 20f)
        canvas?.drawArc(
            circleRect, 0f, 360f, true, borderPaint
        )
        wedgeData?.wedgeSlices?.let { wedges ->
            wedges.forEach {
                setTextLocation(it.key)
                canvas?.drawArc(
                    oval,
                    it.value.startAngle + lastProgress,
                    it.value.sweepAngle,
                    true,
                    it.value.paint
                )
                drawWedgeText(canvas, it.value)
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
            // textAlign flips the text to match the example demo.
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.DEFAULT_BOLD
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
                wedgeData?.add(model.displayText.toString(), model, backColor)
            } else {
                wedgeData?.add(model.displayText.toString(), model, lightBackColor)
            }
        }
        initView()
    }

    private fun initView() {
        var lastAngle = 0f
        wedgeData?.wedgeSlices?.let {
            for (data in it) {
                // starting angle is the location of the last angle drawn
                data.value.startAngle = lastAngle
                data.value.sweepAngle = 360f / wedgeData?.totalValue!!
                lastAngle += data.value.sweepAngle

                setTextLocation(data.key)
            }
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
    private fun setTextLocation(key: String) {
        wedgeData?.wedgeSlices?.get(key)?.let {
            val middleAngle = ((it.sweepAngle + (mainTextPaint.textSize / 10)) / (2) + it.startAngle) + lastProgress
            it.textLocation.x = (height.toFloat() / 2 - height / 12) *
                    cos(Math.toRadians(middleAngle.toDouble())).toFloat() + (width) / 2
            it.textLocation.y = (height.toFloat() / 2 - height / 12) *
                    sin(Math.toRadians(middleAngle.toDouble())).toFloat() + (height) / 2
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
        mainTextPaint.textSize = height / 25f
    }

    /**
     * Re-calculates graphic sizes if size of view is changed
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setCircleBounds()
        setGraphicSizes()
        wedgeData?.wedgeSlices?.forEach {
            setTextLocation(it.key)
        }
    }

    /**
     * Draws wedge names onto the canvas
     *
     * @param canvas the canvas to draw onto
     * @param wedgeItem the wedge data to draw
     */
    private fun drawWedgeText(canvas: Canvas?, wedgeItem: WedgeModel) {
        val path = Path().apply {
            moveTo(wedgeItem.textLocation.x, wedgeItem.textLocation.y)
            lineTo(oval.centerX(), oval.centerY())
            close()
        }
        canvas?.drawPath(path, mainTextPaint)
        canvas?.drawTextOnPath(wedgeItem.name, path, 0f, 0f, mainTextPaint)
    }

    private fun setAnim() {
        valueAnimator.cancel()
        maxProgress = Random.nextDouble(1700.0, 2500.0).toFloat()
        val currentTime = System.currentTimeMillis()
        valueAnimator = ValueAnimator.ofFloat(lastProgress, maxProgress).apply {
            interpolator = DecelerateInterpolator()
            duration = abs(3 * ((lastProgress + maxProgress))).toLong()

            addUpdateListener { animation ->
                lastProgress = animation.animatedValue as Float
                postInvalidate()
                val progressRounded = lastProgress.roundToInt()
                val divisible = (360f / wedgeData?.totalValue!!).toInt()

                val elapsedTime = (System.currentTimeMillis() - currentTime)
                val velocity = (maxProgress - lastProgress) / elapsedTime
                // A rather messy solution, but it does the job.
                if (velocity > 9.0000000E-4f) {
                    for (i in IntRange(progressRounded, progressRounded+8)){
                        if (i % divisible == 0){
                            tickerListener?.onTick(v = this@GameWheelView)
                        }
                    }
                } else if (progressRounded % divisible == 0 ){
                    tickerListener?.onTick(v = this@GameWheelView)
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    valueAnimator.cancel()

                    val modelArray = ArrayList<WedgeModel>()
                    for (model in wedgeData?.wedgeSlices!!) {
                        modelArray.add(model.value)
                    }
                    val data = getClosestToTarget(0, modelArray)
                    Log.d(TAG, "onAnimationEnd: ${data.displayText}")
                    tickerListener?.onFinished(v = this@GameWheelView, data)
                }
            })

            start()
        }
    }

    /**
     * When the animation ends, calculate player's earnings by which view's Y is closest to 0
     */
    fun getClosestToTarget(
        target: Int,
        values: ArrayList<WedgeModel>
    ): WheelSpinnerResponseModel.WheelSpinnerResponseModelItem {
        require(values.isNotEmpty()) { "The values should be at least one element" }
        if (values.size == 1) {
            return values[0].value
        }
        var returnModelItem = values[0].value
        var leastDistance = abs(values[0].textLocation.y - target)
        for (i in values.indices) {
            val currentDistance = abs(values[i].textLocation.y - target)
            if (currentDistance < leastDistance) {
                leastDistance = currentDistance
                returnModelItem = values[i].value
            }
        }
        return returnModelItem
    }

    private fun resetProgress() {
        lastProgress = 0f
        setAnim()
    }

    fun spin() {
        wedgeData?.let {
            // resetProgress() instead of setAnim() in-case of user tapping "Spin!" before the animation finished.
            resetProgress()
        } ?: kotlin.run{
            tickerListener?.onError(context.getString(R.string.err_wedgedata_null))
        }
    }

    fun stop() {
        valueAnimator.cancel()
        lastProgress = 0f
        wedgeData = null
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

        /**
         * Called when a GameWheelView encountered an error
         * @param e: The error string given.
         */
        fun onError(e: String)
    }

    /**
     * Sets the OnTickListener or function to be called when the ticker hits a new wedge
     * or the spin animation ends
     * @param listener The listener to be executed on ticker events
     */
    fun setOnTickListener(listener: OnTickerTickListener?) {
        tickerListener = listener
    }

}