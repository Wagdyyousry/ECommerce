package com.wagdybuild.ecommerce.views

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        //Firebase references
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.spinnerCcp.registerCarrierNumberEditText(binding.etPhoneNumber)

        binding.tvAlreadyHaveAccount.paintFlags =
            binding.tvAlreadyHaveAccount.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


        binding.btnSignUp.setOnClickListener {
            val full_phone_number = binding.spinnerCcp.fullNumberWithPlus.replace(" ", "")

            val password: String =binding.etPassword.text.toString().trim()

            val name: String =binding.etName.text.toString().trim()

            if (password.isEmpty()) {
                binding.etPassword.error = "You must write a password"
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                binding.etName.error = "Your must enter your Name"
                return@setOnClickListener
            }
            if (full_phone_number.isEmpty()) {
                binding.etPhoneNumber.error = "Your must enter your phone  number"
                return@setOnClickListener
            }
            else {
                val intent = Intent(this, VerifyOtpActivity::class.java)
                intent.putExtra("phone_number", full_phone_number)
                intent.putExtra("name", name)
                intent.putExtra("password", password)

                startActivity(intent)
            }

        }
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