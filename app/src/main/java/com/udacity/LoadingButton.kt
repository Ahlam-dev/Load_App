package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.green
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonLabel = ""
    private var buttonAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()
    private var loadingWidth = 0f
    private var sweepAngle = 0
private var foregroundColor=0
    private var backgroundcolor = 0
    private var textColor = 0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) {


        p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                        .apply {
                            duration = 2000
                            addUpdateListener {
                                repeatMode = ValueAnimator.RESTART
                                repeatCount = ValueAnimator.INFINITE
                                loadingWidth = animatedValue as Float
                                this@LoadingButton.invalidate()
                            }
                            start()
                        }
                circleAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration = 2000
                    addUpdateListener {
                        sweepAngle = animatedValue as Int
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = ValueAnimator.INFINITE
                        this@LoadingButton.invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                buttonAnimator.end()
                circleAnimator.end()
                sweepAngle = 0
                loadingWidth = 0f
                this@LoadingButton.invalidate()
            }
        }
    }


    init {
        isClickable = true
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            backgroundcolor=getColor(R.styleable.LoadingButton_buttonBackgroundColor,0)
            textColor=getColor(R.styleable.LoadingButton_buttonTextColor,0)
            foregroundColor=getColor(R.styleable.LoadingButton_buttonForegroundColor,0)
        }


    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    override fun performClick(): Boolean {
        if (super.performClick()) return true
        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //button's background
        paint.color = backgroundcolor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        //animate button
        paint.color = foregroundColor
        canvas?.drawRect(0f, 0f, (widthSize.toFloat() * loadingWidth) / 100, heightSize.toFloat(), paint)

        //button's text
        paint.color = textColor
        buttonLabel = when (buttonState) {
            ButtonState.Completed -> "Download"
            else -> "We are loading.."
        }
        val textHeight: Float = paint.descent() - paint.ascent()
        val textOffset: Float = textHeight / 2 - paint.descent()
        canvas?.drawText(buttonLabel,
                widthSize.toFloat() / 2,
                heightSize.toFloat() / 2 + textOffset, paint)

        //circle
        paint.color = context.getColor(R.color.colorAccent)
        canvas?.drawArc(widthSize.toFloat() / 2 + 250F, heightSize.toFloat() / 2 - 40f,
                widthSize.toFloat() / 2 + 330f,
                heightSize / 2 + 40f, 0F, sweepAngle.toFloat(), true, paint)
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

    fun setState(state: ButtonState) {
        buttonState = state
    }

}