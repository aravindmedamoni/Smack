package com.example.smack.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smack.R
import com.example.smack.models.Message
import com.example.smack.services.UserDataService
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val context : Context, val messages : ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userImage = itemView.findViewById<ImageView>(R.id.messageUserImage)
        val userName = itemView.findViewById<TextView>(R.id.messageUserName)
        val timeStamp = itemView.findViewById<TextView>(R.id.messageTimeStamp)
        val messageText = itemView.findViewById<TextView>(R.id.messageText)

        fun bindMessage(context: Context,message:Message){
            val resourceId = context.resources.getIdentifier(message.userAvatar,"drawable",context.packageName)
            userImage.setImageResource(resourceId)
            userImage.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName.text = message.userName
            timeStamp.text = returnTimeStamp(message.timeStamp)
            messageText.text = message.message
        }

       private fun returnTimeStamp(isoString : String) : String{

            val isoFormatter = SimpleDateFormat("yyyy-mm-dd'T' HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var covertDate = Date()
            try {
                covertDate = isoFormatter.parse(isoString)
            }catch (e:Exception){
                Log.d("ERROR","Can not Parse ")
            }
            val outDateFormat = SimpleDateFormat("E, h:m a", Locale.getDefault())
            return outDateFormat.format(covertDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,parent,false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindMessage(context,messages[position])
    }

}