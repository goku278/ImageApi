package com.example.nasaimageapi.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.google.android.exoplayer2.Player
import com.google.android.material.snackbar.Snackbar

class ApplicationUtils {

    private val PERMISSIONS_REQUEST_CODE = 123

    lateinit var exoPlayer: Player
    var videoUrl = "";

    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var videoLayout: RelativeLayout
    private lateinit var imageLayout: RelativeLayout

    constructor(
        activity: Activity,
        context: Context,
        relativeLayout: RelativeLayout,
        videoLayout: RelativeLayout,
        imageLayout: RelativeLayout
    ) {
        this.activity = activity
        this.context = context
        this.relativeLayout = relativeLayout
        this.videoLayout = videoLayout
        this.imageLayout = imageLayout
    }

    /* fun isImageUrl(urlString: String): Boolean {
         try {
             val url = URL(urlString)
             val connection = url.openConnection() as HttpURLConnection
             connection.requestMethod = "GET"
             val contentType = connection.contentType

             if (contentType != null && contentType.startsWith("image")) {
                 // Check if the content type suggests it's an image
                 return true
             }
         } catch (e: Exception) {
             // Handle exceptions if any occur during the request
             e.printStackTrace()
         }
         return false
     }*/

    fun showSnackBar(msg: String?) {
        val rootView =
            activity.findViewById<View>(android.R.id.content) // or any other View you want to anchor the Snackbar to

        val message = "Please turn on your mobile data or wifi to proceed!"
        val duration = Snackbar.LENGTH_SHORT

        Snackbar.make(rootView, message, duration).show()
    }
}