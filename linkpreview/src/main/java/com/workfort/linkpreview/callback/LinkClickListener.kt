package com.workfort.linkpreview.callback

import android.view.View
import com.workfort.linkpreview.LinkData

interface LinkClickListener {
    fun onClick(view: View, linkData: LinkData)
}