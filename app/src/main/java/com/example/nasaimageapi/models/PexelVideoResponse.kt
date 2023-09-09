package com.example.nasaimageapi.models

import com.google.gson.annotations.SerializedName

data class PexelVideoResponse (

    @SerializedName("page"          ) var page         : Int?              = null,
    @SerializedName("per_page"      ) var perPage      : Int?              = null,
    @SerializedName("videos"        ) var videos       : ArrayList<Videos> = arrayListOf(),
    @SerializedName("total_results" ) var totalResults : Int?              = null,
    @SerializedName("next_page"     ) var nextPage     : String?           = null,
    @SerializedName("url"           ) var url          : String?           = null

)

data class User (

    @SerializedName("id"   ) var id   : Int?    = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("url"  ) var url  : String? = null

)

data class VideoFiles (

    @SerializedName("id"        ) var id       : Int?    = null,
    @SerializedName("quality"   ) var quality  : String? = null,
    @SerializedName("file_type" ) var fileType : String? = null,
    @SerializedName("width"     ) var width    : Int?    = null,
    @SerializedName("height"    ) var height   : Int?    = null,
    @SerializedName("fps"       ) var fps      : Double? = null,
    @SerializedName("link"      ) var link     : String? = null

)

data class VideoPictures (

    @SerializedName("id"      ) var id      : Int?    = null,
    @SerializedName("nr"      ) var nr      : Int?    = null,
    @SerializedName("picture" ) var picture : String? = null

)

data class Videos (

    @SerializedName("id"             ) var id            : Int?                     = null,
    @SerializedName("width"          ) var width         : Int?                     = null,
    @SerializedName("height"         ) var height        : Int?                     = null,
    @SerializedName("duration"       ) var duration      : Int?                     = null,
    @SerializedName("full_res"       ) var fullRes       : String?                  = null,
    @SerializedName("tags"           ) var tags          : ArrayList<String>        = arrayListOf(),
    @SerializedName("url"            ) var url           : String?                  = null,
    @SerializedName("image"          ) var image         : String?                  = null,
    @SerializedName("avg_color"      ) var avgColor      : String?                  = null,
    @SerializedName("user"           ) var user          : User?                    = User(),
    @SerializedName("video_files"    ) var videoFiles    : ArrayList<VideoFiles>    = arrayListOf(),
    @SerializedName("video_pictures" ) var videoPictures : ArrayList<VideoPictures> = arrayListOf()

)