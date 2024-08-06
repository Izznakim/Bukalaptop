package com.example.bukalaptop

import android.graphics.Matrix
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ZoomImageActivity : AppCompatActivity(), View.OnTouchListener {
    private lateinit var imgZoom: ImageView

    private var matrix: Matrix = Matrix()
    var savedMatrix: Matrix = Matrix()
    var mode = NONE

    var start = PointF()
    var mid = PointF()
    var oldDist = 1f

    companion object{
        const val EXTRA_IMAGE="extra_image"

        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image)

        imgZoom = findViewById(R.id.imgZoom)

        val image=intent.getStringExtra(EXTRA_IMAGE)

        Glide.with(this)
            .load(image)
            .apply(RequestOptions())
            .into(imgZoom)

        imgZoom.setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val view: ImageView = v as ImageView
        view.scaleType = ImageView.ScaleType.MATRIX
        val scale: Float
        dumpEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                matrix.set(view.imageMatrix)
                savedMatrix.set(matrix)
                start[event.x] = event.y
                mode = DRAG
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 5f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                matrix.set(savedMatrix)
                matrix.postTranslate(
                    event.x - start.x,
                    event.y - start.y
                )
            } else if (mode == ZOOM) {
                val newDist = spacing(event)
                if (newDist > 5f) {
                    matrix.set(savedMatrix)
                    scale = newDist / oldDist
                    matrix.postScale(scale, scale, mid.x, mid.y)
                }
            }
        }
        view.imageMatrix = matrix
        return true
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    private fun dumpEvent(event: MotionEvent) {
        val names = arrayOf(
            "DOWN",
            "UP",
            "MOVE",
            "CANCEL",
            "OUTSIDE",
            "POINTER_DOWN",
            "POINTER_UP",
            "7?",
            "8?",
            "9?"
        )
        val sb = StringBuilder()
        val action = event.action
        val actionCode = action and MotionEvent.ACTION_MASK
        sb.append("event ACTION_").append(names[actionCode])
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
            sb.append(")")
        }
        sb.append("[")
        for (i in 0 until event.pointerCount) {
            sb.append("#").append(i)
            sb.append("(pid ").append(event.getPointerId(i))
            sb.append(")=").append(event.getX(i).toInt())
            sb.append(",").append(event.getY(i).toInt())
            if (i + 1 < event.pointerCount) sb.append(";")
        }
        sb.append("]")
    }
}