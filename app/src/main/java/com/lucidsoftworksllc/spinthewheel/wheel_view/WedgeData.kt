package com.lucidsoftworksllc.spinthewheel.wheel_view

import android.graphics.Paint
import android.graphics.PointF
import com.lucidsoftworksllc.spinthewheel.models.WheelSpinnerResponseModel
import com.lucidsoftworksllc.spinthewheel.wheel_view.models.WedgeModel

class WedgeData {
    val wedgeSlices = LinkedHashMap<String, WedgeModel>()
    var totalValue: Int = 0

    /**
     * Adds data to the wedgeSlices hashmap
     *
     * @param name the name of the item being added
     * @param value the value (data class) of the item being added
     * @param color the color the item should be represented as (if not already in the map)
     */
    fun add(name: String, value: WheelSpinnerResponseModel.WheelSpinnerResponseModelItem, color: Int) {
        val id = wedgeSlices.size.toString()
        wedgeSlices[id]
        color.let {
            wedgeSlices[id] = WedgeModel(name, value, 0f, 0f, PointF(), createPaint(it))
        }

        totalValue = wedgeSlices.size
    }

    /**
     * Dynamically create paints for a given project
     * @param color the color of the paint to create
     */
    private fun createPaint(color: Int): Paint {
        val newPaint = Paint()
        color.let {
            newPaint.color = color
        }
        newPaint.isAntiAlias = true
        return newPaint
    }
}