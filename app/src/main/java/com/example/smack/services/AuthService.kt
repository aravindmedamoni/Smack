package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utils.URL_LOGIN
import com.example.smack.utils.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLogin : Boolean = false
    var tokenId : String = ""
    var userEmail : String = ""

    fun registerUser(context: Context, email : String, password : String, complete : (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
            println(response)
            complete(true)

        }, Response.ErrorListener {
            error ->
//            print("error caught ${error.message}")
            Log.d("ERROR","Error Caught because of ${error.message}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context,email: String,password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN,null,Response.Listener {
            response ->
            try {
                userEmail = response.getString("user")
                tokenId = response.getString("token")
                isLogin = true

                complete(true)
            } catch (e: JSONException) {
                Log.d("EXCEPTION:","${e.message}")
                complete(false)
            }
        }, Response.ErrorListener {
            error ->
            Log.d("ERROR","Error caught because of ${error.message}")
            complete(false)
        })  {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(loginRequest)
    }
}