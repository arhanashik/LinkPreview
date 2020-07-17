package com.workfort.linkpreview.callback

import com.workfort.linkpreview.LinkData

interface LinkViewCallback {
    fun onSuccess(data: LinkData)
    fun onError(exception: Exception)
}