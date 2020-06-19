package com.workfort.linkpreviewexample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.workfort.linkpreview.MetaData
import com.workfort.linkpreview.callback.LinkClickListener
import com.workfort.linkpreview.callback.LinkViewCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linkPreview.setClickListener(object: LinkClickListener {
            override fun onClick(view: View, metaData: MetaData) {
                inputNewUrl()
            }
        })
    }

    private fun inputNewUrl() {
        val view = layoutInflater.inflate(R.layout.prompt_input, null, false)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Load") { _,_ ->
                val etUrl = view.findViewById<EditText>(R.id.etUrl)
                val newUrl = etUrl.text.toString()
                if(newUrl.isNotEmpty()) loadNewUrl(newUrl)
            }
            .setNegativeButton("Cancel") {_,_ ->}
            .create()
            .show()
    }

    private fun loadNewUrl(url: String) {
        linkPreview.load(url, object: LinkViewCallback {
            override fun onSuccess(data: MetaData) {
                Log.d("LinkPreview", "Loaded: $url")
            }

            override fun onError(exception: Exception) {
                exception.printStackTrace()
            }
        })
    }
}