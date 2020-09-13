package com.funrisestudio.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class StoriesAdapter(
    activity: FragmentActivity,
    private val styling: StoriesSetView.Styling? = null
) : FragmentStateAdapter(activity) {

    var stories: List<List<StoryContent>> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun createFragment(position: Int): Fragment =
        StoriesSetFragment.newInstance(stories[position], styling)

    override fun getItemCount(): Int = stories.size

}