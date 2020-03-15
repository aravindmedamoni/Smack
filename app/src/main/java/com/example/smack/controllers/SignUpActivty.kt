package com.example.smack.controllers

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.services.AuthService
import com.example.smack.services.UserDataService
import com.example.smack.utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivty : AppCompatActivity() {
    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        progressBar.visibility = View.INVISIBLE
    }

    fun onSignUpButtonClicked(view: View) {
        enableSpinner(true)
        val name = userName.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(email,password) {
                    registerSuccess ->
                if(registerSuccess){
                    AuthService.loginUser(email,password){
                            loginSuccess ->
                        if(loginSuccess){
                            AuthService.createUser(email,name,userAvatar,avatarColor){
                                    createSuccess ->
                                if(createSuccess){
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                     LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                     enableSpinner(false)
                                    //if the user created we need to setback all our fields to default
                                    userName.text.clear()
                                    emailText.text.clear()
                                    passwordText.text.clear()
                                    userAvatar = "profileDefault"
                                    avatarColor = "[0.5,0.5,0.5,1]"

//                                println(UserDataService.name)
//                                println(UserDataService.email)
//                                println(UserDataService.avatarName)
//                                println(UserDataService.avatarColor)
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
                    errorToast()
                }
            }
        }else {
            Toast.makeText(this,"Please Enter all the details.",Toast.LENGTH_LONG).show()
            enableSpinner(false)

        }

    }

    private fun errorToast(){
        Toast.makeText(this,"Something went wrong, please try again later",Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun enableSpinner(isEnable : Boolean){
        if(isEnable){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.INVISIBLE
        }
        userAvatarProfile.isEnabled = !isEnable
        signUpButton.isEnabled = !isEnable
        changeBGColorButton.isEnabled = !isEnable
    }

    fun generateUserAvatar(view: View) {
        val color = Random().nextInt(2)
        val avatar = Random().nextInt(28)

        userAvatar = if(color == 0){
            "light$avatar"
        }else{
            "dark$avatar"
        }
        val resourceId = resources.getIdentifier(userAvatar,"drawable",packageName)
        userAvatarProfile.setImageResource(resourceId)
    }
    fun changebackgroundColor(view: View) {
        val r = Random().nextInt(255)
        val g = Random().nextInt(255)
        val b = Random().nextInt(255)
        userAvatarProfile.setBackgroundColor(Color.rgb(r,g,b))
        avatarColor = "[${r.toDouble()/255},${g.toDouble()/255},${b.toDouble()/255},1]"
    }
}