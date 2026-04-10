package net.studymongolian.fontmetrics

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class FontMetricsView : View {
    private var mText = "My text line"
    private var mTextSize = DEFAULT_FONT_SIZE_PX

    private val mAscentPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.ascent)
        strokeWidth = STROKE_WIDTH
    }

    private val mTopPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.top)
        strokeWidth = STROKE_WIDTH
    }

    private val mBaselinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.baseline)
        strokeWidth = STROKE_WIDTH
    }

    private val mDescentPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.descent)
        strokeWidth = STROKE_WIDTH
    }

    private val mBottomPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.bottom)
        strokeWidth = STROKE_WIDTH
    }

    private val mMeasuredWidthPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.measured_width)
        strokeWidth = STROKE_WIDTH
    }

    private val mTextBoundsPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_bounds)
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val mTextPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = mTextSize.toFloat()
        color = Color.BLACK
    }

    private val mLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = STROKE_WIDTH
    }

    private val mRectPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val mBounds = Rect()

    private var mIsTopVisible = true
    private var mIsAscentVisible = true
    private var mIsBaselineVisible = true
    private var mIsDescentVisible = true
    private var mIsBottomVisible = true
    private var mIsBoundsVisible = true
    private var mIsWidthVisible = true

    // getters
    val fontMetrics: Paint.FontMetrics get() = mTextPaint.fontMetrics

    val textBounds: Rect
        get() {
            mTextPaint.getTextBounds(mText, 0, mText.length, mBounds)
            return mBounds
        }

    val measuredTextWidth: Float get() = mTextPaint.measureText(mText)

    private var onInvalidateBlock: () -> Unit = {}

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    // setters
    fun setText(text: String?) {
        mText = text.orEmpty()
        invalidate()
        requestLayout()
    }

    fun setTextSizeInPixels(pixels: Int) {
        mTextSize = pixels
        mTextPaint.textSize = mTextSize.toFloat()
        invalidate()
        requestLayout()
    }

    fun setSize(size: String?) {
        size?.toIntOrNull()?.let(::setTextSizeInPixels)
    }

    fun setTopVisible(isVisible: Boolean) {
        mIsTopVisible = isVisible
        invalidate()
    }

    fun setAscentVisible(isVisible: Boolean) {
        mIsAscentVisible = isVisible
        invalidate()
    }

    fun setBaselineVisible(isVisible: Boolean) {
        mIsBaselineVisible = isVisible
        invalidate()
    }

    fun setDescentVisible(isVisible: Boolean) {
        mIsDescentVisible = isVisible
        invalidate()
    }

    fun setBottomVisible(isVisible: Boolean) {
        mIsBottomVisible = isVisible
        invalidate()
    }

    fun setBoundsVisible(isVisible: Boolean) {
        mIsBoundsVisible = isVisible
        invalidate()
    }

    fun setWidthVisible(isVisible: Boolean) {
        mIsWidthVisible = isVisible
        invalidate()
    }

    fun setTypeFace(tf: Typeface) {
        mTextPaint.typeface = tf
        invalidate()
    }

    fun onInvalidate(block: () -> Unit) {
        onInvalidateBlock = block
    }

    override fun invalidate() {
        super.invalidate()
        onInvalidateBlock()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val fontMetrics = mTextPaint.fontMetrics
        val contentTop = paddingTop.toFloat()
        val contentBottom = (height - paddingBottom).toFloat()
        val contentHeight = contentBottom - contentTop
        val baseline = contentTop + (contentHeight - (fontMetrics.bottom - fontMetrics.top)) / 2f - fontMetrics.top

        var startX = paddingLeft.toFloat()
        var startY = baseline
        var stopX = (width - paddingRight).toFloat()
        var stopY = baseline

        // draw text
        canvas.drawText(mText, startX, startY, mTextPaint) // x=0, y=0

        // draw lines
        startX = paddingLeft.toFloat()
        if (mIsTopVisible) {
            startY = baseline + fontMetrics.top
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mTopPaint)
        }
        if (mIsAscentVisible) {
            startY = baseline + fontMetrics.ascent
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mAscentPaint)
        }
        if (mIsBaselineVisible) {
            startY = baseline
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mBaselinePaint)
        }
        if (mIsDescentVisible) {
            startY = baseline + fontMetrics.descent
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mDescentPaint)
        }
        if (mIsBottomVisible) {
            startY = baseline + fontMetrics.bottom
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mBottomPaint)
        }
        if (mIsBoundsVisible) {
            mTextPaint.getTextBounds(mText, 0, mText.length, mBounds)
            val dx = paddingLeft.toFloat()
            canvas.drawRect(
                mBounds.left + dx,
                baseline + mBounds.top,
                mBounds.right + dx,
                baseline + mBounds.bottom,
                mTextBoundsPaint
            )
        }
        if (mIsWidthVisible) {
            val width = mTextPaint.measureText(mText)

            mTextPaint.getTextBounds(mText, 0, mText.length, mBounds)

            startX = paddingLeft + mBounds.left - (width - mBounds.width()) / 2
            stopX = startX
            startY = contentTop
            stopY = contentBottom
            canvas.drawLine(startX, startY, stopX, stopY, mMeasuredWidthPaint)

            startX = startX + width
            stopX = startX
            canvas.drawLine(startX, startY, stopX, stopY, mMeasuredWidthPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 200
        var height = 200
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthRequirement = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthRequirement
        } else if (widthMode == MeasureSpec.AT_MOST && width > widthRequirement) {
            width = widthRequirement
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightRequirement = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightRequirement
        } else if (heightMode == MeasureSpec.AT_MOST && height > heightRequirement) {
            height = heightRequirement
        }
        setMeasuredDimension(width, height)
    }

    companion object {
        const val DEFAULT_FONT_SIZE_PX = 200

        //private static final int PURPLE = Color.parseColor("#9315db");
        //private static final int ORANGE = Color.parseColor("#ff8a00");
        private const val STROKE_WIDTH = 5.0F
    }
}
