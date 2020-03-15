package com.example.smack.models

class Channel(val channelName:String, val channelDescription:String, val channelId:String) {
    override fun toString(): String {
        return "#$channelName"
    }
}