package com.lionmgtllcagencyp.chatapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.lionmgtllcagencyp.chatapp.MainActivity
import com.lionmgtllcagencyp.chatapp.MessageChatActivity
import com.lionmgtllcagencyp.chatapp.R
import com.lionmgtllcagencyp.chatapp.modelClasses.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    mContext: Context,
    mUsersList:List<Users>,
    isChatCheck:Boolean
): RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private val mContext:Context = mContext
    private val mUsersList:List<Users> = mUsersList
    private var isChatCheck:Boolean = isChatCheck

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_user_search,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUsersList[position]
        holder.userNameTxt.text = user.getUserName()
        Picasso.get().load(user.getProfile()).into(holder.profileImageView)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Select an Option")
            builder.setItems(options,DialogInterface.OnClickListener{dialog, position ->
                if(position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
                if(position == 1){

                }
            })
            builder.show()


        }
    }

    override fun getItemCount(): Int {
        return mUsersList.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var userNameTxt:TextView
        var profileImageView:CircleImageView
        var onlineImageView :CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageTxt:TextView

        init {
            userNameTxt = itemView.findViewById(R.id.user_name)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }
    }
}