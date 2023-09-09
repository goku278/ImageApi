package com.example.nasaimageapi.screen

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.nasaimageapi.R
import com.example.nasaimageapi.api.NasaApiService
import com.example.nasaimageapi.models.NasaImageApiResponse
import com.example.nasaimageapi.utils.ApplicationUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  MVC architecture to create an application, which fetches the Nasa image of the day,
 *  from Nasa Api.
 */

class MainActivity : AppCompatActivity() {

    /**
     *  Declaring exo player if the Nasa api returns video url, instead of image url.
     */

    lateinit var exoPlayer: Player

    private lateinit var progressDialog: ProgressDialog

    /**
     *  Permisssion code for Internet, access network state, change network state
     */

    private val PERMISSIONS_REQUEST_CODE = 123

    lateinit var applicationUtils: ApplicationUtils

    lateinit var videoLayout: RelativeLayout
    lateinit var imageLayout: RelativeLayout
    lateinit var cvCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        var parentView = findViewById<RelativeLayout>(R.id.parent)
        videoLayout = findViewById<RelativeLayout>(R.id.videoLayout)
        imageLayout = findViewById<RelativeLayout>(R.id.imageLayout)
        cvCard = findViewById<CardView>(R.id.cvCard)
        setClicks()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        applicationUtils =
            ApplicationUtils(this, applicationContext, parentView, videoLayout, imageLayout)
        initCheckPermissions()
    }

    private fun setClicks() {
        val refresh = findViewById<ImageView>(R.id.ivRefresh)

        refresh?.setOnClickListener {
            cvCard?.isVisible = false
            getImageFromNasa(imageLayout, videoLayout, cvCard)
        }
    }

    private fun getImageFromNasa(
        imageLayout: RelativeLayout,
        videoLayout: RelativeLayout,
        cvCard: CardView
    ) {

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        /**
         *
         *  At first I am creating an instance of retroit, which will further
         *  make api calls and fetch all the data in a json format.
         */


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(NasaApiService::class.java)

        /**
         * Api Key which is being passed in this api url as query parameter.
         */

        var apiKey = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

        val call = apiService.getImage(apiKey)

        /**
         *  Progress dialog will be shown incase, there is an api call to Nasa's server
         *  Accordingly, progress dialog will be closed when the server return data, either
         *  in success, error states.
         */

        progressDialog.show()

        call.enqueue(object : Callback<NasaImageApiResponse> {
            override fun onResponse(
                call: Call<NasaImageApiResponse>,
                response: Response<NasaImageApiResponse>
            ) {

//                Pr
                /**
                 *  Fetching the data in the json format as response from the Nasa server.
                 *  Parsing the data using a kotlin model class, and displaying the
                 *  relevant image, title, date and description data, accordingly.
                 */


                val imageView = imageLayout.findViewById<ImageView>(R.id.ivNasaImage)
                val title = imageLayout.findViewById<TextView>(R.id.tvTitle)
                val date = imageLayout.findViewById<TextView>(R.id.tvDate)
                val desc = imageLayout.findViewById<TextView>(R.id.tvDesc)

                if (response.isSuccessful) {
                    val apodResponse = response.body()

                    var isVideo = apodResponse?.media_type?.trim()

                    Log.d("", "isVideo => $isVideo")

                    if (isVideo?.contains("image") == true) {

                        /**
                         *  Incase the json data returns media_type = "image"
                         *  then I am letting one of the layout for displaying
                         *  the image of the day, it's visibility to true.
                         *  videoLayout's visibility to false
                         */

                        imageLayout?.isVisible = true
                        cvCard?.isVisible = true
                        videoLayout?.isVisible = false

                        if (apodResponse != null || apodResponse?.hdurl != null) {
                            Glide.with(applicationContext).load(apodResponse?.hdurl).into(imageView)
                        }
                        if (apodResponse != null || apodResponse?.title != null) {
                            title.setText("Title : " + apodResponse.title?.trim())
                        }
                        if (apodResponse != null || apodResponse?.date != null) {
                            date.setText("Date : " + apodResponse?.date?.trim())
                        }
                        if (apodResponse != null || apodResponse?.explanation != null) {
                            desc.setText("Description : " + apodResponse?.explanation?.trim())
                        }
                    } else {

                        /**
                         *  Incase the Nasa's api does not return media_type = "image"
                         *  Then I am enabling videoLayout to true
                         *  Disabling imageLayout to false
                         */

                        imageLayout?.isVisible = false
                        videoLayout?.isVisible = true

                        var videoUrl =
                            "${apodResponse?.hdurl}"

                        var playerView: StyledPlayerView = findViewById(R.id.videoPlayer)
                        playerView.player = exoPlayer
                        var mediaItem: MediaItem = MediaItem.fromUri(videoUrl);
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true

                    }
                    progressDialog.dismiss()
                } else {

                    /**
                     *  Incase of error, error will be displayed in the snackbar.
                     */

                    applicationUtils.showSnackBar("Data fetching failed")
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<NasaImageApiResponse>, t: Throwable) {
                // Handle the network error
                progressDialog.dismiss()
            }
        })
    }

    /**
     *  initCheckPermission()
     *  if permission for Internet, aAccess Network State, Change Network State is not given,
     *  then it can be given via these code.
     */

    private fun initCheckPermissions() {
        if (checkPermissions()) {
            // Permissions are already granted, proceed with your network operations
            // You can use the internet, change network state, and access network state here
            val internetConnection = isDeviceConnectedToInternet(applicationContext)
            Log.d("MainActivity", "internet connection $internetConnection")
            if (internetConnection) {
                /**
                 *  Incase the mobile is connected to internet, then we go for fetching the
                 *  image of the day from Nasa'a api.
                 */
                getImageFromNasa(imageLayout, videoLayout, cvCard)
            } else {
                showNoInternetAvailable()
            }
        } else {
            // Request permissions from the user
            requestPermissions()
        }
    }

    private fun showNoInternetAvailable() {
        /**
         * Incase the mobile is not having an active internet connection
         */

        applicationUtils.showSnackBar("Please turn on mobile data or connect to wifi before proceed.")
    }

    fun isDeviceConnectedToInternet(context: Context): Boolean {

        /**
         *  This method checks if the device is connected to an active internet connection.
         *  Return boolean value for connected or not-connected
         */

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
            return activeNetwork != null &&
                    (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun checkPermissions(): Boolean {
        /**
         * Permission checking is initiated
         */
        val internetPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        )

        val changeNetworkStatePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CHANGE_NETWORK_STATE
        )

        val accessNetworkStatePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        return (internetPermission == PackageManager.PERMISSION_GRANTED &&
                changeNetworkStatePermission == PackageManager.PERMISSION_GRANTED &&
                accessNetworkStatePermission == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            ) {
                // Permissions granted, proceed with your network operations
            } else {
                // Permissions denied, handle the case where the user didn't grant the required permissions
                requestPermissions()
            }
        }
    }

    /**
     * These are the various overridden methods, inside these methods
     * exo player's states are defined.
     */

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.playWhenReady = false
        exoPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.playWhenReady = false
        exoPlayer.release() // Release the player when the activity is destroyed
    }
}

