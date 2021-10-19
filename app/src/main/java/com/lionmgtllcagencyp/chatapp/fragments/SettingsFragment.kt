package com.lionmgtllcagencyp.chatapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lionmgtllcagencyp.chatapp.R
import com.lionmgtllcagencyp.chatapp.databinding.FragmentSearchBinding
import com.lionmgtllcagencyp.chatapp.databinding.FragmentSettingsBinding
import com.lionmgtllcagencyp.chatapp.modelClasses.Users
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment() {
    var usersReference : DatabaseReference ?= null
    var firebaseUser : FirebaseUser ?= null
    private val REQUEST_CODE = 281219
    var imageUri: Uri?= null
    var storageRef:StorageReference ?= null
    var coverChecker:String = ""
    var socialChecker:String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val binding = FragmentSettingsBinding.bind(view)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference =
            firebaseUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("Users").child(
                    it
                )
            }
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        usersReference?.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(Users::class.java)
                    if(context != null){
                        binding.usernameSettings.text = user?.getUserName()
                        Picasso.get().load(user?.getProfile()).into(binding.profileImageSettings)
                        Picasso.get().load(user?.getCover()).into(binding.coverImageSettings)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })

        binding.profileImageSettings.setOnClickListener{
            pickImage()
        }
        binding.coverImageSettings.setOnClickListener{
            coverChecker = "cover"
            pickImage()
        }
        binding.setFacebookSettings.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }
        binding.setInstagramSettings.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }
        binding.setWebsiteSettings.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }
        return view
    }

    private fun setSocialLinks() {
        val builder = AlertDialog.Builder(context)
        if(socialChecker == "website"){
            builder.setTitle("Write Url:")
        }else{
            builder.setTitle("Write Username:")
        }
        val editText = EditText(context)
        if(socialChecker == "website"){
            editText.hint = "Example: www.google.com"
        }else{
            editText.hint = "Example: Peterson123"
        }
        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
            dialog,which ->

            val str = editText.text.toString()
            if(str == ""){
                Toast.makeText(context,"Please type something",Toast.LENGTH_SHORT).show()
            }else{
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                dialog,_ ->
            dialog.cancel()
        })
        builder.show()

    }

    private fun saveSocialLink(str: String) {
        val socialMap = HashMap<String,Any>()

        when(socialChecker){
            "facebook" -> {
                socialMap["facebook"] = "http://m.facebook.com/$str"
            }
            "instagram" -> {
                socialMap["instagram"] = "http://m.instagram.com/$str"
            }
            "website" -> {
                socialMap["website"] = "http://$str"
            }
        }
        usersReference?.updateChildren(socialMap)?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(context,"Uploaded Successfully..",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK &&  data?.data != null){
            imageUri = data.data
            Toast.makeText(requireContext(),"Uploading image....",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(requireContext())
        progressBar.setMessage("Image is uploading, please wait.")
        progressBar.show()
        if(imageUri != null){
            val fileRef = storageRef?.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask = fileRef?.putFile(imageUri!!)
            uploadTask!!.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover"){
                        val mapCoverImage = HashMap<String,Any>()
                        mapCoverImage["cover"] = url
                        usersReference?.updateChildren(mapCoverImage)
                        coverChecker = ""

                    }else{
                        val mapProfileImage = HashMap<String,Any>()
                        mapProfileImage["profile"] = url
                        usersReference?.updateChildren(mapProfileImage)
                        coverChecker = ""

                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}