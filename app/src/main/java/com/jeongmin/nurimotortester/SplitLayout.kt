package com.jeongmin.nurimotortester

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class SplitLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ViewGroup(context, attrs, defStyleAttr) {
    private val mOrientation: Int
    private var mSplitFraction: Float
    private var mSplitPosition = INVAID_SPLITPOSITION
    private var mHandleDrawable: Drawable?
    private var mHandleSize: Int
    private val mHandleHapticFeedback: Boolean
    private var mChild0: View? = null
    private var mChild1: View? = null
    private var mLastMotionX = 0f
    private var mLastMotionY = 0f
    private val mChildMinSize: Int
    private var mWidth = 0
    var mHeight = 0
    private var mIsDragging = false
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        checkChildren()
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSize > 0 && heightSize > 0) {
            mWidth = widthSize
            mHeight = heightSize
            setMeasuredDimension(widthSize, heightSize)
            checkSplitPosition()
            val splitPosition = Math.round(mSplitPosition)
            if (mOrientation == VERTICAL) {
                mChild0!!.measure(
                    MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                        splitPosition - mHandleSize / 2,
                        MeasureSpec.EXACTLY
                    )
                )

                mChild1!!.measure(
                    MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                        heightSize - splitPosition - mHandleSize / 2,
                        MeasureSpec.EXACTLY
                    )
                )
            } else {
                mChild0!!.measure(
                    MeasureSpec.makeMeasureSpec(
                        splitPosition - mHandleSize / 2,
                        MeasureSpec.EXACTLY
                    ),
                    MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
                )
                mChild1!!.measure(
                    MeasureSpec.makeMeasureSpec(
                        widthSize - splitPosition - mHandleSize / 2,
                        MeasureSpec.EXACTLY
                    ),
                    MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
                )
            }
        } else {
            throw IllegalStateException("SplitLayout with or height must not be MeasureSpec.UNSPECIFIED")
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val w = r - l
        val h = b - t
        val splitPosition = Math.round(mSplitPosition)
        if (mOrientation == VERTICAL) {
            mChild0!!.layout(0, 0, w, splitPosition - mHandleSize / 2)
            mChild1!!.layout(0, splitPosition + mHandleSize / 2, w, h)
        } else {
            mChild0!!.layout(0, 0, splitPosition - mHandleSize / 2, h)
            mChild1!!.layout(splitPosition + mHandleSize / 2, 0, w, h)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        val x = ev.x
        val y = ev.y
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (isUnderSplitHandle(x, y)) {
                    if (mHandleHapticFeedback) {
                        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                    mHandleDrawable!!.state = PRESSED_STATE_SET
                    mIsDragging = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    invalidate()
                } else {
                    mIsDragging = false
                }
                mLastMotionX = x
                mLastMotionY = y
            }
            MotionEvent.ACTION_MOVE -> if (mIsDragging) {
                parent.requestDisallowInterceptTouchEvent(true)
                if (mOrientation == VERTICAL) {
                    val deltaY = y - mLastMotionY
                    updateSplitPositionWithDelta(deltaY)
                } else {
                    val deltaX = x - mLastMotionX
                    updateSplitPositionWithDelta(deltaX)
                }
                mLastMotionX = x
                mLastMotionY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (mIsDragging) {
                mHandleDrawable!!.state = EMPTY_STATE_SET
                if (mOrientation == VERTICAL) {
                    val deltaY = y - mLastMotionY
                    updateSplitPositionWithDelta(deltaY)
                } else {
                    val deltaX = x - mLastMotionX
                    updateSplitPositionWithDelta(deltaX)
                }
                mLastMotionX = x
                mLastMotionY = y
                mIsDragging = false
            }
        }
        return mIsDragging
    }

    private fun isUnderSplitHandle(x: Float, y: Float): Boolean {
        return if (mOrientation == VERTICAL) {
            y >= mSplitPosition - mHandleSize / 2 && y <= mSplitPosition + mHandleSize / 2
        } else {
            x >= mSplitPosition - mHandleSize / 2 && x <= mSplitPosition + mHandleSize / 2
        }
    }

    private fun updateSplitPositionWithDelta(delta: Float) {
        mSplitPosition = mSplitPosition + delta
        checkSplitPosition()
        requestLayout()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (mSplitPosition != INVAID_SPLITPOSITION && mHandleDrawable != null) {
            val splitPosition = Math.round(mSplitPosition)
            if (mOrientation == VERTICAL) {
                mHandleDrawable!!.setBounds(
                    0,
                    splitPosition - mHandleSize / 2,
                    mWidth,
                    splitPosition + mHandleSize / 2
                )
            } else {
                mHandleDrawable!!.setBounds(
                    splitPosition - mHandleSize / 2,
                    0,
                    splitPosition + mHandleSize / 2,
                    mHeight
                )
            }
            mHandleDrawable!!.draw(canvas)
        }
    }

    private fun checkSplitFraction() {
        if (mSplitFraction < 0) {
            mSplitFraction = 0f
        } else if (mSplitFraction > 1) {
            mSplitFraction = 1f
        }
    }

    private fun checkSplitPosition() {
        if (mOrientation == VERTICAL) {
            if (mSplitPosition == INVAID_SPLITPOSITION) {
                mSplitPosition = mHeight * mSplitFraction
            }
            val min = mChildMinSize + mHandleSize / 2
            if (mSplitPosition < min) {
                mSplitPosition = min.toFloat()
            } else {
                val max = mHeight - mChildMinSize - mHandleSize / 2
                if (mSplitPosition > max) {
                    mSplitPosition = max.toFloat()
                }
            }
        } else {
            if (mSplitPosition == INVAID_SPLITPOSITION) {
                mSplitPosition = mWidth * mSplitFraction
            }
            val min = mChildMinSize + mHandleSize / 2
            if (mSplitPosition < min) {
                mSplitPosition = min.toFloat()
            } else {
                val max = mWidth - mChildMinSize - mHandleSize / 2
                if (mSplitPosition > max) {
                    mSplitPosition = max.toFloat()
                }
            }
        }
    }

    private fun checkChildren() {
        if (childCount == 2) {
            mChild0 = getChildAt(0)
            mChild1 = getChildAt(1)
        } else {
            throw IllegalStateException("SplitLayout ChildCount must be 2.")
        }
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        mHandleDrawable?.jumpToCurrentState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateSplitPositionWithDelta(0f)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mHandleDrawable
    }

    private fun dp2px(dp: Float): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {
        const val TAG = "SplitLayout"
        const val DEBUG = true
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        private const val INVAID_SPLITPOSITION = Float.MIN_VALUE
        private const val DEFAULT_SPLIT_HANDLE_SIZE_DP = 0
        //16

        private const val DEFAULT_CHILD_MIN_SIZE_DP = 0
//        32
        private val PRESSED_STATE_SET = intArrayOf(android.R.attr.state_pressed)
        private val EMPTY_STATE_SET = intArrayOf()
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SplitLayout, defStyleAttr, 0)
        mOrientation = a.getInteger(R.styleable.SplitLayout_splitOrientation, HORIZONTAL)
        mChildMinSize = a.getDimensionPixelSize(
            R.styleable.SplitLayout_splitChildMinSize,
            dp2px(DEFAULT_CHILD_MIN_SIZE_DP.toFloat())
        )
        mSplitFraction = a.getFloat(R.styleable.SplitLayout_splitFraction, 0.5f)
        checkSplitFraction()
        mHandleDrawable = a.getDrawable(R.styleable.SplitLayout_splitHandleDrawable)
        if (mHandleDrawable == null) {
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(
                intArrayOf(android.R.attr.state_pressed),
                ColorDrawable(-0x66fd772f)
            )
            stateListDrawable.addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
            stateListDrawable.setEnterFadeDuration(150)
            stateListDrawable.setExitFadeDuration(150)
            mHandleDrawable = stateListDrawable
        }
        mHandleDrawable!!.setCallback(this)
        mHandleSize = Math.round(a.getDimension(R.styleable.SplitLayout_splitHandleSize, 0f))
        if (mHandleSize <= 0) {
            mHandleSize =
                if (mOrientation == HORIZONTAL) mHandleDrawable!!.getIntrinsicWidth() else mHandleDrawable!!
                    .getIntrinsicHeight()
        }
        if (mHandleSize <= 0) {
            mHandleSize = dp2px(DEFAULT_SPLIT_HANDLE_SIZE_DP.toFloat())
        }
        mHandleHapticFeedback =
            a.getBoolean(R.styleable.SplitLayout_splitHandleHapticFeedback, false)
        a.recycle()
    }
}
