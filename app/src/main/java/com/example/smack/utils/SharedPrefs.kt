package com.example.smack.utils

import android.content.Context
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME,0)

    val IS_LOGGEDIN = "isLoggedIn"
    val AUTH_TOKEN_ID = "authTokenId"
    val USER_EMAIL = "userEmail"

    var isLoggedIn : Boolean
    get() = prefs.getBoolean(IS_LOGGEDIN,false)
    set(value) = prefs.edit().putBoolean(IS_LOGGEDIN,value).apply()

    var authTokenId : String?
    get() = prefs.getString(AUTH_TOKEN_ID,"")
    set(value) = prefs.edit().putString(AUTH_TOKEN_ID,value).apply()

    var userEmail : String?
    get() = prefs.getString(USER_EMAIL,"")
    set(value) = prefs.edit().putString(USER_EMAIL,value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}