package com.funrisestudio.stories

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

private const val DEF_STORY_DURATION_MILLIS = 10 * 1000L
private const val LONG_PRESS_DELAY_MILLIS = 800L
private const val TAG = "StoriesContent"

class StoriesContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val contentView: ImageView = ImageView(context).also {
        it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        it.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(it)
    }
    private var pressTimestampMillis = 0L
    private var progressBar: StoriesSequenceView? = null
    private var stories: List<StoryContent> = emptyList()

    private val imageRequestListener = ImageRequestListener()
    private var onImageRequestCompleted: (() -> Unit)? = null

    fun setUp(stories: List<StoryContent>, storyDurationMillis: Long = DEF_STORY_DURATION_MILLIS) {
        this.stories = stories
        progressBar?.let {
            removeView(it)
        }
        initProgressBar(stories.size, storyDurationMillis)
        dispatchNext()
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
            onImageRequestCompleted = progress::next
            renderContent(progress.nextIndex())
        } else {
            progress.resume()
        }
    }

    private fun dispatchPrevious() {
        val progress = progressBar ?: return
        if (progress.hasPrevious()) {
            progress.unCompleteCurrent()
            onImageRequestCompleted = progress::previous
            renderContent(progress.previousIndex())
        } else {
            progress.resume()
        }
    }

    private fun renderContent(index: Int) {
        loadImage(stories[index].img, this, contentView, imageRequestListener)
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

    inner class ImageRequestListener: RequestListener<Drawable> {
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

}