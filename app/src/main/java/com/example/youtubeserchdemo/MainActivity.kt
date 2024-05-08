package com.example.youtubeserchdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubeserchdemo.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // searchEdit와 searchButton
        val searchButton = binding.searchButton
        val searchEdit = binding.searchEditText
        val dateButton = binding.dateButton

        val items = mutableListOf<Item>()

        // dateButton을 눌렀을 때의 동작, 조회 수
        dateButton.setOnClickListener {
            val searchQuery = searchEdit.text.toString()
            if (searchQuery.isEmpty()) {
                Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val youTubeApiService = retrofit.create(YouTubeApiService::class.java)
                youTubeApiService.searchVideos(
                    "snippet",
                    50,
                    "viewCount",
                    searchQuery,
                    "AIzaSyBJChZvHdl4oa6_F0KzJ6Wya2BP2pzLX-8"
                ).enqueue(object : Callback<YouTubeSearchResponse> {
                    override fun onResponse(
                        call: Call<YouTubeSearchResponse>,
                        response: Response<YouTubeSearchResponse>
                    ) {
                        if (response.isSuccessful) {
                            val searchItems = response.body()?.items?.map { item ->
                                Item(item.id, item.snippet)
                            } ?: listOf()

                            items.clear()
                            items.addAll(searchItems)
                            binding.itemRecyclerView.adapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<YouTubeSearchResponse>, t: Throwable) {
                        // 실패 처리
                        Toast.makeText(this@MainActivity, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Recyclerview에 Adapter 및 GridLayoutManager 설정
        binding.itemRecyclerView.adapter = MyAdapter(items)
        binding.itemRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // searchButton을 눌렀을 떄의 동작, 날짜 순으로 입력
        searchButton.setOnClickListener {
            val searchQuery = searchEdit.text.toString()
            if (searchQuery.isEmpty()) {
                Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val youTubeApiService = retrofit.create(YouTubeApiService::class.java)
                youTubeApiService.searchVideos(
                    "snippet",
                    50,
                    "date",
                    searchQuery,
                    "AIzaSyBJChZvHdl4oa6_F0KzJ6Wya2BP2pzLX-8"
                ).enqueue(object :
                    Callback<YouTubeSearchResponse> {
                    override fun onResponse(
                        call: Call<YouTubeSearchResponse>,
                        response: Response<YouTubeSearchResponse>
                    ) {
                        if (response.isSuccessful) {
                            val searchItems = response.body()?.items?.map { item ->
                                Item(item.id, item.snippet)
                            } ?: listOf()

                            items.clear()
                            items.addAll(searchItems)
                            binding.itemRecyclerView.adapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<YouTubeSearchResponse>, t: Throwable) {
                        // 실패 처리
                        Toast.makeText(this@MainActivity, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    // youtube 설정을 위한 최소한의 interface
    interface YouTubeApiService {
        @GET("youtube/v3/search")
        fun searchVideos(
            @Query("part") part: String,
            @Query("maxResults") maxResults: Int,
            @Query("order") order: String,
            @Query("q") query: String,
            @Query("key") apiKey: String
        ): Call<YouTubeSearchResponse>
    }

    data class YouTubeSearchResponse(
        val items: List<Item>
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

    // recyclerview
    class MyAdapter(private val items: List<Item>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var ImageThumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
            val textView: TextView = view.findViewById(R.id.text_site_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.textView.text = item.snippet.title
            Glide.with(holder.itemView.context)
                .load(item.snippet.thumbnails.medium.url)
                .into(holder.ImageThumbnail)
        }

        override fun getItemCount() = items.size
    }
}