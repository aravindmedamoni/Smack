package com.example.smack.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLoginButtonClicked(view: View) {}
    fun onSignUpButtonClicked(view: View) {
        startActivity(Intent(this, SignUpActivty::class.java))
    }
}