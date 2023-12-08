package com.wagdybuild.ecommerce.views

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ActivityVerifyOtpBinding
import com.wagdybuild.ecommerce.models.User
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpBinding
    private var resendingToken: ForceResendingToken?=null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore
    private var loader: ProgressDialog? = null
    private var otp_code: String = ""
    private var full_phone_number: String = ""
    private var name: String = ""
    private var password: String = ""
    private var timeOutSeconds: Long = 60L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        full_phone_number = intent.getStringExtra("phone_number").toString()
        name = intent.getStringExtra("name").toString()
        password = intent.getStringExtra("password").toString()

        Toast.makeText(this, "Full number -> $full_phone_number", Toast.LENGTH_LONG).show()


        sendOTP(full_phone_number, false)

        binding.btnVerifyOtp.setOnClickListener {
            val code = binding.etOtpCode.text.toString()
            if (code.isEmpty()) {
                binding.etOtpCode.error = "Write the code we have sent to you first"
            } else {
                val credential = PhoneAuthProvider.getCredential(otp_code, code)
                signInWithCredential(credential)
            }
        }

        binding.tvReSendCode.paintFlags = binding.tvReSendCode.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvReSendCode.setOnClickListener { sendOTP(full_phone_number, true) }

    }

    private fun init() {
        //Custom status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        loader = ProgressDialog(this)

    }

    private fun sendOTP(fullPhoneNumber: String, isResend: Boolean) {
        startResendTimer()
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(fullPhoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    signInWithCredential(p0)
                }

                override fun onCodeSent(p0: String, p1: ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    otp_code = p0
                    resendingToken = p1
                    Toast.makeText(
                        this@VerifyOtpActivity,
                        "Code sent successfully",
                        Toast.LENGTH_LONG
                    ).show();
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(
                        this@VerifyOtpActivity,
                        p0.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            })

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(
                options.setForceResendingToken(resendingToken!!).build()
            )
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options.build())
        }
    }

    private fun startResendTimer() {
        binding.tvReSendCode.isEnabled = false
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                timeOutSeconds--
                runOnUiThread {
                    binding.tvReSendCode.text = "Resend code in $timeOutSeconds seconds"
                }
                if (timeOutSeconds <= 0) {
                    timeOutSeconds = 60L
                    timer.cancel()
                    runOnUiThread {
                        binding.tvReSendCode.isEnabled = true
                    }
                }
            }
        }, 0, 1000)

    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val currentUserId: String = it.result.user!!.uid
                signNewUser(currentUserId)
            } else {
                Toast.makeText(this, "Sign in Code Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signNewUser(currentUserId: String) {
        loader?.setMessage("Registering ...")
        loader?.setCanceledOnTouchOutside(false)
        loader?.show()
        val newUser = User()
        newUser.id = currentUserId
        newUser.phone_number = full_phone_number
        newUser.name = name
        newUser.password = password

        /*mDatabase.reference.child("Users").child(currentUserId).setValue(newUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show()
                    finish()
                    loader!!.dismiss()
                }
                else {
                    Toast.makeText(this, "Error ," + it.exception!!.message, Toast.LENGTH_LONG)
                        .show()
                }
                finish()
            }*/

        mFireStore.collection("Users").document(currentUserId).set(newUser).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                loader!!.dismiss()
            }
            else {
                Toast.makeText(this, "Error ,${it.exception!!.message}", Toast.LENGTH_LONG).show()
            }
            finish()
        }
    }

}