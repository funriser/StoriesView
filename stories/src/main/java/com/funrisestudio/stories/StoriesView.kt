package com.funrisestudio.stories

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.funrisestudio.stories.transformer.StoryPagerTransformer

class StoriesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), StoriesNavigationListener {

    private lateinit var styling: StoriesSetView.Styling

    private val storiesPager = ViewPager2(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        //disable overscroll effect
        (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        setPageTransformer(StoryPagerTransformer())
    }

    private var fragmentManager: FragmentManager? = null
    private var adapter: StoriesAdapter? = null

    private var onStoriesCompleted: (() -> Unit)? = null

    /**
     * A callback that tracks if user finished swiping the story page
     * to resume playing content of the current page
     */
    private val storiesPagerCallback = StoriesPagerCallback()

    init {
        addView(storiesPager)
        val bgColor = ContextCompat.getColor(context, R.color.black)
        background = ColorDrawable(bgColor)
        setStyling(attrs)
    }

    /**
     * Sets attributes from attribute set
     * or applies default styling if attribute is not specified in the given set
     *
     * @param attrs attribute set passed to this view
     */
    private fun setStyling(attrs: AttributeSet?) {
        val ta = if (attrs != null) {
            context.theme.obtainStyledAttributes(attrs, R.styleable.StoriesView, 0, 0)
        } else {
            null
        }
        try {
            val defProgressHeight = context.resources.getDimensionPixelSize(R.dimen.height_stories)
            val progressHeight = ta?.getDimensionPixelSize(R.styleable.StoriesView_progressHeight, defProgressHeight)
                ?: defProgressHeight

            val defProgressMrgTop = context.resources.getDimensionPixelSize(R.dimen.mrg_progress_top)
            val progressMarginTop = ta?.getDimensionPixelSize(R.styleable.StoriesView_progressMarginTop, defProgressMrgTop)
                ?: defProgressMrgTop

            val defProgressMrgLeft = context.resources.getDimensionPixelSize(R.dimen.mrg_progress_bar_side)
            val progressMarginLeft = ta?.getDimensionPixelSize(R.styleable.StoriesView_progressMarginLeft, defProgressMrgLeft)
                ?: defProgressMrgLeft

            val defProgressMrgRight = context.resources.getDimensionPixelSize(R.dimen.mrg_progress_bar_side)
            val progressMarginRight = ta?.getDimensionPixelSize(R.styleable.StoriesView_progressMarginRight, defProgressMrgRight)
                ?: defProgressMrgRight

            val defProgressSpacing = context.resources.getDimensionPixelSize(R.dimen.mrg_progress_spacing)
            val progressSpacing = ta?.getDimensionPixelSize(R.styleable.StoriesView_progressSpacing, defProgressSpacing)
                ?: defProgressSpacing

            val defProgressColor = ContextCompat.getColor(context, R.color.white)
            val progressColor = ta?.getColor(R.styleable.StoriesView_progressColor, defProgressColor)
                ?: defProgressColor

            val defProgressBgColor = ContextCompat.getColor(context, R.color.grey)
            val progressBackgroundColor = ta?.getColor(R.styleable.StoriesView_progressBackgroundColor, defProgressBgColor)
                ?: defProgressBgColor

            styling = StoriesSetView.Styling(
                progressHeight,
                progressMarginTop,
                progressMarginLeft,
                progressMarginRight,
                StoriesSetProgressBar.Styling(
                    progressSpacing,
                    ProgressView.Styling(
                        progressColor,
                        progressBackgroundColor
                    )
                )
            )
        } finally {
            ta?.recycle()
        }
    }

    /**
     * Initialize main components with given fragment activity.
     * This method is required to call before starting to work with view.
     *
     * You can customize how the view looks like using styling params
     *
     * @param activity activity that will host StoriesView
     *
     * @param progressHeight The height of the progress bar
     * @param progressMarginTop Margin between the top of the view and the top of progress bar
     * @param progressMarginLeft Margin between the left side of the view
     * and the left side of progress bar
     * @param progressMarginRight Margin between the right side of the view
     * and the right side of progress bar
     * @param progressSpacing Progress bar consists of number of elements each one representing the
     * progress of one story. This param defines margin between those elements.
     * @param progressColor Color of the progress line
     * @param progressBackgroundColor Color of the progress' line background
     */
    fun init(
        activity: FragmentActivity,
        progressHeight: Int = styling.progressHeight,
        progressMarginTop: Int = styling.progressMarginTop,
        progressMarginLeft: Int = styling.progressMarginLeft,
        progressMarginRight: Int = styling.progressMarginRight,
        progressSpacing: Int = styling.progressBarStyling!!.progressSpacing,
        progressColor: Int = styling.progressBarStyling!!.progressStyling!!.progressColor,
        progressBackgroundColor: Int = styling.progressBarStyling!!.progressStyling!!.progressBackgroundColor
    ) {
        styling = StoriesSetView.Styling(
            progressHeight,
            progressMarginTop,
            progressMarginLeft,
            progressMarginRight,
            StoriesSetProgressBar.Styling(
                progressSpacing,
                ProgressView.Styling(
                    progressColor,
                    progressBackgroundColor
                )
            )
        )
        init(activity)
    }

    /**
     * Initialize main components with given fragment activity.
     * This method is required to call before starting to work with view.
     *
     * @param activity activity that will host StoriesView
     */
    fun init(activity: FragmentActivity) {
        adapter = StoriesAdapter(activity, styling)
        fragmentManager = activity.supportFragmentManager
        storiesPager.adapter = adapter
    }

    /**
     * Bind a dataset that will be used to display images for stories
     *
     * @param stories List of lists containing StoryContent object.
     * Two-dimension list needed because a stories view consists of pages
     * and each of those pages contains multiple stories.
     */
    fun setStories(stories: List<List<StoryContent>>) {
        adapter?.stories = stories
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        storiesPager.registerOnPageChangeCallback(storiesPagerCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        storiesPager.unregisterOnPageChangeCallback(storiesPagerCallback)
    }

    override fun toNextStoriesSet() {
        val storiesAdapter = adapter ?: return
        val currPosition = storiesPager.currentItem
        if (currPosition + 1 < storiesAdapter.itemCount) {
            storiesPager.setCurrentItem(currPosition + 1, true)
        } else {
            onStoriesCompleted?.invoke()
        }
    }

    override fun toPrevStoriesSet() {
        val currPosition = storiesPager.currentItem
        if (currPosition - 1 >= 0) {
            storiesPager.setCurrentItem(currPosition - 1, true)
        }
    }

    /**
     * Set listener which will be invoked when user finishes watching all the stories
     *
     * @param listener listener function
     */
    fun setOnStoryCompletedListener(listener: () -> Unit) {
        this.onStoriesCompleted = listener
    }

    inner class StoriesPagerCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == 0) {
                val currentItemTag = "f" + storiesPager.currentItem
                val currFragment =
                    fragmentManager?.findFragmentByTag(currentItemTag) as StoriesSetFragment?
                currFragment?.resumeProgress()
            }
        }
    }

}