package com.example.youtubeserchdemo

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: YouTubeViewModel
    private lateinit var youTubeAdapter: YouTubeAdapter

    private var isLoading = false
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
        val topButton = binding.theUppperFloatingButton

        // Recyclerview에 Adapter 및 GridLayoutManager 설정
        youTubeAdapter = YouTubeAdapter()
        binding.itemRecyclerView.adapter = youTubeAdapter
        binding.itemRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // viewModel 초기화
        viewModel = ViewModelProvider(this).get(YouTubeViewModel::class.java)

        // ViewModel의 items LiveData를 관찰하고 UI 업데이트
        viewModel.items.observe(this, Observer { items ->
            youTubeAdapter.addItems(items)
            youTubeAdapter.notifyDataSetChanged()
            isLoading = false
        })

        // viewModel의 toast
        viewModel.toastMessage.observe(this, Observer {message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        // 검색 버튼 클릭 이벤트
        searchButton.setOnClickListener {
            val searchQuery = searchEdit.text.toString()
            if (searchQuery.isEmpty()) {
                Toast.makeText(this@MainActivity, "검색어를 입력해주시요", Toast.LENGTH_SHORT).show()
            } else {
                youTubeAdapter.clearItem()
                viewModel.searchVideos(searchQuery)
                binding.sportChipGroup.visibility = View.GONE
            }
        }

        // chipGroup 선택
        binding.sportChipGroup.setOnCheckedChangeListener { _, checkId ->
            when(checkId) {
                R.id.soccerChip -> {
                    youTubeAdapter.clearItem()
                    viewModel.searchVideos("축구")
                }
                R.id.baseballChip -> {
                    youTubeAdapter.clearItem()
                    viewModel.searchVideos("야구")
                }
            }
        }
        binding.itemRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount // 현재 표시된 아이템의 총 수
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition() // 마지막 위치
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() // 첫번째 위치

                // 스크롤이 끝에 도달했는지, 데이터 로딩 중이 아닌지 확인
                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                    // 여기서 데이터 로딩을 시작
                    viewModel.searchVideos(binding.searchEditText.text.toString(), isNewSearch = false)
                    isLoading = true // 데이터 로딩 시작
                }

                if (firstVisibleItemPosition > 1) {
                    topButton.visibility = View.VISIBLE
                } else {
                    topButton.visibility = View.GONE
                }
            }
        })

        // topBotton을 누르면 스크롤이 제일 위로 올라감
        topButton.setOnClickListener {
            binding.itemRecyclerView.smoothScrollToPosition(0)
        }
    }
}