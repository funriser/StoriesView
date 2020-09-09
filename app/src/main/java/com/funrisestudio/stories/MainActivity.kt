package com.funrisestudio.stories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private val storiesView: StoriesContentView by lazy {
        findViewById(R.id.vStories)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        initStoriesView()
    }

    private fun initStoriesView() {
        storiesView.setUp(listOf(
            StoryContent("https://wallpapercave.com/wp/wp5245094.jpg"),
            StoryContent("https://cutewallpaper.org/21/1080x1920-4k-wallpaper/1080x1920-4k-Wallpaper-Nature-Fitrinis-Wallpaper.jpg"),
            StoryContent("https://i.pinimg.com/originals/d3/f2/e6/d3f2e6f4da4bfc47a96a0e8aae1fffd4.jpg"),
            StoryContent("https://i.pinimg.com/originals/58/ac/71/58ac71573f863994bf554b391788ee54.png"),
            StoryContent("https://archive-media-1.nyafuu.org/wg/image/1384/61/1384619944965.jpg"),
            StoryContent("https://www.wallpapertip.com/wmimgs/83-836224_1080-x-1920-wallpapers-vertical-hd-data-src.jpg"),
            StoryContent("https://wallpapercave.com/wp/wp2890925.jpg"),
            StoryContent("https://wallpaperaccess.com/full/695978.jpg"),
            StoryContent("https://phonewallpaperhd.com/wp-content/uploads/2019/09/Space-Wallpaper-For-Phone-HD.jpg"))
        )
    }

}