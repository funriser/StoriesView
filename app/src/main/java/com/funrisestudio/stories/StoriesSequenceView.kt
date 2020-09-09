package com.funrisestudio.stories

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import java.util.*

class StoriesSequenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var storiesCount = 0
    private var storyDurationMillis = 0L

    private var activeProgressView: ProgressView? = null
    private var storiesIterator = StoriesIterator()

    init {
        orientation = HORIZONTAL
    }

    fun setUp(storiesCount: Int, storyDurationMillis: Long) {
        this.storiesCount = storiesCount
        this.storyDurationMillis = storyDurationMillis
        initChildren()
        storiesIterator = StoriesIterator()
    }

    private fun initChildren() {
        removeAllViews()
        repeat(storiesCount) { i ->
            val pv = ProgressView(context, storyDurationMillis).apply {
                layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT).apply {
                    weight = 1f
                    if (i != 0) {
                        leftMargin =
                            context.resources.getDimensionPixelSize(R.dimen.mrg_story_progress)
                    }
                }
            }
            addView(pv)
        }
    }

    fun pause() {
        activeProgressView?.pause()
    }

    fun resume() {
        activeProgressView?.resume()
    }

    fun next() {
        activeProgressView?.setCompleted()
        if (storiesIterator.hasNext()) {
            activeProgressView = storiesIterator.next()
            activeProgressView?.start(onCompleted = ::next)
        }
    }

    fun previous() {
        activeProgressView?.setUncompleted()
        if (storiesIterator.hasPrevious()) {
            activeProgressView = storiesIterator.previous()
        }
        activeProgressView?.start(onCompleted = ::next)
    }

    inner class StoriesIterator : ListIterator<ProgressView> {

        var cursor = -1

        override fun hasNext(): Boolean = cursor < childCount - 1
        override fun next(): ProgressView =
            getChildAt(++cursor) as ProgressView? ?: throw IndexOutOfBoundsException()
        override fun nextIndex(): Int = cursor + 1

        override fun hasPrevious(): Boolean = cursor > 0
        override fun previous(): ProgressView =
            getChildAt(--cursor) as ProgressView? ?: throw IndexOutOfBoundsException()
        override fun previousIndex(): Int = cursor - 1

    }

}