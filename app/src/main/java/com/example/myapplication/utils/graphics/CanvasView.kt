package com.example.myapplication.utils.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.myapplication.utils.graphics.CanvasView

class CanvasView(context: Context) : View(context){

//    class CanvasView @JvmOverloads constructor(
//        context: Context?,
//        attrs: AttributeSet? = null,
//        defStyleAttr: Int = 0
//    ) : View(context, attrs, 0, 0) {
    enum class TYPE {
        BITMAP, DRAW
    }

    enum class CENSORED {
        TRUE, FALSE, UNKWON
    }

    private val TAG = CanvasView::class.java.simpleName
    private var mPaint: Paint? = null
    private var _type = TYPE.BITMAP
    private var _bitmap: Bitmap? = null
    private var _censored = CENSORED.UNKWON
    private var _age: String? = null

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.color = Color.BLACK
    }

    fun setType(type: TYPE) {
        _type = type
    }

    fun setBitmap(bitmap: Bitmap?) {
        _bitmap = bitmap
    }

    fun setAge(age: String?) {
        _age = age
    }

    fun setCensored(censored: CENSORED) {
        _censored = censored
    }

    override fun onDraw(canvas: Canvas) {
        try {
            when (_type) {
                TYPE.BITMAP -> if (_bitmap != null) {
                    var dx = 0f
                    val wScale = width.toFloat() / _bitmap!!.width.toFloat()
                    val hScale = height.toFloat() / _bitmap!!.height.toFloat()
                    val scale = if (hScale > wScale) wScale else hScale
                    val matrix = Matrix()
                    matrix.setScale(scale, scale)
                    val imageWidth = scale * _bitmap!!.width
                    //center aligment
                    dx = (width - imageWidth) / 2
                    matrix.postTranslate(dx, (height - scale * _bitmap!!.height) / 2)
                    canvas.drawBitmap(_bitmap!!, matrix, mPaint)
                }

                TYPE.DRAW -> {
                    setBackgroundColor(Color.TRANSPARENT)
                    val middleX = width / 2
                    val middleY = height / 2
                    val radius = if (middleX > middleY) middleY else middleX
                    val bounds = Rect()
                    if (_censored == CENSORED.UNKWON) {
                        val text = "¿+$_age?"
                        mPaint!!.color = Color.BLUE
                        mPaint!!.textSize = (radius * 0.80).toInt().toFloat()
                        mPaint!!.getTextBounds(text, 0, text.length, bounds)
                        canvas.drawText(
                            text,
                            (middleX - bounds.width() / 2).toFloat(),
                            (middleY - bounds.height() / -2).toFloat(),
                            mPaint!!
                        )
                    } else {
                        val text = "+$_age"
                        val textSize = (radius * 0.80).toInt()
                        mPaint!!.textSize = textSize.toFloat()
                        mPaint!!.getTextBounds(text, 0, text.length, bounds)
                        val strokeWidth = (radius * 0.20).toInt()
                        mPaint!!.strokeWidth = (strokeWidth / 2).toFloat()
                        if (_censored == CENSORED.FALSE) {
                            mPaint!!.color = Color.parseColor("#00ff80")
                            canvas.drawCircle(
                                middleX.toFloat(),
                                middleY.toFloat(),
                                radius.toFloat(),
                                mPaint!!
                            )
                            mPaint!!.color = Color.BLACK
                            canvas.drawText(
                                text,
                                (middleX - bounds.width() / 2).toFloat(),
                                (middleY - bounds.height() / -2).toFloat(),
                                mPaint!!
                            )
                        } else {
                            mPaint!!.color = Color.RED
                            canvas.drawCircle(
                                middleX.toFloat(),
                                middleY.toFloat(),
                                radius.toFloat(),
                                mPaint!!
                            )
                            mPaint!!.color = Color.WHITE
                            canvas.drawCircle(
                                middleX.toFloat(),
                                middleY.toFloat(),
                                (radius - strokeWidth).toFloat(),
                                mPaint!!
                            )
                            mPaint!!.color = Color.BLACK
                            canvas.drawText(
                                text,
                                (middleX - bounds.width() / 2).toFloat(),
                                (middleY - bounds.height() / -2).toFloat(),
                                mPaint!!
                            )
                            mPaint!!.color = Color.RED
                            canvas.save()
                            canvas.rotate(45f, middleX.toFloat(), middleY.toFloat())
                            canvas.drawLine(
                                middleX.toFloat(),
                                (strokeWidth / 2).toFloat(),
                                middleX.toFloat(),
                                middleY.toFloat(),
                                mPaint!!
                            )
                            canvas.drawLine(
                                middleX.toFloat(),
                                middleY.toFloat(),
                                middleX.toFloat(),
                                (height - strokeWidth / 2).toFloat(),
                                mPaint!!
                            )
                            canvas.restore()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }
        super.onDraw(canvas)
    }
}