package com.example.smack.services

import android.graphics.Color
import com.example.smack.controllers.App
import java.util.*

object UserDataService {
    var id =""
    var name = ""
    var email = ""
    var avatarName = ""
    var avatarColor = ""

    fun logout(){
         id =""
         name = ""
         email = ""
         avatarName = ""
         avatarColor = ""
        App.prefs.authTokenId = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
        MessageService.clearChannels()
        MessageService.clearMessages()
    }

    fun returnAvatarColor(components : String) : Int{
        val strippedColor = components
            .replace("[","")
            .replace("]","")
            .replace(",","")
        var r = 0
        var g = 0
        var b = 0
        val scanner = Scanner(strippedColor)
        if(scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r,g,b)
    }
}