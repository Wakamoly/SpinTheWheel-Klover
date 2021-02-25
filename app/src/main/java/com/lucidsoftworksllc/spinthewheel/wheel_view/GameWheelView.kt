package com.lucidsoftworksllc.spinthewheel.wheel_view

import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.lucidsoftworksllc.spinthewheel.R
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel

class GameWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circleRect = RectF()
    private val textRect = Rect()

    private var backColor = ContextCompat.getColor(context, R.color.wheel_item_dark)
    private var lightBackColor = ContextCompat.getColor(context, R.color.wheel_item_light)
    private var textColor = ContextCompat.getColor(context, R.color.white)
    private var data = WheelSpinnerResponseModel()

    init {
        //setLoadingButtonState(ButtonState.Init)
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
    }

    fun setData(newData: WheelSpinnerResponseModel) {

    }

}