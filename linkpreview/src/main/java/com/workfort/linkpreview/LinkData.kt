package com.workfort.linkpreview

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LinkData (
    var url: String? = "",
    var imageUrl: String? = "",
    var title: String? = "",
    var description: String? = "",
    var siteName: String? = "",
    var mediaType: String? = "",
    var favicon: String? = ""
): Parcelable