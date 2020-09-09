package com.funrisestudio.stories

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener

object ImageUtils {

    fun loadImage(img: String, parent: View, target: ImageView, requestListener: RequestListener<Drawable>? = null) {
        Glide.with(parent)
            .load(img)
            .listener(requestListener)
            .into(target)
    }

    fun preloadImage(img: String, parent: View, target: ImageView) {
        Glide.with(parent)
            .load(img)
            .into(target)
    }

}