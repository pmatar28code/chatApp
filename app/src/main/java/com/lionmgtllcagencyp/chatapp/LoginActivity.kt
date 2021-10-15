package com.lionmgtllcagencyp.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lionmgtllcagencyp.chatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityLoginBinding.inflate(inflater)
        setContentView(binding.root)

        val toolBar = binding.toolbarLogin
        setSupportActionBar(toolBar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener {
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        mAuth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            loginUser(binding)
        }
    }

    private fun loginUser(binding: ActivityLoginBinding) {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        if(email.equals("")){
            Toast.makeText(this,"Email Missing",Toast.LENGTH_SHORT).show()
        }else if(password.equals("")){
            Toast.makeText(this,"Password Missing",Toast.LENGTH_SHORT).show()
        }else{
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Login successful",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this,"Error login:${task.exception?.message}",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}