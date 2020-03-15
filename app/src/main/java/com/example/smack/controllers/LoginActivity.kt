package com.example.smack.controllers

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import com.example.smack.R
import com.example.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.reflect.Method

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //At the time screen launch our progress should be invisible
        loginProgressBar.visibility = View.INVISIBLE
    }

    fun onLoginButtonClicked(view: View) {
        enableProgressBar(true)
        hideKeyBoard()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(email,password){
                    loginSuccess ->
                if(loginSuccess){
                    AuthService.getUserByMail(this){
                            findUser ->
                        if(findUser){
                            enableProgressBar(false)
                            finish()
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }
            }
        }else{
            Toast.makeText(this,"Enter all credentials", Toast.LENGTH_LONG).show()
            enableProgressBar(false)
        }

    }

   private fun errorToast(){
        Toast.makeText(this,"Something went wrong please try again later!.", Toast.LENGTH_LONG).show()
        enableProgressBar(false)
    }

   private fun enableProgressBar(isEnable : Boolean){
        if(isEnable){
            loginProgressBar.visibility = View.VISIBLE
        }else{
            loginProgressBar.visibility = View.INVISIBLE
        }
        loginButton.isEnabled = !isEnable
        signUpButton.isEnabled = !isEnable
    }

   private fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
        }
    }

    fun onSignUpButtonClicked(view: View) {
        startActivity(Intent(this, SignUpActivty::class.java))
        finish()
    }
}