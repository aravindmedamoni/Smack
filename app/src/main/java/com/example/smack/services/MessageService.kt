package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.controllers.App
import com.example.smack.models.Channel
import com.example.smack.models.Message
import com.example.smack.utils.BASE_URL
import com.example.smack.utils.URL_GET_CHANNELS
import org.json.JSONException
import java.lang.reflect.Method

object MessageService {
    var channels = ArrayList<Channel>()
    var messages = ArrayList<Message>()
    fun getChannels(complete : (Boolean) -> Unit){

        val channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS,null,Response.Listener {
            response ->
            try {
                for (x in 0 until response.length()){
                    val channel = response.getJSONObject(x)
                    val channelName = channel.getString("name")
                    val channelDescription = channel.getString("description")
                    val channelId = channel.getString("_id")
                    val newChannel = Channel(channelName,channelDescription,channelId)
                    this.channels.add(newChannel)

                    complete(true)
                }
            } catch (e: Exception) {
                Log.d("JSON","EXCP ${e.localizedMessage}")
                complete(false)
            }
        },Response.ErrorListener {
            error ->
            Log.d("ERROR","could not get channel ${error.message}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authTokenId}")
                return headers
            }
        }
        App.prefs.requestQueue.add(channelRequest)
    }

    fun getMessages(channelId : String, complete: (Boolean) -> Unit){

        val url = "${BASE_URL}/$channelId"
        val messagesRequest = object : JsonArrayRequest(Method.GET,url,null, Response.Listener {
            response ->
            clearMessages()
            try {
                for (x in 0 until response.length()){
                    val msg = response.getJSONObject(x)
                    val message = msg.getString("messageBody")
                    val channelId = msg.getString("channelId")
                    val id = msg.getString("_id")
                    val userName = msg.getString("userName")
                    val avatarName = msg.getString("userAvatar")
                    val avatarColor = msg.getString("userAvatarColor")
                    val timeStamp = msg.getString("timeStamp")
                    val newMessage = Message(message,userName,channelId,avatarName,avatarColor,id,timeStamp)
                    this.messages.add(newMessage)
                }
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXCP ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {
            error ->
            Log.d("ERROR","Could not get Messages ${error.message}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authTokenId}")
                return headers
            }
        }
        App.prefs.requestQueue.add(messagesRequest)
    }

    fun clearMessages(){
        messages.clear()
    }
    fun clearChannels(){
        channels.clear()
    }
}