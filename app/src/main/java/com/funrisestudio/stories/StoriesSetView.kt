package com.funrisestudio.stories

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

private const val DEF_STORY_DURATION_MILLIS = 10 * 1000L
private const val LONG_PRESS_DELAY_MILLIS = 800L
private const val TAG = "StoriesContent"

class StoriesSetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    /**
     * Flag that indicated that this view received a resume command
     * Without this command progress bars cannot be animated
     */
    private var isResumed = false

    private var isLoading = false

    init {
        isSaveEnabled = true
    }

    private val backedImageView: ImageView = ImageView(context).also {
        it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        it.scaleType = ImageView.ScaleType.CENTER_CROP
        it.visibility = View.INVISIBLE
        addView(it)
    }
    private val contentView: ImageView = ImageView(context).also {
        it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        it.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(it)
    }
    private var pressTimestampMillis = 0L
    private var progressBar: StoriesSetProgressBar? = null
    private var stories: List<StoryContent> = emptyList()

    private val imageRequestListener = ImageRequestListener()
    private var onImageRequestCompleted: (() -> Unit)? = null

    var storiesNavigationListener: StoriesNavigationListener? = null

    fun setUp(
        stories: List<StoryContent>,
        storyDurationMillis: Long = DEF_STORY_DURATION_MILLIS
    ) {
        this.stories = stories
        progressBar?.let {
            removeView(it)
        }
        initProgressBar(stories.size, storyDurationMillis)
        dispatchNext()
    }

    private fun initProgressBar(storiesCount: Int, storyDurationMillis: Long) {
        progressBar = StoriesSetProgressBar(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.height_stories)
            ).apply {
                val sideMrg = context.resources.getDimensionPixelSize(R.dimen.mrg_story_progress)
                setMargins(sideMrg, sideMrg, sideMrg, 0)
            }
            setUp(storiesCount, storyDurationMillis)
            onStoryCompleted = ::dispatchNext
        }
        addView(progressBar)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        return when (event.action) {
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

    private fun dispatchNext() {
        val progress = progressBar ?: return
        if (progress.hasNext()) {
            progress.completeCurrent()
            onImageRequestCompleted = {
                post {
                    isLoading = false
                    if (isResumed) {
                        progress.start()
                    }
                }
            }
            isLoading = true
            renderContent(progress.nextIndex())
            progress.next()
        } else {
            storiesNavigationListener?.toNextStoriesSet()
            //progress bar is paused at this moment because of user's touch interaction
            //We need to resume progress in the case when this is last story in the set
            //and navigation did not happen in previous call
            progress.resume()
        }
    }

    private fun dispatchPrevious() {
        val progress = progressBar ?: return
        if (progress.hasPrevious()) {
            progress.unCompleteCurrent()
            onImageRequestCompleted = {
                post {
                    isLoading = false
                    if (isResumed) {
                        progress.start()
                    }
                }
            }
            isLoading = true
            renderContent(progress.previousIndex())
            progress.previous()
            progress.unCompleteCurrent()
        } else {
            storiesNavigationListener?.toPrevStoriesSet()
            //progress bar is paused at this moment because of user's touch interaction
            //We need to resume progress in the case when this is first story in the set
            //and navigation did not happen in previous call
            progress.resume()
        }
    }

    private fun dispatchAtIndex(index: Int) {
        check(index >= 0)
        val progress = progressBar ?: return
        progress.setCurrentItem(index - 1)
        dispatchNext()
    }

    private fun renderContent(index: Int) {
        ImageUtils.loadImage(
            img = stories[index].img,
            parent = this,
            target = contentView,
            placeholderId = R.drawable.story_placeholder,
            requestListener = imageRequestListener
        )
        for (i in index + 1..index + 2) {
            if (i == stories.size) {
                break
            }
            ImageUtils.preloadImage(stories[i].img, this, backedImageView)
        }
    }

    private fun onRightSideClick() {
        Log.d(TAG, "Right side click")
        dispatchNext()
    }

    private fun onLeftSideClick() {
        Log.d(TAG, "Left side click")
        dispatchPrevious()
    }

    private fun onPressed() {
        Log.d(TAG, "Press")
        progressBar?.pause()
    }

    private fun onLongPressReleased() {
        Log.d(TAG, "Press release")
        progressBar?.resume()
    }

    /**
     * Start animation if not already started else resume
     */
    fun resume() {
        val progress = progressBar ?: return
        if (!progress.isStarted() && !isLoading) {
            progress.start()
        } else {
            progress.resume()
        }
        isResumed = true
    }

    fun pause() {
        progressBar?.pause()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
            putInt(KEY_INDEX, progressBar?.currentIndex() ?: -1)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) {
            return super.onRestoreInstanceState(state)
        }
        val bundle = state as Bundle
        val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
        super.onRestoreInstanceState(superState)
        val savedIndex = bundle.getInt(KEY_INDEX, -1)
        if (savedIndex != -1) {
            dispatchAtIndex(savedIndex)
        }
    }

    inner class ImageRequestListener : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onImageRequestCompleted?.invoke()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onImageRequestCompleted?.invoke()
            return false
        }
    }

    companion object {
        private const val KEY_SUPER_STATE = "super_state"
        private const val KEY_INDEX = "key_index"
    }

}