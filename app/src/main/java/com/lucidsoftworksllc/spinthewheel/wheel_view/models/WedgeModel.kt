package com.lucidsoftworksllc.spinthewheel.wheel_view.models

import android.graphics.Paint
import android.graphics.PointF
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel

// Model for a single wedge
data class WedgeModel(
    val name: String,
    var value: WheelSpinnerResponseModel.WheelSpinnerResponseModelItem,
    var startAngle: Float,
    var sweepAngle: Float,
    var indicatorCircleLocation: PointF,
    val paint: Paint
)