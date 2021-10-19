package com.lionmgtllcagencyp.chatapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.lionmgtllcagencyp.chatapp.adapters.ChatsAdapter
import com.lionmgtllcagencyp.chatapp.databinding.ActivityMessageChatBinding
import com.lionmgtllcagencyp.chatapp.modelClasses.Chat
import com.lionmgtllcagencyp.chatapp.modelClasses.Users
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit = ""
    var firebaseUser:FirebaseUser ?=null
    var RESULT_CODE = 820
    var chatsAdapter:ChatsAdapter ?=null
    var mChatList:List<Chat> ?= null
    lateinit var chatsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMessageChatBinding.inflate(inflater)
        setContentView(binding.root)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        chatsRecyclerView = binding.recyclerViewChats
        chatsRecyclerView.apply {
            setHasFixedSize(true)
            var linearLayoutManager = LinearLayoutManager(this@MessageChatActivity)
            linearLayoutManager.stackFromEnd =  true
            layoutManager = linearLayoutManager



        }

        val reference = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userIdVisit)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user: Users? = p0.getValue(Users::class.java)
                    binding.usernameMessageChat.text = user?.getUserName()
                    Picasso.get().load(user?.getProfile()).into(binding.profileImageMessageChat)

                    retreiveMessages(firebaseUser?.uid,userIdVisit,user?.getProfile())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })

        binding.sendMessageBtn.setOnClickListener {
            val message = binding.textMessage.text.toString()
            if(message == ""){
                Toast.makeText(this,"Please type a message..",Toast.LENGTH_SHORT).show()
            }else{
                sendMessageToUser(firebaseUser!!.uid,userIdVisit,message)
            }
            binding.textMessage.setText("")
        }
        binding.attachImageFileBtn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),RESULT_CODE )
        }

    }

    private fun retreiveMessages(senderId: String?, receiverId: String, receiverImageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for(snapshot in p0.children){
                    val chat = snapshot.getValue(Chat::class.java)
                    if(chat?.getReceiver().equals(senderId) && chat?.getSender().equals(receiverId)
                        || chat?.getReceiver().equals(receiverId) && chat?.getSender().equals(senderId)
                    ){
                        if (chat != null) {
                            (mChatList as ArrayList<Chat>).add(chat)
                            chatsAdapter = ChatsAdapter(this@MessageChatActivity,(mChatList as ArrayList<Chat>),receiverImageUrl!!)
                            chatsRecyclerView.adapter = chatsAdapter
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String,Any?>()

        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                val chatListReference = FirebaseDatabase.getInstance()
                    .reference
                    .child("ChatList")
                    .child(firebaseUser!!.uid)
                    .child(userIdVisit)

                chatListReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        if(!p0.exists()){
                            chatListReference.child("id").setValue(userIdVisit)
                        }
                        val chatListReceiverRef = FirebaseDatabase.getInstance()
                            .reference
                            .child("ChatList")
                            .child(userIdVisit)
                            .child(firebaseUser!!.uid)
                        chatListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }

                })




                val reference = FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(firebaseUser!!.uid)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_CODE && resultCode == RESULT_OK && data != null && data.data != null){
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Please wait , image is loading...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId  = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String,Any?>()

                    messageHashMap["sender"] = firebaseUser?.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)

                    progressBar.dismiss()
                }
            }


        }
    }


}