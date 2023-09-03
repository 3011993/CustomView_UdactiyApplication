package com.udacity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var buttonColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var loadingColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var circleColor = ContextCompat.getColor(context, R.color.colorAccent)

    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f

    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)
    private var circleXOffset = textSize / 2

    private lateinit var buttonTitle: String

    private var progressWidth = 0f
    private var progressCircle = 0f


    private var valueAnimator = ValueAnimator()




    init {
        buttonTitle = resources.getString(R.string.button_name)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
        }

    }
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
                when (new) {
                    is ButtonState.Clicked -> {
                        buttonTitle = "Clicked"
                        invalidate()
                    }
                    is ButtonState.Loading -> {
                        buttonTitle = resources.getString(R.string.button_loading)
                        startAnimations()
                    }
                    is ButtonState.Completed -> {
                        valueAnimator.cancel()
                        progressWidth = 0f
                        progressCircle = 0f
                        buttonTitle = resources.getString(R.string.button_name)
                        invalidate()
                    }
                }

            }





    private fun startAnimations() {
        buttonTitle = resources.getString(R.string.button_loading)
        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        valueAnimator.duration = 2000
        valueAnimator.addUpdateListener { animation ->
            progressWidth = animation.animatedValue as Float
            progressCircle = (widthSize.toFloat() / 365) * progressWidth
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                progressWidth = 0f
                if (buttonState == ButtonState.Loading) {
                    buttonState = ButtonState.Loading
                }
            }
        })
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw background
        paint.color = buttonColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        //draw progress background
        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, progressWidth, heightSize.toFloat(), paint)
        //draw text
        paint.color = Color.WHITE
        textWidth = paint.measureText(buttonTitle)
        canvas?.drawText(buttonTitle, widthSize / 2 - textWidth / 2, heightSize / 2 - (paint.descent() + paint.ascent()) / 2, paint)
        //drawCircle
        drawCircleProgress(canvas)
        invalidate()

        }



    private fun drawCircleProgress(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(widthSize / 2 + textWidth / 2 + circleXOffset, heightSize / 2 - textSize / 2)
        paint.color = circleColor
        canvas?.drawArc(RectF(0f, 0f, textSize, textSize), 0F, progressCircle * 0.365f, true,  paint)
        canvas?.restore()
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}