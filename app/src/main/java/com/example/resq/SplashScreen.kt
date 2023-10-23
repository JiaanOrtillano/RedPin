package com.example.resq

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toolbar

class SplashScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val handler = Handler(Looper.getMainLooper())


        handler.postDelayed({
            val intent = Intent(this,LogIn::class.java)
            startActivity(intent)
            finish()


        },2000)

    }
}