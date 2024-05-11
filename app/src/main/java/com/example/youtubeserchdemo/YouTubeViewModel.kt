package com.example.youtubeserchdemo

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YouTubeViewModel() : ViewModel() {
    // LiveData를 사용하여 UI 컨트롤러와 데이터를 동기화
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    // ToastMessage
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    // nextPageToken을 저장
    private var nextPageToken: String? = null

    // YouTube API를 통해 비디오 검색
    fun searchVideos(searchQuery: String, isNewSearch: Boolean = true) {
        if (isNewSearch) {
            nextPageToken = null // 새 검색인 경우, nextPageToken을 초기화
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val youTubeApiService = retrofit.create(YouTubeApiService::class.java)
        youTubeApiService.searchVideos(
            "snippet",
            "date", // 날짜순으로 입력
            searchQuery,
            "",
            "video",
            nextPageToken

        ).enqueue(object : Callback<YouTubeSearchResponse> {
            override fun onResponse(
                call: Call<YouTubeSearchResponse>,
                response: Response<YouTubeSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val newItems = response.body()?.items ?: emptyList() // 결과를 가져온다
                    nextPageToken = response.body()?.nextPageToken // 다음페이지에 사용할 토큰을 업데이트

                    if (isNewSearch) {
                        // ViewModel의 item을 업데이트하기 위해서는 .postValue()를 사용
                        _items.postValue(newItems) // 새 검색인 경우, 새 아이템으로 대체
                    } else {
                        // 새 검색이 아닌 경우, 기존 아이템 리스트에 새 아이템 추가
                        val currentItems = _items.value ?: emptyList()
                        _items.postValue(currentItems + newItems)
                    }

                    _toastMessage.postValue("해당 검색 결과입니다.")
                } else {
                    _toastMessage.postValue("데이터를 불러오는데 실패하였습니다 ${response.code()}")
                }
            }

            override fun onFailure(call: Call<YouTubeSearchResponse>, t: Throwable) {
                _toastMessage.postValue("네트워크 통신에 실패했습니다.")
            }
        })
    }
}
