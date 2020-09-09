package com.funrisestudio.stories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
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
        storiesView.setUp(listOf(StoryContent(""), StoryContent(""), StoryContent("")))
    }

}