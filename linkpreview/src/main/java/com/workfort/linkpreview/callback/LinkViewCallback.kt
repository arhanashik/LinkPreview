package com.workfort.linkpreview.callback

import com.workfort.linkpreview.MetaData

interface LinkViewCallback {
    fun onSuccess(data: MetaData)
    fun onError(exception: Exception)
}