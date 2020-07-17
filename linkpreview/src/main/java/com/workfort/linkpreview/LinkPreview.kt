package com.workfort.linkpreview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import coil.api.load
import com.workfort.linkpreview.callback.LinkClickListener
import com.workfort.linkpreview.callback.LinkViewCallback
import com.workfort.linkpreview.callback.ParserCallback
import com.workfort.linkpreview.util.Helper
import com.workfort.linkpreview.util.LinkParser
import kotlinx.android.synthetic.main.layout_link_preview.view.lp_loader
import kotlinx.android.synthetic.main.layout_link_preview.view.lp_card
import kotlinx.android.synthetic.main.layout_link_preview.view.lp_desp
import kotlinx.android.synthetic.main.layout_link_preview.view.lp_image
import kotlinx.android.synthetic.main.layout_link_preview.view.lp_title
import kotlinx.android.synthetic.main.layout_link_preview.view.lp_url
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
    private var mCornerRadius = 10f
    private var mShadow = 10f
    private var mBackgroundColor = Color.WHITE
    private var mTitleColor = Color.BLACK
    private var mDescriptionColor = Color.BLACK
    private var mLinkColor = Color.BLACK
    private var mOriginalLinkColor = Color.BLACK

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

                //get corner radius
                mCornerRadius = getDimension(R.styleable.LinkPreview_borderRadius, Helper.dpToPx(context, 10))

                //get elevation
                mShadow = getDimension(R.styleable.LinkPreview_shadow, Helper.dpToPx(context, 5))

                //get background color
                mBackgroundColor = getColor(R.styleable.LinkPreview_backgroundColor, Color.WHITE)

                //get title color
                mTitleColor = getColor(R.styleable.LinkPreview_titleColor, Color.BLACK)

                //get description color
                mDescriptionColor = getColor(R.styleable.LinkPreview_descriptionColor,
                    Helper.getColor(context, R.color.blue_grey_300))

                //get link color
                mLinkColor = getColor(R.styleable.LinkPreview_linkColor,
                    Helper.getColor(context, R.color.light_blue_500))

                //get original link color
                mOriginalLinkColor = getColor(R.styleable.LinkPreview_originalLinkColor,
                    Helper.getColor(context, R.color.light_blue_400))
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
            PreviewStyle.LARGE -> R.layout.layout_link_preview_large
        }

        inflate(context, viewId, this)
        lp_card.radius = mCornerRadius
        lp_card.cardElevation = mShadow
        lp_card.setCardBackgroundColor(mBackgroundColor)
        lp_title.setTextColor(mTitleColor)
        lp_desp.setTextColor(mDescriptionColor)
        if(mPreviewStyle != PreviewStyle.STRIP) {
            lp_url.setTextColor(mLinkColor)
        }
        if(mPreviewStyle == PreviewStyle.DETAILS) {
            lp_original_url.setTextColor(mOriginalLinkColor)
        }
    }

    private fun setData() {
        lp_loader.visibility = View.GONE

        //set title
        if (mMetaData.title.isNullOrEmpty()) {
            lp_title.visibility = View.GONE
        } else {
            lp_title.visibility = View.VISIBLE
            lp_title.text = mMetaData.title
        }

        //set description
        if (mMetaData.description.isNullOrEmpty()) {
            lp_desp.visibility = View.GONE
        } else {
            lp_desp.visibility = View.VISIBLE
            lp_desp.text = mMetaData.description
        }

        //set url
        if(mPreviewStyle != PreviewStyle.STRIP) {
            if (mMetaData.url.isNullOrEmpty()) {
                lp_url.visibility = View.GONE
            } else {
                lp_url.visibility = View.VISIBLE
                lp_url.text = mMetaData.url
                if(mPreviewStyle != PreviewStyle.DETAILS) {
                    lp_url.paint?.isUnderlineText = true
                }
            }
        }

        //set image url
        if (mMetaData.imageUrl.isNullOrEmpty()) {
            lp_image.visibility = View.GONE
        } else {
            lp_image.visibility = View.VISIBLE
            lp_image.load(mMetaData.imageUrl)
        }

        //set favicon
        if(mPreviewStyle == PreviewStyle.BANNER || mPreviewStyle == PreviewStyle.LARGE) {
            if (mMetaData.favicon.isNullOrEmpty()) {
                lp_favicon.visibility = View.GONE
            } else {
                lp_favicon.visibility = View.VISIBLE
                lp_favicon.load(mMetaData.favicon)
            }
        }

        //set main url
        if(mPreviewStyle == PreviewStyle.DETAILS) {
            lp_original_url.text = mUrl
        }

        //set click listener
        lp_card.setOnClickListener { view ->
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
        LinkParser(url, object : ParserCallback {
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