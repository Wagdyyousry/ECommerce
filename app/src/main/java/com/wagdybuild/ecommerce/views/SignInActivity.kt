package com.wagdybuild.ecommerce.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignInBinding
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var mStorage:FirebaseStorage
    private lateinit var mFireStore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    private fun init() {
        //Custom status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
    }

}