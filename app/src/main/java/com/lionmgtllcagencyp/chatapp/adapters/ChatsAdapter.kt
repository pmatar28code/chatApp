package com.lionmgtllcagencyp.chatapp.adapters

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lionmgtllcagencyp.chatapp.R
import com.lionmgtllcagencyp.chatapp.modelClasses.Chat
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    mContext: Context, mChatList:List<Chat>, imageUrl:String
):RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private val mContext = mContext
    private val mChatList = mChatList
    private val imageUrl = imageUrl
    private var firebaseUser = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if(position == 1) {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.item_message_right, parent, false)
            ViewHolder(view)
        }else {
            val view: View =
                LayoutInflater.from(mContext).inflate(R.layout.item_message_left, parent, false)
            ViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat:Chat = mChatList[position]
        Picasso.get().load(imageUrl).into(holder.profileImage)

        if(chat.getMessage() == "sent you an image." && chat.getUrl() != "") {
            //image right side
            if (chat.getSender().equals(firebaseUser.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.rightImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)
            }
            //image left side
            else if (!chat.getSender().equals(firebaseUser.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.leftImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)
            }
        }else{
            //text right side
            holder.showTextMessage?.text = chat.getMessage()
        }

        //sent and seen message
        if(position == mChatList.size - 1){
            if(chat.getIsSeen()){
                holder.textSeen?.text = "Seen"
                if(chat.getMessage() == "sent you an image." && chat.getUrl() != ""){
                    val lp:RelativeLayout.LayoutParams = holder.textSeen?.layoutParams as RelativeLayout.LayoutParams
                    lp.setMargins(0,245,10,0)
                    holder.textSeen.layoutParams = lp
                }
            }else{
                holder.textSeen?.text = "Sent"
                if(chat.getMessage() == "sent you an image." && chat.getUrl() != ""){
                    val lp:RelativeLayout.LayoutParams = holder.textSeen?.layoutParams as RelativeLayout.LayoutParams
                    lp.setMargins(0,245,10,0)
                    holder.textSeen.layoutParams = lp
                }
            }
        }else{
            holder.textSeen?.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

     class ViewHolder(
         itemView:View
     ):RecyclerView.ViewHolder(itemView){
         val profileImage: CircleImageView? = itemView.findViewById(R.id.profile_image)
         val showTextMessage: TextView? = itemView.findViewById(R.id.show_text_message)
         val leftImageView: ImageView? = itemView.findViewById(R.id.left_image_view)
         val rightImageView: ImageView? = itemView.findViewById(R.id.right_image_view)
         val textSeen: TextView? = itemView.findViewById(R.id.text_seen)
    }

    override fun getItemViewType(position: Int): Int {
        return if(mChatList[position].getSender() == firebaseUser.uid){
                1
        }else{
            0
        }
    }


}