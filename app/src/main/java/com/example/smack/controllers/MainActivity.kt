package com.example.smack.controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.AppBarConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.models.Channel
import com.example.smack.models.Message
import com.example.smack.services.AuthService
import com.example.smack.services.MessageService
import com.example.smack.services.UserDataService
import com.example.smack.utils.BROADCAST_USER_DATA_CHANGE
import com.example.smack.utils.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    var selectedChannel: Channel? = null

    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>

    private lateinit var appBarConfiguration: AppBarConfiguration

    private fun setUpAdapter() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangedReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //   val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //write a logic for connecting to the socket
        socket.connect()
        //logic creating new channel
        socket.on("channelCreated", onNewChannel)
        //logic for creating new message
        socket.on("messageCreated", onMessageChannel)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //here is setting the nav header to default details
        navHeaderSetToDefault()

        //here is setting the channel list adapter
        setUpAdapter()

        //checking for user already loggedIn or not
        if (App.prefs.isLoggedIn) {
            AuthService.getUserByMail(this) {}
        }

        //Logic for selecting the specific channel from the navigation drawer
        channel_list.setOnItemClickListener { _, _, position, id ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannelName()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->

        if(App.prefs.isLoggedIn){
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
//            println(newChannel.channelName)
//            println(newChannel.channelDescription)
//            println(newChannel.channelId)

            }
        }

    }
    private val onMessageChannel = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn){
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel?.channelId){
                    val message = args[0] as String
                    val userName = args[3] as String
                    val avatarName = args[4] as String
                    val avatarColor =args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(message,userName,channelId,avatarName,avatarColor,id,timeStamp)
                    MessageService.messages.add(newMessage)
                  //  println(newMessage.message)
                }

            }
        }

    }

    private val userDataChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {
                navHeaderProfilerName.text = UserDataService.name
                navHeaderprofilerMail.text = UserDataService.email
                navHeaderLoginButton.text = "Logout"
                val resourceId =
                    resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                navHeaderProfile.setImageResource(resourceId)
                // navHeaderProfile.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                MessageService.getChannels() { complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannelName()
                        }

                    }
                }
            }
        }

    }

    fun updateWithChannelName() {
        channelName.text = "#${selectedChannel?.channelName}"
        // get the message for the channel
        MessageService.getMessages(selectedChannel!!.channelId){
            complete ->
            if(complete){
                //println()
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    fun onLoginButtonClicked(view: View) {
        if (App.prefs.isLoggedIn) {
            UserDataService.logout()
            navHeaderSetToDefault()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    fun onAddChannelButtonClicked(view: View) {
        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    //add some logic here to display getting content from the user
                    val nameEditText = dialogView.findViewById<EditText>(R.id.channelName)
                    val descriptionEditText =
                        dialogView.findViewById<EditText>(R.id.channelDescEditText)
                    val channelName = nameEditText.text.toString()
                    val channelDescription = descriptionEditText.text.toString()

                    //create channel with name and description
                    if (channelName.isNotEmpty() && channelDescription.isNotEmpty()) {
                        socket.emit("newChannel", channelName, channelDescription)
                    } else {
                        Toast.makeText(this, "enter all fields ", Toast.LENGTH_LONG).show()
                    }

                }
                .setNegativeButton("Cancel") { _, _ ->
                    //add logic for cancel and close the window
                }
                .show()
        }
    }

    private fun hideKeyBoard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    fun onSendButtonClicked(view: View) {
        if (App.prefs.isLoggedIn && messageText.text.isNotEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.channelId
            socket.emit(
                "newMessage", messageText.text.toString(), userId, channelId,
                UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor
            )

            messageText.text.clear()
            hideKeyBoard()
        }
    }

    private fun navHeaderSetToDefault() {
        navHeaderLoginButton.text = "Login"
        navHeaderProfilerName.text = "Please Login"
        navHeaderprofilerMail.text = ""
        navHeaderProfile.setImageResource(R.drawable.profiledefault)
        navHeaderProfile.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangedReceiver)
        socket.disconnect()
        super.onDestroy()
    }
}
