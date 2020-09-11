package com.funrisestudio.stories.transformer

import android.view.View

class StoryPagerTransformer(
    private val distanceMultiplier: Int = 20
): BaseTransformer() {

    public override val isPagingEnabled: Boolean = true

    override fun onTransform(page: View, position: Float) {
        page.cameraDistance = (page.width * distanceMultiplier).toFloat()
        page.pivotX = if (position < 0f) page.width.toFloat() else 0f
        page.pivotY = page.height * 0.5f
        page.rotationY = 90f * position
    }

}