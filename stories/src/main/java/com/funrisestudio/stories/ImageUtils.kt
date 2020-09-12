package com.funrisestudio.stories

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener

object ImageUtils {

    fun loadImage(
        img: String,
        parent: View,
        target: ImageView,
        @DrawableRes placeholderId: Int,
        requestListener: RequestListener<Drawable>? = null
    ) {
        Glide.with(parent)
            .load(img)
            .placeholder(placeholderId)
            .listener(requestListener)
            .into(target)
    }

    fun preloadImage(img: String, parent: View, target: ImageView) {
        Glide.with(parent)
            .load(img)
            .into(target)
    }

}