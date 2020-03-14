package com.example.smack.controllers

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.R
import com.example.smack.services.AuthService
import com.example.smack.services.UserDataService
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivty : AppCompatActivity() {
    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun onSignUpButtonClicked(view: View) {
        val userName = userName.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        AuthService.registerUser(this,email,password) {
            registerSuccess ->
            if(registerSuccess){
                AuthService.loginUser(this,email,password){
                    loginSuccess ->
                    if(loginSuccess){
                        AuthService.createUser(this,email,userName,userAvatar,avatarColor){
                            createSuccess ->
                            if(createSuccess){
                                println(UserDataService.name)
                                println(UserDataService.email)
                                println(UserDataService.avatarName)
                                println(UserDataService.avatarColor)
                                finish()
                            }
                        }
                    }
                }
            }
        }
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