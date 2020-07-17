package com.workfort.linkpreview.util

import android.webkit.URLUtil
import com.workfort.linkpreview.MetaData
import com.workfort.linkpreview.callback.ParserCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class LinkParser(private val url: String, private val callback: ParserCallback) {

    /*
    * Start parsing the html from the given url
    * */
    fun parse() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    val doc = Jsoup.connect(url).timeout(30 * 1000).get()
                    val metaData = parseDoc(url, doc)

                    withContext(Dispatchers.Main) { callback.onData(metaData) }
                } catch (ex: IllegalArgumentException) {
                    ex.printStackTrace()
                    withContext(Dispatchers.Main) {
                        callback.onError(Exception("Invalid url: $url. ${ex.localizedMessage}"))
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    withContext(Dispatchers.Main) {
                        callback.onError(
                            Exception("No Html Received from $url. ${ex.localizedMessage}")
                        )
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    withContext(Dispatchers.Main) {
                        callback.onError(Exception(ex))
                    }
                }
            }
        }
    }

    /*
    * Parse the document found from the given url
    * */
    private suspend fun parseDoc(url: String, doc: Document): MetaData = withContext(Dispatchers.Default) {
        val metaData = MetaData()
        val elements = doc.getElementsByTag("meta")

        //getTitle
        metaData.title = getTitle(doc)
        //getDescription
        metaData.description = getDescription(doc)
        // getMediaType
        metaData.mediaType = getMediaType(doc)
        //getImages
        metaData.imageUrl = getImageUrl(url, doc)
        //getFavicon
        metaData.favicon = getFavicon(url, doc)
        //get url and siteName
        for (element in elements) {
            if (element.hasAttr("property")) {
                val strProperty: String = element.attr("property").toString().trim()
                if (strProperty == "og:url") {
                    metaData.url = element.attr("content").toString()
                }
                if (strProperty == "og:site_name") {
                    metaData.siteName = element.attr("content").toString()
                }
            }
        }
        if (metaData.url.isNullOrEmpty()) {
            var uri: URI? = null
            try {
                uri = URI(url)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            metaData.url = uri?.host
        }

        metaData
    }

    /*
    * Get the title from the document
    * */
    private fun getTitle(doc: Document): String {
        val title = doc.select("meta[property=og:title]").attr("content")

        return if(title.isNullOrEmpty()) doc.title() else title
    }

    /*
    * Get document description
    * */
    private fun getDescription(doc: Document): String {
        var description = doc.select("meta[name=description]").attr("content")
        if (description.isNullOrEmpty()) {
            description = doc.select("meta[name=Description]").attr("content")
        }
        if (description.isNullOrEmpty()) {
            description = doc.select("meta[property=og:description]").attr("content")
        }

        return if(description.isNullOrEmpty()) "" else description
    }

    /*
    * Get mediaType
    * */
    private fun getMediaType(doc: Document): String {
        val mediaTypes = doc.select("meta[name=medium]")
        return if (mediaTypes.size > 0) {
            val media = mediaTypes.attr("content")
            if (media == "image") "photo" else media
        } else {
            doc.select("meta[property=og:type]").attr("content")
        }
    }

    /*
    * Get image url
    * */
    private fun getImageUrl(url: String, doc: Document): String? {
        var imageUrl: String? = null

        val imageElements = doc.select("meta[property=og:image]")
        if (imageElements.size > 0) {
            val image = imageElements.attr("content")
            if (!image.isNullOrEmpty()) {
                imageUrl = resolveURL(url, image)
            }
        }

        return if (imageUrl.isNullOrEmpty()) {
            var src: String = doc.select("link[rel=image_src]").attr("href")
            if (src.isNotEmpty()) resolveURL(url, src) else {
                src = doc.select("link[rel=apple-touch-icon]").attr("href")
                if (src.isNotEmpty()) resolveURL(url, src) else {
                    src = doc.select("link[rel=icon]").attr("href")
                    return resolveURL(url, src)
                }
            }
        } else imageUrl
    }

    /*
    * Get favicon
    * */
    private fun getFavicon(url: String, doc: Document): String? {
        var src = doc.select("link[rel=apple-touch-icon]").attr("href")
        return if (!src.isNullOrEmpty()) resolveURL(url, src) else {
            src = doc.select("link[rel=icon]").attr("href")
            return resolveURL(url, src)
        }
    }

    private fun resolveURL(url: String, part: String): String? {
        return if (URLUtil.isValidUrl(part)) part else {
            var baseUri: URI? = null
            try {
                baseUri = URI(url)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            baseUri = baseUri?.resolve(part)
            baseUri.toString()
        }
    }
}