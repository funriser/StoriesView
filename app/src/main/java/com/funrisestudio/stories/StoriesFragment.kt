package com.funrisestudio.stories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class StoriesFragment: Fragment() {

    private lateinit var stories: List<StoryContent>
    private lateinit var storiesView: StoriesContentView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stories = arguments!!.getParcelableArrayList(KEY_STORIES)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stories, container, false).also {
            storiesView = it as StoriesContentView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storiesView.setUp(stories)
    }

    override fun onResume() {
        super.onResume()
        storiesView.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("StoriesFragment", "Stories destroyed")
    }

    companion object {

        private const val KEY_STORIES = "key_stories"

        fun newInstance(stories: List<StoryContent>): StoriesFragment {
            return StoriesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(KEY_STORIES, ArrayList(stories))
                }
            }
        }

    }

}