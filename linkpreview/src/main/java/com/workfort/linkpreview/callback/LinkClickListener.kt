package com.workfort.linkpreview.callback

import android.view.View
import com.workfort.linkpreview.MetaData

interface LinkClickListener {
    fun onClick(view: View, metaData: MetaData)
}