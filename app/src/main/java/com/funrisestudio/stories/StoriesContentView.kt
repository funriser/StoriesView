package com.funrisestudio.stories

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout

private const val DEF_STORY_DURATION_MILLIS = 10 * 1000L
private const val LONG_PRESS_DELAY_MILLIS = 800L
private const val TAG = "StoriesContent"

class StoriesContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): FrameLayout(context, attrs) {

    private var pressTimestampMillis = 0L
    private lateinit var progressBar: StoriesSequenceView

    fun setUp(stories: List<StoryContent>, storyDurationMillis: Long = DEF_STORY_DURATION_MILLIS) {
        removeAllViews()
        initProgressBar(stories.size, storyDurationMillis)
        progressBar.next()
    }

    private fun initProgressBar(storiesCount: Int, storyDurationMillis: Long) {
        progressBar = StoriesSequenceView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.height_stories)
            ).apply {
                val sideMrg = context.resources.getDimensionPixelSize(R.dimen.mrg_story_progress)
                setMargins(sideMrg, sideMrg, sideMrg, 0)
            }
            setUp(storiesCount, storyDurationMillis)
        }
        addView(progressBar)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?:return false
        return when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                onPressed()
                pressTimestampMillis = System.currentTimeMillis()
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (System.currentTimeMillis() - pressTimestampMillis >= LONG_PRESS_DELAY_MILLIS) {
                    onLongPressReleased()
                } else {
                    if (event.x >= width / 2) {
                        onRightSideClick()
                    } else {
                        onLeftSideClick()
                    }
                }
                pressTimestampMillis = 0
                return true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun onRightSideClick() {
        Log.d(TAG, "Right side click")
        progressBar.next()
    }

    private fun onLeftSideClick() {
        Log.d(TAG, "Left side click")
        progressBar.previous()
    }

    private fun onPressed() {
        Log.d(TAG, "Press")
        progressBar.pause()
    }

    private fun onLongPressReleased() {
        Log.d(TAG, "Press release")
        progressBar.resume()
    }

}