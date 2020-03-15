package com.example.smack.utils

const val BASE_URL = "http://devslopes-chattin.herokuapp.com/v1/"
//const val BASE_URL = "http://localhost:27017/v1/"
const val SOCKET_URL = "http://devslopes-chattin.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}/account/login"
const val URL_CREATE_USER = "${BASE_URL}/user/add"
const val URL_GET_USER = "${BASE_URL}/user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}/channel/"

//Broadcast Constant

const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"