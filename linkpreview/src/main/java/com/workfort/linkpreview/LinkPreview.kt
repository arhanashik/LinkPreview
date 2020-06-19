package com.workfort.linkpreview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import coil.api.load
import com.workfort.linkpreview.callback.LinkClickListener
import com.workfort.linkpreview.callback.LinkViewCallback
import com.workfort.linkpreview.callback.ParserCallback
import com.workfort.linkpreview.util.UrlParser
import kotlinx.android.synthetic.main.layout_link_preview.view.loader
import kotlinx.android.synthetic.main.layout_link_preview.view.rich_link_card
import kotlinx.android.synthetic.main.layout_link_preview.view.rich_link_desp
import kotlinx.android.synthetic.main.layout_link_preview.view.rich_link_image
import kotlinx.android.synthetic.main.layout_link_preview.view.rich_link_title
import kotlinx.android.synthetic.main.layout_link_preview.view.rich_link_url
import kotlinx.android.synthetic.main.layout_link_preview_banner.view.*
import kotlinx.android.synthetic.main.layout_link_preview_details.view.*

class LinkPreview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    var mMetaData =  MetaData()
    private var mUrl = ""
    private var mPreviewStyle = PreviewStyle.SIMPLE
    private var mIsDefaultClick = true
    private var mListener: LinkClickListener? = null

    init {
        readAttrs(attrs)

        initView()

        if(mUrl.isNotEmpty()) load(mUrl)
    }

    private fun readAttrs(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LinkPreview,
            0, 0).apply {
            try {
                //get the Preview style
                val previewStyleInt = getInteger(
                    R.styleable.LinkPreview_previewStyle, PreviewStyle.SIMPLE.id
                )
                mPreviewStyle = PreviewStyle.values()[previewStyleInt]

                //get default url
                getString(R.styleable.LinkPreview_url)?.let { mUrl = it }

                //get defaultClick enable state
                mIsDefaultClick = getBoolean(R.styleable.LinkPreview_enableDefaultClick, true)
            } finally {
                recycle()
            }
        }
    }

    private fun initView() {
        val viewId = when(mPreviewStyle) {
            PreviewStyle.SIMPLE -> R.layout.layout_link_preview
            PreviewStyle.BANNER -> R.layout.layout_link_preview_banner
            PreviewStyle.STRIP -> R.layout.layout_link_preview_strip
            PreviewStyle.DETAILS -> R.layout.layout_link_preview_details
        }

        inflate(context, viewId, this)
    }

    private fun setData() {
        loader.visibility = View.GONE

        //set title
        if (mMetaData.title.isNullOrEmpty()) {
            rich_link_title.visibility = View.GONE
        } else {
            rich_link_title.visibility = View.VISIBLE
            rich_link_title.text = mMetaData.title
        }

        //set description
        if (mMetaData.description.isNullOrEmpty()) {
            rich_link_desp.visibility = View.GONE
        } else {
            rich_link_desp.visibility = View.VISIBLE
            rich_link_desp.text = mMetaData.description
        }

        //set url
        if(mPreviewStyle != PreviewStyle.STRIP) {
            if (mMetaData.url.isNullOrEmpty()) {
                rich_link_url.visibility = View.GONE
            } else {
                rich_link_url.visibility = View.VISIBLE
                rich_link_url.text = mMetaData.url
                if(mPreviewStyle != PreviewStyle.DETAILS) {
                    rich_link_url.paint?.isUnderlineText = true
                }
            }
        }

        //set image url
        if (mMetaData.imageUrl.isNullOrEmpty()) {
            rich_link_image.visibility = View.GONE
        } else {
            rich_link_image.visibility = View.VISIBLE
            rich_link_image.load(mMetaData.imageUrl)
        }

        //set favicon for Skype
        if(mPreviewStyle == PreviewStyle.BANNER) {
            if (mMetaData.favicon.isNullOrEmpty()) {
                rich_link_favicon.visibility = View.GONE
            } else {
                rich_link_favicon.visibility = View.VISIBLE
                rich_link_favicon.load(mMetaData.favicon)
            }
        }

        //set main url for telegram
        if(mPreviewStyle == PreviewStyle.DETAILS) {
            rich_link_original_url.text = mUrl
        }

        //set click listener
        rich_link_card.setOnClickListener { view ->
            mListener?.onClick(view, mMetaData)
            if (mListener == null && mIsDefaultClick) onLinkClicked()
        }
    }

    private fun onLinkClicked() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))
        context.startActivity(intent)
    }

    fun setDefaultClickListener(isDefault: Boolean) {
        mIsDefaultClick = isDefault
    }

    fun setClickListener(listener: LinkClickListener) {
        this.mListener = listener
    }

    fun loadFromMetaData(metaData: MetaData) {
        this.mMetaData = metaData
        setData()
    }

    fun load(url: String, callback: LinkViewCallback? = null) {
        mUrl = url
        UrlParser(url, object : ParserCallback {
            override fun onData(metaData: MetaData) {
                mMetaData = metaData
                setData()
                callback?.onSuccess(metaData)
            }

            override fun onError(exception: Exception) {
                callback?.onError(exception)
            }
        }).parse()
    }
}