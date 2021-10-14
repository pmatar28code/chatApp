package com.lionmgtllcagencyp.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.lionmgtllcagencyp.chatapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
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
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}