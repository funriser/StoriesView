package com.funrisestudio.stories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.funrisestudio.stories.transformer.StoryPagerTransformer

class MainActivity : AppCompatActivity() {

    private val storiesPager: ViewPager2 by lazy {
        findViewById(R.id.storiesPager)
    }

    /**
     * A callback that tracks if user finished swiping the story page
     * to resume playing content of the current page
     */
    private val storiesPagerCallback = StoriesPagerCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        initStoriesView()
    }

    private fun initStoriesView() {
        storiesPager.adapter = StoriesAdapter(this).apply {
            stories = listOf(
                //1
                listOf(
                    StoryContent("https://wallpaperaccess.com/full/695978.jpg"),
                    StoryContent("https://i.pinimg.com/originals/d3/f2/e6/d3f2e6f4da4bfc47a96a0e8aae1fffd4.jpg"),
                    StoryContent("https://archive-media-1.nyafuu.org/wg/image/1384/61/1384619944965.jpg"),
                    StoryContent("https://wallpapercave.com/wp/wp5245094.jpg"),
                    StoryContent("https://cutewallpaper.org/21/1080x1920-4k-wallpaper/1080x1920-4k-Wallpaper-Nature-Fitrinis-Wallpaper.jpg"),
                ),
                listOf(
                    StoryContent("https://i.pinimg.com/originals/58/ac/71/58ac71573f863994bf554b391788ee54.png"),
                    StoryContent("https://cutewallpaper.org/21/1080x1920-4k-wallpaper/1080x1920-4k-Wallpaper-Nature-Fitrinis-Wallpaper.jpg")
                ),
                //2
                listOf(
                    StoryContent("https://wallpapercave.com/wp/wp5245094.jpg"),
                    StoryContent("https://cutewallpaper.org/21/1080x1920-4k-wallpaper/1080x1920-4k-Wallpaper-Nature-Fitrinis-Wallpaper.jpg"),
                    StoryContent("https://i.pinimg.com/originals/d3/f2/e6/d3f2e6f4da4bfc47a96a0e8aae1fffd4.jpg"),
                    StoryContent("https://i.pinimg.com/originals/58/ac/71/58ac71573f863994bf554b391788ee54.png"),
                    StoryContent("https://archive-media-1.nyafuu.org/wg/image/1384/61/1384619944965.jpg"),
                    StoryContent("https://www.wallpapertip.com/wmimgs/83-836224_1080-x-1920-wallpapers-vertical-hd-data-src.jpg"),
                    StoryContent("https://wallpapercave.com/wp/wp2890925.jpg"),
                    StoryContent("https://wallpaperaccess.com/full/695978.jpg"),
                    StoryContent("https://phonewallpaperhd.com/wp-content/uploads/2019/09/Space-Wallpaper-For-Phone-HD.jpg")
                ),
                //3
                listOf(
                    StoryContent("https://wallpapercave.com/wp/wp2890925.jpg"),
                    StoryContent("https://wallpaperaccess.com/full/695978.jpg"),
                    StoryContent("https://phonewallpaperhd.com/wp-content/uploads/2019/09/Space-Wallpaper-For-Phone-HD.jpg")
                ),
                //4
                listOf(
                    StoryContent("https://wallpapercave.com/wp/wp5245094.jpg"),
                    StoryContent("https://cutewallpaper.org/21/1080x1920-4k-wallpaper/1080x1920-4k-Wallpaper-Nature-Fitrinis-Wallpaper.jpg"),
                    StoryContent("https://i.pinimg.com/originals/d3/f2/e6/d3f2e6f4da4bfc47a96a0e8aae1fffd4.jpg")
                )
            )
        }
        storiesPager.registerOnPageChangeCallback(storiesPagerCallback)
        storiesPager.setPageTransformer(StoryPagerTransformer())
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesPager.unregisterOnPageChangeCallback(storiesPagerCallback)
    }

    inner class StoriesPagerCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == 0) {
                val currentItemTag = "f" + storiesPager.currentItem
                val currFragment =
                    supportFragmentManager.findFragmentByTag(currentItemTag) as StoriesFragment
                currFragment.resumeProgress()
            }
        }
    }

}