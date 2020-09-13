package com.funrisestudio.stories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class StoriesSetFragment: Fragment() {

    private var storiesStyling: StoriesSetView.Styling? = null

    private lateinit var stories: List<StoryContent>
    private lateinit var storiesSetView: StoriesSetView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = requireNotNull(arguments)
        stories = args.getParcelableArrayList(KEY_STORIES)!!
        storiesStyling = args.getParcelable(KEY_STORIES_STYLING)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storiesSetView = StoriesSetView(requireContext(), styling = storiesStyling).also {
            it.id = R.id.vStories
        }
        return storiesSetView
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
        private const val KEY_STORIES_STYLING = "key_stories_styling"

        fun newInstance(
            stories: List<StoryContent>,
            storiesStyling: StoriesSetView.Styling? = null
        ): StoriesSetFragment {
            return StoriesSetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(KEY_STORIES, ArrayList(stories))
                    if (storiesStyling != null) {
                        putParcelable(KEY_STORIES_STYLING, storiesStyling)
                    }
                }
            }
        }

    }

}