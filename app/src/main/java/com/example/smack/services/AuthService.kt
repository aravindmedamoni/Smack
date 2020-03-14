package com.example.smack.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utils.*
import org.json.JSONException
import org.json.JSONObject


object AuthService {

    var isLogedIn : Boolean = false
    var authTokenId : String = ""
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
            Log.d("ERROR","Error could not register ${error.message}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        registerRequest.setRetryPolicy(DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
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
                authTokenId = response.getString("token")
                isLogedIn = true

                complete(true)
            } catch (e: JSONException) {
                Log.d("EXCEPTION:","${e.message}")
                complete(false)
            }
        }, Response.ErrorListener {
            error ->
            Log.d("ERROR","Error could not login ${error.message}")
            complete(false)
        })  {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        loginRequest.setRetryPolicy(DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context, email: String, userName: String, avatarName : String, avatarColor : String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("name",userName)
        jsonBody.put("email",email)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER,null,Response.Listener {
            response ->
            try {
                UserDataService.id = response.getString("_id")
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                complete(true)

            }catch (e : JSONException){
                Log.d("JSON","EXC ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {
            error ->
            Log.d("ERROR","Could not create user : ${error.message}")
             complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authTokenId")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(createRequest)
    }

    fun getUserByMail(context: Context,complete: (Boolean) -> Unit){

        val findUserRequest = object : JsonObjectRequest(Method.GET,"${URL_GET_USER}/$userEmail", null, Response.Listener {
            response ->
            try {
                UserDataService.id = response.getString("_id")
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXCP ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {
            error ->
            Log.d("ERROR","Could not find User ${error.message}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authTokenId")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(findUserRequest)
    }
}