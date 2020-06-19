package com.workfort.linkpreview.callback

import com.workfort.linkpreview.MetaData

interface ParserCallback {
    fun onData(metaData: MetaData)
    fun onError(exception: Exception)
}