package com.lionmgtllcagencyp.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lionmgtllcagencyp.chatapp.databinding.ActivityRegisterBinding
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityRegisterBinding.inflate(inflater)
        setContentView(binding.root)

        val toolBar = binding.toolbarRegister
        setSupportActionBar(toolBar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        mAuth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            registerUser(binding)
        }
    }

    private fun registerUser(binding: ActivityRegisterBinding) {
        val username = binding.usernameRegister.text.toString()
        val email = binding.emailRegister.text.toString()
        val password =binding.passwordRegister.text.toString()

        if (email == "") {
            Toast.makeText(this, "Email is Missing", Toast.LENGTH_SHORT).show()
        } else if (username == "") {
            Toast.makeText(this, "UserName is Missing", Toast.LENGTH_SHORT).show()
        } else if (password == "") {
            Toast.makeText(this, "Password is Missing", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(
                email,password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUserId = mAuth.currentUser?.uid.toString()
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(firebaseUserId)
                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserId
                    userHashMap["username"] = username
                    userHashMap["profile"] =
                        "https://firebasestorage.googleapis.com/v0/b/chatapp-d4aa5.appspot.com/o/profile.png?alt=media&token=c6467fca-2900-4901-955e-db637122f8f1"
                    userHashMap["cover"] =
                        "https://firebasestorage.googleapis.com/v0/b/chatapp-d4aa5.appspot.com/o/fbcover.jpeg?alt=media&token=3400ac94-9b13-4490-a18d-0c533871e572"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.lowercase(Locale.getDefault())
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Error Message: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }else{
                    Toast.makeText(
                        this,
                        "Error Message: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

