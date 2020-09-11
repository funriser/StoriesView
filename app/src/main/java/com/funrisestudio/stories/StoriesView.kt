package com.funrisestudio.stories

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.funrisestudio.stories.transformer.StoryPagerTransformer

class StoriesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), StoriesNavigationListener {

    private val storiesPager = ViewPager2(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        setPageTransformer(StoryPagerTransformer())
    }

    private var fragmentManager: FragmentManager? = null
    private var adapter: StoriesAdapter? = null

    /**
     * A callback that tracks if user finished swiping the story page
     * to resume playing content of the current page
     */
    private val storiesPagerCallback = StoriesPagerCallback()

    init {
        addView(storiesPager)
    }

    fun init(activity: FragmentActivity) {
        adapter = StoriesAdapter(activity)
        fragmentManager = activity.supportFragmentManager
        storiesPager.adapter = adapter
    }

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
        val storiesAdapter = adapter?:return
        val currPosition = storiesPager.currentItem
        if (currPosition + 1 < storiesAdapter.itemCount) {
            storiesPager.setCurrentItem(currPosition + 1, true)
        }
    }

    override fun toPrevStoriesSet() {
        val currPosition = storiesPager.currentItem
        if (currPosition - 1 >= 0) {
            storiesPager.setCurrentItem(currPosition - 1, true)
        }
    }

    inner class StoriesPagerCallback: ViewPager2.OnPageChangeCallback() {
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