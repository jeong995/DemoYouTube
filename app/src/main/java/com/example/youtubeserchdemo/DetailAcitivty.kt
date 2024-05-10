package com.example.youtubeserchdemo

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class DetailAcitivty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_acitivty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val title = intent.getStringExtra("title")
        val thumbnailUrl = intent.getStringExtra("thumbnailUrl")

        val ntitle = findViewById<TextView>(R.id.titile)
        val nthumbnailUrl = findViewById<ImageView>(R.id.detailImageView)

        ntitle.text = title
        Glide.with(this)
            .load(thumbnailUrl)
            .into(nthumbnailUrl)
    }
}