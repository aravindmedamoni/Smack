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
import com.example.smack.services.AuthService
import com.example.smack.services.MessageService
import com.example.smack.services.UserDataService
import com.example.smack.utils.BROADCAST_USER_DATA_CHANGE
import com.example.smack.utils.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<Channel>

    private lateinit var appBarConfiguration: AppBarConfiguration

    fun setUpAdapter(){
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangedReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     //   val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
       //write a logic for connecting to the socket
        socket.connect()
        socket.on("New Channel",onNewChannel);
        val toggle = ActionBarDrawerToggle(
            this,drawer_layout,toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //here is setting the nav header to default details
        navHeaderSetToDefault()

        //here is setting the channel list adapter
        setUpAdapter()

    }

    private val onNewChannel = Emitter.Listener{
        args ->
        runOnUiThread{val channelName = args[0] as String
        val channelDescription = args[1] as String
        val channelId = args[2] as String

        val newChannel = com.example.smack.models.Channel(channelName,channelDescription, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
//            println(newChannel.channelName)
//            println(newChannel.channelDescription)
//            println(newChannel.channelId)

        }
    }

    private val userDataChangedReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if(AuthService.isLogedIn){
                navHeaderProfilerName.text = UserDataService.name
                navHeaderprofilerMail.text = UserDataService.email
                navHeaderLoginButton.text = "Logout"
                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                navHeaderProfile.setImageResource(resourceId)
               // navHeaderProfile.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                MessageService.getChannels(context){
                    complete ->
                    if(complete){
                        channelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    override fun onBackPressed(){
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    fun onLoginButtonClicked(view:View){
        if(AuthService.isLogedIn){
            UserDataService.logout()
            navHeaderSetToDefault()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    fun onAddChannelButtonClicked(view : View){
        if(AuthService.isLogedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog,null)
            builder.setView(dialogView)
                .setPositiveButton("Add"){ dialogInterface,i ->
                    //add some logic here to display getting content from the user
                    val nameEditText = dialogView.findViewById<EditText>(R.id.channelName)
                    val descriptionEditText = dialogView.findViewById<EditText>(R.id.channelDescEditText)
                    val channelName = nameEditText.text.toString()
                    val channelDescription = descriptionEditText.text.toString()

                    //create channel with name and description
                    if(channelName.isNotEmpty() && channelDescription.isNotEmpty()){
                        socket.emit("new Channel", channelName,channelDescription)
                    }else{
                        Toast.makeText(this,"enter all fields ",Toast.LENGTH_LONG).show()
                    }

                }
                .setNegativeButton("Cancel"){
                    dialogInterface ,i ->
                    //add logic for cancel and close the window
                }
                .show()
        }
    }

    private fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
        }
    }

    fun onSendButtonClicked(view : View){

    }
   private fun navHeaderSetToDefault(){
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
