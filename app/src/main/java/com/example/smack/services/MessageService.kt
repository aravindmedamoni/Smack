package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.models.Channel
import com.example.smack.utils.URL_GET_CHANNELS

object MessageService {
    var channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete : (Boolean) -> Unit){

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
                headers.put("Authorization", "Bearer ${AuthService.authTokenId}")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(channelRequest)
    }
}