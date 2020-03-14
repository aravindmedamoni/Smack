package com.example.smack.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.smack.R
import com.example.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLoginButtonClicked(view: View) {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(this,email,password){
                    loginSuccess ->
                if(loginSuccess){
                    AuthService.getUserByMail(this){
                            findUser ->
                        if(findUser){
                            finish()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(this,"Enter all credentials", Toast.LENGTH_LONG).show()
        }

    }
    fun onSignUpButtonClicked(view: View) {
        startActivity(Intent(this, SignUpActivty::class.java))
        finish()
    }
}