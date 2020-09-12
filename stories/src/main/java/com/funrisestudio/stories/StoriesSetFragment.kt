package com.funrisestudio.stories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class StoriesSetFragment: Fragment() {

    private lateinit var stories: List<StoryContent>
    private lateinit var storiesSetView: StoriesSetView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stories = arguments!!.getParcelableArrayList(KEY_STORIES)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stories_set, container, false).also {
            storiesSetView = it as StoriesSetView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storiesSetView.setUp(stories)
    }

    override fun onResume() {
        super.onResume()
        //find parent view that listens for story completed event
        //and invoke callback
        var parentView = view?.parent
        while (parentView != null) {
            if (parentView is StoriesNavigationListener) {
                storiesSetView.storiesNavigationListener = parentView
                break
            }
            parentView = parentView.parent
        }
        resumeProgress()
    }

    override fun onPause() {
        super.onPause()
        storiesSetView.pause()
    }

    fun resumeProgress() {
        storiesSetView.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("StoriesFragment", "Stories destroyed")
    }

    companion object {

        private const val KEY_STORIES = "key_stories"

        fun newInstance(stories: List<StoryContent>): StoriesSetFragment {
            return StoriesSetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(KEY_STORIES, ArrayList(stories))
                }
            }
        }

    }

}