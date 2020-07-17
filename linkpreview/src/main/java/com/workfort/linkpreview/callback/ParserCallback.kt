package com.workfort.linkpreview.callback

import com.workfort.linkpreview.LinkData

interface ParserCallback {
    fun onData(linkData: LinkData)
    fun onError(exception: Exception)
}