package com.example.smack.controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.AppBarConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.services.AuthService
import com.example.smack.services.UserDataService
import com.example.smack.utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     //   val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //hide keyboard
        hideKeyBoard()

        val toggle = ActionBarDrawerToggle(
            this,drawer_layout,toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navHeaderSetToDefault()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangedReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
    }

    private val userDataChangedReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLogedIn){
                navHeaderProfilerName.text = UserDataService.name
                navHeaderprofilerMail.text = UserDataService.email
                navHeaderLoginButton.text = "Logout"
                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                navHeaderProfile.setImageResource(resourceId)
               // navHeaderProfile.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
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
                    val channelName = channelNameEditText.text.toString()
                    val channelDescription = channelDescEditText.text.toString()

                    //create channel with name and description
                    hideKeyBoard()
                }
                .setNegativeButton("Cancel"){
                    dialogInterface ,i ->
                    //add logic for cancel and close the window
                    hideKeyBoard()
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
}
