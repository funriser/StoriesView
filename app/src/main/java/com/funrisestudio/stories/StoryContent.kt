package com.funrisestudio.stories

import android.os.Parcel
import android.os.Parcelable

data class StoryContent(val img: String): Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoryContent> {
        override fun createFromParcel(parcel: Parcel): StoryContent {
            return StoryContent(parcel)
        }

        override fun newArray(size: Int): Array<StoryContent?> {
            return arrayOfNulls(size)
        }
    }

}