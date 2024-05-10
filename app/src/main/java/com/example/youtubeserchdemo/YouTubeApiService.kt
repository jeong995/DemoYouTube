package com.example.youtubeserchdemo

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// youtube 설정을 위한 최소한의 interface
interface YouTubeApiService {
    @GET("youtube/v3/search")
    fun searchVideos(
        @Query("part") part: String, // API 응답에서 반환되어야 하는 데이터 부분
        @Query("order") order: String, // 검색 결과 정렬 순
        @Query("q") query: String, // youtube에서 검색하고자 하는 문자열
        @Query("key") apiKey: String, // api key
        @Query("type") type: String,
        @Query("pageToken") pageToken: String?
    ): Call<YouTubeSearchResponse>
}

data class YouTubeSearchResponse(
    val items: List<Item>,
    val nextPageToken: String?,
    val prePageToken: String?
)

data class Item(
    val id: Id,
    val snippet: Snippet
)

data class Id(
    val videoId: String
)

data class Snippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String
)

data class Thumbnails(
    val default: ThumbnailDetail,
    val medium: ThumbnailDetail,
    val high: ThumbnailDetail
)

data class ThumbnailDetail(
    val url: String,
    val width: Int,
    val height: Int
)