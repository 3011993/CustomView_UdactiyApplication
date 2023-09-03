package com.udacity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.EXTRA_NAME
import com.udacity.MainActivity.Companion.EXTRA_STATUS


import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)





        val okbutton = findViewById<Button>(R.id.button_ok)

         val filename = intent.getStringExtra(EXTRA_NAME)
         val statusName = intent.getStringExtra(EXTRA_STATUS)



        val filenametext = findViewById<TextView>(R.id.textview_filename)
        val statusText = findViewById<TextView>(R.id.textview_status)
        filenametext.text = filename
        statusText.text = statusName



        okbutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
