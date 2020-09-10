package com.funrisestudio.stories

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class StoriesSequenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var storiesCount = 0
    private var storyDurationMillis = 0L

    private var activeProgressView: ProgressView? = null
    private var storiesIterator = StoriesIterator()

    var onStoryCompleted: (() -> Unit)? = null

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

    /**
     * Start animation if not already started else resume
     */
    fun resume() {
        activeProgressView?.let {
            if (!it.isStarted()) {
                startActiveProgressView()
            } else {
                it.resume()
            }
        }
    }

    fun hasNext() = storiesIterator.hasNext()

    fun nextIndex() = storiesIterator.nextIndex()

    fun completeCurrent() {
        activeProgressView?.setCompleted()
    }

    fun next(animate: Boolean = true) {
        activeProgressView = storiesIterator.next()
        if (animate) {
            startActiveProgressView()
        }
    }

    fun hasPrevious() = storiesIterator.hasPrevious()

    fun previousIndex() = storiesIterator.previousIndex()

    fun unCompleteCurrent() {
        activeProgressView?.setUncompleted()
    }

    fun previous() {
        activeProgressView = storiesIterator.previous()
        startActiveProgressView()
    }

    fun currentIndex() = storiesIterator.cursor

    private fun startActiveProgressView() {
        activeProgressView?.start(onCompleted = ::onStoryCompleted)
    }

    /**
     * Set active progress bar at index
     * Marks all bars before the active one as completed
     * Ensures that progress bar at the index and all bars
     * after index are marked as uncompleted
     *
     * @param index - the index of an active progress bar
     */
    fun setCurrentItem(index: Int) {
        var i = 0
        StoriesIterator().forEach {
            if (i < index) {
                it.setCompleted()
            } else {
                if (i == index) {
                    activeProgressView = it
                }
                it.setUncompleted()
            }
            i ++
        }
        storiesIterator.cursor = index
    }

    private fun onStoryCompleted() {
        onStoryCompleted?.invoke()
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