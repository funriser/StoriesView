package com.funrisestudio.stories

import android.view.View
import androidx.core.view.doOnNextLayout


//View pager compat version of androidX version of doOnLayout
//Original method is not working because of wrong flags
//that determine if view is laid out while using view pager
inline fun View.doOnLayout(crossinline action: (view: View) -> Unit) {
    if (hasDimensions() && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout {
            action(it)
        }
    }
}

fun View.hasDimensions() = width > 0 && height > 0