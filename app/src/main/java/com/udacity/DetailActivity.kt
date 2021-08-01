package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        val name = intent.getStringExtra("fileName")
        val status = intent.getStringExtra("status")

        file_name.text = name
        status_data.text = status

        when (status) {
            "Success" -> status_data.setTextColor(Color.BLUE)
            else -> status_data.setTextColor(Color.RED)
        }
        back_button.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

    }
}
